package de.jfmarten.de.entity;

import de.jfmarten.de.world.World;

public class EntityPlayer extends Entity{

	public EntityPlayer(World w) {
		super(w);
		id = "player_"+Entity.genID();
	}

}
