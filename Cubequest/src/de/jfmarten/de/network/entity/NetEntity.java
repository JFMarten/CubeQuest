package de.jfmarten.de.network.entity;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import de.jfmarten.de.AABB;
import de.jfmarten.de.network.world.NetWorld;

public class NetEntity {

	public NetWorld world;

	public String id = "";

	public float x = 20, y = 20;
	public float mX = 0, mY = 0;

	public long jumpDuration = 0;

	public AABB collisionBox = new AABB(-0.5f, -1.5f, 0.5f, 1.5f);
	public AABB currentBox = collisionBox;

	public boolean onGround = false;

	public NetEntity(NetWorld w) {
		world = w;
	}

	public void move(float mX, float mY) throws IOException {
		currentBox = collisionBox.move(x, y);
		AABB dir = currentBox.expand(mX, mY);
		dir.render(Color.red);

		ArrayList<AABB> aabbs = world.getAABBs(dir);
		for (AABB aabb : aabbs) {
			aabb.render(Color.BLUE);
			mY = aabb.clipYCollide(currentBox, mY);
		}

		for (AABB aabb : aabbs) {
			mX = aabb.clipXCollide(currentBox, mX);
		}

		y += mY;
		x += mX;

		if (mY == 0) {
			this.mY = 0;
			onGround = true;
		} else {
			onGround = false;
		}
		if (mX == 0) {
			this.mX = 0;
		}
	}
}
