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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URL;
import java.util.logging.Level;

import net.morematerials.morematerials.Main;
import net.morematerials.morematerials.http.MMHttpHandler;

import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("restriction")
public class WebManager {

	private String assetsUrl;

	public WebManager(Main plugin) {
		// Get an unused port.
		Integer port = 8080;
		for (Integer i = port; i < port + 100; i++) {
			try {
				new ServerSocket(i).close();
				port = i;
			} catch (IOException exception) {
			}
		}

		// Get the hostname of this machine.
		String hostname = "127.0.0.1";
		if (!plugin.getServer().getIp().equals("")) {
			hostname = plugin.getServer().getIp();
		} else {
			try {
				String url = "http://automation.whatismyip.com/n09230945.asp";
				InputStream in = new URL(url).openStream();
				InputStreamReader stream = new InputStreamReader(in);
				BufferedReader reader = new BufferedReader(stream);
				hostname = reader.readLine();
			} catch (IOException exception) {
			}
		}

		// Store assetsUrl
		this.assetsUrl = hostname + ":" + port;

		// Create assets-server.
		try {
			HttpServer srv = HttpServer.create(new InetSocketAddress(port), 0);
			srv.createContext("/", new MMHttpHandler(plugin));
			srv.setExecutor(null);
			srv.start();
			plugin.getUtilsManager().log("Listening: " + this.assetsUrl);
		} catch (IOException exception) {
			plugin.getUtilsManager().log("Assets server error!", Level.SEVERE);
		}
	}

	public String getAssetsUrl(String asset) {
		return "http://" + this.assetsUrl + "/" + asset;
	}

}
