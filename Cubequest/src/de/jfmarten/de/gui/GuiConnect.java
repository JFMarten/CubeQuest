package de.jfmarten.de.gui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;

import org.newdawn.slick.opengl.Texture;

import de.jfmarten.de.CubeQuest;
import de.jfmarten.de.Log;
import de.jfmarten.de.Translator;
import de.jfmarten.de.network.Server;
import de.jfmarten.de.network.packet.PacketLogin;
import de.jfmarten.de.network.packet.PacketWorldData;
import de.jfmarten.de.network.packet.PacketWorldInfo;
import de.jfmarten.de.render.Render;

public class GuiConnect extends Gui {

	public Server server;

	public boolean init = false;

	public boolean abort = false;

	public boolean login = false;
	public String login_result = "";

	public boolean world = false;
	public String world_result = "";

	public boolean world_load = false;
	public String world_load_result = "0:9";

	public int[][] chunkList = { { 0, 0 }, { 1, 0 }, { 2, 0 }, { 0, 1 }, { 1, 1 }, { 2, 1 }, { 0, 2 }, { 1, 2 }, { 2, 2 } };

	public GuiConnect(CubeQuest cq, Server s) {
		super(cq);
		server = s;
	}

	public int calcMenuLineX(int y) {
		return (CubeQuest.GAME_HEIGHT / 8) + (CubeQuest.GAME_WIDTH / 2) - (y / 4);
	}

	public int calcMenuLineY(int x) {
		return -4 * x + (CubeQuest.GAME_HEIGHT / 2) + 2 * CubeQuest.GAME_WIDTH;
	}

	public void renderBackground() throws FileNotFoundException, IOException {

		// Init

		if (!init) {
			init = true;
			PacketLogin p = new PacketLogin();
			p.username = game.username;
			game.client.sendPacket(p, server);
			new Thread(new Runnable() {
				public void run() {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (login == false) {
						login = true;
						login_result = "timeout";
					}
				}
			}).start();
			;
		}

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

		// Rendern des Textes
		Render.drawString(Translator.get("menu.connect.header") + " " + server.name, 10, h + 10, 0, 24);
		Render.drawString(server.address.getHostAddress() + ":" + server.port, 20, h + 35, 0, 16);

		Render.drawString(Translator.get("menu.connect.login") + game.username, 10, h + 100, 0, 24);
		if (login) {
			abort = true;
			if (login_result.equals("success")) {
				Render.drawString(Translator.get("menu.connect.login.done"), 20, h + 130, 0, 16);
				abort = false;
			} else if (login_result.equals("already_on_server")) {
				Render.drawString(Translator.get("menu.connect.login.alreadyonserver"), 20, h + 130, 0, 16);
			} else if (login_result.equals("timeout")) {
				Render.drawString(Translator.get("menu.connect.login.timeout"), 20, h + 130, 0, 16);
			} else if (login_result.equals("whitelist")) {
				Render.drawString(Translator.get("menu.connect.login.whitelisted"), 20, h + 130, 0, 16);
			} else if (login_result.equals("full")) {
				Render.drawString(Translator.get("menu.connect.login.full"), 20, h + 130, 0, 16);
			}

			if (!abort) {
				Render.drawString(Translator.get("menu.connect.world"), 10, h + 200, 0, 24);
				if (world && world_result != "") {
					if (world_result.equals("canceled")) {
						Render.drawString(Translator.get("menu.connect.world.canceled"), 20, h + 230, 0, 16);
						abort = true;
					} else {
						String[] ss = world_result.split(":");
						abort = false;
						Render.drawString(Translator.get("menu.connect.world.done") + ss[0] + " (" + ss[1] + "*" + ss[2] + ")", 20, h + 230, 0, 16);
					}
					if (!abort) {
						Render.drawString(Translator.get("menu.connect.world.load"), 10, h + 300, 0, 24);
						if (world_load_result != "") {
							String[] ss = world_load_result.split(":");
							if (Integer.parseInt(ss[0]) + 1 == 9) {
								Render.drawString(Translator.get("menu.connect.world.load.done", Integer.parseInt(ss[0]) + 1 + "", ss[1]), 20, h + 330, 0, 16);
							} else {
								Render.drawString(Translator.get("menu.connect.world.load.progress", Integer.parseInt(ss[0]) + 1 + "", ss[1]), 20, h + 330, 0, 16);
							}
						}
					}
				}
			}
		}
	}

	public void mouseEvent(int obj) throws IOException {

	}

	public void packetReceived(DatagramPacket dp) {
		String s = new String(dp.getData());
		if (s.startsWith("2")) {
			PacketLogin p = new PacketLogin();
			p.set(s);
			login = true;
			login_result = p.status;
			PacketWorldInfo pp = new PacketWorldInfo();
			game.client.sendPacket(pp, server);
			game.client.connect(server);
		}
		if (s.startsWith("3")) {
			PacketWorldInfo p = new PacketWorldInfo();
			p.set(s);
			world = true;
			if (!p.version.equals("canceled")) {
				world_result = p.name + ":" + p.width + ":" + p.height;
			} else {
				world_result = "canceled";
			}
			PacketWorldData pp = new PacketWorldData();
			String[] ss = world_load_result.split(":");
			world_load_result = (Integer.parseInt(ss[0])) + ":" + ss[1];
			pp.data = new byte[1];
			pp.x = chunkList[Integer.parseInt(ss[0])][0];
			pp.y = chunkList[Integer.parseInt(ss[0])][1];
			game.client.sendPacket(pp, server);
		}
		if (s.startsWith("4")) {
			PacketWorldData p = new PacketWorldData();
			p.set(s);
			String[] ss = world_load_result.split(":");
			if (Integer.parseInt(ss[0]) != 8) {
				world_load_result = (Integer.parseInt(ss[0]) + 1) + ":" + ss[1];
				p.data = new byte[1];
				p.x = chunkList[Integer.parseInt(ss[0]) + 1][0];
				p.y = chunkList[Integer.parseInt(ss[0]) + 1][1];
				game.client.sendPacket(p, server);
			} else {
				game.setGui(null);
			}
		}
	}
}
