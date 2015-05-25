package de.jfmarten.de.exception;

import de.jfmarten.de.network.packet.Packet;

public class PacketParseException extends RuntimeException {

	private static final long serialVersionUID = 7517194646523135311L;

	public PacketParseException(Packet p,String s) {
		super(p.getClass().getName()+" : "+s);
	}
}
