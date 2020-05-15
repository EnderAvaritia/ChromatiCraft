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

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;


public class FloatingRelayRecipe extends MultiBlockCastingRecipe {

	public FloatingRelayRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaStacks.auraDust, -2, -2);
		this.addAuxItem(ChromaStacks.beaconDust, 2, -2);
		this.addAuxItem(ChromaStacks.beaconDust, -2, 2);
		this.addAuxItem(ChromaStacks.auraDust, 2, 2);
		this.addAuxItem(Items.glowstone_dust, -2, 0);
		this.addAuxItem(Items.glowstone_dust, 2, 0);
		this.addAuxItem(ChromaStacks.enderDust, 0, 2);
		this.addAuxItem(ChromaStacks.enderDust, 0, -2);
	}

	@Override
	public boolean canGiveDoubleOutput() {
		return true;
	}

}
