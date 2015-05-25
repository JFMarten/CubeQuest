package de.jfmarten.de;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Translator {

	public static String locale = "de_DE";
	public static HashMap<String, String> dict = new HashMap<String, String>();

	public static void load(String s) {
		Log.i(new Translator(), "Loading " + s);
		locale = s;
		dict.clear();
		File f = new File("assets/translation/" + locale + ".locale");
		if (f.exists()) {
			try {
				FileReader fr = new FileReader(f);
				BufferedReader br = new BufferedReader(fr);
				String ss = "";
				while ((ss = br.readLine()) != null) {
					String[] sss = ss.split(":");
					if (sss.length == 2) {
						dict.put(sss[0], sss[1]);
						Log.i(new Translator(), "'" + sss[0] + "' '" + sss[1] + "'");
					}
				}
				Log.i(new Translator(), "Found " + dict.size() + " translations");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			throw new RuntimeException("Locale not found : assets/translation/" + locale + ".locale");
		}
		Log.i(new Translator(), "Done.");
	}

	public static String get(String... args) {
		String s = args[0];
		if (dict.containsKey(s)) {
			s = dict.get(s);
			for (int i = 1; i < args.length; i++) {
				s = s.replace("%" + i, args[i]);
			}
		}
		return s;
	}
}
