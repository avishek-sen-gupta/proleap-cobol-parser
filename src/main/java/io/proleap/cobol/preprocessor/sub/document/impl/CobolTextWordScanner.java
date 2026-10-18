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

/**
 * Splits text into the text-words that COPY REPLACING and REPLACE compare.
 */
public class CobolTextWordScanner {

	private static boolean isQuote(final char c) {
		return c == '"' || c == '\'';
	}

	private static boolean isSingleCharacterSeparator(final char c) {
		return c == '(' || c == ')' || c == ':';
	}

	private static boolean isSpace(final char c) {
		return Character.isWhitespace(c);
	}

	private boolean isPeriodSeparator(final String text, final int i) {
		return text.charAt(i) == '.' && (i + 1 == text.length() || isSpace(text.charAt(i + 1)));
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
			if (isSpace(text.charAt(i))) {
				i++;
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
			} else if (isSpace(c) || isSingleCharacterSeparator(c) || isPeriodSeparator(text, i)) {
				return i;
			} else {
				i++;
			}
		}

		return i;
	}
}
