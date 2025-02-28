/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.RangeTracker;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.DragonAPI.Instantiable.ParticlePath;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockVector;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaLiquidRenderer;
import Reika.DragonAPI.ModInteract.DeepInteract.TinkerSmelteryHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerToolHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntitySmelteryDistributor extends TileEntityChromaticBase {

	public static final int SCAN_RADIUS_XZ = 16;

	private final RangeTracker range = new RangeTracker(SCAN_RADIUS_XZ);

	private final StepTimer cacheTimer = new StepTimer(40);
	private final StepTimer operationTimer = new StepTimer(5);

	private final ArrayList<SmelteryDrain> drains = new ArrayList();
	private final ArrayList<CastingBlock> targets = new ArrayList();

	private double rotation;
	private double rotationSpeed;
	private double MAX_ROTATION_SPEED = 3.5;
	private float colorIntensity = 1;

	private void sendFluid(Fluid f, ForgeDirection dir, Coordinate drain, Coordinate target) {
		int x = drain.xCoord;
		int y = drain.yCoord;
		int z = drain.zCoord;
		int x2 = target.xCoord;
		int y2 = target.yCoord;
		int z2 = target.zCoord;
		ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.SMELTERYFLUIDSEND.ordinal(), this, 48, x, y, z, x2, y2, z2, dir.ordinal(), f.getID());
		ChromaSounds.FIRE.playSoundAtBlock(this, 0.7F, 2);
	}

	@SideOnly(Side.CLIENT)
	public void sendFluidToClient(int x, int y, int z, int x2, int y2, int z2, ForgeDirection drainFace, Fluid f) {
		ParticlePath p = ParticlePath.getPath(worldObj, new BlockVector(x, y, z, drainFace), new BlockVector(x2, y2+1, z2, ForgeDirection.UP), 0.5, 0.5);

		List<DecimalPosition> path = p.getPath();
		for (int i = 0; i < path.size(); i++) {
			DecimalPosition pos = path.get(i);
			double f2a = (i+1)/(double)path.size();
			double f2b = 1-f2a;
			double f2 = 2*Math.min(f2a, f2b);
			int l = (int)(90*f2);
			float s = 0.75F+(float)(1.25*f2);
			IIcon ico = ReikaLiquidRenderer.getFluidIconSafe(f);
			EntityFX fx = new EntityCCBlurFX(worldObj, pos.xCoord, pos.yCoord, pos.zCoord).setIcon(ico).setLife(l).setScale(s).setRapidExpand().setAlphaFading().setBasicBlend();
			fx.noClip = true;
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.SMELTERYDISTRIBUTOR;
	}

	public double getRotation() {
		return rotation;
	}

	public float getColorIntensity() {
		return colorIntensity;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		boolean active = !this.hasRedstoneSignal();
		if (world.isRemote) {
			rotation += rotationSpeed;
			if (active) {
				this.doParticles(world, x, y, z);
				double d = Math.max(1, 3-this.getTicksExisted()/32D);
				rotationSpeed = Math.min(rotationSpeed+0.1*d*d, MAX_ROTATION_SPEED*d);
				colorIntensity = Math.min(1, colorIntensity*1.05F+0.05F);
			}
			else {
				rotationSpeed = Math.max(rotationSpeed-0.08, 0);
				colorIntensity *= 0.9;
			}
		}
		else {
			cacheTimer.setCap(drains.isEmpty() || targets.isEmpty() ? 40 : 160);
			cacheTimer.update();
			operationTimer.update();

			if (cacheTimer.checkCap() || this.getTicksExisted() == 0) {
				this.findDrainsAndTargets(world, x, y, z);
			}

			if (active && !drains.isEmpty() && operationTimer.checkCap()) {
				SmelteryDrain sd = ReikaJavaLibrary.getRandomListEntry(rand, drains);
				if (sd.isValid(world)) {
					FluidStack fs = this.getTransferrableFluid(world, sd);
					if (fs != null) {
						for (CastingBlock cb : targets) {
							cb.update(world);
						}
						Collections.sort(targets);
						CastingBlock cb = this.getFirstValidTarget(world, fs);
						if (cb != null) {
							this.doTransfer(world, sd, cb, fs.getFluid());
						}
					}
				}
				else {
					drains.remove(sd);
				}
			}
		}
	}

	private void findDrainsAndTargets(World world, int x, int y, int z) {
		drains.clear();
		targets.clear();
		int r = range.getRange();
		int r2 = r/2;
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				for (int j = -r2; j <= 0; j++) {
					TileEntity te = world.getTileEntity(x+i, y+j, z+k);
					if (te != null) {
						if (te.getClass() == TinkerSmelteryHandler.drainClass()) {
							drains.add(new SmelteryDrain(te));
						}
						else if (te.getClass() == TinkerSmelteryHandler.basinClass() || (te.getClass() == TinkerSmelteryHandler.tableClass() && !this.isPartmaker(te))) {
							targets.add(new CastingBlock(te));
						}
					}
				}
			}
		}
	}

	private boolean isPartmaker(TileEntity te) {
		for (int y = te.yCoord+1; y <= te.yCoord+4; y++) {
			TileEntity at = te.worldObj.getTileEntity(te.xCoord, y, te.zCoord);
			if (at != null && at.getClass() == TinkerSmelteryHandler.faucetClass())
				return true;
		}
		return false;
	}

	private FluidStack getTransferrableFluid(World world, SmelteryDrain sd) {
		IFluidHandler te = (IFluidHandler)sd.location.getTileEntity(world);
		return te.drain(sd.facing, Integer.MAX_VALUE, false);
	}

	private CastingBlock getFirstValidTarget(World world, FluidStack fs) {
		Iterator<CastingBlock> it = targets.iterator();
		while (it.hasNext()) {
			CastingBlock cb = it.next();
			if (cb.isValid(world)) {
				int capacity = cb.getCapacity(world, fs.getFluid());
				if (capacity > 0 && capacity <= fs.amount) {
					IFluidHandler te = cb.getTile(world);
					int space = te.fill(ForgeDirection.UP, fs, false);
					if (space >= capacity) {
						//ReikaJavaLibrary.pConsole("Can add "+space+" of "+fs.getLocalizedName()+" to "+te);
						return cb;
					}
				}
			}
			else {
				it.remove();
			}
		}
		return null;
	}

	private void doTransfer(World world, SmelteryDrain sd, CastingBlock cb, Fluid f) {
		IFluidHandler te = cb.getTile(world);
		int amt = cb.getCapacity(world, f);
		FluidStack out = sd.drain(world, f, amt);
		if (out != null && out.amount > 0) {
			if (te.fill(ForgeDirection.DOWN, new FluidStack(f, out.amount), true) > 0)
				this.sendFluid(f, sd.facing, sd.location, cb.location);
		}
	}

	@SideOnly(Side.CLIENT)
	private void doParticles(World world, int x, int y, int z) {
		if (rand.nextInt(3) == 0) {
			double px = x+0.5;
			double py = y+0.5;//+rand.nextDouble();
			double pz = z+0.5;
			int l = 20+rand.nextInt(60);
			float g = -(float)ReikaRandomHelper.getRandomBetween(0.03125, 0.125);
			float s = 1.5F;
			double v = 0.0625;
			double vx = ReikaRandomHelper.getRandomPlusMinus(0, v);
			double vz = ReikaRandomHelper.getRandomPlusMinus(0, v);
			double vy = ReikaRandomHelper.getRandomBetween(0, 1.25*g);
			EntityFX fx = new EntityCCBlurFX(world, px, py, pz, vx, vy, vz).setIcon(ChromaIcons.FADE_RAY).setColor(0xFFA700).setLife(l).setScale(s).setGravity(g).setRapidExpand();
			EntityFX fx2 = new EntityCCBlurFX(world, px, py, pz, vx, vy, vz).setColor(0xffffff).setLife(l).setScale(s/2.5F).setGravity(g).setRapidExpand();
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	private static final class SmelteryDrain {

		private final Coordinate location;
		private final ForgeDirection facing;

		private SmelteryDrain(TileEntity te) {
			location = new Coordinate(te);
			facing = TinkerSmelteryHandler.getDrainDirection(te);
		}

		public FluidStack drain(World world, Fluid f, int amt) {
			return this.getTile(world).drain(facing, new FluidStack(f, amt), true);
		}

		public boolean isValid(World world) {
			TileEntity te = location.getTileEntity(world);
			return te != null && te.getClass() == TinkerSmelteryHandler.drainClass();
		}

		public IFluidHandler getTile(World world) {
			return (IFluidHandler)location.getTileEntity(world);
		}

	}

	private static class CastingBlock implements Comparable<CastingBlock> {

		private final Coordinate location;
		private final boolean isBasin;

		private boolean isOutputFull;
		private boolean hasCast;
		private boolean hasNonIngotCast;

		private CastingBlock(TileEntity te) {
			location = new Coordinate(te);
			isBasin = te.getClass() == TinkerSmelteryHandler.basinClass();
		}

		public int getCapacity(World world, Fluid f) {
			return this.getTile(world).fill(ForgeDirection.UP, new FluidStack(f, Integer.MAX_VALUE), false);
		}

		public IFluidHandler getTile(World world) {
			return (IFluidHandler)location.getTileEntity(world);
		}

		private void update(World world) {
			IInventory ii = (IInventory)location.getTileEntity(world);
			isOutputFull = ii.getStackInSlot(1) != null;
			hasCast = ii.getStackInSlot(0) != null;
			hasNonIngotCast = hasCast && !ReikaItemHelper.matchStacks(ii.getStackInSlot(0), TinkerToolHandler.getInstance().getIngotCast());
		}

		public boolean isValid(World world) {
			TileEntity te = location.getTileEntity(world);
			return te != null && (te.getClass() == TinkerSmelteryHandler.basinClass() || te.getClass() == TinkerSmelteryHandler.tableClass());
		}

		@Override
		public int compareTo(CastingBlock o) {
			return Integer.compare(o.getPriority(), this.getPriority());
		}

		private int getPriority() {
			if (isOutputFull)
				return -1000;
			if (isBasin) {
				return 100;
			}
			else {
				if (hasNonIngotCast) {
					return 500;
				}
				else if (hasCast) {
					return 50;
				}
				else { //empty table
					return 0;
				}
			}
		}

	}

}
