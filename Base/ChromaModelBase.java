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

import java.util.ArrayList;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;
import Reika.DragonAPI.Interfaces.TileModel;

public abstract class ChromaModelBase extends ModelBase implements TileModel {

	public static boolean allowRendering = true;

	protected final float f5 = 0.0625F;
	protected int pass;

	public abstract void renderAll(TileEntity te, ArrayList conditions);

	public void setRenderPass() {
		pass = MinecraftForgeClient.getRenderPass();
	}

	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f6) {}

	@Override
	public final void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f6) {}

	protected final void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}
