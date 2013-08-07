package gui;

import java.awt.Component;
import java.util.Locale;
import java.util.MissingResourceException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class CountryComboBoxRenderer extends JLabel implements ListCellRenderer {

	private static final long serialVersionUID = -830789576354771305L;

	public CountryComboBoxRenderer() {

	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {

		ImageIcon icon = (ImageIcon) value;

		if (isSelected || cellHasFocus) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		}
		else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		// Set the icon and text. If icon was null, say so.
		if ( icon == null ) { return this; }
		setIcon(icon);
		setText(getDisplayCountry(icon.getDescription()));

		if (icon != null) {
			// setText(pet);
			setFont(list.getFont());
		}

		return this;
	}

	/**
	 * Renvoie le nom du pays correspondant au code normé par l'ISO 3166
	 * 
	 * @param codeISO3
	 * @return Si aucune locale correspond, on renvoie le paramètre codeISO3
	 */
	public static String getDisplayCountry(String codeISO3) {
		for (Locale locale : Locale.getAvailableLocales()) {
			try {
				if (locale.getLanguage().equalsIgnoreCase(codeISO3)) {
					return locale.getDisplayLanguage();
				}
			}
			catch (MissingResourceException e) {
				System.err.println(locale.getDisplayCountry() + " " + locale.getDisplayName());
			}
		}

		return codeISO3;
	}

}
