package de.jfmarten.de.network.packet;

import de.jfmarten.de.exception.PacketParseException;

public class PacketEntityUpdate extends Packet {

	public boolean remove = false;
	public String entityID = "";
	public float x = 20, y = 20;

	public PacketEntityUpdate() {
		super(6);
	}

	@Override
	public String getString() {
		return id + ":" + entityID + ":" + x + ":" + y + ":" + (remove ? 1 : 0) + ":";
	}

	@Override
	public void set(String s) {
		String[] ss = s.split(":");
		try {
			entityID = ss[1];
			x = Float.parseFloat(ss[2]);
			y = Float.parseFloat(ss[3]);
			remove = (ss[4].equals("1"));
		} catch (Exception e) {
			throw new PacketParseException(this, s, e);
		}
	}

}
