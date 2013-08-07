package tools;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import commun.AbsPartyInfo;
import commun.Player;

public class TableParty extends AbstractTableModel {

	private static final Logger logger = Logger.getLogger(TableParty.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<AbsPartyInfo> parties = new ArrayList<AbsPartyInfo>();

	public TableParty(ArrayList<AbsPartyInfo> arrayList) {
		this.parties = arrayList;
	}

	public void refresh() {
		fireTableDataChanged();
	}

	public void refresh(ArrayList<AbsPartyInfo> listParties) {
		if (listParties != null) {
			parties = listParties;
		}
		fireTableDataChanged();
	}

	public void refresh(Player p) {
		int n = parties.indexOf(p);
		fireTableRowsUpdated(n, n);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return 5;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(final int col) {
		switch (col) {
			case 0:
				return "Id";
			case 1:
				return "Joueurs";
			case 2:
				return "Config";
			case 3:
				return "Statut";
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
		return parties.size();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int row, final int col) {
		if (row > parties.size() - 1) {
			return "";
		}
		switch (col) {
			case 0:
				return parties.get(row).getId();
			case 1:
				return parties.get(row).getPlayers();
			case 2:
				return parties.get(row).getOption();
			case 3:
				return parties.get(row).getStatut();
			default:
				return null;
		}
	}

	@Override
	public boolean isCellEditable(final int row, final int col) {
		return false;
	}

	public void removeRow(final int row) {

	}

}
