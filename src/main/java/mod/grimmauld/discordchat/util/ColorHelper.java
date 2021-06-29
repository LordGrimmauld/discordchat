package mod.grimmauld.discordchat.util;

import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public class ColorHelper {
	private static final Map<Color, TextFormatting> formattingMap =
		Arrays.stream(TextFormatting.values())
			.filter(TextFormatting::isColor)
			.filter(textFormatting -> textFormatting.getColor() != null)
			.collect(Collectors.toMap(textFormatting -> new Color(textFormatting.getColor()), textformatting -> textformatting));


	private ColorHelper() {
	}

	public static TextFormatting getClosest(@Nullable Color color) {
		if (color == null)
			return TextFormatting.WHITE;
		return formattingMap
			.entrySet()
			.stream()
			.min(Comparator.comparing(colorTextFormattingEntry -> colorDistance(colorTextFormattingEntry.getKey(), color)))
			.map(Map.Entry::getValue)
			.orElse(TextFormatting.WHITE);
	}

	private static double colorDistance(Color c1, Color c2) {
		if (c1 == null || c2 == null)
			return Double.MAX_VALUE;

		return Math.pow(c1.getRed() - c2.getRed(), 2) * 2
			+ Math.pow(c1.getGreen() - c2.getGreen(), 2) * 4
			+ Math.pow(c1.getBlue() - c2.getBlue(), 2) * 3;
	}
}
