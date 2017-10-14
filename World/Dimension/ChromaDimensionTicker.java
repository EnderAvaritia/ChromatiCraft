/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

import org.lwjgl.input.Keyboard;

import paulscode.sound.StreamThread;
import Reika.ChromatiCraft.ChromaClient;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.MonumentCompletionRitual;
import Reika.ChromatiCraft.Auxiliary.MusicLoader;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.DragonAPI.Auxiliary.Trackers.RemoteAssetLoader.RemoteAssetsDownloadCompleteEvent;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.Trackers.TickRegistry.TickType;
import Reika.DragonAPI.IO.DirectResourceManager;
import Reika.DragonAPI.Instantiable.IO.CustomMusic;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ChromaDimensionTicker implements TickHandler {

	public static final ChromaDimensionTicker instance = new ChromaDimensionTicker();

	private final Random rand = new Random();

	public final int dimID = ExtraChromaIDs.DIMID.getValue();
	private final Collection<Ticket> tickets = new ArrayList();
	private final ArrayList<DimensionMusic> music = new ArrayList();
	private final ArrayList<DimensionMusic> freshTracks = new ArrayList();

	private int musicCooldown;

	@SideOnly(Side.CLIENT)
	private ISound currentMusic;

	private ChromaDimensionTicker() {

	}

	@SideOnly(Side.CLIENT)
	public ISound getCurrentMusic() {
		return currentMusic;
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void registerMusic(RemoteAssetsDownloadCompleteEvent evt) {
		Collection<String> li = MusicLoader.instance.getMusicFiles();
		ChromatiCraft.logger.log(li.size()+" music tracks available for the dimension: "+li);
		for (String path : li) {
			DimensionMusic mus = new DimensionMusic(path, path.substring(0, path.length()-4).endsWith("_c"));
			music.add(mus);
			DirectResourceManager.getInstance().registerCustomPath(mus.path, ChromaClient.chromaCategory, true);
		}
	}

	@Override
	public void tick(TickType type, Object... tickData) {
		switch(type) {
			case WORLD: {
				World world = (World)tickData[0];
				if (world.provider.dimensionId == dimID) {
					world.ambientTickCountdown = Integer.MAX_VALUE;

					if (!world.isRemote) {
						this.unloadChunks();
					}

					ChromaDimensionManager.tickPlayersInStructures(world);
					SkyRiverManager.tickSkyRiverServer(world);
					world.setAllowedSpawnTypes(false, false);
					if (!world.playerEntities.isEmpty()) {
						ChromaDimensionManager.dimensionAge++;
					}
				}
				break;
			}
			case CLIENT:
				if (!music.isEmpty())
					this.playMusic();
				SkyRiverManagerClient.handleSkyRiverMovementClient();
				break;
			case PLAYER:
				EntityPlayer ep = (EntityPlayer)tickData[0];
				if (ep.worldObj.provider.dimensionId == dimID) {
					if (!RegionMapper.isPointInCentralRegion(ep.posX, ep.posZ)) {
						OuterRegionsEvents.instance.tickPlayerInOuterRegion(ep);
						/*
						if (rand.nextInt(3200) == 0) {
							ChromaDimensionBiome b = BiomeDistributor.getBiome(MathHelper.floor_double(ep.posX), MathHelper.floor_double(ep.posZ));
							if (b == Biomes.SKYLANDS.getBiome() || b == SubBiomes.VOIDLANDS.getBiome()) {
								for (EntityAurora e : WorldGenAurorae.generateAurorae(ep.worldObj, rand, ep.posX, ep.posY, ep.posZ))
									ChromaDimensionManager.addAurora(e);
							}
						}*/
					}
				}
				break;
			default:
				break;
		}
	}

	@SideOnly(Side.CLIENT)
	private void playMusic() {
		if (Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().theWorld.provider.dimensionId == dimID) {
			if (!MonumentCompletionRitual.areRitualsRunning()) {
				SoundHandler sh = Minecraft.getMinecraft().getSoundHandler();
				StreamThread th = ReikaSoundHelper.getStreamingThread(sh);
				if (th == null || !th.isAlive()) {
					sh.stopSounds();
					ReikaSoundHelper.restartStreamingSystem(sh);
				}
				//ReikaJavaLibrary.pConsole(s.path+":"+sh.isSoundPlaying(s));
				if (currentMusic != null && ReikaObfuscationHelper.isDeObfEnvironment() && Keyboard.isKeyDown(Keyboard.KEY_END)) {
					sh.stopSound(currentMusic);
					musicCooldown = 0;
				}
				if (currentMusic != null && sh.isSoundPlaying(currentMusic)) {
					return;
				}
				if (musicCooldown > 0) {
					musicCooldown--;
					return;
				}

				DimensionMusic s = this.selectTrack(Minecraft.getMinecraft().thePlayer);
				if (s != null)
					s.play(sh);

				currentMusic = s;
				musicCooldown = 300+rand.nextInt(900);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private DimensionMusic selectTrack(EntityPlayer ep) {
		if (freshTracks.isEmpty()) {
			freshTracks.addAll(music);
			Collections.shuffle(freshTracks);
		}
		DimensionMusic s = freshTracks.remove(0);
		while (!s.canPlay(ep) && !freshTracks.isEmpty()) {
			s = freshTracks.remove(0);
		}
		return s.canPlay(ep) ? s : null;
	}

	private void unloadChunks() {
		for (Ticket t : tickets) {
			for (ChunkCoordIntPair p : t.getChunkList()) {
				ForgeChunkManager.unforceChunk(t, p);
			}
		}
		tickets.clear();
	}

	public void scheduleTicketUnload(Ticket t) {
		tickets.add(t);
	}

	@Override
	public EnumSet<TickType> getType() {
		return EnumSet.of(TickType.WORLD, TickType.CLIENT, TickType.PLAYER);
	}

	@Override
	public boolean canFire(Phase p) {
		return p == Phase.END;
	}

	@Override
	public String getLabel() {
		return "Chroma Dimension Tag";
	}

	private static class DimensionMusic extends CustomMusic {

		private final boolean isCompletionGated;

		private DimensionMusic(String path, boolean b) {
			super(path);

			isCompletionGated = b;
		}

		public final boolean canPlay(EntityPlayer ep) {
			return isCompletionGated ? ProgressStage.CTM.isPlayerAtStage(ep) : true;
		}

	}

}
