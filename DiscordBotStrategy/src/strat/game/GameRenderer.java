package strat.game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import javax.imageio.ImageIO;

import strat.game.relationships.Alliance;

public class GameRenderer {
	public static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 12);
	
	// hidden away to mask code for future variability between game files
	private static final File regionView = new File("images/regions.png");
	private static final File cityView = new File("images/cities.png");
	private static final File politicalView = new File("images/political.png");
	private static final File nationView = new File("images/nations.png");
	
	private BufferedImage drawBuffer;
	private Graphics2D drawG;
	private int[] bufferData;
	
	public GameRenderer(int width, int height) {
		drawBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		drawG = (Graphics2D)drawBuffer.getGraphics();
		bufferData = ((DataBufferInt)drawBuffer.getRaster().getDataBuffer()).getData();
		
		drawG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	}
	
	public void renderSingleImages(Game game) {
		BufferedImage fileBuffer = new BufferedImage(game.getMap().getWidth(), game.getMap().getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D)fileBuffer.getGraphics();
		int[] data = ((DataBufferInt)fileBuffer.getRaster().getDataBuffer()).getData();
		
		clear();
		renderRegions(game);
		g.drawImage(drawBuffer, 0, 0, null);
		renderToFile(fileBuffer, regionView);
		
		clear();
		Arrays.fill(data, 0);
		renderCities(game);
		g.drawImage(drawBuffer, 0, 0, null);
		renderToFile(fileBuffer, cityView);
	}
	
	public void renderPoliticalImages(Game game) {
		BufferedImage fileBuffer = new BufferedImage(game.getMap().getWidth(), game.getMap().getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D)fileBuffer.getGraphics();
		int[] data = ((DataBufferInt)fileBuffer.getRaster().getDataBuffer()).getData();
		
		clear();
		renderPoliticalView(game);
		g.drawImage(drawBuffer, 0, 0, null);
		renderToFile(fileBuffer, politicalView);
		
		clear();
		Arrays.fill(data, 0);
		renderNationView(game);
		g.drawImage(drawBuffer, 0, 0, null);
		renderToFile(fileBuffer, nationView);
	}
	
	public File renderArmyImage(Game game, Nation nation) {
		BufferedImage fileBuffer = new BufferedImage(game.getMap().getWidth(), game.getMap().getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D)fileBuffer.getGraphics();
		
		File file = new File(String.format("images/army-%d.png", nation.getNationID()));
		
		clear();
		renderArmyView(game, nation);
		g.drawImage(drawBuffer, 0, 0, null);
		renderToFile(fileBuffer, file);
		
		return file;
	}
	
	public BufferedImage renderCities(Game game) {
		drawG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		game.getMap().forEach(h -> {
			Region r = game.getMap().getRegion(h.getRegionID());
			h.render(drawG, r.getColor(), game.getMap().getOffsetX(),
					game.getMap().getOffsetY(), game.getMap().getRadius(), false);
		});
		drawG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		game.getMap().getCities().forEach(c -> c.render(drawG));
		
		return drawBuffer;
	}
	
	public BufferedImage renderRegions(Game game) {
		int[] pos = new int[2];
		HashMap<Integer, Integer> sx = new HashMap<>(), sy = new HashMap<>();
		HashMap<Integer, Integer> counts = new HashMap<>();
		
		for (Region r : game.getMap().getRegions().values()) {
			sx.put(r.getRegionID(), 0);
			sy.put(r.getRegionID(), 0);
			counts.put(r.getRegionID(), 0);
		}
		
		drawG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		game.getMap().forEach(h -> {
			h.toPixels(game.getMap().getOffsetX(), game.getMap().getOffsetY(), game.getMap().getRadius(), pos);
			Region r = game.getMap().getRegion(h.getRegionID());
			h.render(drawG, r.getColor(), game.getMap().getOffsetX(),
					game.getMap().getOffsetY(), game.getMap().getRadius(), false);
			
			sx.put(h.getRegionID(), sx.get(h.getRegionID()) + pos[0]);
			sy.put(h.getRegionID(), sy.get(h.getRegionID()) + pos[1]);
			counts.put(h.getRegionID(), counts.get(h.getRegionID()) + 1);
		});
		drawG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		drawG.setColor(Color.BLACK);
		
		for (Region r : game.getMap().getRegions().values()) {
			if (counts.get(r.getRegionID()) == 0) {
				continue;
			}
			
			int px = sx.get(r.getRegionID()) / counts.get(r.getRegionID()) - r.getName().length() * drawG.getFont().getSize() / 4;
			int py = sy.get(r.getRegionID()) / counts.get(r.getRegionID());
			
			drawG.drawString(r.getName(), px, py); // TODO: replace with improved string renderer
		}
		
		return drawBuffer;
	}
	
	public BufferedImage renderArmyView(Game game, Nation n) {
		game.getMap().forEach(h -> {
			Region r = game.getMap().getRegion(h.getRegionID());
			h.render(drawG, r.getColor(), game.getMap().getOffsetX(),
					game.getMap().getOffsetY(), game.getMap().getRadius(), true);
		});
		
		for (Army a : game.getArmies()) {
			if (a.getOwnerID() == n.getNationID()) {
				for (int i = 0, l = a.getPendingMoves().size(); i < l; ++i) {
					Hexagon h = a.getPendingMoves().get(i);
					h.render(drawG, new Color(0, i * 128 / l + 128, 0),
							game.getMap().getOffsetX(), game.getMap().getOffsetY(), game.getMap().getRadius(), false);
				}
			}
		}
		
		game.getMap().getCities().forEach(c -> c.render(drawG, false));
		
		HashSet<Army> rendered = new HashSet<>();
		
		for (Army a : game.getArmies()) {
			if (a.getOwnerID() == n.getNationID() || game.findAllianceBetween(a.getOwner(), n) != null) {
				a.getHexagon().render(drawG, new Color(0, 255, 0),
						game.getMap().getOffsetX(), game.getMap().getOffsetY(), game.getMap().getRadius(), false);
				a.render(drawG);
				rendered.add(a);
				
				if (a.getOwnerID() == n.getNationID()) {
					for (Army a2 : game.getArmies()) {
						if (a2.getOwnerID() != a.getOwnerID()
								&& !rendered.contains(a2)
								&& a2.getHexagon().distanceFrom(a.getHexagon()) <= GameRules.getRulei("movesPerTurn") + 1) {
							a2.render(drawG);
							rendered.add(a2);
						}
					}
				}
			}
			else if (!rendered.contains(a)) {
				for (City c : game.getMap().getCities()) {
					if (a.getHexagon().distanceFrom(c.getHexagon()) <= GameRules.getRulei("movesPerTurn") + 1) {
						a.render(drawG);
						rendered.add(a);
					}
				}
			}
		}
		
		return drawBuffer;
	}
	
	public BufferedImage renderPoliticalView(Game game) {
		int[] pos = new int[2];
		HashMap<Alliance, Integer> asx = new HashMap<>(), asy = new HashMap<>();
		HashMap<Alliance, Integer> aCounts = new HashMap<>();
		HashMap<Integer, Integer> sx = new HashMap<>(), sy = new HashMap<>();
		HashMap<Integer, Integer> counts = new HashMap<>();
		
		for (Nation n : game.getNations().values()) {
			sx.put(n.getNationID(), 0);
			sy.put(n.getNationID(), 0);
			counts.put(n.getNationID(), 0);
		}
		
		for (Alliance a : game.getAlliances()) {
			asx.put(a, 0);
			asy.put(a, 0);
			aCounts.put(a, 0);
		}
		
		drawG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		game.getMap().forEach(h -> {
			h.toPixels(game.getMap().getOffsetX(),
					game.getMap().getOffsetY(), game.getMap().getRadius(), pos);
			Nation n = game.getNation(game.getMap().getRegion(h.getRegionID()).getOwnerID());
			Alliance a = game.getAllianceForNation(n);
			
			if (a != null) {
				h.render(drawG, new Color(a.getRGB()), game.getMap().getOffsetX(),
						game.getMap().getOffsetY(), game.getMap().getRadius(), false);
				
				asx.put(a, asx.get(a) + pos[0]);
				asy.put(a, asy.get(a) + pos[1]);
				aCounts.put(a, aCounts.get(a) + 1);
			}
			else {
				h.render(drawG, n.getColor(), game.getMap().getOffsetX(),
						game.getMap().getOffsetY(), game.getMap().getRadius(), false);
				
				sx.put(n.getNationID(), sx.get(n.getNationID()) + pos[0]);
				sy.put(n.getNationID(), sy.get(n.getNationID()) + pos[1]);
				counts.put(n.getNationID(), counts.get(n.getNationID()) + 1);
			}
		});
		drawG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		game.getMap().getCities().forEach(c -> c.render(drawG, false));
		
		drawG.setColor(Color.BLACK);
		
		for (Nation n : game.getNations().values()) {
			if (counts.get(n.getNationID()) == 0) {
				continue;
			}
			
			int px = sx.get(n.getNationID()) / counts.get(n.getNationID()) - n.getName().length() * drawG.getFont().getSize() / 4;
			int py = sy.get(n.getNationID()) / counts.get(n.getNationID());
			
			drawG.drawString(n.getName(), px, py); // TODO: change to better text renderer
		}
		
		for (Alliance a : game.getAlliances()) {
			int px = asx.get(a) / aCounts.get(a) - a.getName().length() * drawG.getFont().getSize() / 4;
			int py = asy.get(a) / aCounts.get(a);
			
			drawG.drawString(a.getName(), px, py); // TODO: change to better text renderer
		}
		
		return drawBuffer;
	}
	
	public BufferedImage renderNationView(Game game) {
		int[] pos = new int[2];
		HashMap<Integer, Integer> sx = new HashMap<>(), sy = new HashMap<>();
		HashMap<Integer, Integer> counts = new HashMap<>();
		
		for (Nation n : game.getNations().values()) {
			sx.put(n.getNationID(), 0);
			sy.put(n.getNationID(), 0);
			counts.put(n.getNationID(), 0);
		}
		
		drawG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		game.getMap().forEach(h -> {
			h.toPixels(game.getMap().getOffsetX(),
					game.getMap().getOffsetY(), game.getMap().getRadius(), pos);
			Nation n = game.getNation(game.getMap().getRegion(h.getRegionID()).getOwnerID());
			
			h.render(drawG, n.getColor(), game.getMap().getOffsetX(),
					game.getMap().getOffsetY(), game.getMap().getRadius(), false);
			
			sx.put(n.getNationID(), sx.get(n.getNationID()) + pos[0]);
			sy.put(n.getNationID(), sy.get(n.getNationID()) + pos[1]);
			counts.put(n.getNationID(), counts.get(n.getNationID()) + 1);
		});
		drawG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		game.getMap().getCities().forEach(c -> c.render(drawG, false));
		
		drawG.setColor(Color.BLACK);
		
		for (Nation n : game.getNations().values()) {
			if (counts.get(n.getNationID()) == 0) {
				continue;
			}
			
			int px = sx.get(n.getNationID()) / counts.get(n.getNationID()) - n.getName().length() * drawG.getFont().getSize() / 4;
			int py = sy.get(n.getNationID()) / counts.get(n.getNationID());
			
			drawG.drawString(n.getName(), px, py); // TODO: change to better text renderer
		}
		
		return drawBuffer;
	}
	
	public BufferedImage renderEditorView(Game game) {
		game.getMap().forEach(h -> h.render(drawG, Color.WHITE,
				game.getMap().getOffsetX(), game.getMap().getOffsetY(), game.getMap().getRadius(), true));
		
		return drawBuffer;
	}
	
	public void clear() {
		Arrays.fill(bufferData, 0);
	}
	
	public BufferedImage getRenderTarget() {
		return drawBuffer;
	}
	
	public int getWidth() {
		return drawBuffer.getWidth();
	}
	
	public int getHeight() {
		return drawBuffer.getHeight();
	}
	
	public File getRegionView() {
		return regionView;
	}
	
	public File getCityView() {
		return cityView;
	}
	
	public File getPoliticalView() {
		return politicalView;
	}
	
	public File getNationView() {
		return nationView;
	}
	
	public static void drawOutlinedString(Graphics g, String str, int x, int y) {
	    Color outlineColor = new Color(0xF0F0F0);
	    Color fillColor = Color.BLACK;
	    BasicStroke outlineStroke = new BasicStroke(0.8f);

	    if (g instanceof Graphics2D) {
	    	g.translate(x, y);
	        Graphics2D g2 = (Graphics2D) g;

	        Color originalColor = g2.getColor();
	        Stroke originalStroke = g2.getStroke();
	        RenderingHints originalHints = g2.getRenderingHints();

	        GlyphVector glyphVector = g.getFont().createGlyphVector(g2.getFontRenderContext(), str);
	        Shape textShape = glyphVector.getOutline();

	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                RenderingHints.VALUE_ANTIALIAS_ON);
	        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
	                RenderingHints.VALUE_RENDER_DEFAULT);

	        g2.setColor(outlineColor);
	        g2.setStroke(outlineStroke);
	        g2.draw(textShape);

	        g2.setColor(fillColor);
	        g2.fill(textShape);
	        g2.setColor(originalColor);
	        g2.setStroke(originalStroke);
	        g2.setRenderingHints(originalHints);
	        
	        g.translate(-x, -y);
	    }
	}
	
	private static void renderToFile(BufferedImage image, File file) {
		if (!file.exists()) {
			try {
				file.createNewFile();
			}
			catch (IOException e) {
				System.out.println("Could not create file " + file.getName());
			}
		}
		
		try {
			ImageIO.write(image, "png", file);
		}
		catch (IOException e) {
			System.out.println("Could not write image to file " + file.getName());
		}
	}
}
