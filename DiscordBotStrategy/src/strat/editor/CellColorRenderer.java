package strat.editor;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

@SuppressWarnings("serial")
public class CellColorRenderer extends JLabel implements TableCellRenderer {
	private Border selectedBorder;
	private Border unselectedBorder;
	private boolean bordered;
	
	public CellColorRenderer(boolean bordered) {
		setOpaque(true);
		this.bordered = bordered;
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object oColor,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Color color = (Color)oColor;
		setBackground(color);
		
		if (bordered) {
			if (isSelected) {
				if (selectedBorder == null) {
					selectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5, table.getSelectionBackground());
				}
				
				setBorder(selectedBorder);
			}
			else {
				if (unselectedBorder == null) {
					unselectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5, table.getBackground());
				}
				
				setBorder(unselectedBorder);
			}
		}
		
		setToolTipText(String.format("#%X", color.getRGB() & 0xFFFFFF));
		
		return this;
	}

}
