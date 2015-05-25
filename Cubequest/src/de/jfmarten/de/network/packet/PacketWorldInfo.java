package de.jfmarten.de.network.packet;

import de.jfmarten.de.exception.PacketParseException;

public class PacketWorldInfo extends Packet {

	public int width, height;
	public String name = "World";
	public String version = "V1.0";
	public int spawnX = 0, spawnY = 0;

	public PacketWorldInfo() {
		super(3);
	}

	public String getString() {
		return id + ":" + width + ":" + height + ":" + name + ":" + version + ":" + spawnX + ":" + spawnY + ":";
	}

	public void set(String s) {
		String[] ss = s.split(":");
		try {
			width = Integer.parseInt(ss[1]);
			height = Integer.parseInt(ss[2]);
			name = ss[3];
			version = ss[4];
			spawnX = Integer.parseInt(ss[5]);
			spawnY = Integer.parseInt(ss[6]);
		} catch (Exception e) {
			throw new PacketParseException(this, s);
		}
	}

}
