package de.jfmarten.de;

import java.io.File;

public class Main {

	public static void main(String[] args) {
		if (System.getProperty("os.name").contains("Windows")) {
			System.setProperty("org.lwjgl.librarypath", new File("natives/windows").getAbsolutePath());
		} else if (System.getProperty("os.name").contains("Linux")) {
			System.setProperty("org.lwjgl.librarypath", new File("natives/linux").getAbsolutePath());
		}/*
		 * else if (System.getProperty("os.name").contains("Mac")) {
		 * System.setProperty("org.lwjgl.librarypath", new
		 * File("/natives/macosx").getAbsolutePath()); } else if
		 * (System.getProperty("os.name").contains("Sun")) {
		 * System.setProperty("org.lwjgl.librarypath", new
		 * File("/natives/solaris").getAbsolutePath()); }
		 */else {
			throw new RuntimeException("Your OS is not supported");
		}
		//args = new String[] { "-fullscreen" };
		CubeQuest cq = new CubeQuest(args);
		cq.start();
	}
}
