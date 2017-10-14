/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.Locks;

import Reika.ChromatiCraft.Base.LockLevel;
import Reika.ChromatiCraft.World.Dimension.Structure.LocksGenerator;
import Reika.DragonAPI.Instantiable.Worldgen.OriginBlockCache;

public class LocksRoomComplex extends LockLevel {

	public LocksRoomComplex(LocksGenerator g) {
		super(g, null/*LockChannel.COMPLEX*/);
	}

	@Override
	public void generate(OriginBlockCache world, int x, int y, int z) {

	}

	@Override
	public int getWidth() {
		return 0;
	}

	@Override
	public int getLength() {
		return 0;
	}

	@Override
	public int getEnterExitDL() {
		return 0;
	}

	@Override
	public int getEnterExitDT() {
		return 0;
	}

	@Override
	public int getInitialOffset() {
		return 0;
	}

	@Override
	public int getMirroredOffset() {
		return 0;
	}

	@Override
	public int getDifficultyRating() {
		return 4;
	}

	@Override
	public int getFeatureRating() {
		return 3;
	}

}
