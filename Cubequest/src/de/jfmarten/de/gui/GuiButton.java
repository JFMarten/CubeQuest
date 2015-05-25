package de.jfmarten.de.gui;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

import de.jfmarten.de.render.Render;

public class GuiButton extends GuiObject {

	public String text;

	public GuiButton(int xx, int yy, int ww, String s) {
		super(xx, yy, ww, 48);
		text = s;
	}

	public void render() throws FileNotFoundException, IOException {
		Render.glClear();
		if (over) {
			Render.glColor(0.8f, 1, 0.8f);
		} else {
			Render.glColor(1, 1, 1);
		}
		GL11.glPushMatrix();
		int w = width - height;
		float xx = (16f / ((float) height / (float) w)) / 1.5f;
		Render.glBindTexture("assets/button_normal.png");
		Render.setTextureNearest();
		Render.glRenderQuads();
		{
			Render.glVertexUV2f(x, y, 0, 0);
			Render.glVertexUV2f(x + w, y, xx / 320f, 0);
			Render.glVertexUV2f(x + w, y + height, xx / 320f, 1f);
			Render.glVertexUV2f(x, y + height, 0, 1f);
		}
		Render.glRenderEnd();

		Texture t = Render.glBindTexture("assets/button_triangle.png");
		Render.setTextureNearest();
		Render.glRenderQuads();
		{
			Render.glVertexUV2f(x + w, y, 0, 0);
			Render.glVertexUV2f(x + width, y, 1f, 0);
			Render.glVertexUV2f(x + width, y + height, 1f, 1f);
			Render.glVertexUV2f(x + w, y + height, 0, 1f);
		}
		Render.glRenderEnd();
		Render.drawString(text, x + height / 4, y + height / 4, 0, height / 2);
		GL11.glPopMatrix();
	}

}
