package de.jfmarten.de.world;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.jfmarten.de.block.Block;
import de.jfmarten.de.network.packet.PacketWorldData;
import de.jfmarten.de.network.world.NetWorldGenerator;

public class Chunk {

	public int x, y;
	public byte[] blocks = new byte[32 * 32];
	public byte[] meta = new byte[32 * 32];

	public Chunk(PacketWorldData p) {
		x = p.x;
		y = p.y;
		blocks = p.data;
	}

	public Chunk(int xx, int yy) {
		x = xx;
		y = yy;
	}

	/**
	 * Läd den Chunk von einem Path.
	 * 
	 * @param path
	 *            Path des Chunks
	 * @throws IOException
	 */
	public void load(File path) throws IOException {
		if (path.exists()) {
			InputStream is = new FileInputStream(path);
			BufferedInputStream br = new BufferedInputStream(is);
			byte[] b = new byte[32 * 32 * 2];
			br.read(b);
			for (int i = 0; i < 32 * 32; i++) {
				blocks[i] = b[i];
			}
			for (int i = 0; i < 32 * 32; i++) {
				meta[i] = b[i + 32 * 32];
			}
			is.close();
			br.close();
		} else {
			path.createNewFile();
			generate(path);
			load(path);
		}
	}

	/**
	 * Speichert den Chunk auf einen Pfad
	 * 
	 * @param path
	 *            Path des Chunks
	 * @throws IOException
	 */
	public void save(File path) throws IOException {
		OutputStream os = new FileOutputStream(path);
		BufferedOutputStream br = new BufferedOutputStream(os);
		br.write(blocks);
		br.write(meta);
		br.close();
		os.close();
	}

	/**
	 * Generiert den Chunk und speichert ihn aud den Pfad
	 * 
	 * @param path
	 *            Pfad des Chunks
	 * @throws IOException
	 */
	public void generate(File path) throws IOException {
		NetWorldGenerator.generate(this);
		save(path);
	}
}
