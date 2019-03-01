/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Auxiliary.Interfaces.Linkable;
import Reika.ChromatiCraft.Auxiliary.Interfaces.LinkedTile;
import Reika.ChromatiCraft.Auxiliary.Interfaces.LinkerCallback;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Block.Dimension.Structure.NonEuclid.BlockTeleport.TileEntityTeleport;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityTransportWindow;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockVector;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.TileEntity.NBTCopyable;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;

public class ItemConnector extends ItemChromaTool {

	public ItemConnector(int index) {
		super(index);
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float a, float b, float c) {
		if (world.isRemote)
			return true;
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof LinkedTile) {
			return this.connectRift((LinkedTile)te, world, x, y, z, is, ep);
		}
		else if (te instanceof TileEntityTransportWindow) {
			return this.connectWindow((TileEntityTransportWindow)te, world, x, y, z, is, ep);
		}
		else if (te instanceof Linkable) {
			return this.tryConnection((Linkable)te, world, x, y, z, is, ep);
		}
		else if (te instanceof NBTCopyable) {
			if (is.stackTagCompound != null && is.stackTagCompound.hasKey("NBT_transfer")) {
				((NBTCopyable)te).readCopyableData(is.stackTagCompound.getCompoundTag("NBT_transfer"));
				is.stackTagCompound = null;
			}
			else {
				if (is.stackTagCompound == null)
					is.stackTagCompound = new NBTTagCompound();
				NBTTagCompound tag = new NBTTagCompound();
				((NBTCopyable)te).writeCopyableData(tag);
				is.stackTagCompound.setTag("NBT_transfer", tag);
			}
			return true;
		}
		else if (te instanceof LinkerCallback) {
			is.stackTagCompound = new NBTTagCompound();
			NBTTagCompound tag = new NBTTagCompound();
			new WorldLocation(te).writeToNBT(tag);
			is.stackTagCompound.setTag("callback", tag);
			return true;
		}

		if (DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment() && ep.capabilities.isCreativeMode) {
			if (is.stackTagCompound != null && is.stackTagCompound.getBoolean("noneuclid")) {

				int dx = is.stackTagCompound.getInteger("r1x");
				int dy = is.stackTagCompound.getInteger("r1y");
				int dz = is.stackTagCompound.getInteger("r1z");
				int face = is.stackTagCompound.getInteger("facing");

				TileEntity tile = world.getTileEntity(dx, dy, dz);
				if (tile instanceof TileEntityTeleport) {
					((TileEntityTeleport)tile).facing = ForgeDirection.VALID_DIRECTIONS[face].getOpposite();
					((TileEntityTeleport)tile).destination = new BlockVector(x-dx, y+1-dy, z-dz, ForgeDirection.VALID_DIRECTIONS[s].getOpposite());
					ReikaJavaLibrary.pConsole(((TileEntityTeleport)tile).destination);
				}

				is.stackTagCompound = null;
				return true;
			}
			else if (te instanceof TileEntityTeleport) {
				if (is.stackTagCompound == null)
					is.stackTagCompound = new NBTTagCompound();
				is.stackTagCompound.setInteger("r1x", x);
				is.stackTagCompound.setInteger("r1y", y);
				is.stackTagCompound.setInteger("r1z", z);
				is.stackTagCompound.setInteger("facing", s);
				is.stackTagCompound.setBoolean("noneuclid", true);
				ReikaJavaLibrary.pConsole(is.stackTagCompound);
				return true;
			}
		}

		if (is.stackTagCompound != null && is.stackTagCompound.hasKey("callback")) {
			WorldLocation loc = WorldLocation.readFromNBT(is.stackTagCompound.getCompoundTag("callback"));
			if (loc != null) {
				TileEntity tile = loc.getTileEntity();
				if (tile instanceof LinkerCallback) {
					((LinkerCallback)tile).linkTo(world, x, y, z);
					is.stackTagCompound = null;
					return true;
				}
			}
		}
		is.stackTagCompound = null;
		return false;
	}

	private boolean connectRift(LinkedTile te, World world, int x, int y, int z, ItemStack is, EntityPlayer ep) {
		if (is.stackTagCompound == null) {
			is.stackTagCompound = new NBTTagCompound();
			is.stackTagCompound.setInteger("r1x", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("r1y", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("r1z", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("r2x", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("r2y", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("r2z", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("w1", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("w2", Integer.MIN_VALUE);
		}
		if (is.stackTagCompound.getInteger("r1x") == Integer.MIN_VALUE) {
			is.stackTagCompound.setInteger("r1x", x);
			is.stackTagCompound.setInteger("r1y", y);
			is.stackTagCompound.setInteger("r1z", z);
			is.stackTagCompound.setInteger("w1", world.provider.dimensionId);
		}
		else {
			is.stackTagCompound.setInteger("r2x", x);
			is.stackTagCompound.setInteger("r2y", y);
			is.stackTagCompound.setInteger("r2z", z);
			is.stackTagCompound.setInteger("w2", world.provider.dimensionId);
		}
		int x1 = is.stackTagCompound.getInteger("r1x");
		int y1 = is.stackTagCompound.getInteger("r1y");
		int z1 = is.stackTagCompound.getInteger("r1z");
		int x2 = is.stackTagCompound.getInteger("r2x");
		int y2 = is.stackTagCompound.getInteger("r2y");
		int z2 = is.stackTagCompound.getInteger("r2z");
		int dim1 = is.stackTagCompound.getInteger("w1");
		int dim2 = is.stackTagCompound.getInteger("w2");
		World w1 = DimensionManager.getWorld(dim1);
		World w2 = DimensionManager.getWorld(dim2);

		if (x1 != Integer.MIN_VALUE && y1 != Integer.MIN_VALUE && z1 != Integer.MIN_VALUE) {
			if (x1 != Integer.MIN_VALUE && y2 != Integer.MIN_VALUE && z2 != Integer.MIN_VALUE) {
				LinkedTile rf1 = (LinkedTile)world.getTileEntity(x1, y1, z1);
				LinkedTile rf2 = (LinkedTile)world.getTileEntity(x2, y2, z2);

				//ReikaJavaLibrary.pConsole(rec+"\n"+em);
				if (rf1 == null) {
					ReikaChatHelper.writeString("Tile missing at "+x1+", "+y1+", "+z1);
					is.stackTagCompound = null;
					return false;
				}
				if (rf2 == null) {
					ReikaChatHelper.writeString("Tile missing at "+x2+", "+y2+", "+z2);
					is.stackTagCompound = null;
					return false;
				}
				else if (rf1 == rf2) {
					ReikaChatHelper.writeString("Cannot link a rift to itself!");
					is.stackTagCompound = null;
					return false;
				}
				rf1.reset();
				rf2.reset();
				boolean flag = rf1.linkTo(w2, x2, y2, z2);
				if (flag) {
					ReikaChatHelper.sendChatToPlayer(ep, "Linked "+rf1+" and "+rf2);
					rf1.setPrimary(true);
				}
				else
					ReikaChatHelper.sendChatToPlayer(ep, "Link failed.");
				is.stackTagCompound = null;
			}
		}
		return false;
	}

	private boolean connectWindow(TileEntityTransportWindow te, World world, int x, int y, int z, ItemStack is, EntityPlayer ep) {
		if (is.stackTagCompound == null) {
			is.stackTagCompound = new NBTTagCompound();
			is.stackTagCompound.setInteger("w1x", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("w1y", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("w1z", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("w2x", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("w2y", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("w2z", Integer.MIN_VALUE);
		}
		if (is.stackTagCompound.getInteger("w1x") == Integer.MIN_VALUE) {
			is.stackTagCompound.setInteger("w1x", x);
			is.stackTagCompound.setInteger("w1y", y);
			is.stackTagCompound.setInteger("w1z", z);
		}
		else {
			is.stackTagCompound.setInteger("w2x", x);
			is.stackTagCompound.setInteger("w2y", y);
			is.stackTagCompound.setInteger("w2z", z);
		}
		int x1 = is.stackTagCompound.getInteger("w1x");
		int y1 = is.stackTagCompound.getInteger("w1y");
		int z1 = is.stackTagCompound.getInteger("w1z");
		int x2 = is.stackTagCompound.getInteger("w2x");
		int y2 = is.stackTagCompound.getInteger("w2y");
		int z2 = is.stackTagCompound.getInteger("w2z");

		if (x1 != Integer.MIN_VALUE && y1 != Integer.MIN_VALUE && z1 != Integer.MIN_VALUE) {
			if (x1 != Integer.MIN_VALUE && y2 != Integer.MIN_VALUE && z2 != Integer.MIN_VALUE) {
				TileEntityTransportWindow rf1 = (TileEntityTransportWindow)world.getTileEntity(x1, y1, z1);
				TileEntityTransportWindow rf2 = (TileEntityTransportWindow)world.getTileEntity(x2, y2, z2);

				//ReikaJavaLibrary.pConsole(rec+"\n"+em);
				if (rf1 == null) {
					ReikaChatHelper.writeString("Tile missing at "+x1+", "+y1+", "+z1);
					is.stackTagCompound = null;
					return false;
				}
				if (rf2 == null) {
					ReikaChatHelper.writeString("Tile missing at "+x2+", "+y2+", "+z2);
					is.stackTagCompound = null;
					return false;
				}
				else if (rf1 == rf2) {
					ReikaChatHelper.writeString("Cannot link a window to itself!");
					is.stackTagCompound = null;
					return false;
				}
				if (rf1.matchRenderStates(rf2)) {
					//rf1.reset();
					//rf2.reset();
					rf1.linkTo(rf2);
					ReikaChatHelper.sendChatToPlayer(ep, "Linked "+rf1+" and "+rf2);
				}
				else {
					ReikaChatHelper.sendChatToPlayer(ep, "Cannot link windows; states do not match.");
				}
				is.stackTagCompound = null;
			}
		}
		return false;
	}

	private boolean tryConnection(Linkable te, World world, int x, int y, int z, ItemStack is, EntityPlayer ep) {
		if (is.stackTagCompound == null) {
			is.stackTagCompound = new NBTTagCompound();
			is.stackTagCompound.setInteger("x1", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("y1", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("z1", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("x2", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("y2", Integer.MIN_VALUE);
			is.stackTagCompound.setInteger("z2", Integer.MIN_VALUE);
		}
		if (is.stackTagCompound.getInteger("x1") == Integer.MIN_VALUE) {
			is.stackTagCompound.setInteger("x1", x);
			is.stackTagCompound.setInteger("y1", y);
			is.stackTagCompound.setInteger("z1", z);
		}
		else {
			is.stackTagCompound.setInteger("x2", x);
			is.stackTagCompound.setInteger("y2", y);
			is.stackTagCompound.setInteger("z2", z);
		}
		int ex = is.stackTagCompound.getInteger("x1");
		int ey = is.stackTagCompound.getInteger("y1");
		int ez = is.stackTagCompound.getInteger("z1");
		int rx = is.stackTagCompound.getInteger("x2");
		int ry = is.stackTagCompound.getInteger("y2");
		int rz = is.stackTagCompound.getInteger("z2");

		int dl = Math.abs(ex-rx+ey-ry+ez-rz)-1;

		//ReikaJavaLibrary.pConsole(is.stackTagCompound);
		//ReikaJavaLibrary.pConsole(dl);
		//if (is.stackSize >= dl || ep.capabilities.isCreativeMode) {
		if (rx != Integer.MIN_VALUE && ry != Integer.MIN_VALUE && rz != Integer.MIN_VALUE) {
			if (ex != Integer.MIN_VALUE && ey != Integer.MIN_VALUE && ez != Integer.MIN_VALUE) {
				Linkable em = (Linkable)world.getTileEntity(ex, ey, ez);
				Linkable rec = (Linkable)world.getTileEntity(rx, ry, rz);

				//ReikaJavaLibrary.pConsole(rec+"\n"+em);
				if (em == null) {
					ReikaChatHelper.writeString("Tile missing at "+ex+", "+ey+", "+ez);
					is.stackTagCompound = null;
					return false;
				}
				if (rec == null) {
					ReikaChatHelper.writeString("Tile missing at "+rx+", "+ry+", "+rz);
					is.stackTagCompound = null;
					return false;
				}
				else if (rec == em) {
					ReikaChatHelper.writeString("Cannot link a tile to itself!");
					is.stackTagCompound = null;
					return false;
				}
				rec.resetOther();
				em.resetOther();
				em.reset();
				rec.reset();
				boolean src = em.connectTo(world, rx, ry, rz);
				boolean tg = rec.connectTo(world, ex, ey, ez);
				//ReikaJavaLibrary.pConsole(src+":"+tg, Side.SERVER);
				if (src && tg) {
					//ReikaJavaLibrary.pConsole("connected", Side.SERVER);
					ReikaChatHelper.sendChatToPlayer(ep, "Linked "+src+" and "+tg);
				}
				else {
					ReikaChatHelper.sendChatToPlayer(ep, "Link Failed.");
				}
				is.stackTagCompound = null;
			}
		}
		//}
		return false;
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		if (is.stackTagCompound != null) {
			int x1 = is.stackTagCompound.getInteger("r1x");
			int y1 = is.stackTagCompound.getInteger("r1y");
			int z1 = is.stackTagCompound.getInteger("r1z");
			int x2 = is.stackTagCompound.getInteger("r2x");
			int y2 = is.stackTagCompound.getInteger("r2y");
			int z2 = is.stackTagCompound.getInteger("r2z");
			int w1 = is.stackTagCompound.getInteger("w1");
			int w2 = is.stackTagCompound.getInteger("w2");
			if (x1 != Integer.MIN_VALUE)
				li.add(String.format("Connected to %d, %d, %d in DIM%d", x1, y1, z1, w1));
			//li.add(String.format("Anchor 1: %d, %d, %d in DIM%d", x1, y1, z1, w1));
			//if (x2 != Integer.MIN_VALUE)
			//	li.add(String.format("Anchor 2: %d, %d, %d in DIM%d", x2, y2, z2, w2));
		}
	}

}
