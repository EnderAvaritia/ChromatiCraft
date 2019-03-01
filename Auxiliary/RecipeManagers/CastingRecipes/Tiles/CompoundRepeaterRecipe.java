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

import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RepeaterRecipe;
import Reika.ChromatiCraft.Registry.ChromaTiles;

public class CompoundRepeaterRecipe extends RepeaterRecipe {

	public CompoundRepeaterRecipe(ItemStack main) {
		super(ChromaTiles.COMPOUND, main);

		this.addAuxItem(ChromaStacks.beaconDust, -4, -4);
		this.addAuxItem(ChromaStacks.beaconDust, -2, -4);
		this.addAuxItem(ChromaStacks.beaconDust, 0, -4);
		this.addAuxItem(ChromaStacks.beaconDust, 2, -4);
		this.addAuxItem(ChromaStacks.beaconDust, 4, -4);
		this.addAuxItem(ChromaStacks.beaconDust, 4, -2);
		this.addAuxItem(ChromaStacks.beaconDust, 4, 0);
		this.addAuxItem(ChromaStacks.beaconDust, 4, 2);
		this.addAuxItem(ChromaStacks.beaconDust, 4, 4);
		this.addAuxItem(ChromaStacks.beaconDust, 2, 4);
		this.addAuxItem(ChromaStacks.beaconDust, 0, 4);
		this.addAuxItem(ChromaStacks.beaconDust, -2, 4);
		this.addAuxItem(ChromaStacks.beaconDust, -4, 4);
		this.addAuxItem(ChromaStacks.beaconDust, -4, 2);
		this.addAuxItem(ChromaStacks.beaconDust, -4, 0);
		this.addAuxItem(ChromaStacks.beaconDust, -4, -2);

		this.addAuxItem(ChromaTiles.REPEATER.getCraftedProduct(), 0, -2);
		this.addAuxItem(ChromaTiles.REPEATER.getCraftedProduct(), 0, 2);
		this.addAuxItem(ChromaTiles.REPEATER.getCraftedProduct(), 2, 0);
		this.addAuxItem(ChromaTiles.REPEATER.getCraftedProduct(), -2, 0);

		this.addAuxItem(ChromaStacks.focusDust, 2, -2);
		this.addAuxItem(ChromaStacks.focusDust, 2, 2);
		this.addAuxItem(ChromaStacks.focusDust, -2, -2);
		this.addAuxItem(ChromaStacks.focusDust, -2, 2);
	}

	@Override
	public int getDuration() {
		return 8*super.getDuration();
	}

	@Override
	public int getExperience() {
		return 3*super.getExperience();
	}

	@Override
	public int getNumberProduced() {
		return 2;
	}

}
