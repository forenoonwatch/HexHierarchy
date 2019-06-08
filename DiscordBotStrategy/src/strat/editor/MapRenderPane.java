package strat.editor;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import strat.game.Game;
import strat.game.GameRenderer;
import strat.game.Nation;

@SuppressWarnings("serial")
public class MapRenderPane extends JPanel {
	private GameRenderer renderer;
	private int currentView;
	
	private BufferedImage background;
	private int bgOffsetX;
	private int bgOffsetY;
	
	private double bgScale;
	
	private int finalOffsetX;
	private int finalOffsetY;
	
	public MapRenderPane() {
		Dimension d = new Dimension(800, 600);
		setMinimumSize(d);
		setPreferredSize(d);
		setSize(d);
		
		setFocusable(true);
		setDoubleBuffered(true);
		setEnabled(true);
		
		renderer = new GameRenderer(1200, 1200);
		
		background = null;
		bgOffsetX = 0;
		bgOffsetY = 0;
		
		bgScale = 1.0;
		
		finalOffsetX = 0;
		finalOffsetY = 0;
		
		requestFocus();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		if (background != null) {
			g.drawImage(background, finalOffsetX, finalOffsetY,
					(int)(background.getWidth() * bgScale), (int)(background.getHeight() * bgScale), null);
		}
		
		g.drawImage(renderer.getRenderTarget(), 0, 0, null);
	}
	
	public void renderGame(Game game, int view) {
		renderer.clear();
		
		if (game == null) {
			repaint();
			return;
		}
		
		finalOffsetX = (int)(game.getMap().getOffsetX() + bgOffsetX - 0.5 * game.getMap().getWidth() * bgScale);
		finalOffsetY = (int)(game.getMap().getOffsetY() + bgOffsetY - 0.5 * game.getMap().getHeight() * bgScale);
		
		currentView = view;
		
		switch (view) {
			case 0:
				renderer.renderCities(game);
				break;
			case 1:
				renderer.renderEditorView(game);
				break;
			case 2:
				renderer.renderArmyView(game, Nation.NO_NATION);
				break;
			case 3:
				renderer.renderRegions(game);
				break;
			default:
				return;
		}
		
		repaint();
		requestFocus();
	}
	
	public void renderGame(Game game) {
		renderGame(game, currentView);
	}
	
	public void setBackground(BufferedImage background, int bgOffsetX, int bgOffsetY) {
		this.background = background;
		this.bgOffsetX = bgOffsetX;
		this.bgOffsetY = bgOffsetY;
	}
	
	public BufferedImage getBackgroundImage() {
		return background;
	}
	
	public void rescale(Game game, int scl) {
		bgScale = Math.max(0.2, Math.min(5, bgScale - scl * 0.1));
		finalOffsetX = (int)(game.getMap().getOffsetX() + bgOffsetX - 0.5 * game.getMap().getWidth() * bgScale);
		finalOffsetY = (int)(game.getMap().getOffsetY() + bgOffsetY - 0.5 * game.getMap().getHeight() * bgScale);
		
		repaint();
	}
}
