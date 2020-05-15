/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Items;

import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.Interfaces.CoreRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;


public class LumenChunkRecipe extends PylonCastingRecipe implements CoreRecipe {

	public LumenChunkRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		for (int i = -4; i <= 4; i += 2) {
			for (int k = -4; k <= 4; k += 2) {
				if (i != 0 || k != 0)
					this.addAuxItem(ChromaStacks.lumaDust, i, k);
			}
		}

		this.addAuxItem(ChromaStacks.energyPowder, -2, -2);
		this.addAuxItem(ChromaStacks.energyPowder, 2, 2);
		this.addAuxItem(ChromaStacks.iridChunk, 2, -2);
		this.addAuxItem(ChromaStacks.iridChunk, -2, 2);

		this.addAuxItem(ChromaStacks.focusDust, -4, -4);
		this.addAuxItem(ChromaStacks.focusDust, -4, 4);
		this.addAuxItem(ChromaStacks.focusDust, 4, -4);
		this.addAuxItem(ChromaStacks.focusDust, 4, 4);

		this.addAuraRequirement(CrystalElement.BLUE, 10000);
	}

	@Override
	public int getDuration() {
		return super.getDuration()/8;
	}

	@Override
	public boolean canBeStacked() {
		return true;
	}

	@Override
	public boolean canGiveDoubleOutput() {
		return true;
	}

}
