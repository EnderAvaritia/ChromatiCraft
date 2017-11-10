/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Decoration;

import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Rendering.MutableStructureRenderer;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TileEntityAreaHologram extends TileEntityChromaticBase {

	private static final int RANGE = 16;

	private int readX = -RANGE;
	private int readY = -RANGE;
	private int readZ = -RANGE;

	@SideOnly(Side.CLIENT)
	private StructureRenderer renderer;

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		if (world.isRemote)
			this.initRenderer();
	}

	@SideOnly(Side.CLIENT)
	private void initRenderer() {
		renderer = new MutableStructureRenderer(worldObj, RANGE);

		/*
		FilledBlockArray array = new FilledBlockArray(world);
		for (int i = -RANGE; i <= RANGE; i++) {
			for (int j = -RANGE; j <= RANGE; j++) {
				for (int k = -RANGE; k <= RANGE; k++) {
					if (y+j >= 0) {
						array.loadBlock(x+i, y+j, z+k);
					}
				}
			}
		}
		renderer = new StructureRenderer(array);
		 */

		renderer.resetRotation();
		renderer.rotate(30, -45, 0);
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.HOLOGRAM;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (world.isRemote) {
			int n = (RANGE*2+1)*2;
			for (int i = 0; i < n; i++) {
				if (readY+yCoord < 0) {
					readY = -yCoord;
					//ReikaJavaLibrary.pConsole("readY too low, assigning to "+readY);
				}

				int dx = x+readX;
				int dy = y+readY;
				int dz = z+readZ;
				//ReikaWorldHelper.forceGenAndPopulate(world, dx, dy, dz, meta);

				this.addBlock(readX, readY, readZ, world, dx, dy, dz);

				this.updateReadPosition();

				if (readY > RANGE/*>= worldObj.getActualHeight()*/) {
					readX = readY = readZ = -RANGE;
				}
			}
			//renderer.rotate(0, 1, 0);
		}
	}

	@SideOnly(Side.CLIENT)
	private void addBlock(int dx, int dy, int dz, World world, int x, int y, int z) {
		//ReikaJavaLibrary.pConsole(dx+","+dy+","+dz);
		((MutableStructureRenderer)renderer).addBlock(dx, dy, dz, world.getBlock(x, y, z), world.getBlockMetadata(x, y, z), world.getTileEntity(x, y, z));
	}

	@SideOnly(Side.CLIENT)
	public void renderStructure(float ptick) {
		renderer.draw3D(0, 0, ptick, false);
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	private void updateReadPosition() {
		boolean flag1 = false;
		boolean flag2 = false;
		readX++;
		if (readX > RANGE) {
			readX = -RANGE;
			flag1 = true;
		}
		if (flag1) {
			readZ++;
			if (readZ > RANGE) {
				readZ = -RANGE;
				flag2 = true;
			}
			if (flag2) {
				readY++;
			}
		}
	}

}
