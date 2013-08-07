package tools;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import commun.ProfilePartyInfos;
import commun.ProfilePlayerInfos;

public class TableProfileParties extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8300599207233162592L;
	private ArrayList<ProfilePartyInfos> partiesList = new ArrayList<ProfilePartyInfos>();

	public TableProfileParties() {
	}

	public void refresh() {
		fireTableDataChanged();
	}

	public void refresh(ArrayList<ProfilePartyInfos> arrayList) {
		if (arrayList != null) {
			partiesList = arrayList;
		}
		fireTableDataChanged();
	}

	public void refresh(ProfilePartyInfos p) {
		int n = partiesList.indexOf(p);
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

	@Override
	public Class<?> getColumnClass(int column) {
		if (column >= 1) {
			return ProfilePlayerInfos.class;
		}
		else {
			return String.class;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(final int col) {
		switch (col) {
			case 0:
				return "Partie ID";
			case 1:
				return "Joueur Banc";
			case 2:
				return "Joueur Noir";
			case 3:
				return "Vainqueur";
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
		return partiesList.size();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(final int row, final int col) {
		if (row > partiesList.size() - 1) {
			return "";
		}
		switch (col) {
			case 0:
				return partiesList.get(row).getIdentifierOfParty();
			case 1:
				return partiesList.get(row).getWhitePlayer();
			case 2:
				return partiesList.get(row).getBlackPlayer();
			case 3:
				return partiesList.get(row).getWinner();
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
