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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaThaumHelper;

public class AspectFormerRecipe extends MultiBlockCastingRecipe {

	public AspectFormerRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuxItem(ChromaStacks.chromaIngot, -2, -2);
		this.addAuxItem(ChromaStacks.chromaIngot, 0, -2);
		this.addAuxItem(ChromaStacks.chromaIngot, 2, -2);

		this.addAuxItem(ChromaStacks.conductiveIngot, -2, -4);
		this.addAuxItem(ChromaStacks.conductiveIngot, 2, -4);

		this.addAuxItem(ChromaStacks.chromaDust, -2, 0);
		this.addAuxItem(ChromaStacks.chromaDust, 2, 0);

		this.addAuxItem(Items.iron_ingot, 2, 2);
		this.addAuxItem(Items.iron_ingot, 2, 4);

		this.addAuxItem(Items.iron_ingot, -2, 2);
		this.addAuxItem(Items.iron_ingot, -2, 4);
	}

	@Override
	public boolean canRunRecipe(TileEntity te, EntityPlayer ep) {
		return super.canRunRecipe(te, ep) && (!ChromaOptions.HARDTHAUM.getState() || ReikaThaumHelper.isResearchComplete(ep, "INFUSION"));
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 8;
	}

}
