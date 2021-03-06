/*
 * Copyright � 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.ai;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.util.math.BlockPos;
import tk.wurst_client.WurstClient;

public class PathUtils
{
	private static PlayerCapabilities playerCaps;
	
	public static boolean isSafe(BlockPos pos)
	{
		Material material = getMaterial(pos);
		int id = getID(pos);
		boolean alwaysSafe = !material.blocksMovement() && id != 132;// tripwire
		if(isCreative())
			return alwaysSafe;
		Material materialBelow = getMaterial(pos.add(0, -1, 0));
		return alwaysSafe && material != Material.LAVA
			&& materialBelow != Material.CACTUS && material != Material.FIRE;
	}
	
	public static boolean isSolid(BlockPos pos)
	{
		return Minecraft.getMinecraft().theWorld.getBlockState(pos).getBlock()
			.getMaterial(Minecraft.getMinecraft().theWorld.getBlockState(pos))
			.blocksMovement()
			|| getMaterial(pos) == Material.WATER
			&& WurstClient.INSTANCE.mods.jesusMod.isEnabled();
	}
	
	public static boolean isFallable(BlockPos pos)
	{
		for(int i = -1; i >= (isNoFall() ? -256 : -3); i--)
			if(isSolid(pos.add(0, i, 0)))
				return true;
		return false;
	}
	
	public static boolean isClimbable(BlockPos pos)
	{
		if(isSolid(pos.add(0, -1, 0))
			|| WurstClient.INSTANCE.mods.spiderMod.isEnabled()
			|| getID(pos) == 65 || isFlyable(pos))
			if(isSolid(pos.add(0, 0, -1)) || isSolid(pos.add(0, 0, 1))
				|| isSolid(pos.add(1, 0, 0)) || isSolid(pos.add(-1, 0, 0)))
				return true;
		return false;
	}
	
	public static boolean isNoFall()
	{
		return WurstClient.INSTANCE.mods.noFallMod.isEnabled() || isCreative();
	}
	
	public static boolean isCreative()
	{
		if(playerCaps == null)
			playerCaps = Minecraft.getMinecraft().thePlayer.capabilities;
		return playerCaps.isCreativeMode;
	}
	
	public static boolean isFlyable(BlockPos pos)
	{
		if(playerCaps == null)
			playerCaps = Minecraft.getMinecraft().thePlayer.capabilities;
		return WurstClient.INSTANCE.mods.flightMod.isEnabled()
			|| playerCaps.isFlying
			|| !WurstClient.INSTANCE.mods.noSlowdownMod.isEnabled()
			&& getMaterial(pos) == Material.WATER;
	}
	
	public static int getCost(BlockPos current, BlockPos next)
	{
		Material nextMaterial = getMaterial(next);
		if(nextMaterial == Material.WATER)
			if(WurstClient.INSTANCE.mods.noSlowdownMod.isEnabled())
				return 1;
			else if(WurstClient.INSTANCE.mods.antiKnockbackMod.isEnabled())
				return 2;
			else
				return 3;
		else if(nextMaterial == Material.LAVA)
			return 5;
		return 1;
	}
	
	private static Material getMaterial(BlockPos pos)
	{
		return Minecraft.getMinecraft().theWorld.getBlockState(pos).getBlock()
			.getMaterial(Minecraft.getMinecraft().theWorld.getBlockState(pos));
	}
	
	private static int getID(BlockPos pos)
	{
		return Block.getIdFromBlock(Minecraft.getMinecraft().theWorld
			.getBlockState(pos).getBlock());
	}
}
