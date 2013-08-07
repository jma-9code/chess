package tools;


import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import commun.ProfilePlayerInfos;

public class PlayerRenderer extends DefaultTableCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4855563150659852277L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object player, boolean isSelected, boolean hasFocus, int row, int column) {
		super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);

		if (player instanceof ProfilePlayerInfos) {
			ProfilePlayerInfos playerInfos = (ProfilePlayerInfos) player;
			if (playerInfos.getPlayerName().equals("Partie nulle")) {
				setText("Partie nulle");
				setIcon(null);
				setToolTipText(null);
			}
			else {
				setText(playerInfos.getPlayerName());
				for (ImageIcon icon : Sprites.getInstance().getCountries()) {
					if (icon != null && playerInfos.getPlayerCountry().equals(icon.getDescription())) {
						setIcon(icon);
						break;
					}
				}
				setToolTipText("Score : " + playerInfos.getPlayerScore());
			}
		}
		setVerticalAlignment(JLabel.CENTER);
		setHorizontalAlignment(JLabel.CENTER);
		return this;
	}
}
