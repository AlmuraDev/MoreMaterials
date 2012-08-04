package net.morematerials.materials.shapes;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.morematerials.MoreMaterials;

import org.bukkit.configuration.file.YamlConfiguration;
import org.getspout.spoutapi.block.design.GenericBlockDesign;
import org.getspout.spoutapi.block.design.Quad;
import org.getspout.spoutapi.block.design.SubTexture;
import org.getspout.spoutapi.block.design.Texture;

public class CustomShapeShape extends GenericBlockDesign {

	public CustomShapeShape(MoreMaterials plugin, String shapeData, String textureUrl, List<String> coordList) {
		// Load the .shape format string.
		YamlConfiguration config = new YamlConfiguration();
		try {
			config.load(shapeData);
		} catch (Exception exception) {
		}
		
		// Surrounded blocks will always be drawn.
		this.setRenderPass(1);

		// Default settings.
		this.setMinBrightness(0.0F);
		this.setBrightness(0.5F);
		this.setMaxBrightness(1.0F);

		String[] boundingBox = config.getString("BoundingBox").split(" ");

		// Bounding box
		Float xMin = Float.parseFloat("0" + boundingBox[0]);
		Float yMin = Float.parseFloat("0" + boundingBox[1]);
		Float zMin = Float.parseFloat("0" + boundingBox[2]);
		Float xMax = Float.parseFloat("0" + boundingBox[3]);
		Float yMax = Float.parseFloat("0" + boundingBox[4]);
		Float zMax = Float.parseFloat("0" + boundingBox[5]);
		setBoundingBox(xMin, yMin, zMin, xMax, yMax, zMax);
		
		// Get texture.
		BufferedImage bufferedImage = plugin.getWebManager().getCachedImage(textureUrl);
		Texture texture = new Texture(plugin, textureUrl, bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getHeight());
		this.setTexture(plugin, texture);

		// Building subtextures.
		ArrayList<SubTexture> subTextures = new ArrayList<SubTexture>();
		String[] coords;
		for (Integer i = 0; i < coordList.size(); i++) {
			coords = coordList.get(i).split("[\\s]+");
			//FIXME spout reads Y from the lower left - this needs to be fixed!
			SubTexture subtex = new SubTexture(texture, Integer.parseInt(coords[0]), bufferedImage.getHeight() - (Integer.parseInt(coords[1]) + Integer.parseInt(coords[3])), Integer.parseInt(coords[2]), Integer.parseInt(coords[3]));
			subTextures.add(subtex);
		}

		// Building the shape together
		List<?> shapes = config.getList("Shapes");
		setQuadNumber(shapes.toArray().length);
		int i = 0;
		for (Object oshape : shapes) {
			@SuppressWarnings("unchecked")
			Map<String, Object> shape = (Map<String, Object>) oshape;
			Integer subId = (Integer) shape.get("Texture");
			Quad quad = new Quad(i, subTextures.get((subTextures.size() > subId ? subId : 0)));
			int j = 0;
			String[] coordLine = null;
			for (String line : ((String) shape.get("Coords")).split("\\r?\\n")) {
				coordLine = line.split(" ");
				quad.addVertex(j,
					Float.parseFloat("0" + coordLine[0]),
					Float.parseFloat("0" + coordLine[1]),
					Float.parseFloat("0" + coordLine[2])
				);
				j++;
			}
			// Allow triangles.
			if (j == 3) {
				quad.addVertex(j,
					Float.parseFloat("0" + coordLine[0]),
					Float.parseFloat("0" + coordLine[1]),
					Float.parseFloat("0" + coordLine[2])
				);
			}
			setQuad(quad);
			i++;
		}
	}
	
}
