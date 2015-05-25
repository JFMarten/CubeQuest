package de.jfmarten.de.gui;

import java.io.FileNotFoundException;
import java.io.IOException;

import de.jfmarten.de.CubeQuest;

public class GuiObject {

	public int x, y, width, height;
	public boolean over = false;

	public GuiObject(int xx, int yy, int ww, int hh) {
		x = xx;
		y = yy;
		width = ww;
		height = hh;
	}

	public void render() throws FileNotFoundException, IOException {

	}

	public void update() {
		int[] i = CubeQuest.getMouse();
		if (i[0] > x && i[0] < x + width && i[1] > y && i[1] < y + height) {
			over = true;
		} else {
			over = false;
		}
	}

	public void onMouseClickEvent(int btn, boolean state) {
		if (state && btn == 0) {
			if (over) {
				onMouseClick();
			}
		}
	}

	public void onMouseClick() {

	}

}
