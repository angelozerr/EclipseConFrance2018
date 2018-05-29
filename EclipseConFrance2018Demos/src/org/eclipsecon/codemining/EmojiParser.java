package org.eclipsecon.codemining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.Position;

public class EmojiParser {

	public static class Emoji {

		public final Position position;
		public final String tagName;

		public Emoji(int offset, String tagName) {
			this.position = new Position(offset, 1);
			this.tagName = tagName;
		}
		
		@Override
		public String toString() {
			return position.toString() + ", " + tagName;
		}
	}

	public static void main(String[] args) {
		List<Emoji> emojis = extractEmojis("a text :phone: bbb :bbb:");
		System.err.println(emojis);
	}

	private static final Pattern DELIMETERS = Pattern.compile(":(.*?):");

	private static final Map<String, String> mappings;
	
	static {
		mappings = new HashMap<>();
		mappings.put("phone", "\u260E");
		mappings.put("umbrella", "\u2602");		
	}
	
	public static List<Emoji> extractEmojis(String input) {
		List<Emoji> emojis = new ArrayList<>();
		Matcher matcher = DELIMETERS.matcher(input);
		int start = 0;
		while (matcher.find()) {
			emojis.add(new Emoji(matcher.start(), matcher.group(1)));
		}
		if (input.length() > start) {
			// emojis.add(new Position(start, input.length()));
		}
		return emojis;
	}

	public static String getUniCode(String tagName) {
		return mappings.get(tagName);
	}
}
