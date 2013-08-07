package tools;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import commun.Player;

public class TableScore extends AbstractTableModel {
	
	private static final Logger logger = Logger.getLogger(TableScore.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Player> players = new ArrayList<Player>();

	public TableScore ( ArrayList<Player> arrayList ) {
		if ( arrayList != null ) players = arrayList;
	}

	public void refresh() {
		fireTableDataChanged();
	}

	public void refresh(ArrayList<Player> arrayList) {
		if ( arrayList != null ) players = arrayList;
		fireTableDataChanged();
	}

	public void refresh(Player p) {
		int n = players.indexOf(p);
		fireTableRowsUpdated(n, n);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return 3;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(final int col) {
		switch ( col ) {
			case 0:
				return "Rang";
			case 1:
				return "Joueur";
			case 2:
				return "Score";
			default:
				return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return players.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int row, final int col) {
		if ( row > players.size() - 1 ) return "";
		switch ( col ) {
			case 0:
				return row;
			case 1:
				return players.get(row).getName();
			case 2:
				return players.get(row).getScore();
			default:
				return null;
		}
	}

	public boolean isCellEditable(final int row, final int col) {
		return false;
	}

	public void removeRow(final int row) {

	}

}
