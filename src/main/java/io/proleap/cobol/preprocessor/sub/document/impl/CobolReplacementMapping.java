/*
 * Copyright (C) 2017, Ulrich Wolffgang <ulrich.wolffgang@proleap.io>
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the MIT license. See the LICENSE file for details.
 */

package io.proleap.cobol.preprocessor.sub.document.impl;

import java.util.List;

import org.antlr.v4.runtime.BufferedTokenStream;

import io.proleap.cobol.CobolPreprocessorParser.PseudoTextContext;
import io.proleap.cobol.CobolPreprocessorParser.ReplaceableContext;
import io.proleap.cobol.CobolPreprocessorParser.ReplacementContext;
import io.proleap.cobol.preprocessor.sub.util.TokenUtils;

/**
 * A mapping from a replaceable to a replacement.
 */
public class CobolReplacementMapping {

	public ReplaceableContext replaceable;

	public ReplacementContext replacement;

	private String extractPseudoText(final PseudoTextContext pseudoTextCtx, final BufferedTokenStream tokens) {
		final String pseudoText = TokenUtils.getTextIncludingHiddenTokens(pseudoTextCtx, tokens).trim();
		final String content = pseudoText.replaceAll("^==", "").replaceAll("==$", "").trim();
		return content;
	}

	private String getText(final ReplaceableContext ctx, final BufferedTokenStream tokens) {
		final String result;

		if (ctx.pseudoText() != null) {
			result = extractPseudoText(ctx.pseudoText(), tokens);
		} else if (ctx.charDataLine() != null) {
			result = TokenUtils.getTextIncludingHiddenTokens(ctx, tokens);
		} else {
			result = ctx.getText();
		}

		return result;
	}

	private String getText(final ReplacementContext ctx, final BufferedTokenStream tokens) {
		final String result;

		if (ctx.pseudoText() != null) {
			result = extractPseudoText(ctx.pseudoText(), tokens);
		} else if (ctx.charDataLine() != null) {
			result = TokenUtils.getTextIncludingHiddenTokens(ctx, tokens);
		} else {
			result = ctx.getText();
		}

		return result;
	}

	protected List<String> replaceableWords(final BufferedTokenStream tokens, final CobolTextWordScanner scanner) {
		return scanner.scan(getText(replaceable, tokens)).stream().map(CobolTextWord::text).toList();
	}

	protected String replacementText(final BufferedTokenStream tokens) {
		return getText(replacement, tokens);
	}

	@Override
	public String toString() {
		return replaceable.getText() + " -> " + replacement.getText();
	}
}
