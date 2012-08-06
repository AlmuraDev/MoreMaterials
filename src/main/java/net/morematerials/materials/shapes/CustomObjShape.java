package net.morematerials.materials.shapes;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.morematerials.MoreMaterials;

import org.getspout.spoutapi.block.design.GenericBlockDesign;
import org.getspout.spoutapi.block.design.Quad;
import org.getspout.spoutapi.block.design.SubTexture;
import org.getspout.spoutapi.block.design.Texture;

public class CustomObjShape extends GenericBlockDesign {
	public CustomObjShape(MoreMaterials plugin, String shapeData, String textureUrl, List<String> coordList) throws IOException {
		File file = new File(shapeData);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		// Surrounded blocks will always be drawn.
		this.setRenderPass(1);

		// Default settings.
		this.setMinBrightness(0.0F);
		this.setBrightness(0.5F);
		this.setMaxBrightness(1.0F);
		
		// Set Bounding Box
		setBoundingBox(0, 0, 0, 1, 1, 1);
		
		// Get texture.
		BufferedImage bufferedImage = plugin.getWebManager().getCachedImage(textureUrl);
		Texture texture = new Texture(plugin, textureUrl, bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getHeight());
		this.setTexture(plugin, texture);
		
		// Load the obj faces and vertices
		ArrayList<String> faces = new ArrayList<String>();
		HashMap<Integer, String> vertices = new HashMap<Integer, String>();
		String line;
		while ((line = reader.readLine()) != null)   {
			if (line.startsWith("v ")) {
				String vertexLine = line.substring(2);
				vertexLine = vertexLine.replaceAll("\\.([0-9]{3})[0-9]+", ".$1");
				vertices.put(vertices.size() + 1, vertexLine);
			}
			if (line.startsWith("f ")) {
				faces.add(line.substring(2));
			}
		}
		reader.close();
		
		// Building subtextures.
		ArrayList<SubTexture> subTextures = new ArrayList<SubTexture>();
		String[] coords;
		for (Integer i = 0; i < coordList.size(); i++) {
			coords = coordList.get(i).split("[\\s]+");
			//FIXME spout reads Y from the lower left - this needs to be fixed!
			SubTexture subtex = new SubTexture(texture, Integer.parseInt(coords[0]), bufferedImage.getHeight() - (Integer.parseInt(coords[1]) + Integer.parseInt(coords[3])), Integer.parseInt(coords[2]), Integer.parseInt(coords[3]));
			subTextures.add(subtex);
		}
		
		// Build the polygons list
		ArrayList<String> polygons = new ArrayList<String>();
		String polygon = null;
		for (String face : faces) {
			for (String index : face.split(" ")) {
				if (polygon == null) {
					polygon = "      " + vertices.get(Integer.parseInt(index.split("/")[0])) + "\r\n";
				} else {
					polygon += "      " + vertices.get(Integer.parseInt(index.split("/")[0])) + "\r\n";
				}
			}
			polygons.add(polygon);
			polygon = null;
		}
		
		// Create the shape
		setQuadNumber(polygons.size());
		
		int i = 0;
		for (Object oshape : polygons) {
			@SuppressWarnings("unchecked")
			Map<String, Object> shape = (Map<String, Object>) oshape;
			Quad quad = new Quad(i, subTextures.get(0));
			int j = 0;
			String[] coordLine = null;
			for (String pline : ((String) shape.get("Coords")).split("\\r?\\n")) {
				coordLine = pline.split(" ");
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
