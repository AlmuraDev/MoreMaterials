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
import net.morematerials.morematerials.handlers.HandlerManager;
import net.morematerials.morematerials.smp.SmpManager;
import net.morematerials.morematerials.stats.StatHooker;
import net.morematerials.morematerials.utils.WebManager;

public class MainManager {
	private static Main plugin;
	private static SmpManager smpManager;
	private static LegacyManager legacyManager;
	private static WebManager webManager;
	private static WGenManager wgenManager;
	private static StatHooker statsHooker;
	private static Utils utils;
	private static HandlerManager handlerManager;

	public MainManager(Main main) {
		if (smpManager != null) {
			throw new IllegalStateException("Cannot re-initialize MainManager!");
		}
		plugin = main;
		utils = new Utils(plugin);
	}

	public static SmpManager getSmpManager() {
		return smpManager;
	}

	public static LegacyManager getLegacyManager() {
		return legacyManager;
	}

	public static WebManager getWebManager() {
		return webManager;
	}

	public static WGenManager getWGenManager() {
		return wgenManager;
	}

	public static StatHooker getStatHooker() {
		return statsHooker;
	}

	public static Utils getUtils() {
		return utils;
	}
	
	public static HandlerManager getHandlerManager() {
		return handlerManager;
	}

	public static void init() {
		handlerManager = new HandlerManager(plugin);
		webManager = new WebManager(plugin);
		smpManager = new SmpManager(plugin);
		legacyManager = new LegacyManager(plugin);
		wgenManager = new WGenManager(plugin);

		// TODO remove this when api supports this.
		statsHooker = new StatHooker(plugin);
	}
}
