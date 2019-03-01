/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Blocks;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;

public class ChromaFlowerRecipe extends CastingRecipe {

	public ChromaFlowerRecipe(ItemStack out, IRecipe recipe) {
		super(out, recipe);
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 4;
	}

	@Override
	public float getPenaltyMultiplier() {
		return 0.25F;
	}

	@Override
	public int getPenaltyThreshold(){
		return super.getPenaltyThreshold()*4/5;
	}

}
