package de.jfmarten.de;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONObject;

public class Settings {

	public final static int[][] resolutions = { { 640, 480, 4, 3 }, { 1280, 720, 16, 9 }, { 1920, 1080, 16, 9 } };

	public static int RESOLUTION;
	public static int GAME_WIDTH;
	public static int GAME_HEIGHT;

	public static void load() throws IOException {
		FileReader fr = new FileReader(new File("options/settings"));
		BufferedReader br = new BufferedReader(fr);
		String s = "", ss = "";
		while ((ss = br.readLine()) != null)
			s += ss;
		JSONObject obj = new JSONObject(s);
		RESOLUTION = obj.getInt("RESOLUTION");
		if (RESOLUTION == -1) {
			GAME_WIDTH = obj.getInt("GAME_WIDTH");
			GAME_HEIGHT = obj.getInt("GAME_HEIGHT");
		} else {
			GAME_WIDTH = resolutions[RESOLUTION][0];
			GAME_HEIGHT = resolutions[RESOLUTION][1];
		}
	}

	public static void save() throws IOException {
		FileWriter fw = new FileWriter(new File("options/settings"));
		JSONObject obj = new JSONObject();
		obj.put("RESOLUTION", RESOLUTION);
		obj.put("GAME_WIDTH", GAME_WIDTH);
		obj.put("GAME_HEIGHT", GAME_HEIGHT);
		obj.write(fw);
		fw.close();
		
	}
}
