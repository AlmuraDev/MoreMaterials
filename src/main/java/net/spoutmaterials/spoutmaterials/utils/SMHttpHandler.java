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

package net.spoutmaterials.spoutmaterials.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import net.spoutmaterials.spoutmaterials.Main;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

class SMHttpHandler implements HttpHandler {
	private Main instance;

	public SMHttpHandler(Main instance) {
		this.instance = instance;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		// Determine which asset we want
		String fileName = exchange.getRequestURI().getPath().substring(1);
		
		// Used to check for working server.
		Headers headers = exchange.getResponseHeaders();
		if (fileName.equals("status")) {
			headers.add("Content-Type", "text/plain");
			exchange.sendResponseHeaders(200, 0);
			OutputStream outputStream = exchange.getResponseBody();
			outputStream.write("Working!".getBytes());
			outputStream.close();
		// Delivering assets
		} else {
			// Add the required response headers
			if (fileName.endsWith(".png")) {
				headers.add("Content-Type", "image/png");
			} else if (fileName.endsWith(".ogg")) {
				headers.add("Content-Type", "application/ogg");
			}
	
			// Read the file
			File file = new File(this.instance.getDataFolder().getPath() + File.separator + "cache", fileName);
			byte[] bytearray = new byte[(int) file.length()];
			FileInputStream inputStream = new FileInputStream(file);
			BufferedInputStream buffer = new BufferedInputStream(inputStream);
			buffer.read(bytearray, 0, bytearray.length);
	
			// Send response.
			exchange.sendResponseHeaders(200, file.length());
			OutputStream outputStream = exchange.getResponseBody();
			outputStream.write(bytearray, 0, bytearray.length);
			outputStream.close();
		}
	}
}
