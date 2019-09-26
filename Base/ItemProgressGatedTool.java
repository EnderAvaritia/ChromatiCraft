/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import net.minecraft.entity.player.EntityPlayer;

import Reika.ChromatiCraft.Magic.Progression.ChromaResearchManager;
import Reika.ChromatiCraft.Registry.ChromaResearch;


public abstract class ItemProgressGatedTool extends ItemChromaTool {

	//private final ChromaResearch fragment;
	//private final Collection<ProgressStage> progress;
	private final UseResult consequence;
	/*
	public ItemProgressGatedTool(int index, UseResult ur, ChromaResearch res) {
		this(index, ur, res, res.getRequiredProgress());
	}
	public ItemProgressGatedTool(int index, UseResult ur, ChromaResearch res, ProgressStage... req) {
		super(index);

		progress = ReikaJavaLibrary.makeListFrom(req);
		fragment = res;
		consequence = ur;
	}
	 */

	public ItemProgressGatedTool(int index, UseResult ur) {
		super(index);

		consequence = ur;
	}

	@Override
	public final UseResult canPlayerUse(EntityPlayer ep) {
		//if (fragment != null) {
		if (!ChromaResearchManager.instance.playerHasFragment(ep, ChromaResearch.getPageFor(ep.getCurrentEquippedItem())))
			return consequence;
		/*}
		else { //no need to check if the fragment is checked, since cannot have fragment w/o progress
			for (ProgressStage p : progress)
				if (!p.isPlayerAtStage(ep))
					return consequence;
		}*/
		return UseResult.ALLOW;
	}

}
