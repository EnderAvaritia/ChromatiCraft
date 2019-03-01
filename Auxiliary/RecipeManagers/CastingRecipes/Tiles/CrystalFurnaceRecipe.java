/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tiles;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonCastingRecipe;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class CrystalFurnaceRecipe extends PylonCastingRecipe {

	public CrystalFurnaceRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuraRequirement(CrystalElement.ORANGE, 8000);
		this.addAuraRequirement(CrystalElement.WHITE, 3000);
		this.addAuraRequirement(CrystalElement.PURPLE, 2000);

		this.addAuxItem(Blocks.furnace, 2, 0);
		this.addAuxItem(Blocks.furnace, -2, 0);
		this.addAuxItem(Blocks.furnace, 0, 2);
		this.addAuxItem(Blocks.furnace, 0, -2);

		this.addAuxItem(ChromaStacks.fieryIngot, -2, 2);
		this.addAuxItem(ChromaStacks.fieryIngot, -2, -2);
		this.addAuxItem(ChromaStacks.fieryIngot, 2, 2);
		this.addAuxItem(ChromaStacks.fieryIngot, 2, -2);

		this.addAuxItem(ChromaStacks.whiteShard, -4, -4);
		this.addAuxItem(ChromaStacks.whiteShard, 4, 4);
		this.addAuxItem(ChromaStacks.whiteShard, -4, 4);
		this.addAuxItem(ChromaStacks.whiteShard, 4, -4);

		this.addAuxItem(ChromaStacks.chromaIngot, 0, -4);
		this.addAuxItem(ChromaStacks.chromaIngot, 4, 0);
		this.addAuxItem(ChromaStacks.chromaIngot, 0, 4);
		this.addAuxItem(ChromaStacks.chromaIngot, -4, 0);

		this.addAuxItem(ChromaBlocks.HEATLAMP.getStackOf(), -2, -4);
		this.addAuxItem(ChromaBlocks.HEATLAMP.getStackOf(), 2, -4);
		this.addAuxItem(ChromaBlocks.HEATLAMP.getStackOf(), 4, -2);
		this.addAuxItem(ChromaBlocks.HEATLAMP.getStackOf(), 4, 2);
		this.addAuxItem(ChromaBlocks.HEATLAMP.getStackOf(), 2, 4);
		this.addAuxItem(ChromaBlocks.HEATLAMP.getStackOf(), -2, 4);
		this.addAuxItem(ChromaBlocks.HEATLAMP.getStackOf(), -4, 2);
		this.addAuxItem(ChromaBlocks.HEATLAMP.getStackOf(), -4, -2);
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 8;
	}

}
