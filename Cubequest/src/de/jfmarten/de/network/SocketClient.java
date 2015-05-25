package de.jfmarten.de.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import de.jfmarten.de.Log;
import de.jfmarten.de.entity.Entity;
import de.jfmarten.de.entity.EntityPlayer;
import de.jfmarten.de.network.packet.Packet;
import de.jfmarten.de.network.packet.PacketEntityUpdate;
import de.jfmarten.de.network.packet.PacketLogin;
import de.jfmarten.de.network.packet.PacketPing;
import de.jfmarten.de.network.packet.PacketServerRequest;
import de.jfmarten.de.network.packet.PacketWorldData;
import de.jfmarten.de.network.packet.PacketWorldInfo;
import de.jfmarten.de.world.Chunk;
import de.jfmarten.de.world.World;

public class SocketClient implements Runnable {

	private DatagramSocket client;
	Server server;
	public boolean running = true;
	private boolean connected = false;
	public boolean login = false;
	public ArrayList<DatagramPacket> packets = new ArrayList<DatagramPacket>();
	private SocketClientListener listener;
	public InetAddress hamachiAddress;
	public InetAddress networkAddress;

	public World world;

	public SocketClient(SocketClientListener scl) {
		try {
			client = new DatagramSocket();
			// client.setSoTimeout(100);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		listener = scl;
		Log.i(this, "Started with " + client.getLocalAddress().getHostAddress() + ":" + client.getLocalPort());
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface current = interfaces.nextElement();
				Enumeration<InetAddress> addrs = current.getInetAddresses();
				while (addrs.hasMoreElements()) {
					InetAddress addr = addrs.nextElement();
					if (addr instanceof Inet4Address) {
						if (current.getName().startsWith("ham")) {
							Log.i(this, "Found Hamachi-Address " + addr.getHostAddress());
							hamachiAddress = addr;
						} else if (current.getName().startsWith("eth")) {
							Log.i(this, "Found Network-Address " + addr.getHostAddress());
							networkAddress = addr;
						}
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public void connect(Server s) {
		server = s;
		connected = true;
		Log.i(this, "Connected with " + server.address.getHostAddress() + ":" + server.port);
		listener.onSocketClientConnectedandRunning();
	}

	public void run() {
		while (running) {
			byte[] receive = new byte[2048];
			DatagramPacket dp = new DatagramPacket(receive, receive.length);
			try {
				client.receive(dp);
			} catch (IOException e) {

			}
			handle(dp);
		}

		client.close();
		Log.i(this, "Stoped.");
	}

	public void handle(DatagramPacket dp) {
		String s = new String(dp.getData());
		Log.d(this, "Receive Packet : " + s);
		String[] ss = s.split(":");
		if (ss[0].equals("0")) {
			PacketPing p = new PacketPing(s);
			if (p.ping == true) {
				p.ping = false;
				p.time = System.currentTimeMillis();
				sendPacket(p);
				Log.i(this, "PING : ");
			} else {
				Log.i(this, "PONG " + (System.currentTimeMillis() - p.time));
			}
		} else if (ss[0].equals("1")) {
			PacketServerRequest p = new PacketServerRequest();
			p.set(s);
			p.time = System.currentTimeMillis() - p.time;
			dp.setData(p.getString().getBytes());
		} else if (ss[0].equals("2")) {
			PacketLogin p = new PacketLogin();
			p.set(s);
			world = new World(this);
			if (p.status.equals("success")) {
				login = true;
				world.playerEntity = new EntityPlayer(world);
				world.addEntity(world.playerEntity);
			}
		} else if (ss[0].equals("3")) {
			PacketWorldInfo p = new PacketWorldInfo();
			p.set(s);
			world.width = p.width;
			world.height = p.height;
		} else if (ss[0].equals("4")) {
			PacketWorldData p = new PacketWorldData();
			p.set(s);
			Chunk c = new Chunk(p);
			if (world != null)
				world.loaded.put(p.x + "_" + p.y, c);
		} else if (ss[0].equals("6")) {
			PacketEntityUpdate p = new PacketEntityUpdate();
			p.set(s);
			Entity e = world.entities.get(p.entityID);
			if (e != null) {
				e.x = p.x;
				e.y = p.y;
				world.entities.put(p.entityID, e);
			} else {
				world.addEntity(Entity.create(world, p));
			}
		} else {
			packets.add(dp);
		}
		listener.onSocketClientPacketReceived(dp);
	}

	/**
	 * Stoppt den Client
	 */
	public void stop() {
		running = false;
		client.close();
	}

	public boolean isConnectedandRunning() {
		return connected && running;
	}

	public void sendPacket(Packet p) {
		Log.d(this, "Send Packet : " + p.getString());
		byte[] b = p.getString().getBytes();
		DatagramPacket pp = new DatagramPacket(b, b.length, server.address, server.port);
		try {
			client.send(pp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendPacket(Packet p, Server s) {
		Log.d(this, "Send Packet : " + p.getString());
		byte[] b = p.getString().getBytes();
		DatagramPacket pp = new DatagramPacket(b, b.length, s.address, s.port);
		try {
			client.send(pp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
