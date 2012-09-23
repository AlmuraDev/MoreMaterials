package net.morematerials.materials.shapes;

import org.bukkit.util.Vector;
import org.getspout.spoutapi.block.design.GenericBlockDesign;

public abstract class CustomAbstractShape extends GenericBlockDesign {
	
	protected void calculateLightSource(Integer quad, Float f, Float g, Float h, Float i, Float j, Float k, Float l, Float m, Float n) {
		Vector normal = new Vector();
		
		normal.setX(((g - j) * (k - n)) - ((h - k) * (j - m)));
		normal.setY(((h - k) * (i - l)) - ((f - i) * (k - n)));
		normal.setZ(((f - i) * (j - m)) - ((g - j) * (i - l)));
		
		Double length = Math.sqrt((normal.getX() * normal.getX()) + (normal.getY() * normal.getY()) + (normal.getZ() * normal.getZ()));
		
		this.setLightSource(quad, (int) Math.round(normal.getX() / length), (int) Math.round(normal.getY() / length), (int) Math.round(normal.getZ() / length));
	}
}
