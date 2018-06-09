package org.eclipsecon.codemining.emoji;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;
import org.eclipse.jface.text.codemining.LineHeaderCodeMining;

public class EmojiLineHeaderCodeMining extends LineHeaderCodeMining {

	public EmojiLineHeaderCodeMining(int beforeLineNumber, IDocument document, long total, int totalLine,
			ICodeMiningProvider provider) throws BadLocationException {
		super(beforeLineNumber, document, provider);
		super.setLabel(totalLine + "/" + total + " emoji" + (totalLine > 1 ? "s" : ""));
	}

}
