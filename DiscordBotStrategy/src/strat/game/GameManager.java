package strat.game;

import java.awt.image.BufferedImage;

public class GameManager {
	private Game game;
	private GameRenderer gameRenderer;
	
	public GameManager() {
		game = null;
		gameRenderer = new GameRenderer(640, 640);
	}
	
	public BufferedImage renderView(int viewID) {
		if (game == null) {
			return null;
		}
		
		switch (viewID) {
			case 0:
				return gameRenderer.renderCities(game);
			case 1:
				return gameRenderer.renderRegions(game);
			case 2:
				return gameRenderer.renderArmyView(game, Nation.NO_NATION);
			case 3:
				return gameRenderer.renderPoliticalView(game);
			case 4:
				return gameRenderer.renderEditorView(game);
			default:
				return null;
		}
	}
	
	public void setGame(Game game) {
		this.game = game;
	}
	
	public Game getGame() {
		return game;
	}
}
