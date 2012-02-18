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

package net.morematerials.morematerials.utils;

import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.logging.Level;
import net.morematerials.morematerials.Main;
import net.morematerials.morematerials.manager.MainManager;
import org.json.JSONArray;
import org.json.JSONObject;

public class WebManager {
	private Main instance;
	public static String newVer = null;
	public WebManager(Main plugin) {
		this.instance = plugin;
		if (plugin.useAssetsServer()) {
			this.startAssetsServer(plugin.getPort());
		}
	}

	private void startAssetsServer(Integer port) {
		HttpServer server;
		try {
			server = HttpServer.create(new InetSocketAddress(port), 0);
		    server.createContext("/", new SMHttpHandler(this.instance));
		    server.setExecutor(null);
		    server.start();
		    URL assetsStatus = new URL(this.instance.getAssetsUrl() + "status");
    		BufferedReader in = new BufferedReader(new InputStreamReader(assetsStatus.openStream()));
			String inputLine = in.readLine();
			in.close();
		    if (inputLine.equals("Working!")) {
		    	MainManager.getUtils().log("Assets-Host listening on " + this.instance.getAssetsUrl());
		    } else {
		    	MainManager.getUtils().log("Assets-Host not listening on " + this.instance.getAssetsUrl(), Level.SEVERE);
		    }
		} catch (Exception exception) {
	    	System.out.println(exception);
	    	MainManager.getUtils().log("DEBUG" + this.instance.getAssetsUrl());
		}
	}

	public boolean updateAvailable() {
		String version = this.instance.getDescription().getVersion();
		String newest = version;
		try {
			JSONObject json = this.readJsonFromApi("action=version");
			JSONArray versions = json.getJSONArray("versions");
			// Last entry is always newest version!
			newest = versions.getString(versions.length()-1);
		} catch (Exception exception) {
	    	MainManager.getUtils().log(exception.getMessage(), Level.SEVERE);
		}
		if (!version.equals(newest)) {
			newVer = newest;
		}
		return !newest.equals(version);
	}
	
	private JSONObject readJsonFromApi(String params) throws Exception {
		JSONObject json = new JSONObject();
		try {
			URL versionCheck = new URL(this.instance.getApiUrl() + "?" + params);
			BufferedReader in = new BufferedReader(new InputStreamReader(versionCheck.openStream()));
		    StringBuilder sb = new StringBuilder();
		    int cp;
		    while ((cp = in.read()) != -1) {
		      sb.append((char) cp);
		    }
			in.close();
			json = new JSONObject(sb.toString());
		} catch (Exception e) {
		}
		// We always have "error" which is true | false from our json. If not something went wrong!
		if (!json.has("error")) {
			throw new Exception("Cannot access API!");
		}
		// If error is specified, data contains always our error message.
		if (json.getBoolean("error")) {
			throw new Exception(json.getString("data"));
		}
		// Else data contains the response json.
		return json.getJSONObject("data");
	}

	public void downloadSmp(String smpName, String version) {
		//TODO here should a .smp be downloaded (version -1 = newest)
	}
}
