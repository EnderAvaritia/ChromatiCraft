/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Generators;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.ChromaDimensionBiome;
import Reika.ChromatiCraft.Base.ChromaWorldGenerator;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.DimensionGenerators;
import Reika.DragonAPI.Libraries.Registry.ReikaPlantHelper;

public class WorldGenLightedTree extends ChromaWorldGenerator {

	public WorldGenLightedTree(DimensionGenerators g, Random rand, long seed) {
		super(g, rand, seed);
	}

	@Override
	public float getGenerationChance(World world, int cx, int cz, ChromaDimensionBiome biome) {
		switch(biome.biomeType) {
			case PLAINS:
				return 0.05F;
			case ISLANDS:
				return 0.8F;
			case CENTER:
				return 0.5F;
			case SPARKLE:
				return 0.1F;
			default:
				return 0.5F;
		}
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		return this.canGenerateTree(world, x, y, z) && TreeGen.genList[rand.nextInt(TreeGen.genList.length)].generate(world, x, y, z, rand);
	}

	private boolean canGenerateTree(World world, int x, int y, int z) {
		if (!ReikaPlantHelper.SAPLING.canPlantAt(world, x, y, z))
			return false;
		for (int i = 0; i < 8; i++) {
			if (world.getBlock(x, y+i, z) != Blocks.air)
				return false;
		}
		return true;
	}

	public static enum TreeGen {

		OAK(),
		TALL(),
		BALL();

		public static final TreeGen[] genList = values();

		public boolean generate(World world, int x, int y, int z, Random rand) {
			int h = rand.nextInt(4);
			for (int dy = 0; dy < h; dy++) {
				world.setBlock(x, y+dy, z, ChromaBlocks.GLOWLOG.getBlockInstance());
			}
			y += h;
			switch(this) {
				case BALL:
					generateBallTree(world, x, y, z, rand);
					break;
				case OAK:
					generateOakTree(world, x, y, z, rand);
					break;
				case TALL:
					generateTallTree(world, x, y, z, rand);
					break;
			}
			return true;
		}

		private static void generateBallTree(World world, int i, int j, int k, Random rand) {
			Block b = ChromaBlocks.GLOWLOG.getBlockInstance();
			i -= 5;
			k -= 5;
			placeLeaf(world, i + 0, j + 4, k + 4, rand);
			placeLeaf(world, i + 0, j + 4, k + 5, rand);
			placeLeaf(world, i + 0, j + 4, k + 6, rand);
			placeLeaf(world, i + 0, j + 5, k + 5, rand);
			placeLeaf(world, i + 1, j + 4, k + 2, rand);
			placeLeaf(world, i + 1, j + 4, k + 3, rand);
			placeLeaf(world, i + 1, j + 4, k + 4, rand);
			placeLeaf(world, i + 1, j + 4, k + 5, rand);
			placeLeaf(world, i + 1, j + 4, k + 6, rand);
			placeLeaf(world, i + 1, j + 4, k + 7, rand);
			placeLeaf(world, i + 1, j + 4, k + 8, rand);
			placeLeaf(world, i + 1, j + 5, k + 3, rand);
			placeLeaf(world, i + 1, j + 5, k + 4, rand);
			placeLeaf(world, i + 1, j + 5, k + 5, rand);
			placeLeaf(world, i + 1, j + 5, k + 6, rand);
			placeLeaf(world, i + 1, j + 6, k + 4, rand);
			placeLeaf(world, i + 1, j + 6, k + 5, rand);
			placeLeaf(world, i + 1, j + 6, k + 6, rand);
			placeLeaf(world, i + 1, j + 7, k + 5, rand);
			placeLeaf(world, i + 2, j + 4, k + 1, rand);
			placeLeaf(world, i + 2, j + 4, k + 2, rand);
			placeLeaf(world, i + 2, j + 4, k + 3, rand);
			placeLeaf(world, i + 2, j + 4, k + 7, rand);
			placeLeaf(world, i + 2, j + 4, k + 8, rand);
			placeLeaf(world, i + 2, j + 4, k + 9, rand);
			placeLeaf(world, i + 2, j + 5, k + 2, rand);
			placeLeaf(world, i + 2, j + 5, k + 3, rand);
			placeLeaf(world, i + 2, j + 5, k + 7, rand);
			placeLeaf(world, i + 2, j + 5, k + 8, rand);
			placeLeaf(world, i + 2, j + 6, k + 3, rand);
			placeLeaf(world, i + 2, j + 6, k + 7, rand);
			placeLeaf(world, i + 2, j + 7, k + 4, rand);
			placeLeaf(world, i + 2, j + 7, k + 5, rand);
			placeLeaf(world, i + 2, j + 7, k + 6, rand);
			placeLeaf(world, i + 2, j + 8, k + 5, rand);
			placeLeaf(world, i + 3, j + 4, k + 1, rand);
			placeLeaf(world, i + 3, j + 4, k + 2, rand);
			placeLeaf(world, i + 3, j + 4, k + 3, rand);
			placeLeaf(world, i + 3, j + 4, k + 4, rand);
			placeLeaf(world, i + 3, j + 4, k + 6, rand);
			placeLeaf(world, i + 3, j + 4, k + 7, rand);
			placeLeaf(world, i + 3, j + 4, k + 8, rand);
			placeLeaf(world, i + 3, j + 4, k + 9, rand);
			placeLeaf(world, i + 3, j + 5, k + 1, rand);
			placeLeaf(world, i + 3, j + 5, k + 2, rand);
			placeLeaf(world, i + 3, j + 5, k + 3, rand);
			placeLeaf(world, i + 3, j + 5, k + 4, rand);
			placeLeaf(world, i + 3, j + 5, k + 6, rand);
			placeLeaf(world, i + 3, j + 5, k + 7, rand);
			placeLeaf(world, i + 3, j + 5, k + 8, rand);
			placeLeaf(world, i + 3, j + 6, k + 2, rand);
			placeLeaf(world, i + 3, j + 6, k + 8, rand);
			placeLeaf(world, i + 3, j + 7, k + 3, rand);
			placeLeaf(world, i + 3, j + 7, k + 4, rand);
			placeLeaf(world, i + 3, j + 7, k + 6, rand);
			placeLeaf(world, i + 3, j + 7, k + 7, rand);
			placeLeaf(world, i + 3, j + 8, k + 4, rand);
			placeLeaf(world, i + 3, j + 8, k + 5, rand);
			placeLeaf(world, i + 3, j + 8, k + 6, rand);
			placeLeaf(world, i + 4, j + 4, k + 0, rand);
			placeLeaf(world, i + 4, j + 4, k + 1, rand);
			placeLeaf(world, i + 4, j + 4, k + 3, rand);
			placeLeaf(world, i + 4, j + 4, k + 4, rand);
			placeLeaf(world, i + 4, j + 4, k + 5, rand);
			placeLeaf(world, i + 4, j + 4, k + 6, rand);
			placeLeaf(world, i + 4, j + 4, k + 7, rand);
			placeLeaf(world, i + 4, j + 4, k + 9, rand);
			placeLeaf(world, i + 4, j + 4, k + 10, rand);
			placeLeaf(world, i + 4, j + 5, k + 1, rand);
			placeLeaf(world, i + 4, j + 5, k + 3, rand);
			placeLeaf(world, i + 4, j + 5, k + 4, rand);
			placeLeaf(world, i + 4, j + 5, k + 5, rand);
			placeLeaf(world, i + 4, j + 5, k + 6, rand);
			placeLeaf(world, i + 4, j + 5, k + 7, rand);
			placeLeaf(world, i + 4, j + 5, k + 9, rand);
			placeLeaf(world, i + 4, j + 6, k + 1, rand);
			placeLeaf(world, i + 4, j + 6, k + 5, rand);
			placeLeaf(world, i + 4, j + 6, k + 9, rand);
			placeLeaf(world, i + 4, j + 7, k + 2, rand);
			placeLeaf(world, i + 4, j + 7, k + 3, rand);
			placeLeaf(world, i + 4, j + 7, k + 5, rand);
			placeLeaf(world, i + 4, j + 7, k + 7, rand);
			placeLeaf(world, i + 4, j + 7, k + 8, rand);
			placeLeaf(world, i + 4, j + 8, k + 3, rand);
			placeLeaf(world, i + 4, j + 8, k + 4, rand);
			placeLeaf(world, i + 4, j + 8, k + 5, rand);
			placeLeaf(world, i + 4, j + 8, k + 6, rand);
			placeLeaf(world, i + 4, j + 8, k + 7, rand);
			placeLeaf(world, i + 4, j + 9, k + 5, rand);
			world.setBlock(i + 5, j + 0, k + 5, b);
			world.setBlock(i + 5, j + 1, k + 5, b);
			world.setBlock(i + 5, j + 2, k + 5, b);
			world.setBlock(i + 5, j + 3, k + 5, b);
			placeLeaf(world, i + 5, j + 4, k + 0, rand);
			placeLeaf(world, i + 5, j + 4, k + 1, rand);
			placeLeaf(world, i + 5, j + 4, k + 4, rand);
			world.setBlock(i + 5, j + 4, k + 5, b);
			placeLeaf(world, i + 5, j + 4, k + 6, rand);
			placeLeaf(world, i + 5, j + 4, k + 9, rand);
			placeLeaf(world, i + 5, j + 4, k + 10, rand);
			placeLeaf(world, i + 5, j + 5, k + 0, rand);
			placeLeaf(world, i + 5, j + 5, k + 1, rand);
			placeLeaf(world, i + 5, j + 5, k + 4, rand);
			world.setBlock(i + 5, j + 5, k + 5, b);
			placeLeaf(world, i + 5, j + 5, k + 6, rand);
			placeLeaf(world, i + 5, j + 5, k + 9, rand);
			placeLeaf(world, i + 5, j + 5, k + 10, rand);
			placeLeaf(world, i + 5, j + 6, k + 1, rand);
			placeLeaf(world, i + 5, j + 6, k + 4, rand);
			world.setBlock(i + 5, j + 6, k + 5, b);
			placeLeaf(world, i + 5, j + 6, k + 6, rand);
			placeLeaf(world, i + 5, j + 6, k + 9, rand);
			placeLeaf(world, i + 5, j + 7, k + 1, rand);
			placeLeaf(world, i + 5, j + 7, k + 2, rand);
			placeLeaf(world, i + 5, j + 7, k + 4, rand);
			world.setBlock(i + 5, j + 7, k + 5, b);
			placeLeaf(world, i + 5, j + 7, k + 6, rand);
			placeLeaf(world, i + 5, j + 7, k + 8, rand);
			placeLeaf(world, i + 5, j + 7, k + 9, rand);
			placeLeaf(world, i + 5, j + 8, k + 2, rand);
			placeLeaf(world, i + 5, j + 8, k + 3, rand);
			placeLeaf(world, i + 5, j + 8, k + 4, rand);
			world.setBlock(i + 5, j + 8, k + 5, b);
			placeLeaf(world, i + 5, j + 8, k + 6, rand);
			placeLeaf(world, i + 5, j + 8, k + 7, rand);
			placeLeaf(world, i + 5, j + 8, k + 8, rand);
			placeLeaf(world, i + 5, j + 9, k + 4, rand);
			placeLeaf(world, i + 5, j + 9, k + 5, rand);
			placeLeaf(world, i + 5, j + 9, k + 6, rand);
			placeLeaf(world, i + 6, j + 4, k + 0, rand);
			placeLeaf(world, i + 6, j + 4, k + 1, rand);
			placeLeaf(world, i + 6, j + 4, k + 3, rand);
			placeLeaf(world, i + 6, j + 4, k + 4, rand);
			placeLeaf(world, i + 6, j + 4, k + 5, rand);
			placeLeaf(world, i + 6, j + 4, k + 6, rand);
			placeLeaf(world, i + 6, j + 4, k + 7, rand);
			placeLeaf(world, i + 6, j + 4, k + 9, rand);
			placeLeaf(world, i + 6, j + 4, k + 10, rand);
			placeLeaf(world, i + 6, j + 5, k + 1, rand);
			placeLeaf(world, i + 6, j + 5, k + 3, rand);
			placeLeaf(world, i + 6, j + 5, k + 4, rand);
			placeLeaf(world, i + 6, j + 5, k + 5, rand);
			placeLeaf(world, i + 6, j + 5, k + 6, rand);
			placeLeaf(world, i + 6, j + 5, k + 7, rand);
			placeLeaf(world, i + 6, j + 5, k + 9, rand);
			placeLeaf(world, i + 6, j + 6, k + 1, rand);
			placeLeaf(world, i + 6, j + 6, k + 5, rand);
			placeLeaf(world, i + 6, j + 6, k + 9, rand);
			placeLeaf(world, i + 6, j + 7, k + 2, rand);
			placeLeaf(world, i + 6, j + 7, k + 3, rand);
			placeLeaf(world, i + 6, j + 7, k + 5, rand);
			placeLeaf(world, i + 6, j + 7, k + 7, rand);
			placeLeaf(world, i + 6, j + 7, k + 8, rand);
			placeLeaf(world, i + 6, j + 8, k + 3, rand);
			placeLeaf(world, i + 6, j + 8, k + 4, rand);
			placeLeaf(world, i + 6, j + 8, k + 5, rand);
			placeLeaf(world, i + 6, j + 8, k + 6, rand);
			placeLeaf(world, i + 6, j + 8, k + 7, rand);
			placeLeaf(world, i + 6, j + 9, k + 5, rand);
			placeLeaf(world, i + 7, j + 4, k + 1, rand);
			placeLeaf(world, i + 7, j + 4, k + 2, rand);
			placeLeaf(world, i + 7, j + 4, k + 3, rand);
			placeLeaf(world, i + 7, j + 4, k + 4, rand);
			placeLeaf(world, i + 7, j + 4, k + 6, rand);
			placeLeaf(world, i + 7, j + 4, k + 7, rand);
			placeLeaf(world, i + 7, j + 4, k + 8, rand);
			placeLeaf(world, i + 7, j + 4, k + 9, rand);
			placeLeaf(world, i + 7, j + 5, k + 1, rand);
			placeLeaf(world, i + 7, j + 5, k + 2, rand);
			placeLeaf(world, i + 7, j + 5, k + 3, rand);
			placeLeaf(world, i + 7, j + 5, k + 4, rand);
			placeLeaf(world, i + 7, j + 5, k + 6, rand);
			placeLeaf(world, i + 7, j + 5, k + 7, rand);
			placeLeaf(world, i + 7, j + 5, k + 8, rand);
			placeLeaf(world, i + 7, j + 5, k + 9, rand);
			placeLeaf(world, i + 7, j + 6, k + 2, rand);
			placeLeaf(world, i + 7, j + 6, k + 8, rand);
			placeLeaf(world, i + 7, j + 7, k + 3, rand);
			placeLeaf(world, i + 7, j + 7, k + 4, rand);
			placeLeaf(world, i + 7, j + 7, k + 6, rand);
			placeLeaf(world, i + 7, j + 7, k + 7, rand);
			placeLeaf(world, i + 7, j + 8, k + 4, rand);
			placeLeaf(world, i + 7, j + 8, k + 5, rand);
			placeLeaf(world, i + 7, j + 8, k + 6, rand);
			placeLeaf(world, i + 8, j + 4, k + 1, rand);
			placeLeaf(world, i + 8, j + 4, k + 2, rand);
			placeLeaf(world, i + 8, j + 4, k + 3, rand);
			placeLeaf(world, i + 8, j + 4, k + 7, rand);
			placeLeaf(world, i + 8, j + 4, k + 8, rand);
			placeLeaf(world, i + 8, j + 4, k + 9, rand);
			placeLeaf(world, i + 8, j + 5, k + 2, rand);
			placeLeaf(world, i + 8, j + 5, k + 3, rand);
			placeLeaf(world, i + 8, j + 5, k + 7, rand);
			placeLeaf(world, i + 8, j + 5, k + 8, rand);
			placeLeaf(world, i + 8, j + 6, k + 3, rand);
			placeLeaf(world, i + 8, j + 6, k + 7, rand);
			placeLeaf(world, i + 8, j + 7, k + 4, rand);
			placeLeaf(world, i + 8, j + 7, k + 5, rand);
			placeLeaf(world, i + 8, j + 7, k + 6, rand);
			placeLeaf(world, i + 8, j + 8, k + 5, rand);
			placeLeaf(world, i + 9, j + 4, k + 2, rand);
			placeLeaf(world, i + 9, j + 4, k + 3, rand);
			placeLeaf(world, i + 9, j + 4, k + 4, rand);
			placeLeaf(world, i + 9, j + 4, k + 5, rand);
			placeLeaf(world, i + 9, j + 4, k + 6, rand);
			placeLeaf(world, i + 9, j + 4, k + 7, rand);
			placeLeaf(world, i + 9, j + 4, k + 8, rand);
			placeLeaf(world, i + 9, j + 5, k + 3, rand);
			placeLeaf(world, i + 9, j + 5, k + 4, rand);
			placeLeaf(world, i + 9, j + 5, k + 5, rand);
			placeLeaf(world, i + 9, j + 5, k + 6, rand);
			placeLeaf(world, i + 9, j + 5, k + 7, rand);
			placeLeaf(world, i + 9, j + 6, k + 4, rand);
			placeLeaf(world, i + 9, j + 6, k + 5, rand);
			placeLeaf(world, i + 9, j + 6, k + 6, rand);
			placeLeaf(world, i + 9, j + 7, k + 5, rand);
			placeLeaf(world, i + 10, j + 4, k + 4, rand);
			placeLeaf(world, i + 10, j + 4, k + 5, rand);
			placeLeaf(world, i + 10, j + 4, k + 6, rand);
			placeLeaf(world, i + 10, j + 5, k + 5, rand);
		}

		private static void generateTallTree(World world, int i, int j, int k, Random rand) {
			Block b = ChromaBlocks.GLOWLOG.getBlockInstance();
			i -= 2;
			k -= 2;
			placeLeaf(world, i + 0, j + 4, k + 2, rand);
			placeLeaf(world, i + 0, j + 5, k + 1, rand);
			placeLeaf(world, i + 0, j + 5, k + 2, rand);
			placeLeaf(world, i + 0, j + 5, k + 3, rand);
			placeLeaf(world, i + 0, j + 6, k + 1, rand);
			placeLeaf(world, i + 0, j + 6, k + 2, rand);
			placeLeaf(world, i + 0, j + 6, k + 3, rand);
			placeLeaf(world, i + 0, j + 7, k + 1, rand);
			placeLeaf(world, i + 0, j + 7, k + 2, rand);
			placeLeaf(world, i + 0, j + 7, k + 3, rand);
			placeLeaf(world, i + 0, j + 8, k + 1, rand);
			placeLeaf(world, i + 0, j + 8, k + 2, rand);
			placeLeaf(world, i + 0, j + 8, k + 3, rand);
			placeLeaf(world, i + 0, j + 9, k + 2, rand);
			placeLeaf(world, i + 1, j + 3, k + 2, rand);
			placeLeaf(world, i + 1, j + 4, k + 1, rand);
			placeLeaf(world, i + 1, j + 4, k + 2, rand);
			placeLeaf(world, i + 1, j + 4, k + 3, rand);
			placeLeaf(world, i + 1, j + 5, k + 0, rand);
			placeLeaf(world, i + 1, j + 5, k + 1, rand);
			placeLeaf(world, i + 1, j + 5, k + 2, rand);
			placeLeaf(world, i + 1, j + 5, k + 3, rand);
			placeLeaf(world, i + 1, j + 5, k + 4, rand);
			placeLeaf(world, i + 1, j + 6, k + 0, rand);
			placeLeaf(world, i + 1, j + 6, k + 1, rand);
			placeLeaf(world, i + 1, j + 6, k + 2, rand);
			placeLeaf(world, i + 1, j + 6, k + 3, rand);
			placeLeaf(world, i + 1, j + 6, k + 4, rand);
			placeLeaf(world, i + 1, j + 7, k + 0, rand);
			placeLeaf(world, i + 1, j + 7, k + 1, rand);
			placeLeaf(world, i + 1, j + 7, k + 2, rand);
			placeLeaf(world, i + 1, j + 7, k + 3, rand);
			placeLeaf(world, i + 1, j + 7, k + 4, rand);
			placeLeaf(world, i + 1, j + 8, k + 0, rand);
			placeLeaf(world, i + 1, j + 8, k + 1, rand);
			placeLeaf(world, i + 1, j + 8, k + 2, rand);
			placeLeaf(world, i + 1, j + 8, k + 3, rand);
			placeLeaf(world, i + 1, j + 8, k + 4, rand);
			placeLeaf(world, i + 1, j + 9, k + 1, rand);
			placeLeaf(world, i + 1, j + 9, k + 2, rand);
			placeLeaf(world, i + 1, j + 9, k + 3, rand);
			placeLeaf(world, i + 1, j + 10, k + 2, rand);
			world.setBlock(i + 2, j + 0, k + 2, b);
			world.setBlock(i + 2, j + 1, k + 2, b);
			world.setBlock(i + 2, j + 2, k + 2, b);
			placeLeaf(world, i + 2, j + 3, k + 1, rand);
			world.setBlock(i + 2, j + 3, k + 2, b);
			placeLeaf(world, i + 2, j + 3, k + 3, rand);
			placeLeaf(world, i + 2, j + 4, k + 0, rand);
			placeLeaf(world, i + 2, j + 4, k + 1, rand);
			world.setBlock(i + 2, j + 4, k + 2, b);
			placeLeaf(world, i + 2, j + 4, k + 3, rand);
			placeLeaf(world, i + 2, j + 4, k + 4, rand);
			placeLeaf(world, i + 2, j + 5, k + 0, rand);
			placeLeaf(world, i + 2, j + 5, k + 1, rand);
			world.setBlock(i + 2, j + 5, k + 2, b);
			placeLeaf(world, i + 2, j + 5, k + 3, rand);
			placeLeaf(world, i + 2, j + 5, k + 4, rand);
			placeLeaf(world, i + 2, j + 6, k + 0, rand);
			placeLeaf(world, i + 2, j + 6, k + 1, rand);
			world.setBlock(i + 2, j + 6, k + 2, b);
			placeLeaf(world, i + 2, j + 6, k + 3, rand);
			placeLeaf(world, i + 2, j + 6, k + 4, rand);
			placeLeaf(world, i + 2, j + 7, k + 0, rand);
			placeLeaf(world, i + 2, j + 7, k + 1, rand);
			world.setBlock(i + 2, j + 7, k + 2, b);
			placeLeaf(world, i + 2, j + 7, k + 3, rand);
			placeLeaf(world, i + 2, j + 7, k + 4, rand);
			placeLeaf(world, i + 2, j + 8, k + 0, rand);
			placeLeaf(world, i + 2, j + 8, k + 1, rand);
			world.setBlock(i + 2, j + 8, k + 2, b);
			placeLeaf(world, i + 2, j + 8, k + 3, rand);
			placeLeaf(world, i + 2, j + 8, k + 4, rand);
			placeLeaf(world, i + 2, j + 9, k + 0, rand);
			placeLeaf(world, i + 2, j + 9, k + 1, rand);
			world.setBlock(i + 2, j + 9, k + 2, b);
			placeLeaf(world, i + 2, j + 9, k + 3, rand);
			placeLeaf(world, i + 2, j + 9, k + 4, rand);
			placeLeaf(world, i + 2, j + 10, k + 1, rand);
			placeLeaf(world, i + 2, j + 10, k + 2, rand);
			placeLeaf(world, i + 2, j + 10, k + 3, rand);
			placeLeaf(world, i + 3, j + 3, k + 2, rand);
			placeLeaf(world, i + 3, j + 4, k + 1, rand);
			placeLeaf(world, i + 3, j + 4, k + 2, rand);
			placeLeaf(world, i + 3, j + 4, k + 3, rand);
			placeLeaf(world, i + 3, j + 5, k + 0, rand);
			placeLeaf(world, i + 3, j + 5, k + 1, rand);
			placeLeaf(world, i + 3, j + 5, k + 2, rand);
			placeLeaf(world, i + 3, j + 5, k + 3, rand);
			placeLeaf(world, i + 3, j + 5, k + 4, rand);
			placeLeaf(world, i + 3, j + 6, k + 0, rand);
			placeLeaf(world, i + 3, j + 6, k + 1, rand);
			placeLeaf(world, i + 3, j + 6, k + 2, rand);
			placeLeaf(world, i + 3, j + 6, k + 3, rand);
			placeLeaf(world, i + 3, j + 6, k + 4, rand);
			placeLeaf(world, i + 3, j + 7, k + 0, rand);
			placeLeaf(world, i + 3, j + 7, k + 1, rand);
			placeLeaf(world, i + 3, j + 7, k + 2, rand);
			placeLeaf(world, i + 3, j + 7, k + 3, rand);
			placeLeaf(world, i + 3, j + 7, k + 4, rand);
			placeLeaf(world, i + 3, j + 8, k + 0, rand);
			placeLeaf(world, i + 3, j + 8, k + 1, rand);
			placeLeaf(world, i + 3, j + 8, k + 2, rand);
			placeLeaf(world, i + 3, j + 8, k + 3, rand);
			placeLeaf(world, i + 3, j + 8, k + 4, rand);
			placeLeaf(world, i + 3, j + 9, k + 1, rand);
			placeLeaf(world, i + 3, j + 9, k + 2, rand);
			placeLeaf(world, i + 3, j + 9, k + 3, rand);
			placeLeaf(world, i + 3, j + 10, k + 2, rand);
			placeLeaf(world, i + 4, j + 4, k + 2, rand);
			placeLeaf(world, i + 4, j + 5, k + 1, rand);
			placeLeaf(world, i + 4, j + 5, k + 2, rand);
			placeLeaf(world, i + 4, j + 5, k + 3, rand);
			placeLeaf(world, i + 4, j + 6, k + 1, rand);
			placeLeaf(world, i + 4, j + 6, k + 2, rand);
			placeLeaf(world, i + 4, j + 6, k + 3, rand);
			placeLeaf(world, i + 4, j + 7, k + 1, rand);
			placeLeaf(world, i + 4, j + 7, k + 2, rand);
			placeLeaf(world, i + 4, j + 7, k + 3, rand);
			placeLeaf(world, i + 4, j + 8, k + 1, rand);
			placeLeaf(world, i + 4, j + 8, k + 2, rand);
			placeLeaf(world, i + 4, j + 8, k + 3, rand);
			placeLeaf(world, i + 4, j + 9, k + 2, rand);
		}

		private static void generateOakTree(World world, int i, int j, int k, Random rand) {
			Block b = ChromaBlocks.GLOWLOG.getBlockInstance();
			i -= 3;
			k -= 3;
			placeLeaf(world, i + 0, j + 3, k + 1, rand);
			placeLeaf(world, i + 0, j + 3, k + 2, rand);
			placeLeaf(world, i + 0, j + 3, k + 3, rand);
			placeLeaf(world, i + 0, j + 3, k + 4, rand);
			placeLeaf(world, i + 0, j + 3, k + 5, rand);
			placeLeaf(world, i + 0, j + 4, k + 1, rand);
			placeLeaf(world, i + 0, j + 4, k + 2, rand);
			placeLeaf(world, i + 0, j + 4, k + 3, rand);
			placeLeaf(world, i + 0, j + 4, k + 4, rand);
			placeLeaf(world, i + 0, j + 4, k + 5, rand);
			placeLeaf(world, i + 1, j + 3, k + 0, rand);
			placeLeaf(world, i + 1, j + 3, k + 1, rand);
			placeLeaf(world, i + 1, j + 3, k + 5, rand);
			placeLeaf(world, i + 1, j + 3, k + 6, rand);
			placeLeaf(world, i + 1, j + 4, k + 0, rand);
			placeLeaf(world, i + 1, j + 4, k + 1, rand);
			placeLeaf(world, i + 1, j + 4, k + 2, rand);
			placeLeaf(world, i + 1, j + 4, k + 3, rand);
			placeLeaf(world, i + 1, j + 4, k + 4, rand);
			placeLeaf(world, i + 1, j + 4, k + 5, rand);
			placeLeaf(world, i + 1, j + 4, k + 6, rand);
			placeLeaf(world, i + 1, j + 5, k + 1, rand);
			placeLeaf(world, i + 1, j + 5, k + 2, rand);
			placeLeaf(world, i + 1, j + 5, k + 3, rand);
			placeLeaf(world, i + 1, j + 5, k + 4, rand);
			placeLeaf(world, i + 1, j + 5, k + 5, rand);
			placeLeaf(world, i + 2, j + 3, k + 0, rand);
			placeLeaf(world, i + 2, j + 3, k + 6, rand);
			placeLeaf(world, i + 2, j + 4, k + 0, rand);
			placeLeaf(world, i + 2, j + 4, k + 1, rand);
			placeLeaf(world, i + 2, j + 4, k + 2, rand);
			placeLeaf(world, i + 2, j + 4, k + 3, rand);
			placeLeaf(world, i + 2, j + 4, k + 4, rand);
			placeLeaf(world, i + 2, j + 4, k + 5, rand);
			placeLeaf(world, i + 2, j + 4, k + 6, rand);
			placeLeaf(world, i + 2, j + 5, k + 1, rand);
			placeLeaf(world, i + 2, j + 5, k + 2, rand);
			placeLeaf(world, i + 2, j + 5, k + 3, rand);
			placeLeaf(world, i + 2, j + 5, k + 4, rand);
			placeLeaf(world, i + 2, j + 5, k + 5, rand);
			placeLeaf(world, i + 2, j + 6, k + 2, rand);
			placeLeaf(world, i + 2, j + 6, k + 3, rand);
			placeLeaf(world, i + 2, j + 6, k + 4, rand);
			placeLeaf(world, i + 2, j + 7, k + 3, rand);
			world.setBlock(i + 3, j + 0, k + 3, b);
			world.setBlock(i + 3, j + 1, k + 3, b);
			world.setBlock(i + 3, j + 2, k + 3, b);
			placeLeaf(world, i + 3, j + 3, k + 0, rand);
			world.setBlock(i + 3, j + 3, k + 3, b);
			placeLeaf(world, i + 3, j + 3, k + 6, rand);
			placeLeaf(world, i + 3, j + 4, k + 0, rand);
			placeLeaf(world, i + 3, j + 4, k + 1, rand);
			placeLeaf(world, i + 3, j + 4, k + 2, rand);
			world.setBlock(i + 3, j + 4, k + 3, b);
			placeLeaf(world, i + 3, j + 4, k + 4, rand);
			placeLeaf(world, i + 3, j + 4, k + 5, rand);
			placeLeaf(world, i + 3, j + 4, k + 6, rand);
			placeLeaf(world, i + 3, j + 5, k + 1, rand);
			placeLeaf(world, i + 3, j + 5, k + 2, rand);
			world.setBlock(i + 3, j + 5, k + 3, b);
			placeLeaf(world, i + 3, j + 5, k + 4, rand);
			placeLeaf(world, i + 3, j + 5, k + 5, rand);
			placeLeaf(world, i + 3, j + 6, k + 2, rand);
			world.setBlock(i + 3, j + 6, k + 3, b);
			placeLeaf(world, i + 3, j + 6, k + 4, rand);
			placeLeaf(world, i + 3, j + 7, k + 2, rand);
			placeLeaf(world, i + 3, j + 7, k + 3, rand);
			placeLeaf(world, i + 3, j + 7, k + 4, rand);
			placeLeaf(world, i + 4, j + 3, k + 0, rand);
			placeLeaf(world, i + 4, j + 3, k + 6, rand);
			placeLeaf(world, i + 4, j + 4, k + 0, rand);
			placeLeaf(world, i + 4, j + 4, k + 1, rand);
			placeLeaf(world, i + 4, j + 4, k + 2, rand);
			placeLeaf(world, i + 4, j + 4, k + 3, rand);
			placeLeaf(world, i + 4, j + 4, k + 4, rand);
			placeLeaf(world, i + 4, j + 4, k + 5, rand);
			placeLeaf(world, i + 4, j + 4, k + 6, rand);
			placeLeaf(world, i + 4, j + 5, k + 1, rand);
			placeLeaf(world, i + 4, j + 5, k + 2, rand);
			placeLeaf(world, i + 4, j + 5, k + 3, rand);
			placeLeaf(world, i + 4, j + 5, k + 4, rand);
			placeLeaf(world, i + 4, j + 5, k + 5, rand);
			placeLeaf(world, i + 4, j + 6, k + 2, rand);
			placeLeaf(world, i + 4, j + 6, k + 3, rand);
			placeLeaf(world, i + 4, j + 6, k + 4, rand);
			placeLeaf(world, i + 4, j + 7, k + 3, rand);
			placeLeaf(world, i + 5, j + 3, k + 0, rand);
			placeLeaf(world, i + 5, j + 3, k + 1, rand);
			placeLeaf(world, i + 5, j + 3, k + 5, rand);
			placeLeaf(world, i + 5, j + 3, k + 6, rand);
			placeLeaf(world, i + 5, j + 4, k + 0, rand);
			placeLeaf(world, i + 5, j + 4, k + 1, rand);
			placeLeaf(world, i + 5, j + 4, k + 2, rand);
			placeLeaf(world, i + 5, j + 4, k + 3, rand);
			placeLeaf(world, i + 5, j + 4, k + 4, rand);
			placeLeaf(world, i + 5, j + 4, k + 5, rand);
			placeLeaf(world, i + 5, j + 4, k + 6, rand);
			placeLeaf(world, i + 5, j + 5, k + 1, rand);
			placeLeaf(world, i + 5, j + 5, k + 2, rand);
			placeLeaf(world, i + 5, j + 5, k + 3, rand);
			placeLeaf(world, i + 5, j + 5, k + 4, rand);
			placeLeaf(world, i + 5, j + 5, k + 5, rand);
			placeLeaf(world, i + 6, j + 3, k + 1, rand);
			placeLeaf(world, i + 6, j + 3, k + 2, rand);
			placeLeaf(world, i + 6, j + 3, k + 3, rand);
			placeLeaf(world, i + 6, j + 3, k + 4, rand);
			placeLeaf(world, i + 6, j + 3, k + 5, rand);
			placeLeaf(world, i + 6, j + 4, k + 1, rand);
			placeLeaf(world, i + 6, j + 4, k + 2, rand);
			placeLeaf(world, i + 6, j + 4, k + 3, rand);
			placeLeaf(world, i + 6, j + 4, k + 4, rand);
			placeLeaf(world, i + 6, j + 4, k + 5, rand);
		}

		private static void placeLeaf(World world, int x, int y, int z, Random rand) {
			if (world.getBlock(x, y, z).canBeReplacedByLeaves(world, x, y, z))
				world.setBlock(x, y, z, ChromaBlocks.GLOWLEAF.getBlockInstance(), rand.nextInt(5), 3);
		}

	}

}
