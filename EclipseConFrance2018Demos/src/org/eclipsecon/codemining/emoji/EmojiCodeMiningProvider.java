package org.eclipsecon.codemining.emoji;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.codemining.AbstractCodeMiningProvider;
import org.eclipse.jface.text.codemining.ICodeMining;
import org.eclipse.jface.text.codemining.LineContentCodeMining;
import org.eclipse.jface.text.codemining.LineHeaderCodeMining;
import org.eclipsecon.codemining.emoji.EmojiParser.Emoji;

public class EmojiCodeMiningProvider extends AbstractCodeMiningProvider {

	@Override
	public CompletableFuture<List<? extends ICodeMining>> provideCodeMinings(ITextViewer viewer,
			IProgressMonitor monitor) {
		return CompletableFuture.supplyAsync(() -> {
			List<ICodeMining> minings = new ArrayList<>();
			Map<Integer, Integer> emojiByLineCount = new HashMap<>();
			IDocument document = viewer.getDocument();
			// Loop for each line of document
			int lineCount = document.getNumberOfLines();
			for (int i = 0; i < lineCount; i++) {
				// check if request was canceled.
				monitor.isCanceled();
				String lineText = getLineText(document, i);
				try {
					int startLineOffset = document.getLineOffset(i);
					// Collect emoji list for the current line
					List<Emoji> emojis = EmojiParser.extractEmojis(lineText);
					if (!emojis.isEmpty()) {
						for (Emoji emoji : emojis) {
							// Display emoji unicode before the emoji with line content annotation
							String uniCode = EmojiParser.getUniCode(emoji.tagName);
							Position pos = new Position(startLineOffset + emoji.offset, 1);
							LineContentCodeMining mining = new EmojiLineContentCodeMining(uniCode, pos, this);
							minings.add(mining);
						}
						emojiByLineCount.put(i, emojis.size());
					}
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
			// Create line header minings which render the number of emoji for each lines.
			long total = emojiByLineCount.values().stream().mapToInt(Integer::intValue).sum();
			emojiByLineCount.entrySet().forEach(entry -> {
				try {
					int line = entry.getKey();
					int totalLine = entry.getValue();
					LineHeaderCodeMining mining = new EmojiLineHeaderCodeMining(line, document, total, totalLine, this);
					minings.add(mining);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			});
			return minings;
		});
	}

	private static String getLineText(IDocument document, int line) {
		try {
			int lo = document.getLineOffset(line);
			int ll = document.getLineLength(line);
			return document.get(lo, ll);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
