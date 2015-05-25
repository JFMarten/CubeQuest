package de.jfmarten.de.render;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.newdawn.slick.opengl.Texture;

import de.jfmarten.de.Log;

public class FontRender {

	public Texture font;
	public boolean underline = false;
	public boolean strike = false;
	public int color = 0;

	public static String BLACK = "0"; // 0
	public static String DARK_BLUE = "1"; // 1
	public static String DARK_GREEN = "2"; // 2
	public static String DARK_AQUA = "3"; // 3
	public static String DARK_RED = "4"; // 4
	public static String DARK_PURPLE = "5"; // 5
	public static String GOLD = "6"; // 6
	public static String GRAY = "7"; // 7
	public static String DARK_GREY = "8"; // 8
	public static String BLUE = "9"; // 9
	public static String GREEN = "10"; // a
	public static String AQUA = "11"; // b
	public static String RED = "12"; // c
	public static String LIGHT_PURPLE = "13"; // d
	public static String YELLOW = "14"; // e
	public static String WHITE = "15"; // f

	public String chars = "                " + "                " + " !\"#$%&'()*+,-./" + "0123456789:;<=>?" + "@ABCDEFGHIJKLMNO" + "PQRSTUVWXYZ[\\]^_" + "`abcdefghijklmno"
			+ "pqrstuvwxyz{|}~�" + " �  �         � " + "    �     �     " + "                " + "                " + "                " + "                " + "                "
			+ "                ";

	public int[] width = new int[256];

	public static final String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890�!\"$%&/()=?`\\#'+*~-_.:,;<>|^";

	public FontRender(String f) throws IOException {
		font = Render.getTexture(f, "PNG");

		Log.i(this, "Create widthmap");
		BufferedImage bi = Render.getBufferedImageImage("assets/font.png");
		int var4 = bi.getWidth();
		int var5 = bi.getHeight();
		int[] var6 = new int[var4 * var5];
		bi.getRGB(0, 0, var4, var5, var6, 0, var4);

		for (int var15 = 0; var15 < 128; ++var15) {
			var5 = var15 % 16;
			int var7 = var15 / 16;
			int var8 = 0;

			for (boolean var9 = false; var8 < 8 && !var9; ++var8) {
				int var10 = (var5 << 3) + var8;
				var9 = true;

				for (int var11 = 0; var11 < 8 && var9; ++var11) {
					int var12 = ((var7 << 3) + var11) * var4;
					if ((var6[var10 + var12] & 255) > 128) {
						var9 = false;
					}
				}
			}

			if (var15 == 0) {
				var8 = 4;
			}

			this.width[var15] = var8;
		}

		Log.i(this, "Done.");
	}

	public void renderChar(int i, float x, float y, float z, float size, Color c) {
		if (i > 0 && i < 257) {
			float xc, yc;
			xc = (float) (i % 16);
			yc = (float) (i / 16);
			Render.glClear();
			Render.glColor(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
			Render.glBindTexture(font.getTextureID());
			Render.setTextureNearest();
			Render.glRenderQuads();
			{
				Render.glVertexUV3f(x, y + size, z, xc / 16f, (yc + 1) / 16f);
				Render.glVertexUV3f(x + size, y + size, z, (xc + 1) / 16f, (yc + 1) / 16f);
				Render.glVertexUV3f(x + size, y, z, (xc + 1) / 16f, yc / 16f);
				Render.glVertexUV3f(x, y, z, xc / 16f, yc / 16f);
			}
			Render.glRenderEnd();
			Render.glClear();
			if (underline) {
				Render.glColor(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
				Render.glRenderQuads();
				{
					Render.glVertexUV2f(x, y + size / 10, xc / 16f, (yc + 1) / 16f);
					Render.glVertexUV2f(x + size, y + size / 10, (xc + 1) / 16f, (yc + 1) / 16f);
					Render.glVertexUV2f(x + size, y + (size / 200), (xc + 1) / 16f, yc / 16f);
					Render.glVertexUV2f(x, y + (size / 200), xc / 16f, yc / 16f);
				}
				Render.glRenderEnd();
			}
			if (strike) {
				Render.glColor(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f);
				Render.glRenderQuads();
				{
					Render.glVertexUV2f(x, y + (size / 2), xc / 16f, (yc + 1) / 16f);
					Render.glVertexUV2f(x + size, y + (size / 2), (xc + 1) / 16f, (yc + 1) / 16f);
					Render.glVertexUV2f(x + size, y + (size / 2) - (size / 8), (xc + 1) / 16f, yc / 16f);
					Render.glVertexUV2f(x, y + (size / 2) - (size / 8), xc / 16f, yc / 16f);
				}
				Render.glRenderEnd();
			}
		}
	}

	public int getChar(String c) {
		return chars.indexOf(c) == -1 ? 0 : chars.indexOf(c);
	}

	public void renderString(String s, float x, float y, float z, float size) {
		int pointer = 0;
		for (int i = 0; i < s.length(); i++) {
			if (s.substring(i, i + 1).equals("~")) {
				handleFormating(s.substring(i + 1, i + 2));
				i++;
			} else {
				renderChar(getChar(s.substring(i, i + 1)), x + pointer * size / 8f, y, z, size, getColorOfCode(-1));
				pointer += width[(int) (getChar(s.substring(i, i + 1)))];
			}
		}
		handleFormating("r");
	}

	public void handleFormating(String s) {
		if (s.equals("0"))
			color = 0;
		if (s.equals("1"))
			color = 1;
		if (s.equals("2"))
			color = 2;
		if (s.equals("3"))
			color = 3;
		if (s.equals("4"))
			color = 4;
		if (s.equals("5"))
			color = 5;
		if (s.equals("6"))
			color = 6;
		if (s.equals("7"))
			color = 7;
		if (s.equals("8"))
			color = 8;
		if (s.equals("9"))
			color = 9;
		if (s.equals("a"))
			color = 10;
		if (s.equals("b"))
			color = 11;
		if (s.equals("c"))
			color = 12;
		if (s.equals("d"))
			color = 13;
		if (s.equals("e"))
			color = 14;
		if (s.equals("f"))
			color = 15;
		if (s.equals("u"))
			underline = true;
		if (s.equals("s"))
			strike = true;
		if (s.equals("r")) {
			color = 15;
			underline = false;
			strike = false;
		}
	}

	public void renderString(String s, float x, float y, float size, Color c) {
		for (int i = 0; i < s.length(); i++) {
			renderChar(getChar(s.substring(i, i + 1)), x + i * size, y, 0, size, c);
		}
	}

	public void renderString(String s, float x, float y, float z, float size, Color c) {
		for (int i = 0; i < s.length(); i++) {
			renderChar(getChar(s.substring(i, i + 1)), x + i * size, y, z, size, c);
		}
	}

	public Color getColorOfCode(int i) {
		if (i == -1) {
			i = color;
		}
		String prefix = "0x";
		if (i == 0)
			return Color.decode(prefix + "000000");
		if (i == 1)
			return Color.decode(prefix + "0000AA");
		if (i == 2)
			return Color.decode(prefix + "00AA00");
		if (i == 3)
			return Color.decode(prefix + "00AAAA");
		if (i == 4)
			return Color.decode(prefix + "AA0000");
		if (i == 5)
			return Color.decode(prefix + "AA00AA");
		if (i == 6)
			return Color.decode(prefix + "FFAA00");
		if (i == 7)
			return Color.decode(prefix + "AAAAAA");
		if (i == 8)
			return Color.decode(prefix + "555555");
		if (i == 9)
			return Color.decode(prefix + "5555FF");
		if (i == 10)
			return Color.decode(prefix + "55FF55");
		if (i == 11)
			return Color.decode(prefix + "55FFFF");
		if (i == 12)
			return Color.decode(prefix + "FF5555");
		if (i == 13)
			return Color.decode(prefix + "FF55FF");
		if (i == 14)
			return Color.decode(prefix + "FFFF55");
		if (i == 15)
			return Color.decode(prefix + "FFFFFF");
		return Color.white;
	}

	public static String getCodeOfChar(String s) {
		if (s.equals("0"))
			return "~0";
		if (s.equals("1"))
			return "~1";
		if (s.equals("2"))
			return "~2";
		if (s.equals("3"))
			return "~3";
		if (s.equals("4"))
			return "~4";
		if (s.equals("5"))
			return "~5";
		if (s.equals("6"))
			return "~6";
		if (s.equals("7"))
			return "~7";
		if (s.equals("8"))
			return "~8";
		if (s.equals("9"))
			return "~9";
		if (s.equals("10"))
			return "~a";
		if (s.equals("11"))
			return "~b";
		if (s.equals("12"))
			return "~c";
		if (s.equals("13"))
			return "~d";
		if (s.equals("14"))
			return "~e";
		if (s.equals("15"))
			return "~f";
		return "";
	}

	public static float getStringLenght(String s, float f) {
		return s.length() * f;
	}
}
