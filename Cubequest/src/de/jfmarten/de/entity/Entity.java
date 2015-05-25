package de.jfmarten.de.entity;

import java.awt.Color;
import java.util.ArrayList;

import de.jfmarten.de.AABB;
import de.jfmarten.de.network.packet.PacketEntityUpdate;
import de.jfmarten.de.world.World;

public class Entity {

	public World world;

	public String id = "";

	public float x = 20, y = 20;
	public float mX = 0, mY = 0;

	public long jumpDuration = 0;

	public AABB collisionBox = new AABB(-0.5f, -1.5f, 0.5f, 1.5f);
	public AABB currentBox = collisionBox;

	public long nextNetUpdate = 0;

	public boolean onGround = false;

	public static Entity create(World w, PacketEntityUpdate p) {
		Entity e = null;
		if (p.entityID.startsWith("player")) {
			e = new EntityPlayer(w);
		} else {
			e = new Entity(w);
		}
		e.x = p.x;
		e.y = p.y;
		e.id = p.entityID;
		return e;
	}

	public Entity(World w) {
		world = w;
	}

	public void render() {

	}

	public void update(long delta) {
		nextNetUpdate -= 1;
		mY += delta * 0.01f;
		if ((jumpDuration -= delta) > 0) {
			mY -= delta * 0.001f * jumpDuration;
		} else {
			jumpDuration = 0;
		}

		move(mX, mY);
		if (nextNetUpdate < 0) {
			nextNetUpdate = 1000;
		}
	}

	public void netMove(float x, float y) {
		PacketEntityUpdate p = new PacketEntityUpdate();
		p.x = x;
		p.y = y;
		p.remove = false;
		p.entityID = id;
		world.client.sendPacket(p);
	}

	public void move(float mX, float mY) {
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
		if (nextNetUpdate < 0)
			netMove(x, y);
	}

	public static String genID() {
		return (int) (Math.random() * 1000000) + "";
	}
}
