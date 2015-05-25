package de.jfmarten.de.block;

import org.lwjgl.opengl.GL11;

import de.jfmarten.de.AABB;

public class Block {

	/**
	 * RenderMode. <li><b>NORMAL</b> Block wird normal mit texPath-Texture
	 * gerendert <li><b>TRANSPARENT</b> Block wird transparent dargestellt <li>
	 * <b>MANUEL</b> Rendermethode der Blockunterklasse wird aufgerufen
	 * 
	 * @author jfmarten
	 *
	 */
	public enum RenderMode {
		NORMAL, TRANSPARENT, MANUEL
	}

	public static float ERROR = 0.0001f;

	// Liste, in der alle Blocke gespeichert sind
	public static Block[] list = new Block[256];

	// Alle Blöcke und die Standartkonfiguration
	public static Block air = new Block(0, "air").setTextureCoords(0, 0).setRenderMode(RenderMode.TRANSPARENT).setSolid(false);
	public static Block stone = new Block(1, "stone").setTextureCoords(1, 0);
	public static Block dirt = new Block(2, "dirt").setTextureCoords(2, 0);
	public static Block grass = new Block(3, "grass").setTextureCoords(3, 0);
	// TODO Neue Blöcke einfügen

	// Block ID 0 bis 255, jede ID darf nur einmal verwendet werden
	public final int id;
	// Trivialname des Blocks (generell klein geschrieben und Unterarten mit
	// Punkt getrennt)
	public String name;
	// Dateiname der Texture ohne Endung (Standart PNG)
	private String texPath;
	// Rendermodus des Blocks
	private RenderMode renderMode;
	// Texturekoordinaten
	private int texX, texY;
	// Solide
	private boolean solid = true;

	public Block(int id, String name) {
		this.id = id;
		this.name = name;
		this.texPath = "blocks";
		this.renderMode = RenderMode.NORMAL;
		if (list[id] != null)
			try {
				throw new RuntimeException("Block with id " + id + " already used!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		list[id] = this;
	}

	/**
	 * Setzt die <b>Blocktextur</b><br>
	 * Wenn s = "null" dann wird der Block als Transparent angezeigt<br>
	 * Wenn s = "standart" dann wird die Texture "blocks" angezeigt<br>
	 * 
	 * @param texPath
	 * @return Block
	 */
	public Block setTexture(String texPath) {
		if (texPath.equals("null")) {
			this.texPath = "";
			setRenderMode(RenderMode.TRANSPARENT);
		} else if (texPath.equals("standart")) {
			this.texPath = "blocks";
		} else {
			this.texPath = texPath;
		}
		return this;
	}

	/**
	 * Setzt den Rendermodus
	 * 
	 * @param renderMode
	 *            Rendermode des Blocks
	 * @return
	 */
	public Block setRenderMode(RenderMode renderMode) {
		this.renderMode = renderMode;
		return this;
	}

	/**
	 * Setzt Texturkordinaten
	 * 
	 * @param x
	 *            X-Koordinate
	 * @param y
	 *            Y-Koordinate
	 * @return
	 */
	public Block setTextureCoords(int x, int y) {
		texX = x;
		texY = y;
		return this;
	}

	/**
	 * Setzt, on der Block solide ist oder nicht
	 * 
	 * @param s
	 *            Solide ?
	 * @return
	 */
	public Block setSolid(boolean s) {
		solid = s;
		return this;
	}

	public String getTexturePath() {
		return texPath;
	}

	public RenderMode getRenderMode() {
		return renderMode;
	}

	public boolean isSolid() {
		return solid;
	}

	public AABB getCollisionBox(int x, int y) {
		return new AABB(x, y, x + 1, y + 1);
	}

	/**
	 * Manuele Rendermethode
	 */
	public void render() {

	}

	/**
	 * Allgemeine Rendermethode
	 * 
	 * @param b
	 *            Block ID
	 * @param x
	 *            Renderposition x
	 * @param y
	 *            Renderposition y
	 */
	public static void render(byte b, float x, float y) {
		Block bl = list[(int) b];
		GL11.glTexCoord2f(1 / 16f * bl.texX + ERROR, 1 / 16f * bl.texY + ERROR);
		GL11.glVertex2f(x, y);
		GL11.glTexCoord2f(1 / 16f * (bl.texX + 1) - ERROR, 1 / 16f * bl.texY + ERROR);
		GL11.glVertex2f(x + 1, y);
		GL11.glTexCoord2f(1 / 16f * (bl.texX + 1) - ERROR, 1 / 16f * (bl.texY + 1) - ERROR);
		GL11.glVertex2f(x + 1, y + 1);
		GL11.glTexCoord2f(1 / 16f * bl.texX + ERROR, 1 / 16f * (bl.texY + 1) - ERROR);
		GL11.glVertex2f(x, y + 1);
	}
}
