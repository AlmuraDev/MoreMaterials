/*
 The MIT License

 Copyright (c) 2012 Zloteanu Nichita (ZNickq) and Andre Mohren (IceReaper)

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
package net.morematerials.morematerials.manager;

import net.morematerials.morematerials.Main;
import net.morematerials.morematerials.smp.SmpManager;
import net.morematerials.morematerials.stats.StatHooker;
import net.morematerials.morematerials.utils.WebManager;

public class MainManager {

	private static SmpManager smp;
	private static LegacyManager lm;
	private static WebManager wm;
	private static WGenManager wgm;
	private static StatHooker sh;
	private static Utils u;

	public MainManager(Main plugin) {
		if (smp != null) {
			throw new IllegalStateException("Cannot re-initialize MainManager!");
		}
		wm = new WebManager(plugin);
		smp = new SmpManager(plugin);
		lm = new LegacyManager(plugin);
		wgm = new WGenManager(plugin);
		u = new Utils(plugin);
		
		
		//TODO remove this when website is done.
		sh = new StatHooker(plugin);
	}

	public static SmpManager getSmpManager() {
		return smp;
	}

	public static LegacyManager getLegacyManager() {
		return lm;
	}

	public static WebManager getWebManager() {
		return wm;
	}
	
	public static WGenManager getWGenManager() {
		return wgm;
	}
	
	public static StatHooker getStatHooker() {
		return sh;
	}
	
	public static Utils getUtils() {
		return u;
	}
}
