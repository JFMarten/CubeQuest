package de.jfmarten.de.network.world;

import java.io.IOException;
import java.util.Random;

import de.jfmarten.de.Log;
import de.jfmarten.de.block.Block;

public class NetWorldGeneratorUtils {

	public static void generateTree(NetWorld nw, int x, int y) throws IOException {
		int h = new Random().nextInt(2) + 1;
		Log.i(new NetWorldGeneratorUtils(), "Gen tree");
		nw.setBlockW(Block.stone.id, x, y);
		if (y - h > 0) {
			for (int i = y; i > y - h; i--) {
				if(i == y){
					nw.setBlockW(Block.tree_root.id,x,i);
				}else{
					nw.setBlockW(Block.tree_main.id,x,i);
				}
			}
		}
	}
}
