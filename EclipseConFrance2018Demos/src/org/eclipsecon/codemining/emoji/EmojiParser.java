package org.eclipsecon.codemining.emoji;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmojiParser {

	private static final Pattern EMOJIS_PATTERN = Pattern.compile(":(.*?):");

	private static final Map<String, String> mappings;

	static {
		mappings = new HashMap<>();
		mappings.put("phone", "\u260E");
		mappings.put("umbrella", "\u2602");
		mappings.put("heart", "\u2764");
	}

	public static class Emoji {

		public final int offset;
		public final String tagName;

		Emoji(int offset, String tagName) {
			this.offset = offset;
			this.tagName = tagName;
		}

		@Override
		public String toString() {
			return "{" + offset + ", " + tagName + "}";
		}
	}

	public static void main(String[] args) {
		List<Emoji> emojis = extractEmojis("a text :phone: bbb :bbb:");
		System.err.println(emojis);
	}

	public static List<Emoji> extractEmojis(String input) {
		List<Emoji> emojis = new ArrayList<>();
		Matcher matcher = EMOJIS_PATTERN.matcher(input);
		while (matcher.find()) {
			emojis.add(new Emoji(matcher.start(), matcher.group(1)));
		}
		return emojis;
	}

	public static String getUniCode(String tagName) {
		/*synchronized (mappings) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Thread.interrupted();
			}
		}*/
		return mappings.get(tagName);
	}
}
