package org.eclipsecon.codemining;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.codemining.AbstractCodeMiningProvider;
import org.eclipse.jface.text.codemining.ICodeMining;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;
import org.eclipse.jface.text.codemining.LineContentCodeMining;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.AnnotationPainter;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.ISourceViewerExtension5;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipsecon.codemining.EmojiParser.Emoji;

public class EmojiWithCodeMining {

	public static void main(String[] args) throws Exception {

		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		shell.setText("Emoji code mining");

		// Create source viewer and initialize the content
		ISourceViewer sourceViewer = new SourceViewer(shell, null, SWT.V_SCROLL | SWT.BORDER);
		sourceViewer.setDocument(new Document("Here some emoji like :phone: and :umbrella: "), new AnnotationModel());

		// Initialize code mining support
		((ISourceViewerExtension5) sourceViewer).setCodeMiningAnnotationPainter(createAnnotationPainter(sourceViewer));
		((ISourceViewerExtension5) sourceViewer)
				.setCodeMiningProviders(new ICodeMiningProvider[] { new CalculatorCodeMiningProvider() });

		sourceViewer.getTextWidget().addModifyListener(e -> {
			((ISourceViewerExtension5) sourceViewer).updateCodeMinings();
		});
		((ISourceViewerExtension5) sourceViewer).updateCodeMinings();

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	private static class CalculatorCodeMiningProvider extends AbstractCodeMiningProvider {

		@Override
		public CompletableFuture<List<? extends ICodeMining>> provideCodeMinings(ITextViewer viewer,
				IProgressMonitor monitor) {
			return CompletableFuture.supplyAsync(() -> {
				List<ICodeMining> minings = new ArrayList<>();
				IDocument document = viewer.getDocument();
				String text = document.get();
				List<Emoji> emojis = EmojiParser.extractEmojis(text);
				for (Emoji emoji : emojis) {
					String uniCode = EmojiParser.getUniCode(emoji.tagName);
					LineContentCodeMining mining = new LineContentCodeMining(emoji.position, null) {
					};
					mining.setLabel(uniCode != null ? " [" + uniCode + "]": "");
					minings.add(mining);
				}
				return minings;
			});
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

	private static String validate(String text) {
		if (text.length() < 4) {
			return "Text must be >=4";
		}
		if (!Character.isDigit(text.charAt(0))) {
			return "First character must be a number";
		}
		if (text.charAt(1) != '+') {
			return "Second character must be +";
		}
		if (!Character.isDigit(text.charAt(2))) {
			return "Third character must be a number";
		}
		return null;
	}
}
