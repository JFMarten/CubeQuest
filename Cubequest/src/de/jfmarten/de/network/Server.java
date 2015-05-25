package de.jfmarten.de.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import de.jfmarten.de.CubeQuest;
import de.jfmarten.de.Log;
import de.jfmarten.de.network.packet.PacketServerRequest;

public class Server {

	public static ArrayList<Server> servers = new ArrayList<Server>();

	public static void loadServerList() throws IOException {
		FileReader fr = new FileReader(new File("options/serverlist"));
		BufferedReader br = new BufferedReader(fr);
		String s = "", ss = "";
		while ((ss = br.readLine()) != null)
			s += ss;
		JSONObject obj = new JSONObject(s);
		JSONArray list = obj.getJSONArray("list");
		Log.i(new Server(), "Search for servers in serverlist");
		for (int i = 0; i < list.length(); i++) {
			Server server = new Server();
			JSONObject obj1 = list.getJSONObject(i);
			server.address = InetAddress.getByName(obj1.getString("address"));
			server.port = obj1.getInt("port");
			server.name = obj1.getString("name");
			Log.i(server, "" + server.name + " at " + server.address.getHostAddress() + ":" + server.port);
			servers.add(server);
		}
		Log.i(new Server(), "Done.");
	}

	public enum ServerStatus {
		NONE, OFFLINE, ONLINE, PRIVATE, FULL, WHITELISTED, BLOCKED
	}

	public String name = "CubeQuest Server";
	public InetAddress address;
	public int port;
	public ServerStatus status = ServerStatus.NONE;
	public int ping = -1;
	public int maxUserCount = 0;
	public int userCount = 0;

	public Server(InetAddress addr, int prt) {
		address = addr;
		port = prt;
	}

	public Server() {

	}

	public int getPingLevel() {
		return ping == -1 ? 0 : 6;
	}

	public int sendServerRequest(CubeQuest game) {
		PacketServerRequest p = new PacketServerRequest();
		game.client.sendPacket(p, this);
		return p.requestID;
	}
}
