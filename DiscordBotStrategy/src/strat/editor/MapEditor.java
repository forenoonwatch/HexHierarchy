package strat.editor;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.TableModelEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import strat.game.City;
import strat.game.Game;
import strat.game.Hexagon;
import strat.game.Region;

public class MapEditor implements ActionListener, MouseListener, KeyListener, MouseWheelListener {
	public static final int PAN_SPEED = 3;
	
	private final JFrame frame;
	private final MapRenderPane renderPane;
	private final JTextField output;
	
	private Game game;
	private Region selectedRegion;
	
	private String currentTool;
	
	public MapEditor(String title, int width, int height) throws MalformedURLException {
		game = null;
		selectedRegion = null;
		
		currentTool = "";
		
		frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(width, height);
		frame.setLocationRelativeTo(null);
		
		frame.setLayout(new BorderLayout());
		
		
		
		JToolBar toolBar = new JToolBar("Tools");
		toolBar.add(createImageButton("hex-strat.png", "T_Land", "Add or remove land tiles", "Land", this));
		toolBar.add(createImageButton("hex-strat.png", "T_Water", "Add or remove water tiles", "Water", this));
		toolBar.add(createImageButton("hex-strat.png", "T_RegionPaint", "Paint Regions", "Region", this));
		toolBar.add(createImageButton("hex-strat.png", "T_City", "Add or remove cities", "City", this));
		
		toolBar.setOrientation(JToolBar.VERTICAL);
		frame.add(toolBar, BorderLayout.WEST);
		
		renderPane = new MapRenderPane();
		
		frame.add(renderPane, BorderLayout.CENTER);
		
		output = new JTextField("Output");
		output.setEditable(false);
		
		
		
		final JMenuBar menuBar = new JMenuBar();
		fillMenuBar(menuBar, this);
		frame.setJMenuBar(menuBar);
		
		frame.add(output, BorderLayout.SOUTH);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
		
		renderPane.addMouseListener(this);
		renderPane.addKeyListener(this);
		renderPane.addMouseWheelListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().startsWith("T_")) {
			if (game == null) {
				JOptionPane.showMessageDialog(frame, "Must load a map file before editing.",
						"No Map Selected!", JOptionPane.ERROR_MESSAGE);
				return;
			}
			else {
				currentTool = e.getActionCommand();
			}
		}
		else if (e.getActionCommand().startsWith("View")) {
			if (game == null) {
				JOptionPane.showMessageDialog(frame, "Must load a map file before viewing.",
						"No Map Selected!", JOptionPane.ERROR_MESSAGE);
				return;
			}
			else {
				currentTool = "";
			}
		}
		else if (e.getActionCommand().contains("Region")) {
			if (game == null) {
				JOptionPane.showMessageDialog(frame, "Must load a map file before modifying regions.",
						"No Map Selected!", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		
		switch (e.getActionCommand()) {
			case "NewMap":
				presentGame(new Game().initForSize(renderPane.getWidth(), renderPane.getHeight()));
				output.setText("Created new Game");
				break;
			case "OpenMap":
				loadGameFromFile();
				break;
			case "SaveMap":
				saveGameToFile();
				break;
			case "ViewLand":
				renderPane.renderGame(game, 1);
				break;
			case "ViewRegionCells":
				renderPane.renderGame(game, 2);
				break;
			case "ViewRegionNames":
				renderPane.renderGame(game, 3);
				break;
			case "ViewCities":
				renderPane.renderGame(game, 0);
				break;
			case "AddRegion":
				addRegionDialog();
				break;
			case "EditRegion":
				showRegionDialog(false, false, false);
				renderPane.renderGame(game, 3);
				break;
			case "RemoveRegion":
				showRegionDialog(true, true, false);
				resolveRemovedRegions();
				renderPane.renderGame(game, 3);
				break;
			case "OpenBackground":
				updateBackground();
				break;
			case "T_Land":
				output.setText("Tool: Add/Remove Land");
				renderPane.renderGame(game, 1);
				break;
			case "T_Water":
				output.setText("Tool: Add/Remove Water");
				renderPane.renderGame(game, 1);
				break;
			case "T_City":
				output.setText("Tool: Add/Remove Cities");
				renderPane.renderGame(game, 0);
				break;
			case "T_RegionPaint":
				showRegionDialog(true, false, true);
				output.setText("Region: "
							+ (selectedRegion == null ? "No Region" : selectedRegion.getName()));
				renderPane.renderGame(game, 2);
				break;
			default:
				System.out.println(e.getActionCommand());
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if (game == null) {
			return;
		}
		
		int[] pos = new int[2];
		game.getMap().calcHexPosition(e.getX(), e.getY(), pos);
		Hexagon h = game.getMap().get(pos[0], pos[1]);
		
		switch (currentTool) {
			case "T_Land":
			case "T_Water":
				if (e.getButton() == MouseEvent.BUTTON1) {
					if (h == null) {
						h = new Hexagon(pos[0], pos[1], currentTool.equals("T_Water"));
						game.getMap().add(h);
						renderPane.renderGame(game, 1);
					}
				}
				else if (e.getButton() == MouseEvent.BUTTON3) {
					if (h != null) {
						game.getMap().remove(pos[0], pos[1]);
						renderPane.renderGame(game, 1);
					}
				}
				break;
			case "T_City":
				if (h != null && !h.isWater()) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						City c = game.getMap().getCityAt(pos[0], pos[1]);
						
						if (c == null) {
							String res = JOptionPane.showInputDialog(frame, "City Name:");
							
							if (res != null) {
								if (res.equals("")) {
									JOptionPane.showMessageDialog(frame, "Must enter a city name.",
											"Invalid City Name!", JOptionPane.ERROR_MESSAGE);
								}
								else if (!res.matches("([A-Z]|[a-z]|\\s|'|,|-)+")) {
									JOptionPane.showMessageDialog(frame, "City name contains invalid characters.",
											"Invalid City Name!", JOptionPane.ERROR_MESSAGE);
								}
								else {
									c = new City(game.getMap(), h.getRegionID(), res, pos[0], pos[1]);
									game.getMap().addCity(c);
									
									renderPane.renderGame(game, 0);
								}
							}
						}
					}
					else if (e.getButton() == MouseEvent.BUTTON3) {
						City c = game.getMap().getCityAt(pos[0], pos[1]);
						
						if (c != null) {
							game.getMap().removeCity(c);
						}
						
						renderPane.renderGame(game, 0);
					}
				}
				break;
			case "T_RegionPaint":
				if (h != null && !h.isWater()) {
					if (e.getButton() == MouseEvent.BUTTON1 && selectedRegion != null) {
						h.setRegionID(selectedRegion.getRegionID());
						City c = game.getMap().getCityAt(pos[0], pos[1]);
						
						if (c != null) {
							c.setRegionID(selectedRegion.getRegionID());
						}
						
						renderPane.renderGame(game, 2);
					}
					else if (e.getButton() == MouseEvent.BUTTON3) {
						h.setRegionID(Region.NO_REGION.getRegionID());
						City c = game.getMap().getCityAt(pos[0], pos[1]);
						
						if (c != null) {
							c.setRegionID(Region.NO_REGION.getRegionID());
						}
						
						renderPane.renderGame(game, 2);
					}
				}
				break;
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (game == null) {
			return;
		}
		
		int dx = 0;
		int dy = 0;
		
		if (e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_UP) {
			dy += PAN_SPEED;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_S || e.getKeyCode() == KeyEvent.VK_DOWN) {
			dy -= PAN_SPEED;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_LEFT) {
			dx += PAN_SPEED;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_RIGHT) {
			dx -= PAN_SPEED;
		}
		
		game.getMap().setOffsetX(game.getMap().getOffsetX() + dx);
		game.getMap().setOffsetY(game.getMap().getOffsetY() + dy);
		
		renderPane.renderGame(game);
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (game == null || renderPane.getBackgroundImage() == null) {
			return;
		}
		
		renderPane.rescale(game, e.getWheelRotation());
	}
	
	public void setSelectedRegion(Region selectedRegion) {
		this.selectedRegion = selectedRegion;
	}
	
	private void presentGame(Game game) {
		this.game = game;
		
		renderPane.renderGame(game, 0);
	}
	
	private void resolveRemovedRegions() {
		game.getMap().forEach(h -> {
			if (game.getMap().getRegion(h.getRegionID()) == null) {
				h.setRegionID(0);
			}
		});
	}
	
	private void loadGameFromFile() {
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File f) {
				return f.isDirectory() || (f.isFile() && f.getName().endsWith(".game"));
			}

			@Override
			public String getDescription() {
				return "HexHierarchy game files";
			}
		});
		
		int res = fileChooser.showOpenDialog(frame);
		
		if (res == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			Game game;
			
			try {
				game = new Game();
				game.load(file.getAbsolutePath());
			}
			catch (IOException e) {
				game = null;
				JOptionPane.showMessageDialog(frame, "Failed to load file: " + file.getName(),
						"Error opening file!", JOptionPane.ERROR_MESSAGE);
			}
			
			presentGame(game);
			
			output.setText("Loaded Game: " + file.getName());
		}
	}
	
	private void saveGameToFile() {
		if (game == null) {
			JOptionPane.showMessageDialog(frame, "Must have a map open in order to save.",
					"No Map Open!", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File f) {
				return f.isDirectory() || (f.isFile() && f.getName().endsWith(".game"));
			}

			@Override
			public String getDescription() {
				return "HexHierarchy game files";
			}
		});
		
		int res = fileChooser.showOpenDialog(frame);
		
		if (res == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			
			try {
				game.save(file.getAbsolutePath());
			}
			catch (IOException e) {
				JOptionPane.showMessageDialog(frame, "Failed to save to file: " + file.getName(),
						"Error opening file!", JOptionPane.ERROR_MESSAGE);
			}
			
			JOptionPane.showMessageDialog(frame, "Successfully saved map to file: " + file.getName(),
					"Map Saved", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	private void updateBackground() {
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg"));
		
		int res = fileChooser.showOpenDialog(frame);
		
		if (res == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			BufferedImage img = null;
			
			try {
				img = ImageIO.read(file);
			}
			catch (IOException e) {
				img = null;
				JOptionPane.showMessageDialog(frame, "Failed to load file: " + file.getName(),
						"Error opening file!", JOptionPane.ERROR_MESSAGE);
			}
			
			renderPane.setBackground(img, 0, 0);
			renderPane.repaint();
			
			output.setText("Loaded Background: " + file.getName());
		}
	}
	
	private void addRegionDialog() {
		Region newRegion = new Region(game.getMap().getRegions().size(), "");

		final JDialog dialog = new JDialog(frame, "Add New Region", true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		JPanel namePanel = new JPanel();
		Dimension d = new Dimension(300, 70);
		namePanel.setMinimumSize(d);
		namePanel.setMaximumSize(d);
		namePanel.setPreferredSize(d);
		namePanel.setSize(d);
		
		JLabel nameLabel = new JLabel("Region Name:");
		namePanel.add(nameLabel);
		
		JTextField nameField = new JTextField();
		d = new Dimension(200, 20);
		nameField.setMinimumSize(d);
		nameField.setMaximumSize(d);
		nameField.setPreferredSize(d);
		nameField.setSize(d);
		namePanel.add(nameField);
		
		namePanel.add(new JLabel("Color:"));
		
		JButton colorButton = new JButton();
		d = new Dimension(30, 30);
		colorButton.setMinimumSize(d);
		colorButton.setMaximumSize(d);
		colorButton.setPreferredSize(d);
		colorButton.setSize(d);
		colorButton.setActionCommand("edit");
		colorButton.setBackground(newRegion.getColor());
		
		JColorChooser colorChooser = new JColorChooser();
		JDialog ccDialog = JColorChooser.createDialog(colorButton, "Region Color",
				true, colorChooser, e -> {
					newRegion.setColor(colorChooser.getColor());
					colorButton.setBackground(colorChooser.getColor());
				}, null);
		
		colorButton.addActionListener(e -> {
			ccDialog.setVisible(true);
		});
		
		namePanel.add(colorButton);
		
		JButton okButton = new JButton("Add Region");
		
		okButton.addActionListener(e -> {
			final String text = nameField.getText();
			
			if (text.equals("") || !text.matches("([A-Z]|[a-z]|\\s|'|,|-)+")) {
				JOptionPane.showMessageDialog(frame, "Invalid region name.",
						"Invalid Region Name!", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			newRegion.setName(text);
			game.getMap().addRegion(newRegion);
			
			dialog.dispose();
		});
		
		namePanel.add(okButton);
		
		dialog.add(namePanel);
		
		dialog.pack();
		dialog.setResizable(false);
		dialog.setLocationRelativeTo(frame);
		dialog.setVisible(true);
	}
	
	private void showRegionDialog(boolean readOnly, boolean allowRemoval, boolean allowSelection) {
		final JDialog dialog = new JDialog(frame, "Regions", true);
		dialog.setDefaultCloseOperation(allowSelection ? JDialog.DO_NOTHING_ON_CLOSE : JDialog.DISPOSE_ON_CLOSE);
		
		final RegionTable table = new RegionTable(game, readOnly);
		
		RegionTableModel model = (RegionTableModel)table.getModel();
		
		model.addTableModelListener(e -> {
			if (e.getType() == TableModelEvent.UPDATE) {
				Object v = table.getValueAt(e.getFirstRow(), e.getColumn());
				Region r = model.getRegion(e.getFirstRow());
				
				switch (e.getColumn()) {
					case 1:
						r.setName((String)v);
						break;
					case 2:
						r.setColor((Color)v);
						break;
				}
			}
		});
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		JScrollPane sp = new JScrollPane(table);
		
		panel.add(sp, BorderLayout.CENTER);
		
		if (allowRemoval) {
			Button removeButton = new Button("Remove Region");
			
			removeButton.addActionListener(e -> {
				int row = table.getSelectedRow();
				
				if (row > 0) {
					model.removeRow(row);
				}
				else {
					JOptionPane.showMessageDialog(dialog, "Must select a valid region to remove",
							"Invalid Region!", JOptionPane.ERROR_MESSAGE);
				}
			});
			
			panel.add(removeButton, BorderLayout.PAGE_END);
		}
		else if (allowSelection) {
			Button acceptButton = new Button("Accept");
			
			acceptButton.addActionListener(e -> {
				if (table.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(dialog, "Must select a region",
							"No Region Selected!", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				setSelectedRegion(model.getRegion(table.getSelectedRow()));
				dialog.dispose();
			});
			
			panel.add(acceptButton, BorderLayout.PAGE_END);
		}
		
		dialog.setContentPane(panel);
		
		dialog.pack();
		dialog.setLocationRelativeTo(frame);
		dialog.setVisible(true);
	}
	
	private void fillMenuBar(JMenuBar menuBar, ActionListener al) {
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		
		JMenu viewMenu = new JMenu("View");
		viewMenu.setMnemonic(KeyEvent.VK_V);
		
		JMenu regionMenu = new JMenu("Region");
		regionMenu.setMnemonic(KeyEvent.VK_R);
		
		JMenuItem newMenuItem = new JMenuItem("New Map");
		newMenuItem.setMnemonic(KeyEvent.VK_N);
		newMenuItem.setActionCommand("NewMap");
		
		JMenuItem openMenuItem = new JMenuItem("Open Map");
		openMenuItem.setMnemonic(KeyEvent.VK_O);
		openMenuItem.setActionCommand("OpenMap");
		
		JMenuItem saveMenuItem = new JMenuItem("Save Map");
		saveMenuItem.setMnemonic(KeyEvent.VK_S);
		saveMenuItem.setActionCommand("SaveMap");
		
		JMenuItem bgMenuItem = new JMenuItem("Open Background");
		bgMenuItem.setMnemonic(KeyEvent.VK_B);
		bgMenuItem.setActionCommand("OpenBackground");
		
		newMenuItem.addActionListener(al);
		openMenuItem.addActionListener(al);
		saveMenuItem.addActionListener(al);
		bgMenuItem.addActionListener(al);
		
		fileMenu.add(newMenuItem);
		fileMenu.add(openMenuItem);
		fileMenu.add(saveMenuItem);
		fileMenu.add(bgMenuItem);
		
		JMenuItem viewLandItem = new JMenuItem("Land");
		viewLandItem.setMnemonic(KeyEvent.VK_L);
		viewLandItem.setActionCommand("ViewLand");
		
		JMenuItem viewCellsItem = new JMenuItem("Region Cells");
		viewCellsItem.setMnemonic(KeyEvent.VK_R);
		viewCellsItem.setActionCommand("ViewRegionCells");
		
		JMenuItem viewNamesItem = new JMenuItem("Region Names");
		viewNamesItem.setMnemonic(KeyEvent.VK_N);
		viewNamesItem.setActionCommand("ViewRegionNames");
		
		JMenuItem viewCitiesItem = new JMenuItem("Cities");
		viewCitiesItem.setMnemonic(KeyEvent.VK_C);
		viewCitiesItem.setActionCommand("ViewCities");
		
		viewLandItem.addActionListener(al);
		viewCellsItem.addActionListener(al);
		viewNamesItem.addActionListener(al);
		viewCitiesItem.addActionListener(al);
		
		viewMenu.add(viewLandItem);
		viewMenu.add(viewCellsItem);
		viewMenu.add(viewNamesItem);
		viewMenu.add(viewCitiesItem);
		
		JMenuItem addRegionItem = new JMenuItem("Add Region");
		addRegionItem.setMnemonic(KeyEvent.VK_A);
		addRegionItem.setActionCommand("AddRegion");
		
		JMenuItem editRegionItem = new JMenuItem("Edit Region");
		editRegionItem.setMnemonic(KeyEvent.VK_E);
		editRegionItem.setActionCommand("EditRegion");
		
		JMenuItem removeRegionItem = new JMenuItem("Remove Region");
		removeRegionItem.setMnemonic(KeyEvent.VK_R);
		removeRegionItem.setActionCommand("RemoveRegion");
		
		addRegionItem.addActionListener(al);
		editRegionItem.addActionListener(al);
		removeRegionItem.addActionListener(al);
		
		regionMenu.add(addRegionItem);
		regionMenu.add(editRegionItem);
		regionMenu.add(removeRegionItem);
		
		menuBar.add(fileMenu);
		menuBar.add(viewMenu);
		menuBar.add(regionMenu);
	}
	
	private static JButton createImageButton(String imageName, String actionCommand,
			String toolTipText, String altText, ActionListener al) throws MalformedURLException {
		JButton button = new JButton();
		button.setToolTipText(toolTipText);
		button.setActionCommand(actionCommand);
		
		URL imageURL = MapEditor.class.getResource(imageName);
		
		if (imageURL != null) {
			button.setIcon(new ImageIcon(imageURL, altText));
		}
		else {
			button.setText(altText);
		}
		
		button.addActionListener(al);
		
		return button;
	}
	
	public static void main(String[] args) throws MalformedURLException {
		new MapEditor("HexHierarchy Map Editor", 800, 600);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {}
	@Override
	public void mouseEntered(MouseEvent arg0) {}
	@Override
	public void mouseExited(MouseEvent arg0) {}
	@Override
	public void mouseReleased(MouseEvent arg0) {}
	@Override
	public void keyReleased(KeyEvent e) {}
	@Override
	public void keyTyped(KeyEvent e) {}
}
