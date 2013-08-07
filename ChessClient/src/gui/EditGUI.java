package gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import message.Error;
import message.ProfileEdit;
import network.NetworkClient;
import network.NetworkEvent;
import network.NetworkEventHandler;
import network.NetworkManager;
import tools.Sprites;

public class EditGUI extends JDialog implements ActionListener, NetworkEventHandler {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8507031919970587906L;
	private JPanel contentPane;
	private JPanel countryPane;
	private JPanel emailPane;
	private JLabel lblEmail;
	private JTextField txtEmail;
	private JButton btnValider;
	private JComboBox cb_nationalite;
	private ProfileGUI profileGUI;
	private ImageIcon[] countries = Sprites.getInstance().getCountries();
	private JLabel lblNationalite;

	public enum ControllerAction {
		VALIDATE;
	}

	public EditGUI(ProfileGUI profileGUI) {
		initComponent();
		setLocationRelativeTo(null);
		setModal(true);

		this.profileGUI = profileGUI;

		contentPane = new JPanel();
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);

		countryPane = new JPanel();
		sl_contentPane.putConstraint(SpringLayout.NORTH, countryPane, 0, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, countryPane, 0, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, countryPane, 100, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, countryPane, 0, SpringLayout.EAST, contentPane);
		countryPane.setBorder(new TitledBorder(new LineBorder(new Color(41, 41, 41)), "Changement de la nationalit\u00E9", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		SpringLayout sl_countryPane = new SpringLayout();
		countryPane.setLayout(sl_countryPane);
		contentPane.add(countryPane);

		cb_nationalite = new JComboBox(countries);
		cb_nationalite.setRenderer(new CountryComboBoxRenderer());
		countryPane.add(cb_nationalite);

		lblNationalite = new JLabel("Nationalité");
		sl_countryPane.putConstraint(SpringLayout.WEST, lblNationalite, 10, SpringLayout.WEST, countryPane);
		sl_countryPane.putConstraint(SpringLayout.NORTH, cb_nationalite, 6, SpringLayout.SOUTH, lblNationalite);
		sl_countryPane.putConstraint(SpringLayout.WEST, cb_nationalite, 0, SpringLayout.WEST, lblNationalite);
		sl_countryPane.putConstraint(SpringLayout.NORTH, lblNationalite, 10, SpringLayout.NORTH, countryPane);
		countryPane.add(lblNationalite);

		emailPane = new JPanel();
		sl_contentPane.putConstraint(SpringLayout.NORTH, emailPane, -130, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, emailPane, 0, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, emailPane, -40, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, emailPane, 0, SpringLayout.EAST, contentPane);
		emailPane.setBorder(new TitledBorder(null, "Changement d'email", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		SpringLayout sl_emailPane = new SpringLayout();
		emailPane.setLayout(sl_emailPane);
		contentPane.add(emailPane);

		lblEmail = new JLabel("Email");
		sl_emailPane.putConstraint(SpringLayout.NORTH, lblEmail, 10, SpringLayout.NORTH, emailPane);
		sl_emailPane.putConstraint(SpringLayout.WEST, lblEmail, 10, SpringLayout.WEST, emailPane);
		emailPane.add(lblEmail);

		txtEmail = new JTextField();
		sl_emailPane.putConstraint(SpringLayout.EAST, txtEmail, 250, SpringLayout.WEST, emailPane);
		txtEmail.setText("email");
		sl_emailPane.putConstraint(SpringLayout.NORTH, txtEmail, 6, SpringLayout.SOUTH, lblEmail);
		sl_emailPane.putConstraint(SpringLayout.WEST, txtEmail, 10, SpringLayout.WEST, emailPane);
		emailPane.add(txtEmail);
		txtEmail.setColumns(10);

		btnValider = new JButton("Valider");
		btnValider.addActionListener(this);
		btnValider.setActionCommand(ControllerAction.VALIDATE.name());
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnValider, 6, SpringLayout.SOUTH, emailPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnValider, 98, SpringLayout.WEST, contentPane);
		contentPane.add(btnValider);
	}

	@Override
	public void setVisible(boolean b) {
		if (b) {
			NetworkManager.getInstance().addHandler(this, ProfileEdit.class);
			NetworkManager.getInstance().addHandler(this, Error.class);
			refresh();
			validate();
			repaint();
		}
		else {
			NetworkManager.getInstance().removeHandler(this, ProfileEdit.class);
			NetworkManager.getInstance().removeHandler(this, Error.class);
		}
		super.setVisible(b);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ControllerAction.VALIDATE.name().equals(e.getActionCommand())) {
			String email = txtEmail.getText();
			ImageIcon country = (ImageIcon) cb_nationalite.getSelectedItem();
			boolean send = false;
			ProfileEdit profileEdit = new ProfileEdit();
			profileEdit.setEdit(true);
			profileEdit.setUsername(profileGUI.getProfilePartiesList().getPlayerName());
			if (!email.equalsIgnoreCase(profileGUI.getProfilePartiesList().getPlayerEmail())) {
				profileEdit.setEmail(email);
				send = true;
			}
			if (!country.getDescription().equalsIgnoreCase(profileGUI.getProfilePartiesList().getPlayerCountry())) {
				profileEdit.setCountry(country.getDescription());
				send = true;
			}
			if (send) {
				NetworkClient.getInstance().sendObject(profileEdit);
			}
			else {
				setVisible(false);
			}
		}
	}

	private void refresh() {
		txtEmail.setText(profileGUI.getProfilePartiesList().getPlayerEmail());
		// Recherche du pays correspondant au profil du joueur
		ImageIcon countryDefaut = null;
		for (ImageIcon country : countries) {
			if (country.getDescription().equalsIgnoreCase(profileGUI.getProfilePartiesList().getPlayerCountry())) {
				countryDefaut = country;
				break;
			}
		}

		if (countryDefaut != null) {
			cb_nationalite.setSelectedItem(countryDefaut);
		}
	}

	private void initComponent() {
		setSize(275, 260);
		setLocationRelativeTo(null);
		setResizable(false);
		setTitle("Edition du profil");
		setDefaultCloseOperation(HIDE_ON_CLOSE);
	}

	@Override
	public void handleNetworkEvent(NetworkEvent obj) {
		if (obj.getSource() instanceof ProfileEdit) {
			ProfileEdit edit = (ProfileEdit) obj.getSource();
			if (edit.isEdit()) {
				JOptionPane.showMessageDialog(this, "Changements réalisés !", "Edition du profil", JOptionPane.INFORMATION_MESSAGE);
				profileGUI.askFullProfile();
				setVisible(false);
			}
		}
		else if (obj.getSource() instanceof Error) {
			GUIErrorsException error = new GUIErrorsException("Edition du profil", (Error) obj.getSource());
			error.showErrors(this);
		}
	}
}
