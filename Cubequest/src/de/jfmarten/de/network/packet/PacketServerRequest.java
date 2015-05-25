package de.jfmarten.de.network.packet;

import de.jfmarten.de.exception.PacketParseException;

public class PacketServerRequest extends Packet {

	public int requestID;
	public String name = "";
	public long time = 0;
	public int userCount = 0;
	public int maxCount = 0;

	public PacketServerRequest() {
		super(1);
		requestID = (int) (Math.random() * 1000);
	}

	public String getString() {
		return id + ":" + requestID + ":" + name + ":" + time + ":" + userCount + ":" + maxCount + ":";
	}

	public void set(String s) {
		String[] ss = s.split(":");
		try {
			requestID = Integer.parseInt(ss[1]);
			name = ss[2];
			time = Long.parseLong(ss[3]);
			userCount = Integer.parseInt(ss[4]);
			maxCount = Integer.parseInt(ss[5]);
		} catch (Exception e) {
			throw new PacketParseException(this, s);
		}
	}
}
