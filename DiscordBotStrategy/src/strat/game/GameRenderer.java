package strat.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import java.util.HashMap;

public class GameRenderer {
	public static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 12);
	
	private BufferedImage drawBuffer;
	private Graphics2D drawG;
	private FontRenderContext frc;
	private int[] bufferData;
	
	public GameRenderer(int width, int height) {
		drawBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		drawG = (Graphics2D)drawBuffer.getGraphics();
		bufferData = ((DataBufferInt)drawBuffer.getRaster().getDataBuffer()).getData();
		
		drawG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		frc = new FontRenderContext(null, true, true);
	}
	
	public BufferedImage renderCities(Game game) {
		game.getMap().forEach(h -> {
			Region r = game.getMap().getRegion(h.getRegionID());
			h.render(drawG, r.getColor(), game.getMap().getOffsetX(),
					game.getMap().getOffsetY(), game.getMap().getRadius());
		});
		
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
		
		game.getMap().forEach(h -> {
			h.toPixels(game.getMap().getOffsetX(), game.getMap().getOffsetY(), game.getMap().getRadius(), pos);
			Region r = game.getMap().getRegion(h.getRegionID());
			h.render(drawG, r.getColor(), game.getMap().getOffsetX(),
					game.getMap().getOffsetY(), game.getMap().getRadius(), false);
			
			sx.put(h.getRegionID(), sx.get(h.getRegionID()) + pos[0]);
			sy.put(h.getRegionID(), sy.get(h.getRegionID()) + pos[1]);
			counts.put(h.getRegionID(), counts.get(h.getRegionID()) + 1);
		});
		
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
		
		for (Army a : game.getArmies()) {
			if (a.getOwnerID() == n.getNationID()) {
				a.getHexagon().render(drawG, new Color(0, 120, 0),
						game.getMap().getOffsetX(), game.getMap().getOffsetY(), game.getMap().getRadius(), false);
			}
			
			a.render(drawG);
		}
		
		return drawBuffer;
	}
	
	public BufferedImage renderPoliticalView(Game game) {
		int[] pos = new int[2];
		HashMap<Integer, Integer> sx = new HashMap<>(), sy = new HashMap<>();
		HashMap<Integer, Integer> counts = new HashMap<>();
		
		for (Nation n : game.getNations().values()) {
			sx.put(n.getNationID(), 0);
			sy.put(n.getNationID(), 0);
			counts.put(n.getNationID(), 0);
		}
		
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
	
	private void drawString(String text, int x, int y) {
		drawString(text, x, y, DEFAULT_FONT);
	}
	
	private void drawString(String text, int x, int y, int height) {
		drawString(text, x, y, new Font(DEFAULT_FONT.getName(), DEFAULT_FONT.getStyle(), height));
	}
	
	private void drawString(String text, int x, int y, Font font) {
		TextLayout layout = new TextLayout(text, font, frc);
		Rectangle r = layout.getPixelBounds(frc, 0, 0);
		layout.draw(drawG, -r.x, -r.y);
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
}
