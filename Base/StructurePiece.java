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


import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;

public abstract class StructurePiece<V extends DimensionStructureGenerator> extends StructureElement<V> {

	protected StructurePiece(V s) {
		super(s);
	}

	public abstract void generate(ChunkSplicedGenerationCache world, int x, int y, int z);

}
