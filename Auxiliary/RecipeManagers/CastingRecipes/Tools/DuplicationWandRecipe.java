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

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class DuplicationWandRecipe extends PylonCastingRecipe {

	public DuplicationWandRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		//for now
		this.addAuxItem("stickWood", -2, -2);
		this.addAuxItem("stickWood", 2, 2);
		this.addAuxItem("stickWood", -2, 2);
		this.addAuxItem("stickWood", 2, -2);

		this.addAuxItem(ChromaStacks.auraIngot, -2, 0);
		this.addAuxItem(ChromaStacks.auraIngot, 2, 0);
		this.addAuxItem(ChromaStacks.auraIngot, 0, 2);
		this.addAuxItem(ChromaStacks.auraIngot, 0, -2);

		this.addAuxItem(Items.emerald, -4, 0);
		this.addAuxItem(Items.emerald, 4, 0);

		this.addAuxItem(ChromaStacks.spaceDust, 0, -4);
		this.addAuxItem(ChromaStacks.spaceDust, 0, 4);

		this.addAuxItem(ChromaStacks.lightBlueShard, -4, 2);
		this.addAuxItem(ChromaStacks.grayShard, -4, -2);
		this.addAuxItem(ChromaStacks.grayShard, 4, 2);
		this.addAuxItem(ChromaStacks.lightBlueShard, 4, -2);

		this.addAuxItem(ChromaStacks.grayShard, -2, 4);
		this.addAuxItem(ChromaStacks.lightBlueShard, -2, -4);
		this.addAuxItem(ChromaStacks.lightBlueShard, 2, 4);
		this.addAuxItem(ChromaStacks.grayShard, 2, -4);

		this.addAuxItem(Items.diamond, -4, 4);
		this.addAuxItem(Items.diamond, -4, -4);
		this.addAuxItem(Items.diamond, 4, 4);
		this.addAuxItem(Items.diamond, 4, -4);

		this.addAuraRequirement(CrystalElement.LIGHTBLUE, 20000);
		this.addAuraRequirement(CrystalElement.LIME, 10000);
		this.addAuraRequirement(CrystalElement.BROWN, 15000);
	}

}
