package de.jfmarten.de.gui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;

import org.lwjgl.LWJGLException;

import de.jfmarten.de.CubeQuest;

public class Gui {

	public CubeQuest game;
	public ArrayList<GuiObject> objects = new ArrayList<GuiObject>();
	public boolean needWorldRender = false;

	public Gui(CubeQuest cq) {
		game = cq;
	}

	public void render() throws FileNotFoundException, IOException {
		renderBackground();
		for (GuiObject go : objects) {
			go.render();
		}
	}

	public void update() {
		for (GuiObject go : objects) {
			go.update();
		}
	}

	public void renderBackground() throws FileNotFoundException, IOException {

	}

	public void onMouseClickEvent(int btn, boolean state) throws IOException, LWJGLException {
		for (GuiObject go : objects) {
			if (go.over) {
				go.onMouseClickEvent(btn, state);
				if (btn == 0 && state)
					mouseEvent(objects.indexOf(go));
			}
		}
	}

	public void onMouseWheel(int x, int y, int wheel) {

	}

	public void mouseEvent(int obj) throws IOException, LWJGLException {

	}

	public void packetReceived(DatagramPacket dp) {

	}

}
