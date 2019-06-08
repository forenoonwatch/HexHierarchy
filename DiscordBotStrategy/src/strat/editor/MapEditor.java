package strat.editor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;

public class MapEditor implements ActionListener, MouseListener {
	private final JFrame frame;
	
	public MapEditor(String title, int width, int height) throws MalformedURLException {
		frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(width, height);
		frame.setLocationRelativeTo(null);
		
		//frame.setLayout(new GridLayout(3, 1));
		frame.setLayout(new BorderLayout());
		
		final JMenuBar menuBar = new JMenuBar();
		fillMenuBar(menuBar, this);
		frame.setJMenuBar(menuBar);
		
		JToolBar toolBar = new JToolBar("Tools");
		toolBar.add(createImageButton("hex-strat.png", "Land", "Add or remove land tiles", "Land", this));
		toolBar.add(createImageButton("hex-strat.png", "Water", "Add or remove water tiles", "Water", this));
		toolBar.add(createImageButton("hex-strat.png", "RegionPaint", "Paint Regions", "Region", this));
		toolBar.add(createImageButton("hex-strat.png", "City", "Add or remove cities", "City", this));
		
		toolBar.setOrientation(JToolBar.VERTICAL);
		frame.add(toolBar, BorderLayout.WEST);
		
		JPanel centerPanel = new JPanel();
		
		frame.add(centerPanel, BorderLayout.CENTER);
		
		frame.addMouseListener(this);
		
		frame.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
			case "RegionPaint":
				showRegionDialog();
				// TODO: bring up region selection dialog
				break;
			default:
				System.out.println(e.getActionCommand());
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		System.out.printf("%d, %d%n", e.getX(), e.getY());
	}
	
	private void showRegionDialog() {
		final JDialog dialog = new JDialog(frame, "Test", true);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		final JOptionPane pane = new JOptionPane("Select something", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
		
		pane.addPropertyChangeListener(e -> {
			if (dialog.isVisible() && e.getSource() == pane
					&& e.getPropertyName().equals(JOptionPane.VALUE_PROPERTY)) {
				
				dialog.dispose();
			}
		});
		
		dialog.setContentPane(pane);
		
		dialog.pack();
		dialog.setLocationRelativeTo(frame);
		dialog.setVisible(true);
		
		int v = ((Integer)pane.getValue()).intValue();
		System.out.println(v);
	}
	
	private static void fillMenuBar(JMenuBar menuBar, ActionListener al) {
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		
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
}
