package strat.editor;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import strat.game.Game;
import strat.game.Region;

@SuppressWarnings("serial")
public class RegionTableModel extends AbstractTableModel {
	private static final String[] COLUMN_NAMES = {"Region ID", "Name", "Color"};
	private static final Class<?>[] COLUMN_TYPES = {Integer.class, String.class, Color.class};
	
	private ArrayList<Region> data;
	
	private final boolean readOnly;
	private final Game game;
	
	public RegionTableModel(Game game, boolean readOnly) {
		this.game = game;
		data = new ArrayList<>();
		this.readOnly = readOnly;
	}
	
	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public int getRowCount() {
		return data.size();
	}
	
	@Override
	public String getColumnName(int col) {
		return COLUMN_NAMES[col];
	}
	
	@Override
	public Class<?> getColumnClass(int col) {
		return COLUMN_TYPES[col];
	}

	@Override
	public Object getValueAt(int row, int col) {
		Region r = data.get(row);
		
		switch (col) {
			case 0:
				return r.getRegionID();
			case 1:
				return r.getName();
			case 2:
				return r.getColor();
			default:
				return null;
		}
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {
		return !readOnly && (col > 0 && row > 0);
	}
	
	@Override
	public void setValueAt(Object value, int row, int col) {
		Region r = data.get(row);
		
		switch (col) {
			case 1:
				r.setName((String)value);
				break;
			case 2:
				r.setColor((Color)value);
				break;
		}
		
		fireTableCellUpdated(row, col);
	}
	
	public Region getRegion(int row) {
		return data.get(row);
	}
	
	public void addRegion(Region r) {
		data.add(r);
		
		fireTableRowsInserted(data.size() - 1, data.size() - 1);
	}
	
	public void removeRegion(Region r) {
		data.remove(r);
		
		game.getMap().removeRegion(r);
		
		fireTableRowsDeleted(data.size() - 1, data.size() - 1);
	}
	
	public void removeRow(int row) {
		fireTableRowsDeleted(row, row);
		
		Region r = data.remove(row);
		game.getMap().removeRegion(r);
	}
}
