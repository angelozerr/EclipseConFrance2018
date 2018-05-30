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

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;
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
import org.eclipsecon.codemining.emoji.EmojiCodeMiningProvider;

/**
 * Code Mining demo with Emoji
 *
 */
public class EmojiWithCodeMining {

	public static void main(String[] args) throws Exception {

		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		shell.setText("Emoji code mining");

		// Create source viewer and initialize the content
		ISourceViewer sourceViewer = new SourceViewer(shell, null, SWT.V_SCROLL | SWT.BORDER);
		sourceViewer.setDocument(new Document("Here some emoji like :phone:, :umbrella: \nand :heart:"),
				new AnnotationModel());

		// Initialize code mining support
		((ISourceViewerExtension5) sourceViewer).setCodeMiningAnnotationPainter(createAnnotationPainter(sourceViewer));
		((ISourceViewerExtension5) sourceViewer)
				.setCodeMiningProviders(new ICodeMiningProvider[] { new EmojiCodeMiningProvider() });

		sourceViewer.getTextWidget().addModifyListener(e -> {
			// Update code minings when text changed
			((ISourceViewerExtension5) sourceViewer).updateCodeMinings();
		});

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
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
