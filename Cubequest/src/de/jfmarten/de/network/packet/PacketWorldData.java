package de.jfmarten.de.network.packet;

import de.jfmarten.de.exception.PacketParseException;

public class PacketWorldData extends Packet {

	public byte[] data = new byte[32 * 32];
	public int x = 0, y = 0;

	public PacketWorldData() {
		super(4);
	}

	public String getString() {
		return id + ":" + x + ":" + y + ":" + new String(data) + ":";
	}

	public void set(String s) {
		String[] ss = s.split(":");
		try {
			x = Integer.parseInt(ss[1]);
			y = Integer.parseInt(ss[2]);
			data = ss[3].getBytes();
		} catch (Exception e) {
			throw new PacketParseException(this, s);
		}
	}
}
