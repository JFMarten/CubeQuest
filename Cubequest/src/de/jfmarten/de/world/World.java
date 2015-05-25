package de.jfmarten.de.world;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

import de.jfmarten.de.AABB;
import de.jfmarten.de.CubeQuest;
import de.jfmarten.de.KeyConfig;
import de.jfmarten.de.Log;
import de.jfmarten.de.block.Block;
import de.jfmarten.de.block.Block.RenderMode;
import de.jfmarten.de.entity.Entity;
import de.jfmarten.de.network.SocketClient;
import de.jfmarten.de.network.packet.PacketWorldData;
import de.jfmarten.de.render.Render;

public class World {

	public int width, height;

	public float scrollX = 0, scrollY = 0;

	public HashMap<String, Chunk> loaded = new HashMap<String, Chunk>();
	public ArrayList<String> order = new ArrayList<String>();
	public SocketClient client;
	public HashMap<String, Entity> entities = new HashMap<String, Entity>();
	public ArrayList<String> remove = new ArrayList<String>();
	public Entity playerEntity;

	public World(SocketClient c) {
		client = c;
	}

	public void render() throws FileNotFoundException, IOException {
		Render.glColor(1, 1, 1);
		Texture tex = Render.glBindTexture("assets/menu.png");
		Render.setTextureNearest();
		Render.glRenderQuads();
		{
			Render.glVertexUV2f(0, 0, 0, 0);
			Render.glVertexUV2f(CubeQuest.GAME_WIDTH / CubeQuest.BLOCKSIZE, 0, tex.getWidth(), 0);
			Render.glVertexUV2f(CubeQuest.GAME_WIDTH / CubeQuest.BLOCKSIZE, 108f / 192f * (CubeQuest.GAME_WIDTH / CubeQuest.BLOCKSIZE), tex.getWidth(), tex.getHeight());
			Render.glVertexUV2f(0, 108f / 192f * (CubeQuest.GAME_WIDTH / CubeQuest.BLOCKSIZE), 0, tex.getHeight());
		}
		Render.glRenderEnd();

		Render.glClear();
		Render.glBindTexture("assets/blocks.png");
		Render.setTextureNearest();
		Render.glRenderQuads();
		for (int i = 0; i < CubeQuest.GAME_WIDTH / CubeQuest.BLOCKSIZE + 1; i++) {
			for (int j = 0; j < CubeQuest.GAME_HEIGHT / CubeQuest.BLOCKSIZE + 1; j++) {
				int x = (int) (i + scrollX);
				int y = (int) (j + scrollY);
				int t = getBlockTyp(x, y);
				Block b = Block.list[t];
				if (b.getRenderMode() == RenderMode.TRANSPARENT) {

				} else if (b.getRenderMode() == RenderMode.NORMAL) {
					Block.render((byte) t, i - (scrollX % 1), j - (scrollY % 1));
				} else if (b.getRenderMode() == RenderMode.MANUEL) {
					b.render(i - (scrollX % 1), j - (scrollY % 1));
				}
			}
		}
		Render.glRenderEnd();
		Render.glClear();

		GL11.glPushMatrix();
		GL11.glTranslatef(-scrollX, -scrollY, 0);
		for (Entity e : entities.values()) {
			e.render();
		}
		GL11.glPopMatrix();
	}

	public void update(long delta) {
		if (playerEntity != null) {
			scrollX = playerEntity.x - (CubeQuest.GAME_WIDTH / CubeQuest.BLOCKSIZE) / 2;
			scrollY = playerEntity.y - (CubeQuest.GAME_HEIGHT / CubeQuest.BLOCKSIZE) / 2;

			float mX = 0;
			float mY = 0;
			if (Keyboard.isKeyDown(KeyConfig.KEY_LEFT)) {
				mX -= 0.005f * delta;
			}
			if (Keyboard.isKeyDown(KeyConfig.KEY_RIGHT)) {
				mX += 0.005f * delta;
			}
			if (Keyboard.isKeyDown(KeyConfig.KEY_UP) && playerEntity.onGround) {
				playerEntity.jumpDuration = 100;
			}
			playerEntity.mX = mX;
			playerEntity.mY = mY;
		} else {
			if (Keyboard.isKeyDown(KeyConfig.KEY_LEFT)) {
				scrollX -= 0.05f * delta;
			}
			if (Keyboard.isKeyDown(KeyConfig.KEY_RIGHT)) {
				scrollX += 0.05f * delta;
			}
			if (Keyboard.isKeyDown(KeyConfig.KEY_DOWN)) {
				scrollY += 0.05f * delta;
			}
			if (Keyboard.isKeyDown(KeyConfig.KEY_UP)) {
				scrollY -= 0.05f * delta;
			}
		}

		GL11.glPushMatrix();
		GL11.glTranslatef(-scrollX, -scrollY, 0);
		for (Entity e : entities.values()) {
			e.update(delta);
		}
		GL11.glPopMatrix();

		for (String e : remove) {
			if (entities.containsKey(e))
				entities.remove(e);
		}
		remove.clear();
	}

	public int getBlockTyp(int x, int y) {
		if (x < 0 || y < 0) {
			return 0;
		}
		int cX = x / 32;
		int cY = y / 32;
		if (loaded.containsKey(cX + "_" + cY)) {
			return (int) loaded.get(cX + "_" + cY).blocks[(y % 32) * 32 + (x % 32)];
		} else {
			sendChunkRequest(cX, cY);
			return 0;
		}
	}

	public void sendChunkRequest(int x, int y) {
		if (!order.contains(x + "_" + y)) {
			Log.i(this, "Order chunk " + x + " " + y);
			order.add(x + "_" + y);
			PacketWorldData p = new PacketWorldData();
			p.x = x;
			p.y = y;
			client.sendPacket(p);
		}
	}

	public ArrayList<AABB> getAABBs(AABB area) {
		ArrayList<AABB> list = new ArrayList<AABB>();
		int x0 = (int) area.x0;
		int x1 = (int) area.x1 + 1;
		int y0 = (int) area.y0;
		int y1 = (int) area.y1 + 1;
		if (area.x0 < 0)
			x0--;
		if (area.y0 < 0)
			y0--;
		for (int i = x0; i < x1; i++) {
			for (int j = y0; j < y1; j++) {
				if (i >= (CubeQuest.GAME_WIDTH / CubeQuest.BLOCKSIZE) / 2 && j >= (CubeQuest.GAME_WIDTH / CubeQuest.BLOCKSIZE) / 2 && i < width && j < height) {
					Block b = Block.list[getBlockTyp(i, j)];
					if (b.isSolid()) {
						AABB aabb = b.getCollisionBox(i, j);
						list.add(aabb);
					}
				} else {
					list.add(new AABB(i, j, i + 1, j + 1));
				}
			}
		}
		return list;
	}

	public Entity getEntity(String id) {
		if (entities.containsKey(id)) {
			return entities.get(id);
		} else {
			return null;
		}
	}

	public void addEntity(Entity e) {
		entities.put(e.id, e);
	}

	public void removeEntity(Entity e) {
		remove.add(e.id);
	}
}
