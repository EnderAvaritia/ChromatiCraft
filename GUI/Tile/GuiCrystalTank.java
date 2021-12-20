/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Tile;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fluids.Fluid;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.Container.ContainerCrystalTank;
import Reika.ChromatiCraft.TileEntity.Storage.TileEntityCrystalTank;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaEngLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Rendering.ReikaLiquidRenderer;

public class GuiCrystalTank extends GuiChromaBase {

	private final TileEntityCrystalTank tank;

	public GuiCrystalTank(EntityPlayer ep, TileEntityCrystalTank te) {
		super(new ContainerCrystalTank(ep, te), ep, te);
		tank = te;
		ySize = 155;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);
		//fontRendererObj = Minecraft.getMinecraft().fontRenderer;
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		Fluid f = tank.getCurrentFluid();
		if (f != null) {
			if (api.isMouseInBox(j+24, j+151, k+16, k+115)) {
				String s = String.format("%s: %d/%d", f.getLocalizedName(), Math.round(tank.getCurrentFluidLevel()/1000F), tank.getCapacity()/1000);
				api.drawTooltipAt(fontRendererObj, s, api.getMouseRealX()-j, api.getMouseRealY()-k);
			}
		}
		String i = this.getFullTexturePath();
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, i);
		this.drawTexturedModalRect(24, 16, 0, 155, 128, 100);

		GL11.glPushMatrix();
		GL11.glTranslated(0, 0, 500);
		float cap = tank.getCapacity()/1000F;
		float s = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? 0.1F : 0.25F;
		int c = 0xffffff;
		for (float d = s; d < 1; d += s) {
			String sg = String.format("%.1f%sB", ReikaMathLibrary.getThousandBase(cap*d), ReikaEngLibrary.getSIPrefix(cap*d));
			String sg2 = String.format("- %d%s -", Math.round(d*100), "%");
			fontRendererObj.drawString(sg, 146-fontRendererObj.getStringWidth(sg), fontRendererObj.FONT_HEIGHT/2+109-(int)(d*101), c);
			fontRendererObj.drawString(sg2, 24, fontRendererObj.FONT_HEIGHT/2+109-(int)(d*101), c);
		}
		GL11.glPopMatrix();

		BlockArray blocks = tank.getBlocks();
		String form = String.format("Tank Size~%dx%dx%d = %d", blocks.getSizeX(), blocks.getSizeY(), blocks.getSizeZ(), blocks.getSize());
		String form2 = String.format("Capacity~4n^2 = %.1f%sB", ReikaMathLibrary.getThousandBase(cap), ReikaEngLibrary.getSIPrefix(cap));
		fontRendererObj.drawString(form, 25, 125, 0xffffff);
		fontRendererObj.drawString(form2, 25, 126+fontRendererObj.FONT_HEIGHT+3, 0xffffff);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p, int a, int b) {
		super.drawGuiContainerBackgroundLayer(p, a, b);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		Fluid f = tank.getCurrentFluid();
		if (f != null) {
			int amt = tank.getCurrentFluidLevel();
			int cap = tank.getCapacity();
			int frac = (int)(100L*amt/cap);
			int num = frac/16;
			int rem = frac-num*16;
			ReikaTextureHelper.bindTerrainTexture();
			GL11.glColor4f(1, 1, 1, 1);
			for (int i = 0; i < 8; i++) {
				for (int m = 0; m < num; m++) {
					this.drawTexturedModelRectFromIcon(j+24+i*16, k+16+100-(m+1)*16, ReikaLiquidRenderer.getFluidIconSafe(f), 16, 16);
				}
				this.drawTexturedModelRectFromIcon(j+24+i*16, k+16+100-num*16-rem, ReikaLiquidRenderer.getFluidIconSafe(f), 16, rem);
			}
		}
	}

	@Override
	public String getGuiTexture() {
		return "tank";
	}

	@Override
	protected String getGuiName() {
		return "Crystal Tank";
	}

}
