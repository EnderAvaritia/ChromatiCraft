/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Processing;

import java.util.ArrayList;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.API.Interfaces.ProgrammableSpawner;
import Reika.ChromatiCraft.Auxiliary.Interfaces.OperationInterval;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedRelayPowered;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Extras.ItemSpawner;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaSpawnerHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

//Can change both the mob type and the params in MobSpawnerBaseLogic
public class TileEntitySpawnerReprogrammer extends InventoriedRelayPowered implements OperationInterval {

	private String selectedMob;
	private static final ArrayList<String> disallowedMobs = new ArrayList();

	private StepTimer progress = new StepTimer(180);
	public int progressTimer;

	private int minDelay = 200;
	private int maxDelay = 800;
	private int maxNear = 6;
	private int spawnCount = 4;
	private int spawnRange = 4;
	private int activeRange = 16;

	private static final ElementTagCompound required = new ElementTagCompound();

	public static final int MIN_MINDELAY = 100;

	static {
		required.addTag(CrystalElement.PINK, 2000);
		required.addTag(CrystalElement.GRAY, 500);

		addDisallowedMob(EntityWither.class);
		addDisallowedMob(EntityDragon.class);
		addDisallowedMob(EntityGiantZombie.class);
		addDisallowedMob(EntityLiving.class);
		addDisallowedMob(EntityMob.class);
		//addDisallowedMob(EntityIronGolem.class);

		addDisallowedMob("Void Monster");

		addDisallowedMob("TaintedCow");
		addDisallowedMob("TaintedSheep");
		addDisallowedMob("TaintedCreeper");
		addDisallowedMob("TaintSpider");
		addDisallowedMob("TaintedChicken");
		addDisallowedMob("TaintedPig");
		addDisallowedMob("TaintedVillager");
		addDisallowedMob("TaintVillager");
		addDisallowedMob("Thaumcraft.TaintacleTiny");
		//addDisallowedMob("Thaumcraft.TaintSpore");
		addDisallowedMob("Thaumcraft.Golem");
		//addDisallowedMob("Thaumcraft.TaintSwarmer");
		addDisallowedMob("Thaumcraft.TravelingTrunk");
		addDisallowedMob("Thaumcraft.TaintedVillager");
		addDisallowedMob("Thaumcraft.TaintSpider");
		addDisallowedMob("Thaumcraft.TaintedChicken");
		addDisallowedMob("Thaumcraft.TaintedSheep");
		addDisallowedMob("Thaumcraft.TaintedCow");
		addDisallowedMob("Thaumcraft.TaintedPig");
		addDisallowedMob("Thaumcraft.TaintedCreeper");
		addDisallowedMob("Thaumcraft.CultistPortal");

		addDisallowedMob("TwilightForest.Hydra");
		addDisallowedMob("TwilightForest.Naga");
		addDisallowedMob("TwilightForest.HydraHead");
		addDisallowedMob("TwilightForest.Lich Minion");
		//addDisallowedMob("TwilightForest.Questing Ram");
		addDisallowedMob("TwilightForest.Knight Phantom");
		addDisallowedMob("TwilightForest.Twilight Lich");
		addDisallowedMob("TwilightForest.Tower Boss");
		addDisallowedMob("TwilightForest.Loyal Zombie");
		addDisallowedMob("TwilightForest.Minoshroom");
		addDisallowedMob("TwilightForest.Boggard");

		addDisallowedMob("OpenBlocks.Luggage");

		addDisallowedMob("Linkbook");

		addDisallowedMob("Robit");

		addDisallowedMob("etfuturum.ender_dragon");

		addDisallowedMob("DraconicEvolution.EnderDragon");

		addDisallowedMob("Thaumcraft.EldritchGolem");
		addDisallowedMob("Thaumcraft.CultistPortal");
		addDisallowedMob("Thaumcraft.CultistLeader");
		addDisallowedMob("arsmagica2.ShadowHelper");
		addDisallowedMob("arsmagica2.BossEnderGuardian");
		addDisallowedMob("arsmagica2.BossLightningGuardian");
		addDisallowedMob("arsmagica2.BossLifeGuardian");
		addDisallowedMob("arsmagica2.AirSled");
		addDisallowedMob("arsmagica2.DaBroom");
		addDisallowedMob("arsmagica2.BossFireGuardian");
		addDisallowedMob("arsmagica2.Shockwave");
		addDisallowedMob("arsmagica2.Whirlwind");
		addDisallowedMob("arsmagica2.BossAirGuardian");
		addDisallowedMob("arsmagica2.ThrownArm");
		addDisallowedMob("arsmagica2.BossWinterGuardian");
		addDisallowedMob("arsmagica2.BossWaterGuardian");
		addDisallowedMob("arsmagica2.ThrownRock");
		addDisallowedMob("arsmagica2.BossEarthGuardian");
		addDisallowedMob("arsmagica2.BossArcaneGuardian");
		addDisallowedMob("arsmagica2.ThrownSickle");
		addDisallowedMob("arsmagica2.BossNatureGuardian");
		addDisallowedMob("arsmagica2.RiftStorage");
		addDisallowedMob("arsmagica2.ZoneSpell");
		addDisallowedMob("arsmagica2.ManaVortex");
		addDisallowedMob("arsmagica2.SpellProjectile");
		addDisallowedMob("TwilightForest.Yeti Boss");
		addDisallowedMob("TwilightForest.Snow Queen");
		addDisallowedMob("TwilightForest.Apocalypse Cube");
		addDisallowedMob("TwilightForest.Adherent");
		addDisallowedMob("TwilightForest.Questing Ram");
		addDisallowedMob("HardcoreEnderExpansion.FireFiend");
		addDisallowedMob("HardcoreEnderExpansion.EnderDemon");
		addDisallowedMob("HardcoreEnderExpansion.EnderEye");
		addDisallowedMob("HardcoreEnderExpansion.Dragon");
		addDisallowedMob("TConstruct.Crystal");
		addDisallowedMob("witchery.hornedHuntsman");
		addDisallowedMob("witchery.babayaga");
		addDisallowedMob("witchery.death");
		addDisallowedMob("witchery.lordoftorment");
		addDisallowedMob("witchery.goblinmog");
		addDisallowedMob("witchery.goblingulg");
		addDisallowedMob("witchery.leonard");
		addDisallowedMob("witchery.lilith");
		addDisallowedMob("witchery.mirrorface");
		addDisallowedMob("witchery.corpse");
		addDisallowedMob("witchery.eye");
		addDisallowedMob("witchery.attackbat");
	}

	@Override
	public ElementTagCompound getRequiredEnergy() {
		return required.copy();
	}

	private static void addDisallowedMob(String name) {
		disallowedMobs.add(name);
	}

	private static void addDisallowedMob(Class <?extends EntityLiving> name) {
		addDisallowedMob((String)EntityList.classToStringMapping.get(name));
	}

	public static boolean isMobAllowed(String mob) {
		return !ReikaEntityHelper.isTameHostile(mob) && ReikaEntityHelper.isLivingMob(mob, false) && !disallowedMobs.contains(mob);
	}

	public static boolean isMobAllowed(Class<? extends EntityLiving> mob) {
		return isMobAllowed((String)EntityList.classToStringMapping.get(mob));
	}

	public String getSelectedMob() {
		return selectedMob;
	}

	public int[] getData() {
		return new int[]{minDelay, maxDelay, maxNear, spawnCount, spawnRange, activeRange};
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
		if (this.canConvert()) {
			progress.update();
			if (progress.checkCap()) {
				this.programSpawner();
			}
		}
		else {
			progress.reset();
		}
		progressTimer = progress.getTick();
	}

	public int getProgressScaled(int a) {
		return progressTimer * a / progress.getCap();
	}

	private boolean canConvert() {
		return energy.containsAtLeast(required) && this.isValidSpawner(inv[0]) && inv[1] == null;
	}

	private void programSpawner() {
		ItemStack is = ReikaItemHelper.getSizedItemStack(inv[0], 1);
		ReikaInventoryHelper.decrStack(0, inv);
		if (ReikaItemHelper.matchStackWithBlock(is, Blocks.mob_spawner)) {
			if (ReikaEntityHelper.hasID(selectedMob)) {
				int id = ReikaEntityHelper.mobNameToID(selectedMob);
				is.setItemDamage(id);
			}
		}
		else if (is.getItem() instanceof ItemSpawner) {
			ReikaSpawnerHelper.setSpawnerItemNBT(is, minDelay, maxDelay, maxNear, spawnCount, spawnRange, activeRange, true);
			ReikaSpawnerHelper.setSpawnerItemNBT(is, selectedMob, true);
		}
		else if (is.getItem() instanceof ProgrammableSpawner) {
			((ProgrammableSpawner)is.getItem()).setSpawnerType(is, this.getMobClass(selectedMob));
			((ProgrammableSpawner)is.getItem()).setSpawnerData(is, minDelay, maxDelay, maxNear, spawnCount, spawnRange, activeRange);
		}
		inv[1] = is;
		this.drainEnergy(required);
	}

	private Class<? extends EntityLiving> getMobClass(String name) {
		return (Class)EntityList.stringToClassMapping.get(name);
	}

	public void setMobType(String type) {
		selectedMob = type;
	}

	public void setData(int min, int max, int near, int count, int range, int active) {
		minDelay = min;
		maxDelay = max;
		maxNear = near;
		spawnCount = count;
		spawnRange = range;
		activeRange = active;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return i == 1;
	}

	@Override
	public int getSizeInventory() {
		return 2;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return i == 0 && this.isValidSpawner(itemstack);
	}

	private boolean isValidSpawner(ItemStack is) {
		if (is == null)
			return false;
		return ReikaItemHelper.matchStackWithBlock(is, Blocks.mob_spawner) || is.getItem() instanceof ItemSpawner || is.getItem() instanceof ProgrammableSpawner;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.REPROGRAMMER;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		selectedMob = NBT.getString("mob");

		minDelay = NBT.getInteger("min");
		maxDelay = NBT.getInteger("max");
		maxNear = NBT.getInteger("near");
		spawnCount = NBT.getInteger("count");
		spawnRange = NBT.getInteger("range");
		activeRange = NBT.getInteger("active");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		if (selectedMob != null && !selectedMob.isEmpty()) {
			NBT.setString("mob", selectedMob);
		}

		NBT.setInteger("min", minDelay);
		NBT.setInteger("max", maxDelay);
		NBT.setInteger("near", maxNear);
		NBT.setInteger("count", spawnCount);
		NBT.setInteger("range", spawnRange);
		NBT.setInteger("active", activeRange);
	}

	public boolean hasSpawner() {
		return this.isValidSpawner(inv[0]);
	}

	@Override
	public boolean isAcceptingColor(CrystalElement e) {
		return required.contains(e);
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return 10000;
	}

	@Override
	protected boolean canReceiveFrom(CrystalElement e, ForgeDirection dir) {
		return dir != ForgeDirection.DOWN;
	}

	@Override
	public float getOperationFraction() {
		return !this.canConvert() ? 0 : progress.getFraction();
	}

	@Override
	public OperationState getState() {
		return this.isValidSpawner(inv[0]) ? (energy.containsAtLeast(required) ? OperationState.RUNNING : OperationState.PENDING) : OperationState.INVALID;
	}

}
