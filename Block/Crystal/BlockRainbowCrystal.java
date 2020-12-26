/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Crystal;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Auxiliary.Interfaces.CrystalRenderedBlock;
import Reika.ChromatiCraft.Block.BlockCrystalTileNonCube;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaISBRH;
import Reika.ChromatiCraft.TileEntity.Auxiliary.TileEntityChromaCrystal;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockRainbowCrystal extends BlockCrystalTileNonCube implements CrystalRenderedBlock {

	private IIcon inertIcon;

	public BlockRainbowCrystal(Material mat) {
		super(mat);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final IIcon getIcon(int s, int meta) {
		return blockIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s) {
		return ((TileEntityChromaCrystal)iba.getTileEntity(x, y, z)).isConnected() ? blockIcon : inertIcon;
	}

	@Override
	public final void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("chromaticraft:crystal/chroma");
		inertIcon = ico.registerIcon("chromaticraft:crystal/chroma_inert");
	}

	@Override
	public final int getRenderType() {
		return ChromaISBRH.crystal.getRenderID();
	}

	@Override
	public final int getRenderBlockPass() {
		return 1;
	}

	@Override
	public boolean canRenderInPass(int pass) {
		ChromaISBRH.crystal.setRenderPass(pass);
		return pass <= 1;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		double[] v = ReikaDyeHelper.getRandomColor().getRedstoneParticleVelocityForColor();
		ReikaParticleHelper.spawnColoredParticles(world, x, y, z, v[0], v[1], v[2], 1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean addHitEffects(World world, MovingObjectPosition target, EffectRenderer effectRenderer)
	{
		Random rand = new Random();
		int x = target.blockX;
		int y = target.blockY;
		int z = target.blockZ;
		ReikaDyeHelper dye = ReikaDyeHelper.getRandomColor();
		double[] v = dye.getRedstoneParticleVelocityForColor();
		ReikaParticleHelper.spawnColoredParticles(world, x, y, z, v[0], v[1], v[2], 4);
		return false;
	}

	public boolean renderAllArms() {
		return true;
	}

	public boolean renderBase() {
		return true;
	}

	public BlockKey getBaseBlock(IBlockAccess iba, int x, int y, int z, ForgeDirection side) {
		return new BlockKey(ChromaBlocks.PYLONSTRUCT.getBlockInstance(), 0);
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity e) {
		return false;
	}

	public final int getTintColor(int meta) {
		return 0xffffff;
	}

}
