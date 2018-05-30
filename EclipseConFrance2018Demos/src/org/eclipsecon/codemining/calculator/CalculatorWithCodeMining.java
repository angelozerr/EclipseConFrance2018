package org.eclipsecon.codemining.calculator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.codemining.AbstractCodeMiningProvider;
import org.eclipse.jface.text.codemining.ICodeMining;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;
import org.eclipse.jface.text.codemining.LineContentCodeMining;
import org.eclipse.jface.text.codemining.LineHeaderCodeMining;
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

public class CalculatorWithCodeMining {

	public static void main(String[] args) throws Exception {

		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		shell.setText("Calculator code mining");

		// Create source viewer and initialize the content
		ISourceViewer sourceViewer = new SourceViewer(shell, null, SWT.V_SCROLL | SWT.BORDER);
		sourceViewer.setDocument(new Document("1+1// here a demo with line content annotation"), new AnnotationModel());

		// Initialize code mining support
		((ISourceViewerExtension5)sourceViewer).setCodeMiningAnnotationPainter(createAnnotationPainter(sourceViewer));
		((ISourceViewerExtension5)sourceViewer).setCodeMiningProviders(new ICodeMiningProvider[] {new CalculatorCodeMiningProvider()});
		
		sourceViewer.getTextWidget().addModifyListener(e -> {
			((ISourceViewerExtension5)sourceViewer).updateCodeMinings();
		});
		((ISourceViewerExtension5)sourceViewer).updateCodeMinings();

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
				if (!text.isEmpty()) {
					String messageError = validate(text);
					if (messageError != null) {
						// error
						/*Position pos = new Position(0, 1);
						LineHeaderAnnotation error = support.findExistingAnnotation(pos);
						if (error == null) {
							error = new LineHeaderAnnotation(pos, viewer);
						}
						error.setText(messageError);
						annotations.add(error);*/
						// error
						try {
							LineHeaderCodeMining mining = new LineHeaderCodeMining(0, document, null) {};
							mining.setLabel(messageError);
							minings.add(mining);
						} catch (BadLocationException e) {
							e.printStackTrace();
						}						
					} else {
						// Compute the formula
						/*Position pos = new Position(3, 1);
						LineContentAnnotation result = support.findExistingAnnotation(pos);
						if (result == null) {
							result = new LineContentAnnotation(pos, viewer);
						}
						int compute = Integer.parseInt(text.substring(0, 1)) + Integer.parseInt(text.substring(2, 3));
						result.setText("=" + compute);
						annotations.add(result);*/
						// Compute the formula
						Position pos = new Position(3, 1);
						LineContentCodeMining mining = new LineContentCodeMining(pos, null) {};
						int compute = Integer.parseInt(text.substring(0, 1)) + Integer.parseInt(text.substring(2, 3));
						mining.setLabel("=" + compute);
						minings.add(mining);
					}
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
