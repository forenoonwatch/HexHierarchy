package strat.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

@SuppressWarnings("serial")
public class CellColorEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
	private static final String ACTION = "edit";
	
	private Color currentColor;
	private JButton button;
	private JColorChooser colorChooser;
	private JDialog dialog;
	
	public CellColorEditor() {
		button = new JButton();
		button.setActionCommand(ACTION);
		button.addActionListener(this);
		button.setBorderPainted(false);
		
		colorChooser = new JColorChooser();
		dialog = JColorChooser.createDialog(button, "Region Color",
				true, colorChooser, this, null);
	}
	
	@Override
	public Object getCellEditorValue() {
		return currentColor;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(ACTION)) {
			button.setBackground(currentColor);
			colorChooser.setColor(currentColor);
			dialog.setVisible(true);
			
			fireEditingStopped();
		}
		else {
			currentColor = colorChooser.getColor();
		}
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		currentColor = (Color)value;
		return button;
	}

}
