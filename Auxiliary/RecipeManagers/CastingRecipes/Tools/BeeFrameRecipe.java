/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Tools;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.TempleCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;


public class BeeFrameRecipe extends TempleCastingRecipe {

	public BeeFrameRecipe(ItemStack out, IRecipe recipe) {
		super(out, recipe);

		this.addRuneRingRune(CrystalElement.GREEN);
		this.addRuneRingRune(CrystalElement.BLACK);
		this.addRuneRingRune(CrystalElement.LIGHTBLUE);
		this.addRuneRingRune(CrystalElement.GRAY);
	}

}
