/*
 * Copyright (C) 2017, Ulrich Wolffgang <ulrich.wolffgang@proleap.io>
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */

package io.proleap.cobol.preprocessor.sub.document.impl;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import org.antlr.v4.runtime.BufferedTokenStream;

import io.proleap.cobol.CobolPreprocessorParser.ReplaceClauseContext;

/**
 * A replacement context that defines, which replaceables should be replaced by
 * which replacements.
 */
public class CobolDocumentContext {

	private CobolReplacementMapping[] currentReplaceableReplacements;

	private StringBuffer outputBuffer = new StringBuffer();

	public String read() {
		return outputBuffer.toString();
	}

	private int matchLength(final List<CobolTextWord> words, final int at, final List<String> replaceableWords) {
		final int length = replaceableWords.size();
		final boolean fits = length > 0 && at + length <= words.size();
		final boolean matches = fits && IntStream.range(0, length)
				.allMatch(i -> words.get(at + i).text().equals(replaceableWords.get(i)));
		return matches ? length : 0;
	}

	/**
	 * Replaces replaceables with replacements in one left-to-right pass over the
	 * text-words: at each text-word the operands are tried in the order written,
	 * the first that matches is replaced, and replacement text is not compared
	 * again.
	 */
	public void replaceReplaceablesByReplacements(final BufferedTokenStream tokens) {
		if (currentReplaceableReplacements == null) {
			return;
		}

		final CobolTextWordScanner scanner = new CobolTextWordScanner();
		final List<List<String>> replaceables = Arrays.stream(currentReplaceableReplacements)
				.map(mapping -> mapping.replaceableWords(tokens, scanner)).toList();
		final List<String> replacements = Arrays.stream(currentReplaceableReplacements)
				.map(mapping -> mapping.replacementText(tokens)).toList();

		final String text = outputBuffer.toString();
		final List<CobolTextWord> words = scanner.scan(text);
		final StringBuffer replaced = new StringBuffer();
		int copiedUpTo = 0;
		int at = 0;

		while (at < words.size()) {
			final int current = at;
			final OptionalInt operand = IntStream.range(0, replaceables.size())
					.filter(i -> matchLength(words, current, replaceables.get(i)) > 0).findFirst();

			if (operand.isEmpty()) {
				at++;
			} else {
				final int length = replaceables.get(operand.getAsInt()).size();
				replaced.append(text, copiedUpTo, words.get(at).start());
				replaced.append(replacements.get(operand.getAsInt()));
				copiedUpTo = words.get(at + length - 1).end();
				at += length;
			}
		}

		replaced.append(text.substring(copiedUpTo));
		outputBuffer = replaced;
	}

	public void storeReplaceablesAndReplacements(final List<ReplaceClauseContext> replaceClauses) {
		if (replaceClauses == null) {
			currentReplaceableReplacements = null;
		} else {
			final int length = replaceClauses.size();
			currentReplaceableReplacements = new CobolReplacementMapping[length];

			int i = 0;

			for (final ReplaceClauseContext replaceClause : replaceClauses) {
				final CobolReplacementMapping replaceableReplacement = new CobolReplacementMapping();

				replaceableReplacement.replaceable = replaceClause.replaceable();
				replaceableReplacement.replacement = replaceClause.replacement();

				currentReplaceableReplacements[i] = replaceableReplacement;
				i++;
			}
		}
	}

	public void write(final String text) {
		outputBuffer.append(text);
	}
}
