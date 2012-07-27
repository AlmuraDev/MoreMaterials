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

package net.morematerials.manager;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import net.morematerials.MoreMaterials;
import net.morematerials.handlers.GenericHandler;

public class HandlerManager {

	private Map<String, GenericHandler> handlers = new HashMap<String, GenericHandler>();
	private MoreMaterials plugin;

	public HandlerManager(MoreMaterials plugin) {
		this.plugin = plugin;
		File folder = new File(plugin.getDataFolder(), "handlers");
		
		for (File file : folder.listFiles()) {
			if (file.getName().endsWith(".class")) {
				this.load(file);
			}
		}
	}

	public void load(File handlerClass) {
		String className = handlerClass.getName().substring(0, handlerClass.getName().lastIndexOf("."));
		String useName = className.replaceAll("Handler$", "");
		try {
			ClassLoader loader = new URLClassLoader(new URL[] { handlerClass.toURI().toURL() }, GenericHandler.class.getClassLoader());
			Class<?> clazz = loader.loadClass(className);
			Object object = clazz.newInstance();
			if (!(object instanceof GenericHandler)) {
				this.plugin.getUtilsManager().log("Not a handler: " + useName, Level.WARNING);
			} else {
				GenericHandler handler = (GenericHandler) object;
				handler.createAndInit(this.plugin);
				this.handlers.put(useName, handler);
				this.plugin.getUtilsManager().log("Loaded handler: " + useName);
			}
		} catch (Exception exception) {
			this.plugin.getUtilsManager().log("Error loading handler: " + useName, Level.SEVERE);
		}
	}

	public GenericHandler getHandler(String handler) {
		if (this.handlers.containsKey(handler)) {
			return this.handlers.get(handler);
		}
		return null;
	}
}
