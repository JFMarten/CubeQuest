package de.jfmarten.de;

import java.awt.Color;

import de.jfmarten.de.render.Render;

public class AABB {

	public float epsilon = 0.0f;

	public float x0, x1, y0, y1;

	public AABB(float xx0, float yy0, float xx1, float yy1) {
		x0 = xx0;
		x1 = xx1;
		y0 = yy0;
		y1 = yy1;
	}

	public AABB expand(float x, float y) {
		float xx0 = x0, xx1 = x1, yy0 = y0, yy1 = y1;
		if (x > 0)
			xx1 += x;
		if (x < 0)
			xx0 += x;
		if (y > 0)
			yy1 += y;
		if (y < 0)
			yy0 += y;

		return new AABB(xx0, yy0, xx1, yy1);
	}

	public float clipXCollide(AABB other, float x) {
		if (other.y1 > y0 && other.y0 < y1) {
			float var;
			if (x > 0 && other.x1 <= x0 && (var = x0 - other.x1 - this.epsilon) < x) {
				x = var;
			}
			if (x < 0 && other.x0 >= x1 && (var = x1 - other.x0 - this.epsilon) > x) {
				x = var;
			}
			return x;
		} else {
			return x;
		}
	}

	public float clipYCollide(AABB other, float y) {
		if (other.x1 > x0 && other.x0 < x1) {
			float var;
			if (y > 0 && other.y1 <= y0 && (var = y0 - other.y1 - this.epsilon) < y) {
				y = var;
			}
			if (y < 0 && other.y0 >= y1 && (var = y1 - other.y0 - this.epsilon) > y) {
				y = var;
			}
			return y;
		} else {
			return y;
		}
	}

	public AABB move(float x, float y) {
		float xx0 = x0 + x;
		float xx1 = x1 + x;
		float yy0 = y0 + y;
		float yy1 = y1 + y;
		return new AABB(xx0, yy0, xx1, yy1);
	}

	public void render(Color c) {
		Render.glColor(c);
		Render.glRenderQuads();
		{
			Render.glVertex2f(x0, y0);
			Render.glVertex2f(x1, y0);
			Render.glVertex2f(x1, y1);
			Render.glVertex2f(x0, y1);
		}
		Render.glRenderEnd();
	}

}
