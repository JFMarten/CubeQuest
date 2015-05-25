package de.jfmarten.de.network.packet;

public abstract class Packet {

	public final int id;

	public Packet(int id) {
		this.id = id;
	}

	public abstract String getString();

	public abstract void set(String s);
}
