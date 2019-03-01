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

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonCastingRecipe;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModInteract.ItemHandlers.AppEngHandler;

public class VoidStorageRecipe extends PylonCastingRecipe {

	public VoidStorageRecipe(ItemStack out, ItemStack main) {
		super(out, main);

		this.addAuraRequirement(CrystalElement.BLACK, 5000);
		this.addAuraRequirement(CrystalElement.BROWN, 5000);
		this.addAuraRequirement(CrystalElement.PURPLE, 5000);
		this.addAuraRequirement(CrystalElement.WHITE, 5000);

		this.addAuxItem(new ItemStack(AppEngHandler.getInstance().quartzGlass), -2, -2);
		this.addAuxItem(Items.redstone, 0, -2);
		this.addAuxItem(new ItemStack(AppEngHandler.getInstance().quartzGlass), 2, -2);

		this.addAuxItem(Items.redstone, -2, 0);
		this.addAuxItem(Items.redstone, 2, 0);

		this.addAuxItem(Items.iron_ingot, -2, 2);
		this.addAuxItem(Items.iron_ingot, 0, 2);
		this.addAuxItem(Items.iron_ingot, 2, 2);


		this.addAuxItem(AppEngHandler.getInstance().getDiamondProcessor(), 0, -4);
		this.addAuxItem(AppEngHandler.getInstance().getDiamondProcessor(), 0, 4);

		this.addAuxItem(AppEngHandler.getInstance().getQuartzProcessor(), -4, 0);
		this.addAuxItem(AppEngHandler.getInstance().getQuartzProcessor(), 4, 0);

		this.addAuxItem(AppEngHandler.getInstance().getGoldProcessor(), -4, -4);
		this.addAuxItem(AppEngHandler.getInstance().getGoldProcessor(), 4, -4);
		this.addAuxItem(AppEngHandler.getInstance().getGoldProcessor(), -4, 4);
		this.addAuxItem(AppEngHandler.getInstance().getGoldProcessor(), 4, 4);

		this.addAuxItem(ChromaStacks.voidDust, -2, -4);
		this.addAuxItem(ChromaStacks.chromaDust, 2, -4);

		this.addAuxItem(ChromaStacks.voidDust, 2, 4);
		this.addAuxItem(ChromaStacks.chromaDust, -2, 4);

		this.addAuxItem(ChromaStacks.voidDust, 4, -2);
		this.addAuxItem(ChromaStacks.chromaDust, -4, -2);

		this.addAuxItem(ChromaStacks.voidDust, -4, 2);
		this.addAuxItem(ChromaStacks.chromaDust, 4, 2);
	}

	@Override
	public int getTypicalCraftedAmount() {
		return 10; //1 drive bay
	}

}
