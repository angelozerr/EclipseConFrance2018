/**
 *  Copyright (c) 2018, Angelo ZERR and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - [CodeMining] CodeMining should support line header/content annotation type both - Bug 529115
 */
package org.eclipsecon.codemining.emoji.demo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.AnnotationPainter;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.inlined.AbstractInlinedAnnotation;
import org.eclipse.jface.text.source.inlined.InlinedAnnotationSupport;
import org.eclipse.jface.text.source.inlined.LineContentAnnotation;
import org.eclipse.jface.text.source.inlined.LineHeaderAnnotation;
import org.eclipse.jface.text.source.inlined.Positions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipsecon.codemining.emoji.EmojiParser;
import org.eclipsecon.codemining.emoji.EmojiParser.Emoji;

/**
 * Code Mining demo with Emoji
 *
 */
public class EmojiWithInlinedAnnotation {

	public static void main(String[] args) throws Exception {

		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		shell.setText("Emoji inlined annotation");

		// Create source viewer and initialize the content
		ISourceViewer sourceViewer = new SourceViewer(shell, null, SWT.V_SCROLL | SWT.BORDER);
		sourceViewer.setDocument(new Document("Here some emoji like :phone:, :umbrella: \nand :heart:"),
				new AnnotationModel());

		// Initialize inlined annotations support
		InlinedAnnotationSupport support = new InlinedAnnotationSupport();
		support.install(sourceViewer, createAnnotationPainter(sourceViewer));

		sourceViewer.getTextWidget().addModifyListener(e -> {
			redresh(sourceViewer, support);
		});
		redresh(sourceViewer, support);

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	private static void redresh(ISourceViewer sourceViewer, InlinedAnnotationSupport support) {
		Set<AbstractInlinedAnnotation> annotations = getInlinedAnnotation(sourceViewer, support);
		support.updateAnnotations(annotations);
	}

	/**
	 * Returns the inlined annotations list to display in the given viewer.
	 *
	 * @param viewer  the viewer
	 * @param support the inlined annotation suppor.
	 * @return the inlined annotations list to display in the given viewer.
	 */
	private static Set<AbstractInlinedAnnotation> getInlinedAnnotation(ISourceViewer viewer,
			InlinedAnnotationSupport support) {
		Set<AbstractInlinedAnnotation> annotations = new HashSet<>();
		Map<Integer, Integer> emojiByLineCount = new HashMap<>();
		IDocument document = viewer.getDocument();
		// Loop for each line of document
		int lineCount = document.getNumberOfLines();
		for (int i = 0; i < lineCount; i++) {
			// check if request was canceled.
			String lineText = getLineText(document, i);
			try {
				int startLineOffset = document.getLineOffset(i);
				// Collect emoji list for the current line
				List<Emoji> emojis = EmojiParser.extractEmojis(lineText);
				if (!emojis.isEmpty()) {
					for (Emoji emoji : emojis) {
						// Display emoji unicode before the emoji with line content annotation						
						Position pos = new Position(startLineOffset + emoji.offset, 1);
						LineContentAnnotation annotation = support.findExistingAnnotation(pos);
						if (annotation == null) {
							annotation = new LineContentAnnotation(pos, viewer);
						}
						String uniCode = EmojiParser.getUniCode(emoji.tagName);
						annotation.setText(uniCode != null ? "[" + uniCode + "]" : "");
						annotations.add(annotation);
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
				// Display emoji count for the given line with line header annotation
				int line = entry.getKey();
				int totalLine = entry.getValue();
				Position pos = Positions.of(line, document, true);
				LineHeaderAnnotation annotation = support.findExistingAnnotation(pos);
				if (annotation == null) {
					annotation = new LineHeaderAnnotation(pos, viewer);
				}
				annotation.setText(totalLine + "/" + total + " emoji" + (totalLine > 1 ? "s" : ""));
				annotations.add(annotation);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		});
		return annotations;
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

	/**
	 * Create annotation painter.
	 *
	 * @param viewer the viewer.
	 * @return annotation painter.
	 */
	private static AnnotationPainter createAnnotationPainter(ISourceViewer viewer) {
		IAnnotationAccess annotationAccess = new IAnnotationAccess() {
			@Override
			public Object getType(Annotation annotation) {
				return annotation.getType();
			}

			@Override
			public boolean isMultiLine(Annotation annotation) {
				return true;
			}

			@Override
			public boolean isTemporary(Annotation annotation) {
				return true;
			}

		};
		AnnotationPainter painter = new AnnotationPainter(viewer, annotationAccess);
		((ITextViewerExtension2) viewer).addPainter(painter);
		return painter;
	}
}
