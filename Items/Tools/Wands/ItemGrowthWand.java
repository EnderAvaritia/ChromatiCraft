/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools.Wands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeMap;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ItemWandBase;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.ReikaEnchantmentHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class ItemGrowthWand extends ItemWandBase {

	private static final GrowthTicker ticker = new GrowthTicker();

	public ItemGrowthWand(int index) {
		super(index);

		this.addEnergyCost(CrystalElement.BLACK, 2);
		this.addEnergyCost(CrystalElement.PURPLE, 10);
		this.addEnergyCost(CrystalElement.GREEN, 40);
		TickRegistry.instance.registerTickHandler(ticker);
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float f1, float f2, float f3) {
		if (this.sufficientEnergy(ep)) {
			int r = this.getRange(ep);
			ticker.addLocation(ep, world, x, y, z, r, r/2, ReikaEnchantmentHelper.getEnchantmentLevel(Enchantment.power, is) > 0);
			ChromaSounds.USE.playSoundAtBlock(world, x, y, z);
			return true;
		}
		return false;
	}

	private int getRange(EntityPlayer ep) {
		return canUseBoostedEffect(ep) ? 8 : 4;
	}

	public static class GrowthTicker implements TickHandler {

		private final Collection<GrowthLocation> locations = new ArrayList();
		private boolean isIterating;
		private Collection<GrowthLocation> holdingCache = new ArrayList();

		@Override
		public void tick(TickType type, Object... tickData) {
			World world = (World)tickData[0];
			isIterating = true;
			Iterator<GrowthLocation> it = locations.iterator();
			while (it.hasNext()) {
				GrowthLocation g = it.next();
				if (g.origin.dimensionID == world.provider.dimensionId) {
					g.tick(world);
					if (g.isEmpty())
						it.remove();
				}
			}
			locations.addAll(holdingCache);
			holdingCache.clear();
			isIterating = false;
		}

		private void addLocation(EntityPlayer ep, World world, int x, int y, int z, int w, int h, boolean e) {
			GrowthLocation g = new GrowthLocation(ep, world, x, y, z, w, h, e);
			if (isIterating)
				holdingCache.add(g);
			else
				locations.add(g);
		}

		@Override
		public EnumSet<TickType> getType() {
			return EnumSet.of(TickType.WORLD);
		}

		@Override
		public boolean canFire(Phase p) {
			return p == Phase.START;
		}

		@Override
		public String getLabel() {
			return "Growth Wand";
		}

	}

	private static class GrowthLocation {

		private final WorldLocation origin;
		private final EntityPlayer player;

		private static final Random rand = new Random();
		private final boolean enhanced;

		private TreeMap<Integer, ArrayList<Coordinate>> locations = new TreeMap();

		private GrowthLocation(EntityPlayer ep, World world, int x, int y, int z, int r1, int r2, boolean e) {
			origin = new WorldLocation(world, x, y, z);
			player = ep;
			for (int i = -r1; i <= r1; i++) {
				for (int k = -r1; k <= r1; k++) {
					this.addLocation(x+i, y, z+k, i*i+k*k);
				}
			}
			enhanced = e;
		}

		public boolean isEmpty() {
			return locations.isEmpty();
		}

		private void addLocation(int x, int y, int z, int d) {
			ArrayList<Coordinate> li = locations.get(d);
			if (li == null) {
				li = new ArrayList();
				locations.put(d, li);
			}
			li.add(new Coordinate(x, y, z));
		}

		private void removeLocation(Coordinate c, int idx, ArrayList<Coordinate> li, int d) {
			li.remove(idx);
			if (li.isEmpty()) {
				locations.remove(d);
			}
		}

		private void tick(World world) {
			int d = locations.firstKey();
			ArrayList<Coordinate> li = locations.get(d);
			int idx = rand.nextInt(li.size());
			Coordinate c = li.get(idx);
			if (c != null) {
				int n = 1+rand.nextInt(6);
				if (enhanced)
					n = n*(1+n);
				for (int i = 0; i < n; i++) {
					c.updateTick(world);
					PacketTarget pt = new PacketTarget.RadiusTarget(world, c, 16);
					ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.GROWTH.ordinal(), pt, c.xCoord, c.yCoord, c.zCoord);
				}
				this.removeLocation(c, idx, li, d);
				int f = enhanced ? 16 : 1;
				((ItemGrowthWand)ChromaItems.GROWTH.getItemInstance()).drainPlayer(player, f);
			}
		}

	}

}
