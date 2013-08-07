package tools;

import java.util.ArrayList;
import java.util.Locale;

import javax.swing.table.AbstractTableModel;

import commun.EPlayerState;
import commun.Player;

public class TableProfile extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -614943928768041376L;
	private ArrayList<Player> players = new ArrayList<Player>();

	public TableProfile() {
	}

	public TableProfile(ArrayList<Player> arrayList) {
		if (arrayList != null) {
			players = arrayList;
		}
	}

	public void refresh() {
		fireTableDataChanged();
	}

	public void refresh(ArrayList<Player> arrayList) {
		if (arrayList != null) {
			players = arrayList;
		}
		fireTableDataChanged();
	}

	public void refresh(Player p) {
		int n = players.indexOf(p);
		fireTableRowsUpdated(n, n);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return 4;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(final int col) {
		switch (col) {
			case 0:
				return "Nom";
			case 1:
				return "Pays";
			case 2:
				return "Score";
			case 3:
				return "Etat";
			default:
				return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return players.size();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int row, final int col) {
		if (row > players.size() - 1) {
			return "";
		}
		switch (col) {
			case 0:
				return players.get(row).getName();
			case 1:
				return players.get(row).getCountry();
			case 2:
				return players.get(row).getScore();
			case 3:
				return players.get(row).getState();
			default:
				return null;
		}
	}

	@Override
	public Class<?> getColumnClass(int column) {
		switch (column) {
			case 1:
				return Locale.class;
			case 3:
				return EPlayerState.class;
			default:
				return String.class;
		}
	}

	@Override
	public boolean isCellEditable(final int row, final int col) {
		return false;
	}

	public void removeRow(final int row) {

	}
}
