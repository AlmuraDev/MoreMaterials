package net.morematerials.morematerials.materials;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import net.morematerials.morematerials.Main;
import net.morematerials.morematerials.smp.SmpManager;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.getspout.spoutapi.block.design.GenericBlockDesign;
import org.getspout.spoutapi.block.design.Quad;
import org.getspout.spoutapi.block.design.Texture;

public class CustomShape extends GenericBlockDesign {

	public CustomShape(SmpManager smpManager, InputStream inputStream, String textureName, int blockID) throws IOException {
		// Make sure texture file is present.
		File textureFile = null;
		if (Main.getConf().getBoolean("Use-WebServer")) {
			textureFile = new File(
				smpManager.getPlugin().getDataFolder().getPath() + File.separator + "cache",
				textureName.substring(textureName.lastIndexOf("/"))
			);
		} else {
			textureFile = new File(
				smpManager.getPlugin().getDataFolder().getPath() + File.separator + "cache",
				textureName
			);
		}
		
		// Process texture file
		BufferedImage bufferedImage = ImageIO.read(textureFile);
		int textureCount = bufferedImage.getWidth() / bufferedImage.getHeight();
		Texture texture = new Texture(
			smpManager.getPlugin(), textureName,
			bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getHeight()
		);
		int[] textureId = new int[textureCount];
		for (int i = 0; i < textureCount; i++) {
			textureId[i] = i;
		}
			
		// For transparent stuff
		if (blockID == 20) {
			this.setRenderPass(1);
		}
		
		// Reading the config
		YamlConfiguration config = new YamlConfiguration();
		try {
			config.load(inputStream);
		} catch (InvalidConfigurationException exception) {
		}
		
		String boundingString = config.getString("BoundingBox");
		String[] boundingBox = boundingString.split(" ");
		List<Object> shapes = config.getList("Shapes");

		// Bounding box
		int xMin = Integer.parseInt(boundingBox[0]);
		int yMin = Integer.parseInt(boundingBox[1]);
		int zMin = Integer.parseInt(boundingBox[2]);
		int xMax = Integer.parseInt(boundingBox[3]);
		int yMax = Integer.parseInt(boundingBox[4]);
		int zMax = Integer.parseInt(boundingBox[5]);
		setBoundingBox(xMin, yMin, zMin, xMax, yMax, zMax);

		//General options
		setMinBrightness(0.0F).setMaxBrightness(1.0F).setTexture(smpManager.getPlugin(), texture);
		
		// Building the shape together
		setQuadNumber(shapes.toArray().length);
		int i = 0;
		for (Object oshape : shapes) {
			Map<String, Object> shape = (Map<String, Object>) oshape;
			String cords = (String) shape.get("Coords");
			Quad quad = new Quad(i, texture.getSubTexture(textureId[(Integer) shape.get("Texture")]));
			int j = 0;
			for (String line : cords.split("\\r?\\n")) {
				String[] coordLine = line.split(" ");
				quad.addVertex(j,
					Float.parseFloat(coordLine[0]),
					Float.parseFloat(coordLine[1]),
					Float.parseFloat(coordLine[2])
				);
				j++;
			}
			setLightSource(i, 0, 1, 0);
			setQuad(quad);
			i++;
		}
	}

	private void setLightSource(int i, String string, String string2, String string3) {
		setLightSource(i, Integer.parseInt(string), Integer.parseInt(string2), Integer.parseInt(string3));
	}

}
