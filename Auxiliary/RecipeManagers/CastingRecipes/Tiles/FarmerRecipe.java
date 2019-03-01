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

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonCastingRecipe;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;

public class FarmerRecipe extends PylonCastingRecipe {

	public FarmerRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaStacks.auraDust, -4, -2);
		this.addAuxItem(ChromaStacks.auraDust, -4, 2);
		this.addAuxItem(ChromaStacks.auraDust, 4, -2);
		this.addAuxItem(ChromaStacks.auraDust, 4, 2);

		this.addAuxItem(ChromaStacks.auraDust, -2, -4);
		this.addAuxItem(ChromaStacks.auraDust, -2, 4);
		this.addAuxItem(ChromaStacks.auraDust, 2, -4);
		this.addAuxItem(ChromaStacks.auraDust, 2, 4);

		this.addAuxItem(ChromaItems.LENS.getStackOf(CrystalElement.GREEN), -4, 0);
		this.addAuxItem(Items.iron_hoe, 4, 0);

		this.addAuxItem(Items.iron_ingot, -4, -4);
		this.addAuxItem(Items.iron_ingot, 0, -4);
		this.addAuxItem(Items.iron_ingot, 4, -4);

		this.addAuxItem(Items.iron_ingot, -4, 4);
		this.addAuxItem(Items.iron_ingot, 0, 4);
		this.addAuxItem(Items.iron_ingot, 4, 4);

		this.addAuraRequirement(CrystalElement.GREEN, 12000);
		this.addAuraRequirement(CrystalElement.YELLOW, 6000);
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 8;
	}

	@Override
	public int getNumberProduced() {
		return 3;
	}

}
