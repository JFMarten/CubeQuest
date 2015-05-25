package de.jfmarten.de.network.packet;

import de.jfmarten.de.Log;
import de.jfmarten.de.exception.PacketParseException;

public class PacketPing extends Packet {

	public PacketPing(String s) {
		super(0);
		set(s);
	}

	public boolean ping = true;
	public long time;

	@Override
	public String getString() {
		return id + ":" + (ping ? "ping" : "pong") + ":" + time + ":";
	}

	@Override
	public void set(String s) {
		String[] ss = s.split(":");
		try {
			ping = ss[1].equals("ping");
			time = Long.parseLong(ss[2]);
		} catch (Exception e) {
			e.printStackTrace();
			throw new PacketParseException(this, s);
		}
	}

}
