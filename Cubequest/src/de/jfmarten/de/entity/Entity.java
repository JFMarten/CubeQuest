package de.jfmarten.de.entity;

import java.awt.Color;
import java.util.ArrayList;

import de.jfmarten.de.AABB;
import de.jfmarten.de.network.packet.PacketEntityUpdate;
import de.jfmarten.de.render.Render;
import de.jfmarten.de.world.World;

public class Entity {

	public World world;

	public String id = "";

	public float x = 20, y = 20;
	public float aX = 0, aY = 0;
	public float oX = 0, oY = 0;
	public float mX = 0, mY = 0;

	public long jumpDuration = 0;

	public AABB collisionBox = new AABB(-0.5f, -1.5f, 0.5f, 1.5f);
	public AABB currentBox = collisionBox;

	public long nextNetUpdate = 0;

	public boolean onGround = false;

	public boolean noPhysic = false;

	public static Entity create(World w, PacketEntityUpdate p) {
		Entity e = new Entity(w, p.entityID);
		e.x = p.x;
		e.y = p.y;
		return e;
	}

	public Entity(World w, String i) {
		world = w;
		id = i;
		if (id.equals("")) {
			id = genID();
		}
	}

	public void render() {
		Render.drawString("~0" + id, x, y - 2f, 0, 0.4f);
	}

	public void update(long delta) {
		currentBox = collisionBox.move(x, y);
		currentBox.render(Color.GREEN);
		nextNetUpdate -= delta;
		if (!noPhysic) {
			mY += delta * 0.01f;
			if ((jumpDuration -= delta) > 0) {
				mY -= delta * 0.001f * jumpDuration;
			} else {
				jumpDuration = 0;
			}

			move(mX, mY);
		} else {

		}
		if (nextNetUpdate < 0) {
			nextNetUpdate = 100;
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

	public void netPosition(float x, float y) {
		this.x = x;
		this.y = y;
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
