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

package net.morematerials.morematerials.stats;

import java.util.logging.Level;
import net.morematerials.morematerials.Main;
import net.morematerials.morematerials.manager.MainManager;

public class StatHooker {
	public StatHooker(final Main plugin) {
		try {
			Metrics metrics = new Metrics();
			// Add our plotters
			metrics.addCustomData(plugin, new Metrics.Plotter() {
				@Override
				public String getColumnName() {
					return "Total Custom Materials";
				}

				@Override
				public int getValue() {
					return MainManager.getSmpManager().getMaterialNumber();
				}
			});
			metrics.beginMeasuringPlugin(plugin);
			System.out.println("Stat tracking activated!");
		} catch (Exception e) {
			MainManager.getUtils().log("Stats error!", Level.SEVERE);
		}
	}
}
