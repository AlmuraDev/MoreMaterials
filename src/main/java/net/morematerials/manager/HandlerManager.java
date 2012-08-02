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
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.commons.lang.StringUtils;
import org.bukkit.event.Event;

import net.morematerials.MoreMaterials;
import net.morematerials.handlers.GenericHandler;

public class HandlerManager {

	private Map<String, GenericHandler> handlers = new HashMap<String, GenericHandler>();
	private MoreMaterials plugin;
	private List<Map<String, Object>> handlerRegister = new ArrayList<Map<String, Object>>();

	private List<String> compilerOptions = new ArrayList<String>();

	public HandlerManager(MoreMaterials plugin) {
		this.plugin = plugin;
		
		File folder = new File(plugin.getDataFolder(), "handlers");
		
		// We can only compile if the JDK is found.
		if (ToolProvider.getSystemJavaCompiler() != null) {
			this.prepareCompiler();
			for (File file : folder.listFiles()) {
				if (file.getName().endsWith(".java")) {
					try {
						if (this.compile(file)) {
							this.load(new File(file.getName().replaceAll("java$", "class")));
						}
					} catch (Exception exception) {
						System.out.println(exception.getMessage());
					}
				}
			}
		} else {
			this.plugin.getUtilsManager().log("JDK not found - Handlers may not work.", Level.WARNING);
			if (this.plugin.getConfig().getBoolean("BinaryHandlers", false)) {
				for (File file : folder.listFiles()) {
					if (file.getName().endsWith(".class")) {
						this.load(file);
					}
				}
			}
		}
	}

	private Boolean compile(File file) throws IOException {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
		Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(file);
		CompilationTask task = compiler.getTask(null, fileManager, null, this.compilerOptions, null, compilationUnits);
		return task.call();
	}

	public void load(File handlerClass) {
		String className = handlerClass.getName().substring(0, handlerClass.getName().lastIndexOf("."));
		String useName = className.replaceAll("Handler$", "");
		try {
			ClassLoader loader = new URLClassLoader(new URL[] { handlerClass.getParentFile().toURI().toURL() }, GenericHandler.class.getClassLoader());
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

	public void registerHandler(String eventType, Integer materialId, Map<String, Object> config) {
		//TODO this is ugly but works, refactor this!
		config.put("__materialID__", materialId);
		config.put("__eventTYPE__", eventType);
		this.handlerRegister.add(config);
	}
	
	public void triggerHandlers(String eventType, Integer materialId, Event event) {
		for (Map<String, Object> config : this.handlerRegister) {
			if (((Integer) config.get("__materialID__")).equals(materialId) && ((String) config.get("__eventTYPE__")).equals(eventType)) {
				this.getHandler((String) config.get("Name")).onActivation(event, config);
			}
		}
	}
	
	private void prepareCompiler() {
		//Search & add dependencies
		List<String> libs = new ArrayList<String>();
		libs.add(System.getProperty("java.class.path"));
		
		// Add all plugins to allow using other plugins.
		for (File lib : (new File("plugins")).listFiles()) {
			if (lib.getName().endsWith(".jar")) {
				libs.add("plugins/" + lib.getName());
			}
		}
		
		// Set the classpath.
		this.compilerOptions.addAll(Arrays.asList("-classpath", StringUtils.join(libs, File.pathSeparator)));
	}
}
