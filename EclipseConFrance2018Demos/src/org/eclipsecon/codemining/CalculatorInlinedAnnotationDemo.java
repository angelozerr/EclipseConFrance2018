/**
 *  Copyright (c) 2017 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - [CodeMining] Provide inline annotations support - Bug 527675
 */
package org.eclipsecon.codemining;

import java.util.HashSet;
import java.util.Set;

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
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * An inlined demo with {@link LineHeaderAnnotation} and
 * {@link LineContentAnnotation} annotations both:
 *
 * <ul>
 * <li>a status OK, NOK is displayed before the line which starts with 'color:'.
 * This status is the result of the content after 'color' which must be a rgb
 * content. Here {@link ColorStatusAnnotation} is used.</li>
 * <li>a colorized square is displayed before the rgb declaration (inside the
 * line content). Here {@link ColorAnnotation} is used.</li>
 * </ul>
 *
 */
public class CalculatorInlinedAnnotationDemo {

	public static void main(String[] args) throws Exception {

		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		shell.setText("Calculator Inlined annotation demo");

		// Create source viewer and initialize the content
		ISourceViewer sourceViewer = new SourceViewer(shell, null, SWT.V_SCROLL | SWT.BORDER);
		sourceViewer.setDocument(new Document("1+1// here a demo with line content annotation"), new AnnotationModel());

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

	/**
	 * Returns the inlined annotations list to display in the given viewer.
	 *
	 * @param viewer  the viewer
	 * @param support the inlined annotation suppor.
	 * @return the inlined annotations list to display in the given viewer.
	 */
	private static Set<AbstractInlinedAnnotation> getInlinedAnnotation(ISourceViewer viewer,
			InlinedAnnotationSupport support) {
		IDocument document = viewer.getDocument();
		String text = document.get();
		Set<AbstractInlinedAnnotation> annotations = new HashSet<>();
		if (!text.isEmpty()) {
			String messageError = validate(text);
			if (messageError != null) {
				// error
				Position pos = new Position(0, 1);
				LineHeaderAnnotation error = support.findExistingAnnotation(pos);
				if (error == null) {
					error = new LineHeaderAnnotation(pos, viewer);
				}
				error.setText(messageError);
				annotations.add(error);
			} else {
				// Compute the formula
				Position pos = new Position(3, 1);
				LineContentAnnotation result = support.findExistingAnnotation(pos);
				if (result == null) {
					result = new LineContentAnnotation(pos, viewer);
				}
				int compute = Integer.parseInt(text.substring(0, 1)) + Integer.parseInt(text.substring(2, 3));
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {					
					e.printStackTrace();
				}
				result.setText("=" + compute);
				annotations.add(result);
			}
		}
		return annotations;
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

	/**
	 * Returns the line text.
	 *
	 * @param document the document.
	 * @param line     the line index.
	 * @return the line text.
	 */
	private static String getLineText(IDocument document, int line) {
		try {
			int offset = document.getLineOffset(line);
			int length = document.getLineLength(line);
			return document.get(offset, length);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
