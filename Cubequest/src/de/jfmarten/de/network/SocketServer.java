package de.jfmarten.de.network;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import de.jfmarten.de.Log;
import de.jfmarten.de.entity.Entity;
import de.jfmarten.de.network.entity.NetEntity;
import de.jfmarten.de.network.packet.Packet;
import de.jfmarten.de.network.packet.PacketEntityUpdate;
import de.jfmarten.de.network.packet.PacketLogin;
import de.jfmarten.de.network.packet.PacketPing;
import de.jfmarten.de.network.packet.PacketServerRequest;
import de.jfmarten.de.network.packet.PacketWorldData;
import de.jfmarten.de.network.packet.PacketWorldInfo;
import de.jfmarten.de.network.world.NetWorld;
import de.jfmarten.de.world.Chunk;

public class SocketServer implements Runnable {

	private DatagramSocket server;
	public boolean running = true;
	public ArrayList<DatagramPacket> packets = new ArrayList<DatagramPacket>();
	private ArrayList<Client> clients = new ArrayList<Client>();
	public ArrayList<String> whitelist = null;
	public int maxUserCount = 10;

	public NetWorld world;

	public SocketServer(int port) throws SocketException {
		server = new DatagramSocket(port);
		world = new NetWorld();
	}

	public void run() {
		if (server == null)
			try {

			} catch (Exception e) {
				throw new RuntimeException("Server is not assigned !");
			}
		Log.i(this, "Started with " + server.getLocalAddress().getHostAddress() + ":" + server.getLocalPort());
		while (running) {
			byte[] receive = new byte[2048];
			DatagramPacket dp = new DatagramPacket(receive, receive.length);
			try {
				server.receive(dp);
				handle(dp);
			} catch (IOException e) {
				if (!(e instanceof SocketException))
					e.printStackTrace();

			}
		}
		server.disconnect();
		server.close();
		Log.i(this, "Stoped.");

		File f = new File(world.path);
		if (f.isDirectory()) {
			for (File ff : f.listFiles()) {
				ff.delete();
			}
		}
	}

	public void handle(DatagramPacket dp) throws IOException {
		String s = new String(dp.getData());
		Client c = getClient(dp.getAddress(), dp.getPort());
		Log.d(this, "Receive Packet from " + c.address.getHostAddress() + ":" + c.port + " : " + s);
		String[] ss = s.split(":");
		if (ss[0].equals("0")) {
			PacketPing p = new PacketPing(s);
			if (p.ping == true) {
				Log.i(this, "PING");
				p.ping = false;
				p.time = System.currentTimeMillis();
				sendPacket(p, c);
			} else {
				Log.i(this, "PONG : " + (System.currentTimeMillis() - p.time));
			}
		} else if (ss[0].equals("1")) {
			PacketServerRequest p = new PacketServerRequest();
			p.set(s);
			p.name = "Server";
			p.time = System.currentTimeMillis();
			p.maxCount = maxUserCount;
			p.userCount = clients.size();
			sendPacket(p, c);
		} else if (ss[0].equals("2")) {
			PacketLogin p = new PacketLogin();
			p.set(s);
			Client cc = getClient(p.username);
			if (cc != null) {
				p.status = "already_on_server";
			} else {
				if (whitelist != null) {
					p.status = "whitelist";
				} else {
					if (maxUserCount == clients.size()) {
						p.status = "full";
					} else {
						p.status = "success";
						p.playerID = "player_" + Entity.genID();
						c.username = p.username;
						NetEntity e = new NetEntity(world);
						e.id = p.playerID;
						world.addEntity(e);
						clients.add(c);
						Log.i(this, "Player joined server " + p.username + " (" + p.playerID + " ");
					}
				}
			}
			sendPacket(p, c);
		} else if (ss[0].equals("3")) {
			PacketWorldInfo p = new PacketWorldInfo();
			p.set(s);
			if (!c.username.equals("")) {
				p.width = world.width;
				p.height = world.height;
				p.name = "Welt";
				p.version = "V1.0";
				p.spawnX = world.spawnX;
				p.spawnY = world.spawnY;
			} else {
				p.version = "canceled";
			}
			sendPacket(p, c);
		} else if (ss[0].equals("4")) {
			PacketWorldData p = new PacketWorldData();
			p.set(s);
			Chunk chunk = world.getChunk(p.x, p.y);
			Log.d(this, "User request chunk " + p.x + " " + p.y);
			p.data = chunk.blocks;
			sendPacket(p, c);
		} else if (ss[0].equals("6")) {
			PacketEntityUpdate p = new PacketEntityUpdate();
			p.set(s);
			NetEntity ne = world.getEntity(p.entityID);
			if (ne == null) {
				p.remove = true;
			} else {
				ne.x = p.x;
				ne.y = p.y;
			}
			sendAllW(p, c);
		} else {
			packets.add(dp);
		}
	}

	/**
	 * Stoppt den Server
	 */
	public void stop() {
		running = false;
		server.close();
	}

	public void sendPacket(Packet p, Client c) {
		byte[] b = p.getString().getBytes();
		DatagramPacket pp = new DatagramPacket(b, b.length, c.address, c.port);
		try {
			server.send(pp);
			Log.d(this, "Send Packet to " + c.address.getHostAddress() + ":" + c.port + " : " + p.getString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendAll(Packet p) {
		for (Client c : clients) {
			sendPacket(p, c);
		}
	}

	public void sendAllW(Packet p, Client cc) {
		for (Client c : clients) {
			if (c != cc)
				sendPacket(p, c);
		}
	}

	public Client getClient(String s) {
		for (Client c : clients) {
			if (c.username.equals(s)) {
				return c;
			}
		}
		return null;
	}

	public Client getClient(InetAddress addr, int port) {
		for (Client c : clients) {
			if (c.port == port && c.address.getHostAddress().equals(addr.getHostAddress())) {
				return c;
			}
		}
		return new Client(addr, port);
	}

}
