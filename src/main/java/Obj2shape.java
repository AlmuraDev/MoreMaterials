import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class Obj2shape {
	
	public static void main(String[] args) {
		File folder = new File(".");
		for (File file : folder.listFiles()) {
			if (file.getName().endsWith(".obj")) {
				try {
					printMessage("Parsing: " + file.getName());
					ArrayList<String> polygons = getPolygonsFromObj(file);
					createShape(file, polygons);
				} catch (Exception exception) {
					printMessage("- ERROR");
				}
			}
		}
	}
	
	private static void createShape(File file, ArrayList<String> polygons) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file.getName().replaceAll(".obj$", ".shape")));
		writer.write("BoundingBox: \"0 0 0 1 1 1\"\r\nShapes:\r\n");
		for (String polygon : polygons) {
			writer.write("  - Texture: 0\r\n    Coords: |\r\n" + polygon);
		}
		writer.close();
	}

	private static ArrayList<String> getPolygonsFromObj(File file) throws IOException {
		HashMap<Integer, String> vertices = new HashMap<Integer, String>();
		ArrayList<String> faces = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new FileReader(file));
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
		printMessage("- Vertices: " + vertices.size());
		printMessage("- Faces: " + faces.size());

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
		printMessage("- Polygons: " + polygons.size());
		return polygons;
	}

	private static void printMessage(String message) {
		Calendar calendar = Calendar.getInstance();
		String date = new SimpleDateFormat("dd.MM.YYYY HH:mm.ss").format(calendar.getTime());
		System.out.println("[" + date + "] " + message);
	}

}
