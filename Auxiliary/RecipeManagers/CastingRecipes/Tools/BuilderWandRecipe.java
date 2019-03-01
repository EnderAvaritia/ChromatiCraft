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

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;

public class BuilderWandRecipe extends MultiBlockCastingRecipe {

	public BuilderWandRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem("stickWood", -2, -2);
		this.addAuxItem("stickWood", 2, 2);
		this.addAuxItem("stickWood", -2, 2);
		this.addAuxItem("stickWood", 2, -2);

		this.addAuxItem(ChromaStacks.chromaIngot, -2, 0);
		this.addAuxItem(ChromaStacks.chromaIngot, 2, 0);
		this.addAuxItem(ChromaStacks.chromaIngot, 0, 2);
		this.addAuxItem(ChromaStacks.chromaIngot, 0, -2);

		this.addAuxItem(ChromaStacks.elementDust, -4, 0);
		this.addAuxItem(ChromaStacks.elementDust, 4, 0);
		this.addAuxItem(ChromaStacks.elementDust, 0, 4);
		this.addAuxItem(ChromaStacks.elementDust, 0, -4);
	}

}
