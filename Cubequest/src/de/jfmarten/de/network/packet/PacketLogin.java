package de.jfmarten.de.network.packet;

import de.jfmarten.de.exception.PacketParseException;

public class PacketLogin extends Packet {

	public String username = "";
	public String status = "login";
	public String playerID = "player_";

	public PacketLogin() {
		super(2);
	}

	public String getString() {
		return id + ":" + username + ":" + status + ":";
	}

	public void set(String s) {
		String[] ss = s.split(":");
		try {
			username = ss[1];
			status = ss[2];
		} catch (Exception e) {
			throw new PacketParseException(this, s);
		}
	}
}
