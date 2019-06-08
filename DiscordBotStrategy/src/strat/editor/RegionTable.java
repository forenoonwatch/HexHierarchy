package strat.editor;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import strat.game.Game;
import strat.game.Region;

@SuppressWarnings("serial")
public class RegionTable extends JTable {
	private CellColorRenderer colorRenderer;
	private CellColorEditor colorEditor;
	
	public RegionTable(Game game, boolean readOnly) {
		super(new RegionTableModel(game, readOnly));
		setFillsViewportHeight(true);
		getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		colorRenderer = new CellColorRenderer(true);
		colorEditor = new CellColorEditor();
		
		RegionTableModel model = (RegionTableModel)getModel();
		
		for (Region r : game.getMap().getRegions().values()) {
			model.addRegion(r);
		}
		
		initColumnSizes();
	}
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		if (column == 2) {
			return colorRenderer;
		}
		
		return super.getCellRenderer(row, column);
	}
	
	@Override
	public TableCellEditor getCellEditor(int row, int column) {
		if (column == 2) {
			return colorEditor;
		}
		
		return super.getCellEditor(row, column);
	}
	
	private void initColumnSizes() {
		
	}
}
