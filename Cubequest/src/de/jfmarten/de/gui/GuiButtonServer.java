package de.jfmarten.de.gui;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

import de.jfmarten.de.network.Server;
import de.jfmarten.de.render.Render;

public class GuiButtonServer extends GuiObject {

	public Server server;

	public GuiButtonServer(int xx, int yy, int ww, Server s) {
		super(xx, yy, ww, 48);
		server = s;
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
		Render.drawString(server.name, x + height / 4, y + height / 4, 0, height / 2);

		// PING 0-6
		int ping = server.getPingLevel();

		Render.glBindTexture("assets/menu.play/ping_" + ping + ".png");
		Render.setTextureNearest();
		Render.glRenderQuads();
		{
			Render.glVertexUV2f(x + w, y, 0, 0);
			Render.glVertexUV2f(x + width, y, 1f, 0);
			Render.glVertexUV2f(x + width, y + height, 1f, 1f);
			Render.glVertexUV2f(x + w, y + height, 0, 1f);
		}
		Render.glRenderEnd();

		if (server.maxUserCount != 0) {
			Render.drawString("" + server.userCount, w, y + 8, 0, 16);
			Render.drawString("" + server.maxUserCount, w, y + 24, 0, 16);
		}

		GL11.glPopMatrix();
	}

}
