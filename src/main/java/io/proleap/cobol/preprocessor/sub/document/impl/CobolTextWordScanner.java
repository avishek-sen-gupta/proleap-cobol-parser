/*
 * Copyright (C) 2017, Ulrich Wolffgang <ulrich.wolffgang@proleap.io>
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */

package io.proleap.cobol.preprocessor.sub.document.impl;

import java.util.ArrayList;
import java.util.List;

import io.proleap.cobol.preprocessor.CobolPreprocessor;

/**
 * Splits text into the text-words that COPY REPLACING and REPLACE compare.
 */
public class CobolTextWordScanner {

	private static final String COMMENT_TAG = CobolPreprocessor.COMMENT_TAG;

	private static boolean isQuote(final char c) {
		return c == '"' || c == '\'';
	}

	private static boolean isSingleCharacterSeparator(final char c) {
		return c == '(' || c == ')' || c == ':';
	}

	private static boolean isSpace(final char c) {
		return Character.isWhitespace(c);
	}

	private boolean isComment(final String text, final int i) {
		final int afterTag = i + COMMENT_TAG.length();
		return text.startsWith(COMMENT_TAG, i) && (afterTag == text.length() || isSpace(text.charAt(afterTag)));
	}

	private boolean isFollowedBySpace(final String text, final int i) {
		return i + 1 == text.length() || isSpace(text.charAt(i + 1));
	}

	private boolean isIgnoredSeparator(final String text, final int i) {
		final char c = text.charAt(i);
		return isSpace(c) || (c == ',' || c == ';') && isFollowedBySpace(text, i);
	}

	private int lineEnd(final String text, final int i) {
		final int newline = text.indexOf('\n', i);
		return newline < 0 ? text.length() : newline;
	}

	private boolean isPeriodSeparator(final String text, final int i) {
		return text.charAt(i) == '.' && isFollowedBySpace(text, i);
	}

	private int literalEnd(final String text, final int open) {
		final char quote = text.charAt(open);
		int i = open + 1;

		while (i < text.length()) {
			if (text.charAt(i) != quote) {
				i++;
			} else if (i + 1 < text.length() && text.charAt(i + 1) == quote) {
				i += 2;
			} else {
				return i + 1;
			}
		}

		return i;
	}

	public List<CobolTextWord> scan(final String text) {
		final List<CobolTextWord> words = new ArrayList<CobolTextWord>();
		int i = 0;

		while (i < text.length()) {
			if (isIgnoredSeparator(text, i)) {
				i++;
			} else if (isComment(text, i)) {
				i = lineEnd(text, i);
			} else {
				final int end = wordEnd(text, i);
				words.add(new CobolTextWord(text.substring(i, end), i, end));
				i = end;
			}
		}

		return words;
	}

	private int wordEnd(final String text, final int start) {
		final char first = text.charAt(start);

		if (isSingleCharacterSeparator(first) || isPeriodSeparator(text, start)) {
			return start + 1;
		}

		int i = start;

		while (i < text.length()) {
			final char c = text.charAt(i);

			if (isQuote(c)) {
				i = literalEnd(text, i);
			} else if (isIgnoredSeparator(text, i) || isSingleCharacterSeparator(c) || isPeriodSeparator(text, i)) {
				return i;
			} else {
				i++;
			}
		}

		return i;
	}
}
