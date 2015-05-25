package de.jfmarten.de.network.world;

import java.io.IOException;
import java.util.Random;

import de.jfmarten.de.block.Block;
import de.jfmarten.de.network.world.noise.OctaveNoise;
import de.jfmarten.de.world.Chunk;

public class NetWorldGenerator {

	public static OctaveNoise noise = new OctaveNoise(new Random(), 4);
	public static int seed = 123;

	public static Chunk generate(Chunk c, NetWorld nw) throws IOException {
		for (int i = 0; i < 32; i++) {
			for (int j = 0; j < 32; j++) {
				if (c.y == 1) {
					double n = noise.compute(seed, ((c.x * 32 + i) / 3f));
					n = Math.abs(n);
					if (j == (int) n) {
						c.blocks[j * 32 + i] = (byte) Block.grass.id;
						if (new Random().nextInt(10) == 1) {
							NetWorldGeneratorUtils.generateTree(nw, i, j - 1);
						}
					} else if (j > (int) n)
						c.blocks[j * 32 + i] = (byte) Block.dirt.id;
					else
						c.blocks[j * 32 + i] = (byte) Block.air.id;
				} else if (c.y > 1) {
					c.blocks[j * 32 + i] = (byte) Block.stone.id;
				} else {
					c.blocks[j * 32 + i] = (byte) Block.air.id;
				}
			}
		}
		return c;
	}
}
