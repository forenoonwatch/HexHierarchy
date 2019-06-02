package strat.editor;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import strat.commands.CommandRegistry;
import strat.commands.InputLevel;
import strat.commands.Response;
import strat.game.City;
import strat.game.Game;
import strat.game.GameManager;
import strat.game.Hexagon;
import strat.game.LogEntry;
import strat.game.Nation;

@SuppressWarnings("serial")
public class Editor extends Canvas implements MouseListener, KeyListener {
	private GameManager gameManager;
	private int renderType = 0;
	private int regionID = 0;
	private int maxRegionID;
	
	public static final int NUM_RENDER_TYPES = 4;
	
	@SuppressWarnings("unused")
	private BufferedImage background;
	
	private JTextField field;
	
	private int viewX = 0, viewY = 0;
	@SuppressWarnings("unused")
	private int bgX = 206, bgY = 329;
	private int width = 436, height = 434;
	
	private long user = Nation.NO_NATION.getOwner();
	
	public Editor(String title, int width, int height) throws IOException {
		Dimension d = new Dimension(width, height);
		setSize(d);
		setMinimumSize(d);
		setMaximumSize(d);
		setPreferredSize(d);
		
		setFocusable(true);
		
		Game game = new Game();
		game.load("testmap.game");
		gameManager = new GameManager(game);
		
		maxRegionID = game.getMap().getRegions().size() - 1;
		
		//background = ImageIO.read(new File("background.png"));
		
		JFrame window = new JFrame(title);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		field = new JTextField();
		
		panel.add(this);
		panel.add(field);
		window.add(panel);
		window.pack();
		
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		
		addMouseListener(this);
		addKeyListener(this);
		
		field.addActionListener(e -> {
			System.out.println(field.getText());
			Response r = CommandRegistry.executeCommand(gameManager, field.getText(), user,
					InputLevel.GAME_CHANNEL);
			
			if (r != null) {
				System.out.println(r.content);
			}
			
			repaint();
		});
		
		gameManager.getGame().getMap().setOffsetX(375);
		gameManager.getGame().getMap().setOffsetY(360);
		
		requestFocus();
		repaint();
	}
	
	@Override
	public void paint(Graphics g) {
		//g.drawImage(background, -bgX, -bgY, null);
		//g.drawImage(background, 0, 0, getWidth(), getHeight(),
		//		bgX + viewX, bgY + viewY,
		//		bgX + width + viewX, bgY + height + viewY, null);
		gameManager.getGame().getMap().setOffsetX(375.0 - (double)viewX * getWidth() / (double)width);
		gameManager.getGame().getMap().setOffsetY(360.0 - (double)viewY * getHeight() / (double)height);
		//g.setColor(Color.BLACK);
		//g.fillRect(0, 0, gameManager.getGame().getMap().getWidth(), gameManager.getGame().getMap().getHeight());
		gameManager.getRenderer().clear();
		gameManager.renderView(renderType);
		g.drawImage(gameManager.getRenderer().getRenderTarget(), 0, 0, null);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		int[] pos = new int[2];
		gameManager.getGame().getMap().calcHexPosition(e.getX(), e.getY(), pos);
		
		switch (renderType) {
			case 0:
				{ // cities
					City c = gameManager.getGame().getMap().getCityAt(pos[0], pos[1]);
					
					if (e.getButton() == MouseEvent.BUTTON1) {
						Hexagon h = gameManager.getGame().getMap().get(pos[0], pos[1]);
						
						if (c == null && h != null) {
							c = new City(gameManager.getGame().getMap(), h.getRegionID(), field.getText(), pos[0], pos[1]);
							gameManager.getGame().getMap().addCity(c);
							repaint();
						}
					}
					else if (e.getButton() == MouseEvent.BUTTON3) {
						if (c != null) {
							gameManager.getGame().getMap().removeCity(c);
							repaint();
						}
					}
				}
				break;
			case 2:
				{ // regions
					Hexagon h = gameManager.getGame().getMap().get(pos[0], pos[1]);
					
					if (h != null) {
						if (e.getButton() == MouseEvent.BUTTON1) {
							h.setRegionID(regionID);
							repaint();
						}
						else if (e.getButton() == MouseEvent.BUTTON3) {
							h.setRegionID(0);
							repaint();
						}
					}
				}
				break;
			case 4:
				{ // editor
					Hexagon h = gameManager.getGame().getMap().get(pos[0], pos[1]);
					if (e.getButton() == MouseEvent.BUTTON1) {
						if (h == null) {
							gameManager.getGame().getMap().add(new Hexagon(pos[0], pos[1]));
							repaint();
						}
					}
					else if (e.getButton() == MouseEvent.BUTTON3) {
						if (h != null) {
							gameManager.getGame().getMap().remove(pos[0], pos[1]);
							repaint();
						}
					}
				}
				break;
			default:
				System.out.printf("%d, %d%n", pos[0], pos[1]);
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S) {
			try {
				gameManager.saveGame("hre.game");
			}
			catch (Exception e2) {
				e2.printStackTrace();
			}
			
			System.out.println("Saved");
			return;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_Q) {
			++renderType;
			
			if (renderType > NUM_RENDER_TYPES) {
				renderType = 0;
			}
			
			repaint();
		}
		else if (e.getKeyCode() == KeyEvent.VK_R) {
			++regionID;
			
			if (regionID > maxRegionID) {
				regionID = 0;
			}
			
			if (gameManager.getGame().getMap().getRegion(regionID) == null) {
				System.out.println(regionID);
			}
			else {
				System.out.println("Region: " + gameManager.getGame().getMap().getRegion(regionID).getName());
			}
		}
		else if (e.getKeyCode() == KeyEvent.VK_F) {
			--regionID;
			
			if (regionID < 0) {
				regionID = maxRegionID;
			}
			
			System.out.println("Region: " + gameManager.getGame().getMap().getRegion(regionID).getName());
		}
		else if (e.getKeyCode() == KeyEvent.VK_M) {
			gameManager.getGame().endTurn();
			
			for (LogEntry le : gameManager.getGame().getTurnLog().getCommonEntries()) {
				System.out.println(le.title);
				System.out.println(le.description);
				System.out.println();
			}
			
			for (LogEntry le : gameManager.getGame().getTurnLog().getBattleEntries()) {
				System.out.println(le.title);
				System.out.println(le.description);
				System.out.println();
			}
			
			gameManager.getGame().clearLog();
			
			repaint();
		}
		else if (e.getKeyCode() == KeyEvent.VK_W) {
			--viewY;
			repaint();
		}
		else if (e.getKeyCode() == KeyEvent.VK_S) {
			++viewY;
			repaint();
		}
		else if (e.getKeyCode() == KeyEvent.VK_A) {
			++viewX;
			repaint();
		}
		else if (e.getKeyCode() == KeyEvent.VK_D) {
			--viewX;
			repaint();
		}
	}
	
	public static void main(String[] args) throws IOException {
		CommandRegistry.init();
		new Editor("Hexmap Game Editor", 700, 700);
	}

	@Override
	public void keyReleased(KeyEvent arg0) {}
	@Override
	public void keyTyped(KeyEvent arg0) {}
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
}
