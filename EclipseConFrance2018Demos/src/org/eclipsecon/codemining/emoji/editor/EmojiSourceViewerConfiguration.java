package org.eclipsecon.codemining.emoji.editor;

import org.eclipse.jface.text.codemining.CodeMiningReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

public class EmojiSourceViewerConfiguration extends SourceViewerConfiguration{

	@Override
	public IReconciler getReconciler(ISourceViewer sourceViewer) {
		return new CodeMiningReconciler();
	}
}
