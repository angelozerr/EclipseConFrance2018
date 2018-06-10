package org.eclipsecon.codemining.emoji;

import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;
import org.eclipse.jface.text.codemining.LineContentCodeMining;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

public class EmojiLineContentCodeMining extends LineContentCodeMining {

	private final String tagName;

	private String uniCode;

	private Consumer<MouseEvent> action;

	public EmojiLineContentCodeMining(String tagName, Position position, ICodeMiningProvider provider) {
		super(position, provider);
		this.tagName = tagName;
	}

	private String getUniCode() {
		return uniCode;
	}

	@Override
	protected CompletableFuture<Void> doResolve(ITextViewer viewer, IProgressMonitor monitor) {
		return CompletableFuture.runAsync(() -> {
			this.uniCode = EmojiParser.getUniCode(tagName);
			super.setLabel(uniCode != null ? " [" + uniCode + "]" : "");
		});
	}

	@Override
	public Consumer<MouseEvent> getAction() {
		if (uniCode == null) {
			return null;
		}
		if (action == null) {
			action = e -> {
				try {
					// open emoji detail from emojipedia website on click on emoji
					IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
					IWebBrowser browser = browserSupport.createBrowser(
							IWorkbenchBrowserSupport.LOCATION_BAR | IWorkbenchBrowserSupport.NAVIGATION_BAR, null, null,
							null);
					browser.openURL(new URL("https://emojipedia.org/" + getUniCode()));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			};
		}
		return action;
	}
}
