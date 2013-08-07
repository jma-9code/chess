package tools;


import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import commun.EPlayerState;

public class StateRenderer extends DefaultTableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8002166481365891607L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object state, boolean isSelected, boolean hasFocus, int row, int column) {
		super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
		if (state instanceof EPlayerState) {
			EPlayerState etat = (EPlayerState) state;
			if (etat == EPlayerState.ONLINE) {
				setIcon(Sprites.getInstance().getOnline());
				setToolTipText("Online");
			}
			else if (etat == EPlayerState.BUSY) {
				setIcon(Sprites.getInstance().getBusy());
				setToolTipText("Dans une partie");
			}
			else {
				setIcon(Sprites.getInstance().getOffline());
				setToolTipText("Offline");
			}
		}
		setVerticalAlignment(JLabel.CENTER);
		setHorizontalAlignment(JLabel.CENTER);
		return this;
	}

}
