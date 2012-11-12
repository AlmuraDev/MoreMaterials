package net.morematerials.materials.shapes;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import net.morematerials.MoreMaterials;

import org.getspout.spoutapi.block.design.GenericBlockDesign;
import org.getspout.spoutapi.block.design.Texture;

public class CustomObjShape extends GenericBlockDesign {
	
	public CustomObjShape(MoreMaterials plugin, String shapeData, String textureUrl, List<String> coordList) {
		String[] lines = shapeData.split("\n");
		
		// Surrounded blocks will always be drawn.
		this.setRenderPass(1);

		// Default settings.
		this.setMinBrightness(0.0F);
		this.setBrightness(0.5F);
		this.setMaxBrightness(1.0F);

		// Set Bounding Box
		setBoundingBox(0, 0, 0, 1, 1, 1);

		// Get texture.
		BufferedImage bufferedImage = plugin.getAssetManager().getCachedImage(textureUrl);
		Texture texture = new Texture(plugin, textureUrl, bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getHeight());
		this.setTexture(plugin, texture);
		
		// First parse the file in all its data
		ArrayList<String> vertices = new ArrayList<String>();
		ArrayList<String> textureCoords = new ArrayList<String>();
		ArrayList<String> faces = new ArrayList<String>();

		// Load the obj information
		for (String line : lines) {
			if (line.startsWith("v ")) {
				vertices.add(line.substring(2));
			} else if (line.startsWith("vt ")) {
				textureCoords.add(line.substring(3));
			} else if (line.startsWith("f ")) {
				faces.add(line.substring(2));
			}
		}

		// Create the shape
		this.setQuadNumber(faces.size());
		int i = 0;
		for (String face : faces) {
			String[] faceVertex = face.trim().split(" ");
			//FIXME implement non-quads into spout!
			if (faceVertex.length == 3) {
				faceVertex = (face.trim() + " " + faceVertex[2]).split(" ");
			}
			for (Integer j = 0; j < faceVertex.length; j++) {
				String[] faceInfo = faceVertex[j].split("/");
				
				// Get the correct lines.
				String[] vertexInfo = vertices.get(Integer.parseInt(faceInfo[0]) - 1).split(" ");
				String[] coordInfo = textureCoords.get(Integer.parseInt(faceInfo[1]) - 1).split(" ");
				this.setVertex(
					// Vertex index
					i, j,
					// Vertex coord
					Float.parseFloat(vertexInfo[0]), Float.parseFloat(vertexInfo[1]), Float.parseFloat(vertexInfo[2]),
					// Vertex texture mapping
					Float.parseFloat(coordInfo[0]), Float.parseFloat(coordInfo[1])
				);
			}
			i++;
		}
		this.calculateLightSources();
	}
}
