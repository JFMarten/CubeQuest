package de.jfmarten.de.network.world;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import de.jfmarten.de.AABB;
import de.jfmarten.de.CubeQuest;
import de.jfmarten.de.block.Block;
import de.jfmarten.de.network.entity.NetEntity;
import de.jfmarten.de.world.Chunk;

public class NetWorld {

	public String path = "saves/TEST/level";

	public int width = 1000;
	public int height = 100;

	public int spawnX = width / 2;
	public int spawnY = height / 2;

	public HashMap<String, Chunk> loaded = new HashMap<String, Chunk>();
	public ArrayList<NetEntity> entities = new ArrayList<NetEntity>();

	public Chunk getChunk(int x, int y) throws IOException {
		new File(path).mkdirs();
		if (loaded.containsKey(x + "_" + y)) {
			return loaded.get(x + "_" + y);
		}
		Chunk c = new Chunk(x, y);
		c.load(new File(path, "chunk_" + x + "_" + y),this);
		loaded.put(x + "_" + y, c);
		return c;
	}

	public int getBlockTyp(int x, int y) throws IOException {
		if (x < 0 || y < 0) {
			return 0;
		}
		int cX = x / 32;
		int cY = y / 32;
		if (loaded.containsKey(cX + "_" + cY)) {
			return (int) loaded.get(cX + "_" + cY).blocks[(y % 32) * 32 + (x % 32)];
		} else {
			Chunk c = getChunk(cX, cY);
			return c.blocks[(y % 32) * 32 + (x % 32)];
		}
	}

	public ArrayList<AABB> getAABBs(AABB area) throws IOException {
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

	public NetEntity getEntity(String id) {
		for (NetEntity ne : entities) {
			if (ne.id.equals(id)) {
				return ne;
			}
		}
		return null;
	}

	public void addEntity(NetEntity e) {
		entities.add(e);
	}

	public void setBlock(int i, int x, int y) throws IOException {
		if (x < 0 || y < 0) {
			return;
		}
		int cX = x / 32;
		int cY = y / 32;
		if (loaded.containsKey(cX + "_" + cY)) {
			loaded.get(cX + "_" + cY).blocks[(y % 32) * 32 + (x % 32)] = (byte) i;
		} else {
			Chunk c = getChunk(cX, cY);
			c.blocks[(y % 32) * 32 + (x % 32)] = (byte) i;
		}
	}
}
