/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Registry;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.potion.Potion;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Interfaces.Registry.IDRegistry;

public enum ExtraChromaIDs implements IDRegistry {

	GROWTHID(		"Potion IDs", 			"Growth Hormone ID", 			36, 	Potion.class),
	SATID(			"Potion IDs", 			"Saturation ID", 				38, 	Potion.class),
	REGENID(		"Potion IDs", 			"Regen ID", 					39, 	Potion.class),
	LUMARHEAID(		"Potion IDs", 			"Lumarhea ID", 					40, 	Potion.class),
	VOIDGAZEID(		"Potion IDs", 			"Void Gaze ID",					41, 	Potion.class),
	LUMENREGENID(	"Potion IDs", 			"Lumen Regen ID",				42, 	Potion.class),
	RAINBOWFOREST(	"Biome IDs", 			"Rainbow Forest Biome ID", 		48, 	BiomeGenBase.class),
	ENDERFOREST(	"Biome IDs", 			"Ender Forest Biome ID", 		47, 	BiomeGenBase.class),
	LUMINOUSCLIFFS(	"Biome IDs", 			"Luminous Cliffs Biome ID", 	49, 	BiomeGenBase.class),
	LUMINOUSEDGE(	"Biome IDs", 			"Luminous Cliffs Edge Biome ID",50, 	BiomeGenBase.class),
	ISLANDS(		"Dimension Biome IDs", 	"Skyland Biome ID", 			100, 	BiomeGenBase.class),
	SKYLANDS(		"Dimension Biome IDs", 	"Island Biome ID", 				101, 	BiomeGenBase.class),
	PLAINS(			"Dimension Biome IDs", 	"Crystal Plains Biome ID", 		102, 	BiomeGenBase.class),
	FOREST(			"Dimension Biome IDs", 	"Glowing Forest Biome ID", 		103, 	BiomeGenBase.class),
	CRYSFOREST(		"Dimension Biome IDs", 	"Crystal Forest Biome ID", 		104, 	BiomeGenBase.class),
	MOUNTAIN(		"Dimension Biome IDs", 	"Crystal Mountains Biome ID", 	105, 	BiomeGenBase.class),
	OCEAN(			"Dimension Biome IDs", 	"Aura Ocean Biome ID", 			106, 	BiomeGenBase.class),
	STRUCTURE(		"Dimension Biome IDs", 	"Structure Biome ID", 			107, 	BiomeGenBase.class),
	VOID(			"Dimension Biome IDs", 	"Voidland Biome ID", 			108, 	BiomeGenBase.class),
	CENTRAL(		"Dimension Biome IDs", 	"Central Biome ID", 			109, 	BiomeGenBase.class),
	SPARKLE(		"Dimension Biome IDs", 	"Sparkling Sands Biome ID",		110, 	BiomeGenBase.class),
	GLOWCRACKS(		"Dimension Biome IDs", 	"Radiant Fissures Biome ID",	111, 	BiomeGenBase.class),
	WEAPONAOEID(	"Enchantment IDs", 		"Weapon AOE ID", 				90, 	Enchantment.class),
	ENDERLOCKID(	"Enchantment IDs", 		"Ender Lock ID", 				91, 	Enchantment.class),
	AGGROMASKID(	"Enchantment IDs", 		"Aggro Mask ID", 				92, 	Enchantment.class),
	USEREPAIRID(	"Enchantment IDs", 		"Use Repair ID", 				93, 	Enchantment.class),
	RARELOOTID(		"Enchantment IDs", 		"Rare Loot ID", 				94, 	Enchantment.class),
	FASTSINKID(		"Enchantment IDs", 		"Rapid Descent ID", 			95, 	Enchantment.class),
	HARVESTLEVELID(	"Enchantment IDs", 		"Harvest Boost ID", 			96, 	Enchantment.class),
	AIRMINERID(		"Enchantment IDs", 		"Air Miner ID", 				97, 	Enchantment.class),
	PHASINGID(		"Enchantment IDs", 		"Armor Breach ID",				98, 	Enchantment.class),
	BOSSKILLID(		"Enchantment IDs", 		"Capital Strike ID",			99, 	Enchantment.class),
	DIMID(			"Other IDs",			"Dimension ID",					60,		WorldProvider.class),
	CHROMAMATID(	"Other IDs",			"Chromastone Material ID",		90,		null),
	;

	private String name;
	private String category;
	private int defaultID;
	private Class type;

	public static final ExtraChromaIDs[] idList = values();

	private ExtraChromaIDs(String cat, String n, int d, Class c) {
		name = n;
		category = cat;
		defaultID = d;
		type = c;
	}

	public String getName() {
		return name;
	}

	public String getCategory() {
		return category;
	}

	public int getDefaultID() {
		return defaultID;
	}

	public int getValue() {
		return ChromatiCraft.config.getOtherID(this.ordinal());
	}

	@Override
	public String getConfigName() {
		return this.getName();
	}

	public boolean isDummiedOut() {
		return type == null;
	}

	@Override
	public boolean enforceMatch() {
		return true;
	}

	@Override
	public Class getPropertyType() {
		return int.class;
	}

	@Override
	public String getLabel() {
		return this.getName();
	}

	@Override
	public boolean isEnforcingDefaults() {
		return false;
	}

	@Override
	public boolean shouldLoad() {
		return true;
	}

}
