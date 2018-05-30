package org.eclipsecon.codemining.emoji.editor;

import org.eclipse.ui.editors.text.TextEditor;

public class EmojiEditor extends TextEditor {

	public EmojiEditor() {
		super.setSourceViewerConfiguration(new EmojiSourceViewerConfiguration());
	}

}
