package strat.bot;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import strat.game.Map;
import strat.game.Nation;

public final class ImageManager {
	
	public static File regionView;
	public static File cityView;
	public static File politicalView;
	
	private static Map map;
	private static BufferedImage drawBuffer;
	private static Graphics drawG;
	
	public static void init(Map map, int width, int height) {
		ImageManager.map = map;
		drawBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		drawG = drawBuffer.getGraphics();
		
		singleRenders();
		update();
	}
	
	public static void singleRenders() {
		map.renderRegions(drawG);
		regionView = renderToFile("images/regions.png");
		
		Arrays.fill(((DataBufferInt)drawBuffer.getRaster().getDataBuffer()).getData(), 0);
		map.renderCities(drawG);
		cityView = renderToFile("images/cities.png");
	}
	
	public static void update() { 
		Arrays.fill(((DataBufferInt)drawBuffer.getRaster().getDataBuffer()).getData(), 0);
		map.renderPoliticalView(drawG);
		politicalView = renderToFile("images/political.png");
	}
	
	public static File getArmyView(Nation n) {
		Arrays.fill(((DataBufferInt)drawBuffer.getRaster().getDataBuffer()).getData(), 0);
		map.renderArmyView(drawG, n);
		String fileName = String.format("images/army-%d-%d.png", n.getNationID(), map.getCurrentTurn());
		return renderToFile(fileName);
	}
	
	private static File renderToFile(String fileName) {
		File file = new File(fileName);
		
		if (!file.exists()) {
			try {
				file.createNewFile();
			}
			catch (IOException e) {
				System.out.println("Could not create file " + fileName);
			}
		}
		
		try {
			ImageIO.write(drawBuffer, "png", file);
		}
		catch (IOException e) {
			System.out.println("Could not write image to file " + fileName);
		}
		
		return file;
	}
}
