package de.jfmarten.de.network;

import java.net.DatagramPacket;

public interface SocketClientListener {

	public void onSocketClientConnectedandRunning();

	public void onSocketClientPacketReceived(DatagramPacket dp);

}
