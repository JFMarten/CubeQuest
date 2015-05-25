package de.jfmarten.de;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import de.jfmarten.de.gui.Gui;
import de.jfmarten.de.gui.GuiError;
import de.jfmarten.de.gui.GuiMain;
import de.jfmarten.de.network.Server;
import de.jfmarten.de.network.SocketClient;
import de.jfmarten.de.network.SocketClientListener;
import de.jfmarten.de.network.SocketServer;
import de.jfmarten.de.render.FontRender;
import de.jfmarten.de.render.Render;

public class CubeQuest implements SocketClientListener {

	// TO-DO LISTE:
	// TODO Übersetzungen
	// TODO Spieler einfügen

	// KONSTANTEN

	// Standart Port für den Server
	public static final int SERVER_PORT = 30100;

	// Blockgröße in GAME-Einheiten
	public static float BLOCKSIZE = 32;

	// Fenstergröße
	public static int WIDTH = 1000, HEIGHT = 800;
	// Ingamegröße
	public static int GAME_WIDTH = 1000, GAME_HEIGHT = HEIGHT / WIDTH * GAME_WIDTH;
	// Statische FPS
	public static int FPS = 100;
	// Argumente, welche beim Programmstart übergeben werden
	public static ArrayList<String> args;

	// VARIABLEN

	// Der lokale Server
	public SocketServer server;
	// Der lokale Client
	public SocketClient client;

	// Server Thread
	public Thread serverThread;
	// Client Thread
	public Thread clientThread;

	// Menu (Gui)
	private Gui gui = new GuiMain(this);

	// Nutzername des Spielers
	public String username = "" + (int) (Math.random() * 1000000);

	// FPS Bstimmung
	public int fps = 0;
	public int nowFps = 0;
	public long lastFps = 0;

	// Updatedelta Bestimmung
	public long lastDelta = 0;

	public CubeQuest(String[] args) {
		this.args = new ArrayList<String>();
		for (int i = 0; i < args.length; i++) {
			this.args.add(args[i]);
		}
	}

	public void start() {
		// Initialisiereungen

		// Einstellungen laden
		try {
			Settings.load();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.i(this, "Start");
		Log.i(this, "Starts initGL");
		// OpenGL initialiesieren
		initGL();
		Log.i(this, "Done.");
		Log.i(this, "Starts init");
		// Standart Initialisierung
		try {
			init();
		} catch (Exception e) {
			gui = new GuiError(this, e);
		}
		Log.i(this, "Done.");
		Log.i(this, "Ready.");

		lastFps = System.currentTimeMillis();
		lastDelta = System.currentTimeMillis();

		// Laufe solange, wie des Fenster nicht geschlossen wird
		while (!Display.isCloseRequested()) {

			if (lastFps + 1000 < System.currentTimeMillis()) {
				lastFps += 1000;
				fps = nowFps;
				nowFps = 0;
				Display.setTitle("FPS: " + fps);
			}
			nowFps++;

			long delta = System.currentTimeMillis() - lastDelta;
			lastDelta = System.currentTimeMillis();

			// Rendern
			try {
				render();
			} catch (Exception e) {
				gui = new GuiError(this, e);
			}
			// Update
			try {
				update(delta);
			} catch (Exception e) {
				gui = new GuiError(this, e);
			}

			// Fenster aktualisieren
			Display.update();
			// Fenster auf 60 FPS setzten
			if (FPS != -1)
				Display.sync(FPS);
		}

		Log.i(this, "Shuting down...");
		client.stop();
		server.stop();

		// Ablauf nach schließen des Fensters (evnt. speichern...)
		// Fenster zerstören
		try {
			Thread.sleep(150);
			clientThread.join();
			serverThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Display.destroy();
	}

	public void initGL() {
		// Mögliche Fehler abhalten
		try {
			// Fenster erstellen
			Display.create();
			// Fenstergröße setzen
			setDisplaySize(Settings.GAME_WIDTH, Settings.GAME_HEIGHT);
			// Wenn das Argument -fullscreen übergeben wurde, Vollbildmodus
			// einstellen
			if (args.contains("-fullscreen")) {
				// Desktop Displaymode übernehmen
				DisplayMode desktop = Display.getDesktopDisplayMode();
				// Displaygrößen herausfinden und mit Höhe vergleichen
				for (DisplayMode dm : Display.getAvailableDisplayModes()) {
					if (desktop.getHeight() == dm.getHeight()) {
						desktop = dm;
						break;
					}
				}
				// Fenstergröße setzen
				setDisplaySize(desktop.getWidth(), desktop.getHeight());
				// Vollbild setzten
				Display.setFullscreen(true);
			}
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		// OpenGL Initialisierung
		GL11.glViewport(0, 0, WIDTH, HEIGHT);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		// Bildübertragung setzen
		GL11.glOrtho(0, GAME_WIDTH, GAME_HEIGHT, 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

	}

	public void init() throws NumberFormatException, IOException {
		// Übersetzer
		Translator.load("de_DE");
		// Fontrender für Texte
		Render.fontRender = new FontRender("assets/font.png");
		int i = 0;
		while (server == null) {
			try {
				server = new SocketServer(SERVER_PORT + i);
			} catch (Exception e) {
				i++;
			}
		}
		client = new SocketClient(this);

		Server.loadServerList();

		serverThread = new Thread(server, "Server");
		clientThread = new Thread(client, "Client");

		serverThread.start();
		clientThread.start();

		gui = new GuiMain(this);

	}

	public void stop() {

	}

	public void render() throws FileNotFoundException, IOException {
		// Fenster leeren
		Render.glClear();
		GL11.glClearColor(1f, 1f, 1f, 1);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);

		if (client.world != null) {
			GL11.glPushMatrix();
			{
				GL11.glMatrixMode(GL11.GL_PROJECTION);
				GL11.glLoadIdentity();
				GL11.glOrtho(0, GAME_WIDTH / BLOCKSIZE, GAME_HEIGHT / BLOCKSIZE, 0, 1, -1);
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glLoadIdentity();
				client.world.render();
			}
			GL11.glPopMatrix();
		}

		if (gui != null) {
			GL11.glPushMatrix();
			{
				GL11.glMatrixMode(GL11.GL_PROJECTION);
				GL11.glLoadIdentity();
				GL11.glOrtho(0, GAME_WIDTH, GAME_HEIGHT, 0, 1, -1);
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glLoadIdentity();
				gui.render();
			}
			GL11.glPopMatrix();
		}
	}

	public void update(long delta) throws IOException, LWJGLException {
		if (client.world != null)
			client.world.update(delta);
		if (gui != null) {
			gui.update();
		}

		while (Mouse.next()) {
			int[] m = getMouse();
			int x = m[0];
			int y = m[1];
			int btn = Mouse.getEventButton();
			boolean state = Mouse.getEventButtonState();
			int wheel = Mouse.getDWheel();
			if (gui != null) {
				gui.onMouseClickEvent(btn, state);
				if (wheel != 0)
					gui.onMouseWheel(x, y, wheel);
			}
		}
	}

	public void setDisplaySize(int w, int h) throws LWJGLException {
		// Setzen der Breite und Höhe
		WIDTH = w;
		HEIGHT = h;
		// Setzen der Game-Breite
		GAME_WIDTH = 1000;
		// Berechen der Game-Höhe
		GAME_HEIGHT = (int) (HEIGHT / (float) WIDTH * GAME_WIDTH);
		// Fenstergröße setzen
		Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
	}

	public void onSocketClientConnectedandRunning() {

	}

	public static int[] getMouse() {
		int x = Mouse.getX(), y = Mouse.getY();
		x = (int) ((float) x / CubeQuest.WIDTH * CubeQuest.GAME_WIDTH);
		y = (int) ((float) y / CubeQuest.HEIGHT * CubeQuest.GAME_HEIGHT);
		y = CubeQuest.GAME_HEIGHT - y;
		return new int[] { x, y };
	}

	public void setGui(Gui g) {
		gui = g;
	}

	public void onSocketClientPacketReceived(DatagramPacket dp) {
		if (gui != null) {
			gui.packetReceived(dp);
		}
	}
}
