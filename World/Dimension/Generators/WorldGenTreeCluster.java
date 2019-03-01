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

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.ChromaDimensionBiome;
import Reika.ChromatiCraft.Base.ChromaWorldGenerator;
import Reika.ChromatiCraft.World.Dimension.DimensionGenerators;
import Reika.DragonAPI.Auxiliary.WorldGenInterceptionRegistry;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Event.SetBlockEvent;
import Reika.DragonAPI.Interfaces.Registry.TreeType;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaPlantHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaTreeHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModRegistry.ModWoodList;

public class WorldGenTreeCluster extends ChromaWorldGenerator {

	private static final WeightedRandom<TreeGen> genRand = new WeightedRandom();
	private static final WeightedRandom<TreeShape> shapeRand = new WeightedRandom();

	public WorldGenTreeCluster(DimensionGenerators g, Random rand, long seed) {
		super(g, rand, seed);
	}

	static {
		for (int i = 0; i < TreeGen.genList.length; i++) {
			TreeGen gen = TreeGen.genList[i];
			if (gen.type.exists()) {
				genRand.addEntry(gen, gen.weight);
			}
		}

		for (int i = 0; i < TreeShape.shapeList.length; i++) {
			TreeShape gen = TreeShape.shapeList[i];
			shapeRand.addEntry(gen, gen.weight);
		}
	}

	@Override
	public float getGenerationChance(World world, int cx, int cz, ChromaDimensionBiome biome) {
		return 0.67F;
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		int n = 3+rand.nextInt(8);
		boolean flag = false;
		for (int i = 0; i < n; i++) {
			int rx = ReikaRandomHelper.getRandomPlusMinus(x, 16);
			int rz = ReikaRandomHelper.getRandomPlusMinus(z, 16);
			int ry = world.getTopSolidOrLiquidBlock(x, z)+1;
			if (this.canGenerateTree(world, rx, ry, rz)) {
				this.generateTree(world, rx, ry, rz, rand);
				flag = true;
			}
		}
		return flag;
	}

	private void generateTree(World world, int x, int y, int z, Random rand) {
		TreeGen type = genRand.getRandomEntry();
		type.generate(world, x, y, z, rand);
	}

	private boolean canGenerateTree(World world, int x, int y, int z) {
		if (!ReikaPlantHelper.SAPLING.canPlantAt(world, x, y, z))
			return false;
		for (int i = 0; i < 14; i++) {
			if (world.getBlock(x, y+i, z) != Blocks.air)
				return false;
		}
		return true;
	}

	private static enum TreeGen {
		OAK(ReikaTreeHelper.OAK, 10),
		BIRCH(ReikaTreeHelper.BIRCH, 8),
		LIGHT(ModWoodList.LIGHTED, 3),
		SILVERWOOD(ModWoodList.SILVERWOOD, 1),
		SAKURA(ModWoodList.SAKURA, 8),
		SILVERBELL(ModWoodList.SILVERBELL, 6),
		MAPLE(ModWoodList.MAPLE, 6),
		MAGIC(ModWoodList.MAGIC, 5),
		LOFTWOOD(ModWoodList.LOFTWOOD, 4),
		CHERRY(ModWoodList.CHERRY, 6);

		private final TreeType type;
		private final double weight;

		private static final TreeGen[] genList = values();

		private TreeGen(TreeType type, double w) {
			this.type = type;
			weight = w;
		}

		public void generate(World world, int x, int y, int z, Random rand) {
			if (type != ModWoodList.LIGHTED)
				WorldGenInterceptionRegistry.skipLighting = true;
			SetBlockEvent.eventEnabledPre = false;
			SetBlockEvent.eventEnabledPost = false;
			TreeShape shape = WorldGenTreeCluster.getRandomTreeShape(rand);
			shape.generate(world, x, y, z, rand, this);
			WorldGenInterceptionRegistry.skipLighting = false;
			SetBlockEvent.eventEnabledPre = true;
			SetBlockEvent.eventEnabledPost = true;
		}

	}

	public static TreeShape getRandomTreeShape(Random rand) {
		return shapeRand.getRandomEntry();
	}

	private static enum TreeShape {

		OAK(3, 2, 3, 1, 20D),
		HANG(5, 3, 4, 1, 15D),
		TALL(4, 4, 12, 3, 15D),
		WIDE(3, 2, 3, 2, 10D),
		NEEDLE(4, 2, 8, 4, 8D),
		GIANT(5, 3, 20, 10, 1D);

		private final double weight;
		private final int baseLog;
		private final int logVariation;
		private final int baseHeight;
		private final int heightVariation;

		private static final TreeShape[] shapeList = values();

		private TreeShape(int lh, int lhv, int h, int hv, double w) {
			weight = w;
			baseHeight = h;
			heightVariation = hv;
			baseLog = lh;
			logVariation = lhv;
		}

		public void generate(World world, int x, int y, int z, Random rand, TreeGen gen) {
			int dl = rand.nextInt(logVariation);
			int dy = rand.nextInt(heightVariation);
			int lgh = baseLog+dl;
			int h = lgh+baseHeight+dy;
			int lh = h-lgh;
			ItemStack log = gen.type.getItem();
			for (int i = 0; i < h; i++) {
				ReikaWorldHelper.setBlock(world, x, y+i, z, log);
			}
			y += lgh;
			switch(this) {
				case GIANT:
					this.generateGiant(world, x, y, z, rand, log, gen, lh);
					break;
				case HANG:
					this.generateHanging(world, x, y, z, rand, log, gen);
					break;
				case NEEDLE:
					this.generateNeedle(world, x, y, z, rand, log, gen, lh);
					break;
				case OAK:
					this.generateOak(world, x, y, z, rand, log, gen);
					break;
				case TALL:
					this.generateTall(world, x, y, z, rand, log, gen, lh);
					break;
				case WIDE:
					this.generateWide(world, x, y, z, rand, log, gen);
					break;
			}
		}

		private void generateGiant(World world, int x, int y, int z, Random rand, ItemStack log, TreeGen gen, int h) {
			for (int j = 0; j < h; j++) {
				int r = 1;
				if (j > 0 && j < h-2)
					r++;
				if (j > 2 && j < h-4)
					r++;
				if (j > 4 && j < h-7)
					r++;
				if (rand.nextBoolean() && j > 6 && j < h-10)
					r++;
				if (rand.nextBoolean() && j > 9 && j < h-12)
					r++;
				for (int i = -r; i <= r; i++) {
					for (int k = -r; k <= r; k++) {
						if ((i != 0 || k != 0) && (i*i+k*k <= r*r))
							ReikaWorldHelper.setBlock(world, x+i, y+j, z+k, this.getLeaf(rand, gen));
					}
				}
			}
			for (int i = 0; i < 2; i++)
				ReikaWorldHelper.setBlock(world, x, y+h+i, z, this.getLeaf(rand, gen));
		}

		private void generateHanging(World world, int x, int y, int z, Random rand, ItemStack log, TreeGen gen) {
			this.generateOak(world, x, y, z, rand, log, gen);
			for (int dy = y; dy >= y-3; dy--) {
				for (int k = -2; k <= 2; k++) {
					if (dy > y-3 || (k >= -1 && k <= 1)) {
						ReikaWorldHelper.setBlock(world, x-2, dy, z+k, this.getLeaf(rand, gen));
						ReikaWorldHelper.setBlock(world, x+2, dy, z+k, this.getLeaf(rand, gen));
						ReikaWorldHelper.setBlock(world, x+k, dy, z+2, this.getLeaf(rand, gen));
						ReikaWorldHelper.setBlock(world, x+k, dy, z-2, this.getLeaf(rand, gen));
					}
				}
			}
		}

		private void generateNeedle(World world, int x, int y, int z, Random rand, ItemStack log, TreeGen gen, int h) {
			for (int dy = y; dy < y+h; dy++) {
				int r = dy == y || dy == y+h ? 1 : 2;
				for (int i = 2; i < 6; i++) {
					ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
					for (int k = 1; k <= r; k++) {
						int dx = x+dir.offsetX*k;
						int dz = z+dir.offsetZ*k;
						ReikaWorldHelper.setBlock(world, dx, dy, dz, this.getLeaf(rand, gen));
					}
				}
				if (r > 1) {
					ReikaWorldHelper.setBlock(world, x+1, dy, z+1, this.getLeaf(rand, gen));
					ReikaWorldHelper.setBlock(world, x-1, dy, z+1, this.getLeaf(rand, gen));
					ReikaWorldHelper.setBlock(world, x+1, dy, z-1, this.getLeaf(rand, gen));
					ReikaWorldHelper.setBlock(world, x-1, dy, z-1, this.getLeaf(rand, gen));
				}
			}
			ReikaWorldHelper.setBlock(world, x, y+h, z, log);
			ReikaWorldHelper.setBlock(world, x+1, y+h, z, this.getLeaf(rand, gen));
			ReikaWorldHelper.setBlock(world, x-1, y+h, z, this.getLeaf(rand, gen));
			ReikaWorldHelper.setBlock(world, x, y+h, z+1, this.getLeaf(rand, gen));
			ReikaWorldHelper.setBlock(world, x, y+h, z-1, this.getLeaf(rand, gen));
			ReikaWorldHelper.setBlock(world, x, y+h+1, z, this.getLeaf(rand, gen));
		}

		private void generateOak(World world, int x, int y, int z, Random rand, ItemStack log, TreeGen gen) {
			for (int j = 0; j < 3; j++) {
				int r = j <= 1 ? 2 : 1;
				for (int i = -r; i <= r; i++) {
					for (int k = -r; k <= r; k++) {
						if (i != 0 || k != 0)
							ReikaWorldHelper.setBlock(world, x+i, y+j, z+k, this.getLeaf(rand, gen));
					}
				}
			}
			ReikaWorldHelper.setBlock(world, x, y+3, z, this.getLeaf(rand, gen));
			ReikaWorldHelper.setBlock(world, x+1, y+3, z, this.getLeaf(rand, gen));
			ReikaWorldHelper.setBlock(world, x-1, y+3, z, this.getLeaf(rand, gen));
			ReikaWorldHelper.setBlock(world, x, y+3, z+1, this.getLeaf(rand, gen));
			ReikaWorldHelper.setBlock(world, x, y+3, z-1, this.getLeaf(rand, gen));
		}

		private void generateTall(World world, int x, int y, int z, Random rand, ItemStack log, TreeGen gen, int h) {
			this.generateNeedle(world, x, y, z, rand, log, gen, h);
			for (int j = 1; j < h-1; j++) {
				int r = j >= 2 && j < h-2 ? 3 : 2;
				for (int i = -r; i <= r; i++) {
					for (int k = -r; k <= r; k++) {
						if (i != 0 || k != 0) {
							if (((i == 0 && Math.abs(k) <= 2) || (k == 0 && Math.abs(i) <= 2)) && (j == 3 || j == h-3)) {
								ReikaWorldHelper.setBlock(world, x+i, y+j, z+k, log);
							}
							else {
								if ((j == 2 || j == h-3) && ((Math.abs(k) == 2 && Math.abs(i) == 3) || (Math.abs(i) == 2 && Math.abs(k) == 3)))
									continue;
								if (Math.abs(i) != r || Math.abs(k) != r || (j >= h/2-1 && j <= h/2+1))
									ReikaWorldHelper.setBlock(world, x+i, y+j, z+k, this.getLeaf(rand, gen));
							}
						}
					}
				}
			}
			ReikaWorldHelper.setBlock(world, x, y+h, z, this.getLeaf(rand, gen));
			ReikaWorldHelper.setBlock(world, x+1, y+h, z, this.getLeaf(rand, gen));
			ReikaWorldHelper.setBlock(world, x-1, y+h, z, this.getLeaf(rand, gen));
			ReikaWorldHelper.setBlock(world, x, y+h, z+1, this.getLeaf(rand, gen));
			ReikaWorldHelper.setBlock(world, x, y+h, z-1, this.getLeaf(rand, gen));
		}

		private void generateWide(World world, int x, int y, int z, Random rand, ItemStack log, TreeGen gen) {
			for (int j = 0; j < 4; j++) {
				int r = j <= 1 ? 3 : j < 3 ? 2 : 1;
				for (int i = -r; i <= r; i++) {
					for (int k = -r; k <= r; k++) {
						if (i != 0 || k != 0 || j == 3)
							ReikaWorldHelper.setBlock(world, x+i, y+j, z+k, this.getLeaf(rand, gen));
					}
				}
			}
		}

		private ItemStack getLeaf(Random rand, TreeGen gen) {
			if (gen == TreeGen.LIGHT) {
				return new ItemStack(gen.type.getLeafID(), rand.nextInt(16));
			}
			else {
				return gen.type.getBasicLeaf();
			}
		}

	}

}
