/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;

public class BlockHoverBlock extends Block {

	public static enum HoverType {
		STATIONARY("Brake", 0xffff00, 0),
		DAMPER("Damper", 0xff00ff, -0.25),
		ELEVATE("Elevator", 0x2288ff, 0.125),
		FASTELEVATE("Rapid Elevator", 0x00ff00, 0.75);

		public final int renderColor;
		public final double velocityFactor;
		public final String desc;

		public static final HoverType[] list = values();

		private HoverType(String s, int c, double v) {
			renderColor = c;
			velocityFactor = v;
			desc = s;
		}

		public int getPermanentMeta() {
			return this.ordinal();
		}

		public int getDecayMeta() {
			return this.ordinal()+8;
		}

		public int getDecayingMeta() {
			return this.ordinal()+8+4;
		}

		public boolean movesUpwards() {
			return velocityFactor > 0;//this == ELEVATE || this == FASTELEVATE;
		}

		public void updateEntity(EntityPlayer e) {
			double dv = e.motionY-velocityFactor-0.3;
			if (dv < 0) {
				if (dv > 0.0625)
					e.motionY = velocityFactor;
				else
					e.motionY -= 0.25*dv;

				e.fallDistance = 0;
			}
		}

		public static HoverType getFromMeta(int meta) {
			return list[meta%4];
		}
	}

	public BlockHoverBlock(Material mat) {
		super(mat);

		this.setBlockUnbreakable();
		this.setCreativeTab(DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment() ? ChromatiCraft.tabChroma : null);
		this.setResistance(600000);
		this.setTickRandomly(true);
	}

	public boolean decay(int meta) {
		return meta >= 8;
	}

	public boolean doDecay(int meta) {
		return meta >= 12;
	}

	@Override
	public boolean isAir(IBlockAccess iba, int x, int y, int z) {
		return true;
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs cr, List li) {
		for (int i = 0; i < HoverType.list.length; i++) {
			HoverType h = HoverType.list[i];
			li.add(new ItemStack(this, 1, h.getPermanentMeta()));
			li.add(new ItemStack(this, 1, h.getDecayMeta()));
		}
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("chromaticraft:basic/hover");
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random r) {
		int meta = world.getBlockMetadata(x, y, z);
		if (this.decay(meta)) {
			if (this.doDecay(meta)) {
				world.setBlockToAir(x, y, z);
			}
			else {
				world.setBlockMetadataWithNotify(x, y, z, meta+4, 3);
				world.scheduleBlockUpdate(x, y, z, this, 20+r.nextInt(60));
			}
		}
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		world.scheduleBlockUpdate(x, y, z, this, 80);
	}

	@Override
	public int getRenderColor(int meta) {
		int color = HoverType.getFromMeta(meta).renderColor;
		if (this.doDecay(meta)) {
			color = ReikaColorAPI.getModifiedSat(color, 0.75F);
		}
		return color;
	}

	@Override
	public int colorMultiplier(IBlockAccess iba, int x, int y, int z) {
		return this.getRenderColor(iba.getBlockMetadata(x, y, z));
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random r) {
		if (world.getBlock(x, y-1, z) != this || r.nextInt(6) == 0) {
			ReikaParticleHelper.PORTAL.spawnAroundBlock(world, x, y, z, 1);
			ReikaParticleHelper.PORTAL.spawnAroundBlock(world, x, y-1, z, 1);
		}
	}

	@Override
	public Item getItemDropped(int meta, Random r, int fortune) {
		return null;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e) {
		if (e instanceof EntityPlayer && !e.isSneaking()) {
			int meta = world.getBlockMetadata(x, y, z);
			HoverType.getFromMeta(meta).updateEntity((EntityPlayer)e);
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public boolean canCollideCheck(int meta, boolean hitLiquid) {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return 0;//return ChromatiCraft.proxy.hoverRender;
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}
	/*
	@Override
	public boolean canRenderInPass(int pass) {
		HoverBlockRenderer.renderPass = pass;
		return pass <= 1;
	}
	 */
	@Override
	public boolean shouldSideBeRendered(IBlockAccess iba, int x, int y, int z, int s) {
		return s > 1 && iba.getBlock(x, y, z) != this;
	}
}
