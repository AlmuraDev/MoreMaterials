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

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import net.morematerials.morematerials.Main;
import net.morematerials.morematerials.handlers.GenericHandler;

public class HandlerManager {

	private Map<String, Class<?>> handlers = new HashMap<String, Class<?>>();
	private UtilsManager um;

	public HandlerManager(Main plugin) {
		this.um = plugin.getUtilsManager();
		File folder = new File(plugin.getDataFolder(), "handlers");
		for (File file : folder.listFiles()) {
			// TODO compile .java files here
			if (file.getName().endsWith(".class")) {
				this.load(file);
			}
		}
	}

	public void load(File handlerClass) {
		Integer index = handlerClass.getName().lastIndexOf(".");
		String className = handlerClass.getName().substring(0, index);
		try {
			URL[] url = new URL[] { handlerClass.toURI().toURL() };
			ClassLoader genericLoader = GenericHandler.class.getClassLoader();
			ClassLoader loader = new URLClassLoader(url, genericLoader);
			Class<?> clazz = loader.loadClass(className);
			Object object = clazz.newInstance();
			if (!(object instanceof GenericHandler)) {
				this.um.log("Not a handler: " + className, Level.WARNING);
			} else {
				this.handlers.put(className, clazz);
				this.um.log("Loaded handler: " + className);
			}
		} catch (Exception exception) {
			this.um.log("Error loading handler: " + className, Level.SEVERE);
		}
	}

	public Class<?> getHandler(String handler) {
		if (handlers.containsKey(handler)) {
			return handlers.get(handler);
		}
		return null;
	}
}
