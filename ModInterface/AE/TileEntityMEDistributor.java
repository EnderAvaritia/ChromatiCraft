/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.AE;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.ItemElementCalculator;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.Data.Collections.ItemCollection;
import Reika.DragonAPI.Instantiable.ModInteract.BasicAEInterface;
import Reika.DragonAPI.Instantiable.ModInteract.DirectionalAEInterface;
import Reika.DragonAPI.Instantiable.ModInteract.MEWorkTracker;
import Reika.DragonAPI.Interfaces.TileEntity.SidePlacedTile;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.MESystemReader;
import Reika.DragonAPI.ModInteract.DeepInteract.MESystemReader.ExtractedItem;
import Reika.DragonAPI.ModInteract.DeepInteract.MESystemReader.MatchMode;

import appeng.api.AEApi;
import appeng.api.networking.IGridBlock;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.util.AECableType;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Strippable(value={"appeng.api.networking.IActionHost"})
public class TileEntityMEDistributor extends TileEntityChromaticBase implements IActionHost, SidePlacedTile {

	@ModDependent(ModList.APPENG)
	private MESystemReader network;
	private Object aeGridBlock;
	private Object aeGridNode;
	private int AEPowerCost = 1;

	private final ItemCollection output = new ItemCollection();

	private StepTimer checkTimer = new StepTimer(10);
	private StepTimer cacheTimer = new StepTimer(40);

	public static final int NSLOTS = 10;

	private ItemStack[] filter = new ItemStack[NSLOTS*2];
	private int[] threshold = new int[NSLOTS];
	private MatchMode[] match = new MatchMode[NSLOTS];

	private MEWorkTracker hasWork = new MEWorkTracker();

	public TileEntityMEDistributor() {
		if (ModList.APPENG.isLoaded()) {
			aeGridBlock = new DirectionalAEInterface(this, this.getTile().getCraftedProduct());
			aeGridNode = FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER ? AEApi.instance().createGridNode((IGridBlock)aeGridBlock) : null;
		}

		for (int i = 0; i < match.length; i++) {
			match[i] = MatchMode.EXACT;
		}
	}

	@Override
	protected void onInvalidateOrUnload(World world, int x, int y, int z, boolean invalid) {
		super.onInvalidateOrUnload(world, x, y, z, invalid);
		if (ModList.APPENG.isLoaded() && aeGridNode != null)
			((IGridNode)aeGridNode).destroy();
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.MEDISTRIBUTOR;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (world.isRemote) {
			this.spawnParticles(world, x, y, z, meta);
		}
		else {
			cacheTimer.update();
			if (cacheTimer.checkCap()) {
				this.buildCache();
			}

			if (aeGridBlock != null && !world.isRemote) {
				((BasicAEInterface)aeGridBlock).setPowerCost(AEPowerCost);
			}
			if (AEPowerCost > 1)
				AEPowerCost -= Math.max(1, AEPowerCost/40);

			if (network != null) {
				hasWork.tick();
				checkTimer.update();
				if (hasWork.hasWork()) {
					if (checkTimer.checkCap()) {
						checkTimer.setCap(Math.min(40, checkTimer.getCap()+2));
						output.clear();
						TileEntity te = this.getAdjacentTileEntity(this.getFacing().getOpposite());
						if (te instanceof IInventory) {
							output.addInventory((IInventory)te);
						}
						for (int i = 0; i < NSLOTS; i++) {
							ItemStack f1 = filter[i];
							ItemStack f2 = filter[i+NSLOTS];
							if (f1 != null && f2 != null) {
								int fit = f2.getMaxStackSize()-output.addItemsToUnderlyingInventories(ReikaItemHelper.getSizedItemStack(f2, f2.getMaxStackSize()), true);
								if (fit > 0) {
									hasWork.reset();
									MatchMode mode = this.getMode(i);
									long has = mode.countItems(network, f1);//this.isFuzzy(i) ? network.getFuzzyItemCount(f1, FuzzyMode.IGNORE_ALL, this.useOreDict(i)) : network.getItemCount(f1);
									int missing = this.getThreshold(i)-(has > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)has);
									if (missing > 0) {
										this.transferItem(ReikaItemHelper.getSizedItemStack(f2, Math.min(Math.min(fit, missing), f2.getMaxStackSize())), (IInventory)te, mode);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public int getThreshold(int i) {
		return threshold[i];
	}

	public MatchMode getMode(int i) {
		return match[i];
	}

	private void buildCache() {
		if (ModList.APPENG.isLoaded()) {
			((DirectionalAEInterface)aeGridBlock).disconnectAll().connect(this.getFacing());
			Object oldNode = aeGridNode;
			if (aeGridNode == null) {
				aeGridNode = FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER ? AEApi.instance().createGridNode((IGridBlock)aeGridBlock) : null;
			}
			if (aeGridNode != null)
				((IGridNode)aeGridNode).updateState();

			if (oldNode != aeGridNode || network == null) {
				if (aeGridNode == null)
					network = null;
				else if (network == null)
					network = new MESystemReader((IGridNode)aeGridNode, this);
				else
					network = new MESystemReader((IGridNode)aeGridNode, network);

				this.buildCallbacks();
			}
		}
	}

	private void buildCallbacks() {
		if (network != null) {
			network.clearCallbacks();
			for (int i = 0; i < filter.length; i++) {
				ItemStack pattern = filter[i];
				if (pattern != null) {
					network.addCallback(pattern, hasWork);
				}
			}
		}
		hasWork.markDirty();
	}

	private void transferItem(ItemStack is, IInventory ii, MatchMode mode) {
		ExtractedItem rem = mode.removeItems(network, is, true);
		if (rem != null) {
			ItemStack add = rem.getItem();
			add.stackSize = Math.min(add.stackSize, is.stackSize);
			if (ReikaInventoryHelper.addToIInv(add, ii)) {
				mode.removeItems(network, add, false);
			}
			ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.METRANSFER.ordinal(), this, 32, Item.getIdFromItem(add.getItem()), add.getItemDamage());
			AEPowerCost = Math.min(500, AEPowerCost+Math.max(1, add.stackSize/4));
			checkTimer.setCap(Math.max(4, checkTimer.getCap()-4));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBTTagCompound fil = new NBTTagCompound();

		for (int i = 0; i < filter.length; i++) {
			ItemStack is = filter[i];
			if (is != null) {
				NBTTagCompound tag = new NBTTagCompound();
				is.writeToNBT(tag);
				if (i < 9)
					tag.setInteger("thresh", threshold[i]);
				fil.setTag("filter_"+i, tag);
			}
		}

		for (int i = 0; i < threshold.length; i++) {
			fil.setInteger("thresh_"+i, threshold[i]);
			fil.setInteger("match_"+i, match[i].ordinal());
		}

		NBT.setTag("filter", fil);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		filter = new ItemStack[filter.length];
		NBTTagCompound fil = NBT.getCompoundTag("filter");
		for (int i = 0; i < filter.length; i++) {
			String name = "filter_"+i;
			if (fil.hasKey(name)) {
				NBTTagCompound tag = fil.getCompoundTag(name);
				ItemStack is = ItemStack.loadItemStackFromNBT(tag);
				filter[i] = is;
			}
		}

		threshold = new int[threshold.length];
		match = new MatchMode[match.length];
		for (int i = 0; i < threshold.length; i++) {
			String name = "filter_"+i;
			threshold[i] = fil.getInteger("thresh_"+i);
			match[i] = MatchMode.list[fil.getInteger("match_"+i)];
		}
	}

	public void setMapping(int slot, ItemStack is) {
		filter[slot] = is;
		if (ModList.APPENG.isLoaded())
			this.buildCallbacks();
		this.syncAllData(true);
	}

	public ItemStack getMapping(int slot) {
		return filter[slot] != null ? filter[slot].copy() : null;
	}

	public void setThreshold(int slot, int thresh) {
		threshold[slot] = thresh;
		if (ModList.APPENG.isLoaded())
			this.buildCallbacks();
		this.syncAllData(true);
	}

	public void toggleFuzzy(int slot) {
		match[slot] = match[slot].next();
		if (ModList.APPENG.isLoaded())
			this.buildCallbacks();
		this.syncAllData(true);
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z, int meta) {
		ForgeDirection dir = this.getFacing();
		double r = 0.475;
		double v = -ReikaRandomHelper.getRandomPlusMinus(0.03125, 0.015);
		double dx = x+0.5+dir.offsetX*r;
		double dy = y+0.5+dir.offsetY*r;
		double dz = z+0.5+dir.offsetZ*r;
		double vx = v*dir.offsetX;
		double vy = v*dir.offsetY;
		double vz = v*dir.offsetZ;
		dx = ReikaRandomHelper.getRandomPlusMinus(dx, 0.03125);
		dy = ReikaRandomHelper.getRandomPlusMinus(dy, 0.03125);
		dz = ReikaRandomHelper.getRandomPlusMinus(dz, 0.03125);
		vx = ReikaRandomHelper.getRandomPlusMinus(vx, 0.008);
		vy = ReikaRandomHelper.getRandomPlusMinus(vy, 0.008);
		vz = ReikaRandomHelper.getRandomPlusMinus(vz, 0.008);
		float s = (float)ReikaRandomHelper.getRandomPlusMinus(1.25, 0.25);
		int l = 40+rand.nextInt(30);
		EntityFX fx = new EntityBlurFX(world, dx, dy, dz, vx, vy, vz).setRapidExpand().setLife(l).setScale(s).setColor(135, 90, 165);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	@SideOnly(Side.CLIENT)
	public void spawnTransferParticles(World world, int x, int y, int z, int id, int meta) {
		ItemStack is = new ItemStack(Item.getItemById(id), 1, meta);
		ForgeDirection dir = this.getFacing();
		ElementTagCompound tag = ItemElementCalculator.instance.getValueForItem(is);
		float s = ReikaRandomHelper.getRandomPlusMinus(6, 2);
		int l = 20+rand.nextInt(20);
		if (tag == null || tag.isEmpty()) {
			int n = 1+rand.nextInt(5);
			for (int i = 0; i < n; i++) {
				double dx = x-dir.offsetX+rand.nextDouble();
				double dy = y-dir.offsetY+rand.nextDouble();
				double dz = z-dir.offsetZ+rand.nextDouble();
				EntityFX fx = new EntityBlurFX(world, dx, dy, dz).setRapidExpand().setLife(l).setScale(s).setColor(32, 150, 255);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}
		else {
			for (CrystalElement e : tag.elementSet()) {
				double dx = x-dir.offsetX+rand.nextDouble();
				double dy = y-dir.offsetY+rand.nextDouble();
				double dz = z-dir.offsetZ+rand.nextDouble();
				EntityFX fx = new EntityBlurFX(e, world, dx, dy, dz, 0, 0, 0).setRapidExpand().setLife(l).setScale(s);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	@ModDependent(ModList.APPENG)
	public IGridNode getGridNode(ForgeDirection dir) {
		return dir == this.getFacing() ? (IGridNode)aeGridNode : null;
	}

	@Override
	@ModDependent(ModList.APPENG)
	public AECableType getCableConnectionType(ForgeDirection dir) {
		return dir == this.getFacing() ? AECableType.GLASS : null;
	}

	private ForgeDirection getFacing() {
		return dirs[this.getBlockMetadata()];
	}

	@Override
	@ModDependent(ModList.APPENG)
	public void securityBreak() {

	}

	@Override
	@ModDependent(ModList.APPENG)
	public IGridNode getActionableNode() {
		return (IGridNode)aeGridNode;
	}

	@Override
	public void placeOnSide(int s) {
		this.setBlockMetadata(s);
	}

	public boolean checkLocationValidity() {
		return true;
	}

	public void drop() {
		ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, this.getTile().getCraftedProduct());
		this.delete();
	}

}
