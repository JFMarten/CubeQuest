package de.jfmarten.de.gui;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.newdawn.slick.opengl.Texture;

import de.jfmarten.de.CubeQuest;
import de.jfmarten.de.Translator;
import de.jfmarten.de.render.Render;

public class GuiError extends Gui {

	Exception error;

	public GuiError(CubeQuest cq, Exception e) {
		super(cq);
		error = e;
	}

	public int calcMenuLineX(int y) {
		return (CubeQuest.GAME_HEIGHT / 8) + (CubeQuest.GAME_WIDTH / 2) - (y / 4);
	}

	public int calcMenuLineY(int x) {
		return -4 * x + (CubeQuest.GAME_HEIGHT / 2) + 2 * CubeQuest.GAME_WIDTH;
	}

	public void renderBackground() throws FileNotFoundException, IOException {
		// Rendern des Hintergrundes
		Render.glColor(1, 1, 1);
		Texture t = Render.glBindTexture("assets/menu.png");
		Render.setTextureNearest();
		Render.glRenderQuads();
		{
			Render.glVertexUV2f(0, 0, 0, 0);
			Render.glVertexUV2f(CubeQuest.GAME_WIDTH, 0, t.getWidth(), 0);
			Render.glVertexUV2f(CubeQuest.GAME_WIDTH, 108f / 192f * CubeQuest.GAME_WIDTH, t.getWidth(), t.getHeight());
			Render.glVertexUV2f(0, 108f / 192f * CubeQuest.GAME_WIDTH, 0, t.getHeight());
		}
		Render.glRenderEnd();

		// Rendern des Farbverlaufs
		Render.glColor(0, 0, 0, 0.5f);
		Render.glRenderQuads();
		{
			Render.glVertex2f(0, 0);
			Render.glVertex2f(0, CubeQuest.GAME_HEIGHT);
			Render.glVertex2f(calcMenuLineX(CubeQuest.GAME_HEIGHT), CubeQuest.GAME_HEIGHT);
			Render.glVertex2f(calcMenuLineX(0), 0);
		}
		Render.glRenderEnd();

		// Rendern des Logos
		Render.glColor(1, 1, 1);
		t = Render.glBindTexture("assets/logo.png");
		Render.setTextureNearest();
		int w = CubeQuest.GAME_WIDTH / 2 - 20;
		int h = w / 6;
		Render.glRenderQuads();
		{
			Render.glVertexUV2f(10, 10, 0, 0);
			Render.glVertexUV2f(10 + w, 10, t.getWidth(), 0);
			Render.glVertexUV2f(10 + w, 10 + h, t.getWidth(), t.getHeight());
			Render.glVertexUV2f(10, 10 + h, 0, t.getHeight());
		}
		Render.glRenderEnd();

		// Rendern der Fehlermeldung
		Render.drawString("~c" + Translator.get("menu.error.header"), 10, h + 10, 0, 24);
		Render.drawString(error.getMessage(), 10, h + 50, 0, 16);
		Render.drawString(error.getClass().getName(), 10, h + 70, 0, 16);
		for (int i = 0; i < error.getStackTrace().length; i++) {
			StackTraceElement e = error.getStackTrace()[i];
			Render.drawString("[" + e.getClassName() + "]: " + e.getMethodName() + " (l. " + e.getLineNumber() + ") ", 10, h + 90 + 14 * i, 0, 12);
		}
	}
}
