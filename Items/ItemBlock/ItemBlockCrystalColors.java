/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.ItemBlock;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import Reika.ChromatiCraft.Auxiliary.Render.ChromaFontRenderer;
import Reika.ChromatiCraft.Magic.Progression.ProgressionManager;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.DragonAPICore;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockCrystalColors extends ItemBlock {

	public ItemBlockCrystalColors(Block b) {
		super(b);
		hasSubtypes = true;
	}

	@Override
	public void getSubItems(Item id, CreativeTabs par2CreativeTabs, List par3List)
	{
		for (int i = 0; i < 16; i++)
			par3List.add(new ItemStack(id, 1, i));
	}

	@Override
	public String getItemStackDisplayName(ItemStack is) {
		String name = ChromaBlocks.getEntryByID(field_150939_a).getMultiValuedName(is.getItemDamage());
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT && DragonAPICore.hasGameLoaded()) {
			//name = ModList.NEI.isLoaded() && DragonAPICore.hasGameLoaded() ? ObfuscatedNameHandler.registerName(name, is) : name;
			if (this.obfuscate(is))
				//name = EnumChatFormatting.OBFUSCATED.toString()+name;
				name = ChromaFontRenderer.FontType.OBFUSCATED.id+name;
		}
		return name;
	}

	@SideOnly(Side.CLIENT)
	private boolean obfuscate(ItemStack is) {
		CrystalElement e = CrystalElement.elements[is.getItemDamage()%16];
		return !ProgressionManager.instance.hasPlayerDiscoveredColor(Minecraft.getMinecraft().thePlayer, e);
	}

	@Override
	public int getMetadata(int meta) {
		return meta;
	}


}
