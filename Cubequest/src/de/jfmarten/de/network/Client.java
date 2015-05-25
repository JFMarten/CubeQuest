package de.jfmarten.de.network;

import java.net.InetAddress;

public class Client {

	public InetAddress address;
	public int port;
	public String username = "";

	public Client(InetAddress addr, int prt) {
		address = addr;
		port = prt;
	}
}
