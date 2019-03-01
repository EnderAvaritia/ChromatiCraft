/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;

import Reika.ChromatiCraft.TileEntity.Processing.TileEntitySpawnerReprogrammer;
import Reika.DragonAPI.Base.CoreContainer;

public class ContainerSpawnerProgrammer extends CoreContainer {

	private TileEntitySpawnerReprogrammer tile;

	public ContainerSpawnerProgrammer(EntityPlayer player, TileEntitySpawnerReprogrammer te) {
		super(player, te);
		tile = te;

		this.addSlot(0, 26, 21);
		this.addSlotNoClick(1, 134, 21);

		this.addPlayerInventoryWithOffset(player, 0, 24);
	}

	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();

		for (int i = 0; i < crafters.size(); i++)
		{
			ICrafting icrafting = (ICrafting)crafters.get(i);
			icrafting.sendProgressBarUpdate(this, 0, tile.progressTimer);
		}

		//ReikaPacketHelper.sendTankSyncPacket(ChromatiCraft.packetChannel, tile, "tank");
	}

	@Override
	public void updateProgressBar(int par1, int par2)
	{
		if (par1 == 0) {
			tile.progressTimer = par2;
		}
	}


}
