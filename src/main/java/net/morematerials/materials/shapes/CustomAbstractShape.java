package net.morematerials.materials.shapes;

import org.bukkit.util.Vector;
import org.getspout.spoutapi.block.design.GenericBlockDesign;

public abstract class CustomAbstractShape extends GenericBlockDesign {
	
	protected void calculateLightSource(Integer quad, Float x1, Float y1, Float z1, Float x2, Float y2, Float z2, Float x3, Float y3, Float z3) {
		Vector normal = new Vector();
		
		normal.setX(((y1 - y2) * (z2 - z3)) - ((z1 - z2) * (y2 - y3)));
		normal.setY(((z1 - z2) * (x2 - x3)) - ((x1 - x2) * (z2 - z3)));
		normal.setZ(((x1 - x2) * (y2 - y3)) - ((y1 - y2) * (x2 - x3)));
		
		Double length = Math.sqrt((normal.getX() * normal.getX()) + (normal.getY() * normal.getY()) + (normal.getZ() * normal.getZ()));
		
		this.setLightSource(quad, (int) Math.round(normal.getX() / length), (int) Math.round(normal.getY() / length), (int) Math.round(normal.getZ() / length));
	}
}
