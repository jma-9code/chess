package tools;


import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.log4j.Logger;

public class CountryRenderer extends DefaultTableCellRenderer {

	private static final Logger logger = Logger.getLogger(CountryRenderer.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -8165106968199338070L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object country, boolean isSelected, boolean hasFocus, int row, int column) {
		super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);

		for (ImageIcon icon : Sprites.getInstance().getCountries()) {
			if (icon != null && country.toString().equals(icon.getDescription())) {
				setIcon(icon);
				break;
			}
		}
		setVerticalAlignment(JLabel.CENTER);
		setHorizontalAlignment(JLabel.CENTER);
		setToolTipText(" Pays - " + country);
		return this;
	}

}
