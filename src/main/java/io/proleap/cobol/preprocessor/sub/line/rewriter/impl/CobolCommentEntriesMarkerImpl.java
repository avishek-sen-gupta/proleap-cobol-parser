/*
 * Copyright (C) 2017, Ulrich Wolffgang <ulrich.wolffgang@proleap.io>
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */

package io.proleap.cobol.preprocessor.sub.line.rewriter.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.proleap.cobol.asg.params.CobolDialect;
import io.proleap.cobol.preprocessor.CobolPreprocessor;
import io.proleap.cobol.preprocessor.sub.CobolLine;
import io.proleap.cobol.preprocessor.sub.CobolLineTypeEnum;
import io.proleap.cobol.preprocessor.sub.line.rewriter.CobolCommentEntriesMarker;

public class CobolCommentEntriesMarkerImpl implements CobolCommentEntriesMarker {

	protected final Pattern commentEntryTriggerLinePattern;

	/**
	 * The same paragraph headers, without the separator period they should have
	 * been written with, and restricted to area A: a comment-entry paragraph header
	 * can only begin there. The whitespace separating the header from the comment
	 * entry stays part of the comment entry: the tag written in place of the period
	 * has to be followed by whitespace of its own, or it does not read as the start
	 * of a comment-entry line at all.
	 */
	protected final Pattern commentEntryTriggerLineWithoutSeparatorPattern;

	protected boolean foundCommentEntryTriggerInPreviousLine = false;

	/**
	 * The comment-entry paragraphs all belong to the identification division, and
	 * their keywords are only unambiguously headers there. Elsewhere a line can
	 * begin with one of them and mean something else -- notably a continuation line
	 * of an EXEC block, such as SECURITY of an EXEC CICS QUERY SECURITY -- and
	 * tagging it would turn a line of a real statement into commentary, which is a
	 * silently dropped statement rather than a parse error.
	 *
	 * Starts off closed: a copy book is preprocessed on its own and declares no
	 * identification division at all, so a header keyword in one is never a header.
	 */
	protected boolean isInIdentificationDivision = false;

	protected boolean isInCommentEntry = false;

	protected final Pattern identificationDivisionPattern = Pattern
			.compile("[ \\t]{0,3}(IDENTIFICATION|ID)[ \\t]+DIVISION.*", Pattern.CASE_INSENSITIVE);

	protected final Pattern otherDivisionPattern = Pattern
			.compile("[ \\t]{0,3}(ENVIRONMENT|DATA|PROCEDURE)[ \\t]+DIVISION.*", Pattern.CASE_INSENSITIVE);

	protected final String[] triggersEnd = new String[] { "PROGRAM-ID.", "AUTHOR.", "INSTALLATION.", "DATE-WRITTEN.",
			"DATE-COMPILED.", "SECURITY.", "ENVIRONMENT", "DATA.", "PROCEDURE." };

	protected final String[] triggersStart = new String[] { "AUTHOR.", "INSTALLATION.", "DATE-WRITTEN.",
			"DATE-COMPILED.", "SECURITY.", "REMARKS." };

	protected final String[] triggersStartWithoutSeparator = new String[] { "AUTHOR", "INSTALLATION", "DATE-WRITTEN",
			"DATE-COMPILED", "SECURITY", "REMARKS" };

	public CobolCommentEntriesMarkerImpl() {
		final String commentEntryTriggerLineFormat = new String(
				"([ \\t]*)(" + alternationOfLiterals(triggersStart) + ")(.+)");
		commentEntryTriggerLinePattern = Pattern.compile(commentEntryTriggerLineFormat, Pattern.CASE_INSENSITIVE);

		final String commentEntryTriggerLineWithoutSeparatorFormat = new String(
				"([ \\t]{0,3})(" + alternationOfLiterals(triggersStartWithoutSeparator) + ")([ \\t].+)");
		commentEntryTriggerLineWithoutSeparatorPattern = Pattern
				.compile(commentEntryTriggerLineWithoutSeparatorFormat, Pattern.CASE_INSENSITIVE);
	}

	/**
	 * An alternation matching any one of the given triggers literally. The
	 * separator period of a trigger such as AUTHOR. is a regex metacharacter and
	 * has to be quoted: unquoted, it matches any character, so the pattern also
	 * accepts a trigger keyword followed by something that is not a separator
	 * period at all.
	 */
	protected String alternationOfLiterals(final String[] triggers) {
		final List<String> quotedTriggers = new ArrayList<String>();

		for (final String trigger : triggers) {
			quotedTriggers.add(Pattern.quote(trigger));
		}

		return String.join("|", quotedTriggers);
	}

	protected CobolLine buildMultiLineCommentEntryLine(final CobolLine line) {
		return CobolLine.copyCobolLineWithIndicatorArea(CobolPreprocessor.COMMENT_ENTRY_TAG + CobolPreprocessor.WS,
				line);
	}

	/**
	 * Escapes in a given line a potential comment entry.
	 */
	protected CobolLine escapeCommentEntry(final CobolLine line) {
		final CobolLine result;

		final Matcher matcher = matchCommentEntryTriggerLine(line);

		if (matcher != null) {
			final String whitespace = matcher.group(1);
			final String trigger = matcher.group(2);
			final String commentEntry = matcher.group(3);
			final String newContentArea = whitespace + trigger + CobolPreprocessor.WS
					+ CobolPreprocessor.COMMENT_ENTRY_TAG + commentEntry;

			result = CobolLine.copyCobolLineWithContentArea(newContentArea, line);
		} else {
			result = line;
		}

		return result;
	}

	/**
	 * The matcher of whichever comment-entry trigger the given line starts with,
	 * the header written with its separator period taking precedence over the
	 * header written without one; null, if the line starts no comment entry.
	 *
	 * A comment line cannot be a header, and its text is free prose in which a
	 * header word regularly appears, so a banner line beginning with one of them
	 * would otherwise turn the whole banner into a comment entry.
	 */
	protected Matcher matchCommentEntryTriggerLine(final CobolLine line) {
		final String contentArea = line.getContentArea();
		final Matcher matcher = commentEntryTriggerLinePattern.matcher(contentArea);

		if (matcher.matches()) {
			return matcher;
		}

		if (!isInIdentificationDivision || CobolLineTypeEnum.COMMENT.equals(line.getType())) {
			return null;
		}

		final Matcher matcherWithoutSeparator = commentEntryTriggerLineWithoutSeparatorPattern.matcher(contentArea);
		return matcherWithoutSeparator.matches() ? matcherWithoutSeparator : null;
	}

	protected boolean isInCommentEntry(final CobolLine line, final boolean isContentAreaAEmpty,
			final boolean isInOsvsCommentEntry) {
		final boolean result = CobolLineTypeEnum.COMMENT.equals(line.getType()) || isContentAreaAEmpty
				|| isInOsvsCommentEntry;
		return result;
	}

	/**
	 * OSVS: The comment-entry can be contained in either area A or area B of the
	 * comment-entry lines. However, the next occurrence in area A of any one of the
	 * following COBOL words or phrases terminates the comment-entry and begin the
	 * next paragraph or division.
	 */
	protected boolean isInOsvsCommentEntry(final CobolLine line) {
		final boolean result = CobolDialect.OSVS.equals(line.getDialect()) && !startsWithTrigger(line, triggersEnd);
		return result;
	}

	@Override
	public CobolLine processLine(final CobolLine line) {
		final CobolLine result;

		trackIdentificationDivision(line);

		if (line.getFormat().isCommentEntryMultiLine()) {
			result = processMultiLineCommentEntry(line);
		} else {
			result = processSingleLineCommentEntry(line);
		}

		return result;
	}

	@Override
	public List<CobolLine> processLines(final List<CobolLine> lines) {
		final List<CobolLine> result = new ArrayList<CobolLine>();

		for (final CobolLine line : lines) {
			final CobolLine processedLine = processLine(line);
			result.add(processedLine);
		}

		return result;
	}

	/**
	 * If the Compiler directive SOURCEFORMAT is specified as or defaulted to FIXED,
	 * the comment-entry can be contained on one or more lines but is restricted to
	 * area B of those lines; the next line commencing in area A begins the next
	 * non-comment entry.
	 */
	protected CobolLine processMultiLineCommentEntry(final CobolLine line) {
		final boolean foundCommentEntryTriggerInCurrentLine = startsCommentEntry(line);
		final CobolLine result;

		if (foundCommentEntryTriggerInCurrentLine) {
			result = escapeCommentEntry(line);
		} else if (foundCommentEntryTriggerInPreviousLine || isInCommentEntry) {
			final boolean isContentAreaAEmpty = line.getContentAreaA().trim().isEmpty();
			final boolean isInOsvsCommentEntry = isInOsvsCommentEntry(line);

			isInCommentEntry = isInCommentEntry(line, isContentAreaAEmpty, isInOsvsCommentEntry);

			if (isInCommentEntry) {
				result = buildMultiLineCommentEntryLine(line);
			} else {
				result = line;
			}
		} else {
			result = line;
		}

		foundCommentEntryTriggerInPreviousLine = foundCommentEntryTriggerInCurrentLine;

		return result;
	}

	protected CobolLine processSingleLineCommentEntry(final CobolLine line) {
		final boolean foundCommentEntryTriggerInCurrentLine = startsCommentEntry(line);
		final CobolLine result;

		if (foundCommentEntryTriggerInCurrentLine) {
			result = escapeCommentEntry(line);
		} else {
			result = line;
		}

		return result;
	}

	/**
	 * Checks, whether given line starts a comment entry: with a trigger keyword
	 * and its separator period, or with a trigger keyword and no separator period
	 * at all, the latter in area A only.
	 */
	protected boolean startsCommentEntry(final CobolLine line) {
		return startsWithTrigger(line, triggersStart) || matchCommentEntryTriggerLine(line) != null;
	}

	/**
	 * Notes whether the lines that follow belong to an identification division. A
	 * batch of programs holds one identification division per program unit, so this
	 * turns back on rather than latching off.
	 */
	protected void trackIdentificationDivision(final CobolLine line) {
		final String contentArea = line.getContentArea();

		if (identificationDivisionPattern.matcher(contentArea).matches()) {
			isInIdentificationDivision = true;
		} else if (otherDivisionPattern.matcher(contentArea).matches()) {
			isInIdentificationDivision = false;
		}
	}

	/**
	 * Checks, whether given line starts with a trigger keyword indicating a comment
	 * entry.
	 */
	protected boolean startsWithTrigger(final CobolLine line, final String[] triggers) {
		final String contentAreaUpperCase = new String(line.getContentArea()).toUpperCase();

		boolean result = false;

		for (final String trigger : triggers) {
			final boolean containsTrigger = contentAreaUpperCase.trim().startsWith(trigger);

			if (containsTrigger) {
				result = true;
				break;
			}
		}

		return result;
	}
}
