package de.jfmarten.de.gui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;

import org.newdawn.slick.opengl.Texture;

import de.jfmarten.de.CubeQuest;
import de.jfmarten.de.Log;
import de.jfmarten.de.Translator;
import de.jfmarten.de.network.Server;
import de.jfmarten.de.network.Server.ServerStatus;
import de.jfmarten.de.network.packet.PacketServerRequest;
import de.jfmarten.de.render.Render;

public class GuiPlay extends Gui {

	int scroll = 0;
	int[] requestID;

	public GuiPlay(CubeQuest cq) {
		super(cq);
		GuiButton single = new GuiButton(10, 100, calcMenuLineX(100) - 25, Translator.get("menu.play.singleplayer"));
		GuiButton multi = new GuiButton(10, 160, calcMenuLineX(160) - 25, Translator.get("menu.play.multiplayer"));
		GuiButton back = new GuiButton(10, 460, calcMenuLineX(460) - 25, Translator.get("menu.play.back"));
		objects.add(single);
		objects.add(multi);
		objects.add(back);

		int pntr = 0;
		requestID = new int[Server.servers.size()];
		for (Server s : Server.servers) {
			GuiButtonServer gb = new GuiButtonServer(20, 220 + pntr * 60, calcMenuLineX(220 + pntr * 60) - 85, s);
			objects.add(gb);
			requestID[pntr] = s.sendServerRequest(game);
			pntr++;
		}
	}

	public int calcMenuLineX(int y) {
		return (CubeQuest.GAME_HEIGHT / 8) + (CubeQuest.GAME_WIDTH / 2) - (y / 4);
	}

	public int calcMenuLineY(int x) {
		return -4 * x + (CubeQuest.GAME_HEIGHT / 2) + 2 * CubeQuest.GAME_WIDTH;
	}

	public void render() throws FileNotFoundException, IOException {
		renderBackground();
		objects.get(0).render();
		objects.get(1).render();
		objects.get(2).render();

		for (int i = 3; i < 7; i++) {
			if (objects.size() > i + scroll) {
				GuiButtonServer gbs = (GuiButtonServer) objects.get(i + scroll);
				gbs.y = 220 + (i - 3) * 60;
				gbs.width = calcMenuLineX(220 + (i - 3) * 60) - 85;
				gbs.render();
			}
		}
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

	public void mouseEvent(int obj) throws IOException {
		if (obj == 2) {
			game.setGui(new GuiMain(game));
		} else if (obj > 2) {
			Server s = Server.servers.get(obj - 3);
			if (s != null) {
				game.setGui(new GuiConnect(game, s));
			}
		}
	}

	public void onMouseWheel(int x, int y, int wheel) {
		if (y >= 200) {
			if (wheel > 0)
				scroll--;
			if (wheel < 0)
				scroll++;
			if (scroll < 0)
				scroll = 0;
			if (scroll + 4 > Server.servers.size()) {
				scroll = Server.servers.size() - 4;
			}
		}
	}

	public void packetReceived(DatagramPacket dp) {
		String s = new String(dp.getData());
		if (s.startsWith("1:")) {
			PacketServerRequest pp = new PacketServerRequest();
			pp.set(s);
			for (int i = 0; i < requestID.length; i++) {
				if (pp.requestID == requestID[i]) {
					Server server = Server.servers.get(i);
					server.status = ServerStatus.ONLINE;
					server.ping = (int) pp.time;
					server.maxUserCount = pp.maxCount;
					server.userCount = pp.userCount;
					Log.i(this, "Get Request from server " + dp.getAddress().getHostAddress() + ":" + dp.getPort());
				}
			}
		}
	}
}
