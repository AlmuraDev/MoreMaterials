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

package net.morematerials.morematerials.handlers;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import net.morematerials.morematerials.Main;
import net.morematerials.morematerials.manager.MainManager;

public class HandlerManager {
	private Map<String, Class<?>> handlers = new HashMap<String, Class<?>>();

	public HandlerManager(Main instance) {
		File folder = new File(instance.getDataFolder() + File.separator + "handlers");
		if (!folder.exists()) {
			folder.mkdir();
		}
		try {
			load(instance.getDataFolder() + File.separator + "handlers");
		} catch (Exception exception) {
		}
	}

	public void load(String directory) throws Exception {
		File dir = new File(directory);

		// Load the vote listener instances.
		ClassLoader loader = new URLClassLoader(
			new URL[] { dir.toURI().toURL() },
			GenericHandler.class.getClassLoader()
		);
		for (File file : dir.listFiles()) {
			String name = file.getName().substring(0, file.getName().lastIndexOf("."));
			Class<?> clazz = loader.loadClass(name);
			Object object = clazz.newInstance();
			if (!(object instanceof GenericHandler)) {
				MainManager.getUtils().log("Not a handler: " + clazz.getSimpleName());
				continue;
			}
			this.handlers.put(name, clazz);
			MainManager.getUtils().log("Loaded handler: " + clazz.getSimpleName());
		}
	}

	public Class<?> getHandler(String handler) {
		if (handlers.containsKey(handler)) {
			return handlers.get(handler);
		}
		return null;
	}
}
