/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Recipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.CrystalNetworkLogger.FlowFail;
import Reika.ChromatiCraft.Auxiliary.Interfaces.OwnedTile;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Base.TileEntity.CrystalReceiverBase;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.ItemElementCalculator;
import Reika.ChromatiCraft.Magic.Network.CrystalFlow;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityCenterBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityGlobeFX;
import Reika.ChromatiCraft.Render.Particle.EntityLaserFX;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Instantiable.Data.Collections.ItemCollection;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.ModInteract.BasicAEInterface;
import Reika.DragonAPI.Instantiable.Recipe.ItemMatch;
import Reika.DragonAPI.Interfaces.TileEntity.GuiController;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.MESystemReader;
import Reika.DragonAPI.ModInteract.DeepInteract.MESystemReader.ExtractedItem;
import appeng.api.AEApi;
import appeng.api.config.FuzzyMode;
import appeng.api.networking.IGridBlock;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.util.AECableType;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Strippable(value={"appeng.api.networking.IActionHost"})
public class TileEntityCastingAuto extends CrystalReceiverBase implements GuiController, OwnedTile, IActionHost {

	private static final ElementTagCompound required = new ElementTagCompound();

	static {
		required.addTag(CrystalElement.BLACK, 50);
		required.addTag(CrystalElement.PURPLE, 20);
		required.addTag(CrystalElement.LIGHTBLUE, 10);
		required.addTag(CrystalElement.WHITE, 20);
	}

	private CastingRecipe recipe;

	private StepTimer stepDelay = new StepTimer(5);

	private StepTimer cacheTimer = new StepTimer(40);

	private int recipesToGo = 0;
	private int recipeCycles = 0;

	private final ItemCollection ingredients = new ItemCollection();
	@ModDependent(ModList.APPENG)
	private MESystemReader network;
	private Object aeGridBlock;
	private Object aeGridNode;

	public TileEntityCastingAuto() {
		if (ModList.APPENG.isLoaded()) {
			aeGridBlock = new BasicAEInterface(this, this.getTile().getCraftedProduct());
			aeGridNode = FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER ? AEApi.instance().createGridNode((IGridBlock)aeGridBlock) : null;
		}
	}

	private TileEntityCastingTable getTable(World world, int x, int y, int z) {
		for (int i = 1; i < 5; i++) {
			Block b = world.getBlock(x, y-i, z);
			if (!b.isAir(world, x, y-i, z))
				return null;
		}
		TileEntity te = world.getTileEntity(x, y-5, z);
		return te instanceof TileEntityCastingTable ? (TileEntityCastingTable)te : null;
	}

	public Collection<CastingRecipe> getAvailableRecipes() {
		TileEntityCastingTable te = this.getTable(worldObj, xCoord, yCoord, zCoord);
		return te != null ? te.getCompletedRecipes() : new HashSet();
	}

	public CastingRecipe getCurrentRecipeOutput() {
		return recipe;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.AUTOMATOR;
	}

	@Override
	protected void onInvalidateOrUnload(World world, int x, int y, int z, boolean invalid) {
		super.onInvalidateOrUnload(world, x, y, z, invalid);
		if (ModList.APPENG.isLoaded() && aeGridNode != null)
			((IGridNode)aeGridNode).destroy();
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
		TileEntityCastingTable te = this.getTable(world, x, y, z);

		if (te != null && world.isRemote) {
			this.doConnectionParticles(world, x, y, z, te);
		}

		if (!world.isRemote) {
			cacheTimer.update();
			if (cacheTimer.checkCap()) {
				this.buildCache();
			}

			if (this.getCooldown() == 0 && checkTimer.checkCap()) {
				this.checkAndRequest();
			}

			if (ModList.APPENG.isLoaded()) {
				if (aeGridBlock != null && !world.isRemote) {
					((BasicAEInterface)aeGridBlock).setPowerCost(recipe != null ? 6 : 1);
				}
			}

			if (recipe != null && recipesToGo > 0 && energy.containsAtLeast(required)) {
				if (te != null) {
					if (this.canCraft(world, x, y, z, te)) {
						if (this.isRecipeReady(world, x, y, z, te)) {
							if (this.triggerCrafting(world, x, y, z, te)) {
								te.syncAllData(true);
								ElementTagCompound tag = required.copy();
								tag.scale(recipe.getAutomationCostFactor(this, te, null));
								this.drainEnergy(tag);
								recipesToGo -= recipeCycles;
								recipeCycles = 0;
							}
						}
						else {
							int amt = Math.min(recipesToGo, recipe.getOutput().getMaxStackSize()/recipe.getOutput().stackSize);
							UpdateStep c = this.prepareRecipeStep(world, x, y, z, te, amt);
							if (c != null) {
								recipeCycles = recipeCycles > 0 ? Math.min(recipeCycles, c.item.stackSize) : c.item.stackSize;
								ChromaSounds.CAST.playSoundAtBlock(world, c.loc.xCoord, c.loc.yCoord, c.loc.zCoord);
								int[] dat = new int[]{c.loc.xCoord, c.loc.yCoord, c.loc.zCoord, 0, Item.getIdFromItem(c.item.getItem()), c.item.getItemDamage(), c.item.stackTagCompound != null ? 1 : 0};
								ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.CASTAUTOUPDATE.ordinal(), this, 48, dat);
								ElementTagCompound tag = required.copy();
								tag.scale(recipe.getAutomationCostFactor(this, te, c.item));
								this.drainEnergy(tag);
								te.markDirty();
								TileEntity tile = c.loc.getTileEntity(world);
								if (tile != null) {
									tile.markDirty();
									if (tile instanceof TileEntityBase) {
										((TileEntityBase)tile).syncAllData(true);
									}
								}
							}
						}
					}
				}
			}
			else if (recipe == null || recipesToGo == 0) {
				this.cancelCrafting();
			}
		}
	}

	private void checkAndRequest() {
		for (CrystalElement e : required.elementSet()) {
			int amt = this.getEnergy(e);
			int sp = this.getRemainingSpace(e);
			if (amt < sp)
				this.requestEnergy(e, sp);
		}
	}

	@SideOnly(Side.CLIENT)
	private void doConnectionParticles(World world, int x, int y, int z, TileEntityCastingTable te) {
		double a = Math.toRadians((this.getTicksExisted()*2)%360);
		int n = 6;
		int sp = 360/n;
		double r = 0.5+0.125*Math.sin(this.getTicksExisted()/10D);
		for (int i = 0; i < 360; i += sp) {
			double ri = Math.toRadians(i);
			double dx = x+0.5+r*Math.sin(a+ri);
			double dy = te.yCoord+1;
			double dz = z+0.5+r*Math.cos(a+ri);
			EntityFX fx = new EntityGlobeFX(world, dx, dy, dz, 0, 0.125, 0);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@SideOnly(Side.CLIENT)
	public void receiveUpdatePacket(int[] data) {
		double x = data[0]+0.5;
		double y = data[1]+0.5;
		double z = data[2]+0.5;
		ItemStack is = new ItemStack(Item.getItemById(data[4]), 1, data[5]);
		boolean nbt = data[6] > 0;
		ElementTagCompound tag = ItemElementCalculator.instance.getValueForItem(is);
		if (nbt && tag != null) {
			tag.addTag(CrystalElement.PURPLE, 1);
			tag.addTag(CrystalElement.BLACK, 1);
		}
		for (int i = 0; i < 32; i++) {
			CrystalElement e = tag != null ? ReikaJavaLibrary.getRandomCollectionEntry(rand, tag.elementSet()) : null;
			int color = e != null ? e.getColor() : 0x00aaff;
			double px = ReikaRandomHelper.getRandomPlusMinus(x, 1);
			double py = ReikaRandomHelper.getRandomPlusMinus(y, 0.5);
			double pz = ReikaRandomHelper.getRandomPlusMinus(z, 1);
			EntityFX fx = null;
			float g = -(float)ReikaRandomHelper.getRandomPlusMinus(0.0625, 0.03125);
			switch(rand.nextInt(3)) {
				case 0:
					fx = new EntityCenterBlurFX(worldObj, px, py, pz).setColor(color).setGravity(g);
					break;
				case 1:
					fx = new EntityLaserFX(CrystalElement.WHITE, worldObj, px, py, pz).setColor(color).setGravity(g);
					break;
				case 2:
				default:
					fx = new EntityBlurFX(worldObj, px, py, pz).setColor(color).setGravity(g);
					break;
			}
			fx.motionY = 0.03125+rand.nextDouble()*0.0625;
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	private void buildCache() {
		ingredients.clear();
		TileEntity te = this.getAdjacentTileEntity(ForgeDirection.UP);
		if (te instanceof IInventory) {
			ingredients.addInventory((IInventory)te);
		}

		if (ModList.APPENG.isLoaded()) {
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
			}
		}
	}

	private boolean canCraft(World world, int x, int y, int z, TileEntityCastingTable te) {
		return te.isReadyToCraft() && te.getPlacerUUID() != null && te.getPlacerUUID().equals(this.getPlacerUUID());
	}

	private boolean triggerCrafting(World world, int x, int y, int z, TileEntityCastingTable te) {
		return te.trigger();
	}

	private boolean isRecipeReady(World world, int x, int y, int z, TileEntityCastingTable te) {
		return te.getActiveRecipe() == recipe;
	}

	private UpdateStep prepareRecipeStep(World world, int x, int y, int z, TileEntityCastingTable te, int amt) {
		stepDelay.update();
		if (stepDelay.checkCap()) {
			if (recipe instanceof MultiBlockCastingRecipe) {
				MultiBlockCastingRecipe mr = (MultiBlockCastingRecipe)recipe;
				HashMap<List<Integer>, TileEntityItemStand> map = te.getOtherStands();
				Map<List<Integer>, ItemMatch> items = mr.getAuxItems();
				for (List<Integer> key : map.keySet()) {
					ItemMatch item = items.get(key);
					TileEntityItemStand stand = map.get(key);
					if (stand != null) {
						ItemStack in = stand.getStackInSlot(0);
						if ((item == null && in != null) || (item != null && !item.match(in))) {
							if (in != null) {
								if (this.recoverItem(in)) {
									stand.setInventorySlotContents(0, null);
									return new UpdateStep(stand, in);
								}
							}
							else {
								ItemStack ret = this.findItem(item, amt);
								if (ret != null) {
									stand.setInventorySlotContents(0, ret);
									return new UpdateStep(stand, ret);
								}
							}
						}
						else {
							//matches
						}
					}
				}
				ItemStack ctr = mr.getMainInput();
				for (int i = 0; i < 9; i++) {
					ItemStack in = te.getStackInSlot(i);
					if (i == 4) {
						if (in != null) {
							if (ReikaItemHelper.matchStacks(in, ctr) && (ctr.stackTagCompound == null || ItemStack.areItemStackTagsEqual(in, ctr))) {
								//matches
							}
							else {
								if (this.recoverItem(in)) {
									te.setInventorySlotContents(i, null);
									return new UpdateStep(te, in);
								}
							}
						}
						else {
							ItemStack ret = this.findItem(ctr, amt);
							if (ret != null) {
								te.setInventorySlotContents(i, ret);
								return new UpdateStep(te, ret);
							}
						}
					}
					else {
						if (in != null && this.recoverItem(in)) {
							te.setInventorySlotContents(i, null);
							return new UpdateStep(te, in);
						}
					}
				}
			}
			else {
				Object[] arr = recipe.getInputArray();
				for (int i = 0; i < 9; i++) {
					Object item = arr[i];
					ItemStack in = te.getStackInSlot(i);
					if (this.matches(item, in)) {
						//match
					}
					else {
						if (in != null) {
							if (this.recoverItem(in)) {
								te.setInventorySlotContents(i, null);
								return new UpdateStep(te, in);
							}
						}
						else {
							ItemStack ret = this.findItem(item, amt);
							if (ret != null) {
								te.setInventorySlotContents(i, ret);
								return new UpdateStep(te, ret);
							}
						}
					}
				}
			}
		}
		return null;
	}

	private boolean matches(Object object, ItemStack is) {
		if (object instanceof ItemStack) {
			return ReikaItemHelper.matchStacks(is, (ItemStack)object) && ItemStack.areItemStackTagsEqual(is, (ItemStack)object);
		}
		else if (object instanceof List) {
			return ReikaItemHelper.listContainsItemStack((Collection<ItemStack>)object, is, true);
		}
		else if (object == null && is == null)
			return true;
		return false;
	}

	private ItemStack findItem(Object item, int amt) {
		List<ItemStack> li = new ArrayList();
		if (item instanceof ItemStack)
			li.add((ItemStack)item);
		if (item instanceof List)
			li.addAll((List)item);
		if (item instanceof ItemMatch) {
			for (KeyedItemStack ks : ((ItemMatch)item).getItemList()) {
				li.add(ks.getItemStack());
			}
		}

		if (DragonAPICore.debugtest)
			return ReikaItemHelper.getSizedItemStack(li.get(0), amt);

		if (ModList.APPENG.isLoaded()) {
			ChromatiCraft.logger.debug("Delegate "+this+" requesting "+li+" from "+ingredients+" / "+network);
		}
		else {
			ChromatiCraft.logger.debug("Delegate "+this+" requesting "+li+" from "+ingredients);
		}

		for (ItemStack is : li) {
			if (ModList.APPENG.isLoaded()) {
				if (is.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
					ExtractedItem rem = network.removeItemFuzzy(ReikaItemHelper.getSizedItemStack(is, amt), false, FuzzyMode.IGNORE_ALL, false, is.stackTagCompound != null);
					if (rem != null) {
						ItemStack ret = ReikaItemHelper.getSizedItemStack(rem.getItem(), (int)rem.amount);
						ret.setItemDamage(0);
						return ret;
					}
					else {
						//network.triggerFuzzyCrafting(worldObj, is, amt, null, null);
					}
				}
				else {
					int rem = (int)network.removeItem(ReikaItemHelper.getSizedItemStack(is, amt), false, is.stackTagCompound != null);
					if (rem > 0) {
						return ReikaItemHelper.getSizedItemStack(is, rem);
					}
					else {
						//network.triggerCrafting(worldObj, is, amt, null, null); GOD DAMN IT AE
					}
				}
				ChromatiCraft.logger.debug(this+" failed to find "+is+" in its ME System.");
			}
			int has = ingredients.getItemCount(is);
			if (has > 0) {
				int rem = Math.min(amt, has);
				ingredients.removeXItems(is, rem);
				return ReikaItemHelper.getSizedItemStack(is, rem);
			}
		}
		return null;
	}

	private boolean recoverItem(ItemStack is) {
		if (DragonAPICore.debugtest)
			return true;
		if (ModList.APPENG.isLoaded()) {
			int left = (int)network.addItem(is, false);
			is.stackSize = left;
			if (left == 0)
				return true;
		}
		int left = ingredients.addItemsToUnderlyingInventories(is, false);
		return left <= 0;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void onPathBroken(CrystalFlow p, FlowFail f) {
		this.setRecipe(null, 0);
	}

	public void setRecipe(CastingRecipe c, int amt) {
		recipe = c;
		recipesToGo = amt;
	}

	public void cancelCrafting() {
		this.setRecipe(null, 0);
	}

	@Override
	public int getReceiveRange() {
		return 16;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return required.contains(e);
	}

	@Override
	public int maxThroughput() {
		return 2500;
	}

	@Override
	public boolean canConduct() {
		return true;
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return 12000;
	}

	@Override
	@ModDependent(ModList.APPENG)
	public IGridNode getGridNode(ForgeDirection dir) {
		return (IGridNode)aeGridNode;
	}

	@Override
	@ModDependent(ModList.APPENG)
	public AECableType getCableConnectionType(ForgeDirection dir) {
		return AECableType.COVERED;
	}

	@Override
	@ModDependent(ModList.APPENG)
	public void securityBreak() {

	}

	@Override
	public boolean onlyAllowOwnersToUse() {
		return true;
	}

	@Override
	@ModDependent(ModList.APPENG)
	public IGridNode getActionableNode() {
		return (IGridNode)aeGridNode;
	}

	private static class UpdateStep {

		private final Coordinate loc;
		private final ItemStack item;

		private UpdateStep(TileEntity te, ItemStack is) {
			this(new Coordinate(te), is);
		}

		private UpdateStep(Coordinate c, ItemStack is) {
			loc = c;
			item = is;
		}

	}

}
