package org.eclipsecon.codemining.emoji;

import java.net.URL;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;
import org.eclipse.jface.text.codemining.LineContentCodeMining;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

public class EmojiLineContentCodeMining extends LineContentCodeMining {

	public EmojiLineContentCodeMining(String uniCode, Position position, ICodeMiningProvider provider) {
		super(position, provider, e -> {
			try {
				// open emoji detail from emojipedia website on click on emoji
				IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
				IWebBrowser browser = browserSupport.createBrowser(
						IWorkbenchBrowserSupport.LOCATION_BAR | IWorkbenchBrowserSupport.NAVIGATION_BAR, null, null,
						null);
				browser.openURL(new URL("https://emojipedia.org/" + uniCode));
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		});
		super.setLabel(uniCode != null ? " [" + uniCode + "]" : "");
	}
}
