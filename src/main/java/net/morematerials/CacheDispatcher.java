/*
 * This file is part of MoreMaterials.
 * 
 * Copyright (c) 2012 Andre Mohren (IceReaper)
 * 
 * The MIT License
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.morematerials;

import java.io.File;
import java.util.List;
import org.bukkit.Bukkit;
import org.getspout.spoutapi.packet.PacketCacheFile;
import org.getspout.spoutapi.player.SpoutPlayer;

public class CacheDispatcher implements Runnable{
	private List<File> toSend;
	private SpoutPlayer who;
	int which = 0;
	int taskID;
	
	public int getTaskID() {
		return taskID;
	}
	
	public void setTaskID(int newTaskID) {
		taskID = newTaskID;
	}
	
	public CacheDispatcher(SpoutPlayer who, MoreMaterials instance) {
		toSend = instance.getCached();
		this.who = who;
	}

	public void run() {
		if(!who.isOnline()) {
			Bukkit.getScheduler().cancelTask(taskID);
			return;
		}
		sendFile(toSend.get(which));
		which++;
		if(which >= toSend.size()) {
			Bukkit.getScheduler().cancelTask(taskID);
			return;
		}
	}

	private void sendFile(File get) {
		PacketCacheFile pcf = new PacketCacheFile("MoreMaterials",get);
		who.sendPacket(pcf);
	}
	
}
