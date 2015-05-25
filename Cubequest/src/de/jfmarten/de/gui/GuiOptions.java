package de.jfmarten.de.gui;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.lwjgl.LWJGLException;
import org.newdawn.slick.opengl.Texture;

import de.jfmarten.de.CubeQuest;
import de.jfmarten.de.Settings;
import de.jfmarten.de.Translator;
import de.jfmarten.de.render.Render;

public class GuiOptions extends Gui {

	public int resolution = Settings.RESOLUTION;

	public GuiOptions(CubeQuest cq) {
		super(cq);
		GuiButton apply = new GuiButton(10, 400, calcMenuLineX(400) - 25, Translator.get("menu.options.apply"));
		GuiButton back = new GuiButton(10, 460, calcMenuLineX(460) - 25, Translator.get("menu.options.back"));
		GuiButton res = new GuiButton(10, 100, calcMenuLineX(100) - 25, Translator.get("menu.options.resolution") + "   " + Settings.resolutions[Settings.RESOLUTION][0] + "x"
				+ Settings.resolutions[Settings.RESOLUTION][1] + "   (" + Settings.resolutions[Settings.RESOLUTION][2] + ":" + Settings.resolutions[Settings.RESOLUTION][3] + ")");
		objects.add(apply);
		objects.add(back);
		objects.add(res);
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
	}

	public void mouseEvent(int obj) throws IOException, LWJGLException {
		if (obj == 0) {
			Settings.RESOLUTION = resolution;
			Settings.save();
			game.setGui(new GuiMain(game));
		} else if (obj == 1) {
			game.setGui(new GuiMain(game));
		} else if (obj == 2) {
			GuiButton gb = (GuiButton) objects.get(obj);
			resolution++;
			if (resolution >= Settings.resolutions.length)
				resolution = 0;
			gb.text = Translator.get("menu.options.resolution") + "   " + Settings.resolutions[resolution][0] + "x" + Settings.resolutions[resolution][1] + "   ("
					+ Settings.resolutions[resolution][2] + ":" + Settings.resolutions[resolution][3] + ")";
		}
	}
}
