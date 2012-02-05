/*
 The MIT License

 Copyright (c) 2011 Zloteanu Nichita (ZNickq), Sean Porter (Glitchfinder),
 Jan Tojnar (jtojnar, Lisured) and Andre Mohren (IceReaper)

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 */

package net.spoutmaterials.spoutmaterials.wgen;

import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.material.CustomBlock;

public class WGenData extends BlockPopulator {
	
	private int minY, maxY, chance, max, size, diff;
	private CustomBlock which;

	public WGenData(CustomBlock block, int minY, int maxY, int chance, int maxVeins, int maxVeinSize) {
		this.minY = minY;
		this.maxY = maxY;
		this.chance = chance;
		this.which = block;
		this.max = maxVeins;
		this.size = maxVeinSize;
		this.diff = maxY - minY;
	}

	@Override
	public void populate(World world, Random random, Chunk chunk) {

		// Calculate chance that blocks will be present in this chunk.
		int rn = random.nextInt(100);
		if (rn > this.chance) {
			return;
		}
		
		// Calculate how many blocks will be present in this chunk.
		int howMany = random.nextInt(this.max - 1) + 1;
		int howLarge = random.nextInt(this.size - 1) + 1;
		
		int x = 0;
		int y = 0;
		int z = 0;
		Block curBlock;
		
		for (int i = 0; i < howMany; i++) {
			
			// Get a random block in this chunk.
			do {
				x = random.nextInt(16);
				y = random.nextInt(this.diff) + this.minY;
				z = random.nextInt(16);
			} while (y > this.maxY);
			
			curBlock = chunk.getBlock(x, y, z);
			
			// Set this vein.
			for (int j = 1; j < howLarge; j++) {
				if (curBlock.getType() == Material.STONE || curBlock.getType() == Material.DIRT || curBlock.getType() == Material.GRAVEL) {
					SpoutManager.getMaterialManager().overrideBlock(curBlock, which);
				}
				rn = random.nextInt(BlockFace.values().length);
				curBlock = curBlock.getRelative(BlockFace.values()[rn]);
			}
		}

	}
}
