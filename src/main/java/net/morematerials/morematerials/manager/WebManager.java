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
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.logging.Level;

import net.morematerials.morematerials.Main;
import net.morematerials.morematerials.utils.SMHttpHandler;

import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("restriction")
public class WebManager {
	private Main instance;

	public WebManager(Main plugin) {
		this.instance = plugin;
		if (Main.getConf().getBoolean("Use-WebServer")) {
			this.startAssetsServer();
		}
	}

	private void startAssetsServer() {
		HttpServer server;
		try {
			server = HttpServer.create(new InetSocketAddress(Main.getConf().getInt("BindPort")), 0);
			server.createContext("/", new SMHttpHandler(this.instance));
			server.setExecutor(null);
			server.start();
			URL assetsStatus = new URL("http://" + Main.getConf().getString("Hostname") + ":" + Main.getConf().getInt("PublicPort") + "/" + "status");
			BufferedReader in = new BufferedReader(new InputStreamReader(assetsStatus.openStream()));
			String inputLine = in.readLine();
			in.close();
			if (inputLine.equals("Working!")) {
				MainManager.getUtils().log(
					"Assets-Host listening on " + Main.getConf().getString("Hostname") + ":" + Main.getConf().getInt("BindPort")
				);
			} else {
				MainManager.getUtils().log(
					"Assets-Host not listening on " + Main.getConf().getString("Hostname") + ":" + Main.getConf().getInt("BindPort"), Level.SEVERE
				);
			}
		} catch (Exception exception) {
		}
	}
}
