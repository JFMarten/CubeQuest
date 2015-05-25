package de.jfmarten.de.render;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class Render {

	public static FontRender fontRender;

	public static HashMap<String, Texture> textures = new HashMap<String, Texture>();

	public static int calc_size;

	public static void setupTextureCalc(int tiles, int tileSize) {
		calc_size = tiles * tileSize;
	}

	public static Texture getTexture(String path, String format) throws FileNotFoundException, IOException {
		if (textures.containsKey(path)) {
			return textures.get(path);
		} else {
			textures.put(path, TextureLoader.getTexture(format, new FileInputStream(path)));
			return textures.get(path);
		}
	}

	public static BufferedImage getBufferedImageImage(String path) throws IOException {
		return ImageIO.read(new File(path));
	}

	public static void setTextureNearest() {
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
	}

	public static void setTextureLinear() {
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
	}

	public static void setTextureRepeat() {
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
	}

	public static void resetTextureRepeat() {
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
	}

	public static void glRenderQuads() {
		GL11.glBegin(GL11.GL_QUADS);
	}

	public static void glRenderLines() {
		GL11.glBegin(GL11.GL_LINES);
	}

	public static void glVertex3f(float x, float y, float z) {
		GL11.glVertex3f(x, y, z);
	}

	public static void glVertex2f(float x, float y) {
		GL11.glVertex2f(x, y);
	}

	public static void glVertexUV3f(float x, float y, float z, float u, float v) {
		GL11.glTexCoord2f(u, v);
		GL11.glVertex3f(x, y, z);
	}

	public static void glVertexUV2f(float x, float y, float u, float v) {
		GL11.glTexCoord2f(u, v);
		GL11.glVertex2f(x, y);
	}

	public static void glVertexUV3fC(float x, float y, float z, float u, float v) {
		u = u / calc_size;
		v = v / calc_size;
		GL11.glTexCoord2f(u, v);
		GL11.glVertex3f(x, y, z);
	}

	public static void glRenderEnd() {
		GL11.glEnd();
	}

	public static void glClear() {
		glBindTexture(0);
		glColor(1, 1, 1, 1);
		calc_size = 0;
	}

	public static void glBindTexture(int textureID) {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	}

	public static Texture glBindTexture(String path) throws FileNotFoundException, IOException {
		Texture t = Render.getTexture(path, "PNG");
		Render.glBindTexture(t.getTextureID());
		return t;
	}

	public static void glColor(float r, float g, float b) {
		GL11.glColor3f(r, g, b);
	}

	public static void glColor(float r, float g, float b, float a) {
		GL11.glColor4f(r, g, b, a);
	}

	public static void glColor(Color color) {
		GL11.glColor3f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
	}

	public static void drawChar(int c, float x, float y, float z, float size) {
		fontRender.renderChar(c, x, y, z, size, fontRender.getColorOfCode(-1));
	}

	public static void drawString(String s, float x, float y, float z, float size) {
		fontRender.renderString(s, x, y, z, size);
	}
}
