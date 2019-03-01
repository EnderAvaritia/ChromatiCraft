/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base.TileEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Interfaces.TileEntity.BreakAction;

public abstract class TileEntityEntityCacher extends CrystalReceiverBase implements BreakAction {

	private static final Collection<UUID> cache = new ArrayList();
	private List<UUID> local = new ArrayList();

	private final StepTimer resyncTimer = new StepTimer(100);

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		resyncTimer.update();
		if (resyncTimer.checkCap()) {
			cache.removeAll(local);
			AxisAlignedBB box = this.getBox(world, x, y, z);
			local = world.getEntitiesWithinAABB(this.getEntitySearchClass(), box);
			//stopped.addAll(local);
		}
	}

	protected abstract Class getEntitySearchClass();

	public final void breakBlock() {
		cache.removeAll(local);
	}

	protected abstract AxisAlignedBB getBox(World world, int x, int y, int z);

	protected static final boolean cachedEntity(Entity e) {
		return cache.contains(e.getUniqueID());
	}

}
