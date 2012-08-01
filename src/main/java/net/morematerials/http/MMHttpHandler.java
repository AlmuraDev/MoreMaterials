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

package net.morematerials.http;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import net.morematerials.MoreMaterials;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

@SuppressWarnings("restriction")
public class MMHttpHandler implements HttpHandler {

	private File dataFolder;

	public MMHttpHandler(MoreMaterials plugin) {
		this.dataFolder = new File(plugin.getDataFolder().getPath(), "cache");
	}

	public void handle(HttpExchange exchange) throws IOException {
		// Determine which asset we want
		String fileName = exchange.getRequestURI().getPath().substring(1);

		// Add the required response headers
		Headers headers = exchange.getResponseHeaders();
		if (fileName.endsWith(".png")) {
			headers.add("Content-Type", "image/png");
		} else if (fileName.endsWith(".ogg")) {
			headers.add("Content-Type", "application/ogg");
		} else {
			headers.add("Content-Type", "text/plain");
		}

		// Deliver requested asset
		File file = new File(dataFolder, fileName);
		if (file.exists()) {
			// Read asset.
			byte[] bytearray = new byte[(int) file.length()];
			BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(file));
			buffer.read(bytearray, 0, bytearray.length);
			// Return asset.
			exchange.sendResponseHeaders(200, file.length());
			OutputStream outputStream = exchange.getResponseBody();
			outputStream.write(bytearray, 0, bytearray.length);
			outputStream.close();
			buffer.close();
		} else {
			// No asset found.
			exchange.sendResponseHeaders(404, 0);
			OutputStream outputStream = exchange.getResponseBody();
			outputStream.close();
		}
	}

}
