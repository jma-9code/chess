/**
 * 
 */
package gui;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;

import tools.Sprites;

import message.Authentication;
import network.NetworkClient;
import network.NetworkEvent;
import network.NetworkEventHandler;
import network.NetworkManager;

/**
 * @author Florent
 * 
 */
public class SubscribeGUI extends JDialog implements ActionListener, NetworkEventHandler {

	private JPanel contentPane;
	private JTextField tf_pseudo;
	private JLabel lbl_pseudo;
	private JLabel lbl_motDePasse;
	private JTextField tf_email;
	private JPasswordField pf_password;
	private JLabel lblEmail;
	private SubscribeGUI guiThis = this;
	private JComboBox cb_nationalite;
	private JLabel lblNationalit;

	final private LoginGUI loginGUI;
	private JPasswordField pf_passwordConfirm;

	public enum ControllerAction {
		SUBSCRIPTION, CANCELLATION;
	}

	public SubscribeGUI(final LoginGUI login) {
		loginGUI = login;
		initComponent();

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent arg0) {
				closeGUI();
			}
		});

		contentPane = new JPanel();
		contentPane.setBorder(new TitledBorder(null, "Identification", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);

		lbl_pseudo = new JLabel("Pseudonyme");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lbl_pseudo, 10, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, lbl_pseudo, 10, SpringLayout.WEST, contentPane);
		contentPane.add(lbl_pseudo);

		tf_pseudo = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, tf_pseudo, 6, SpringLayout.SOUTH, lbl_pseudo);
		sl_contentPane.putConstraint(SpringLayout.WEST, tf_pseudo, 0, SpringLayout.WEST, lbl_pseudo);
		sl_contentPane.putConstraint(SpringLayout.EAST, tf_pseudo, -158, SpringLayout.EAST, contentPane);
		contentPane.add(tf_pseudo);
		tf_pseudo.setColumns(10);

		lbl_motDePasse = new JLabel("Mot de passe");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lbl_motDePasse, 38, SpringLayout.SOUTH, lbl_pseudo);
		sl_contentPane.putConstraint(SpringLayout.WEST, lbl_motDePasse, 0, SpringLayout.WEST, lbl_pseudo);
		contentPane.add(lbl_motDePasse);

		pf_password = new JPasswordField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, pf_password, 6, SpringLayout.SOUTH, lbl_motDePasse);
		sl_contentPane.putConstraint(SpringLayout.WEST, pf_password, 0, SpringLayout.WEST, lbl_pseudo);
		sl_contentPane.putConstraint(SpringLayout.EAST, pf_password, -158, SpringLayout.EAST, contentPane);
		contentPane.add(pf_password);

		tf_email = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.WEST, tf_email, 0, SpringLayout.WEST, lbl_pseudo);
		sl_contentPane.putConstraint(SpringLayout.EAST, tf_email, -10, SpringLayout.EAST, contentPane);
		tf_email.setColumns(10);
		contentPane.add(tf_email);

		lblEmail = new JLabel("Email");
		sl_contentPane.putConstraint(SpringLayout.NORTH, tf_email, 6, SpringLayout.SOUTH, lblEmail);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblEmail, 10, SpringLayout.WEST, contentPane);
		contentPane.add(lblEmail);

		JLabel lblConditionsGnralesDutilisation = new JLabel("<html><u>Consulter les conditions générales d'utilisation</u></html>");
		sl_contentPane.putConstraint(SpringLayout.WEST, lblConditionsGnralesDutilisation, 0, SpringLayout.WEST, lbl_pseudo);
		lblConditionsGnralesDutilisation.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				URL urlFile = getClass().getResource("/res/cgu.txt");
				StringBuffer msg = new StringBuffer();

				try {
					FileReader fi = new FileReader(urlFile.getPath());
					BufferedReader reader = new BufferedReader(fi);

					// chargement fichier
					String line = null; // variable tampon de lecture
					while ((line = reader.readLine()) != null) {
						msg.append(line);
					}

				}
				catch (FileNotFoundException e1) {
					msg.append("Fichier " + urlFile.getPath() + " introuvable.");
				}
				catch (IOException e2) {
					msg.append("Erreur lors du chargement de " + urlFile.toExternalForm());
				}

				JOptionPane.showMessageDialog(guiThis, msg.toString());
			}
		});
		contentPane.add(lblConditionsGnralesDutilisation);

		final JCheckBox chckbxJaccepteLesCgu = new JCheckBox("J'accepte les CGU du jeu d'échecs");
		sl_contentPane.putConstraint(SpringLayout.WEST, chckbxJaccepteLesCgu, -4, SpringLayout.WEST, lbl_pseudo);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblConditionsGnralesDutilisation, -6, SpringLayout.NORTH, chckbxJaccepteLesCgu);
		contentPane.add(chckbxJaccepteLesCgu);

		ImageIcon[] countries = Sprites.getInstance().getCountries();
		cb_nationalite = new JComboBox(countries);
		sl_contentPane.putConstraint(SpringLayout.WEST, cb_nationalite, 0, SpringLayout.WEST, lbl_pseudo);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, cb_nationalite, -19, SpringLayout.NORTH, lblConditionsGnralesDutilisation);
		sl_contentPane.putConstraint(SpringLayout.EAST, cb_nationalite, -158, SpringLayout.EAST, contentPane);
		cb_nationalite.setRenderer(new CountryComboBoxRenderer());

		// Recherche du pays correspondant à la langue de la JVM
		ImageIcon countryDefaut = null;
		for (ImageIcon country : countries) {
			if (country.getDescription().equalsIgnoreCase(Locale.getDefault().getCountry())) {
				countryDefaut = country;
				break;
			}
		}

		if (countryDefaut != null) {
			cb_nationalite.setSelectedItem(countryDefaut);
		}

		contentPane.add(cb_nationalite);

		lblNationalit = new JLabel("Nationalité");
		sl_contentPane.putConstraint(SpringLayout.NORTH, cb_nationalite, 6, SpringLayout.SOUTH, lblNationalit);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblNationalit, 38, SpringLayout.SOUTH, lblEmail);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblNationalit, 0, SpringLayout.WEST, lbl_pseudo);
		contentPane.add(lblNationalit);

		final JButton btnSinscrire = new JButton("S'inscrire");
		sl_contentPane.putConstraint(SpringLayout.SOUTH, chckbxJaccepteLesCgu, -15, SpringLayout.NORTH, btnSinscrire);
		btnSinscrire.setEnabled(false);
		btnSinscrire.addActionListener(this);
		btnSinscrire.setActionCommand(ControllerAction.SUBSCRIPTION.name());
		sl_contentPane.putConstraint(SpringLayout.WEST, btnSinscrire, 66, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnSinscrire, -10, SpringLayout.SOUTH, contentPane);

		contentPane.add(btnSinscrire);

		JButton btnAnnuler = new JButton("Annuler");
		btnAnnuler.addActionListener(this);
		btnAnnuler.setActionCommand(ControllerAction.CANCELLATION.name());
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnAnnuler, 0, SpringLayout.NORTH, btnSinscrire);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnAnnuler, 31, SpringLayout.EAST, btnSinscrire);
		contentPane.add(btnAnnuler);

		JLabel lblConfirmerMotDe = new JLabel("Confirmer mot de passe");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblEmail, 38, SpringLayout.SOUTH, lblConfirmerMotDe);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblConfirmerMotDe, 38, SpringLayout.SOUTH, lbl_motDePasse);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblConfirmerMotDe, 10, SpringLayout.WEST, contentPane);
		contentPane.add(lblConfirmerMotDe);

		pf_passwordConfirm = new JPasswordField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, pf_passwordConfirm, 6, SpringLayout.SOUTH, lblConfirmerMotDe);
		sl_contentPane.putConstraint(SpringLayout.WEST, pf_passwordConfirm, 0, SpringLayout.WEST, lbl_pseudo);
		sl_contentPane.putConstraint(SpringLayout.EAST, pf_passwordConfirm, 0, SpringLayout.EAST, tf_pseudo);
		contentPane.add(pf_passwordConfirm);

		chckbxJaccepteLesCgu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (btnSinscrire.isEnabled()) {
					btnSinscrire.setEnabled(false);
				}
				else {
					btnSinscrire.setEnabled(true);
				}
			}
		});
	}

	@Override
	public void setVisible(boolean b) {
		if (b) {
			// abonnement à la notification de la réception des objets
			// Authentication
			NetworkManager.getInstance().addHandler(this, Authentication.class);
		}
		else {
			NetworkManager.getInstance().removeHandler(this);
			tf_pseudo.setText("");
			pf_password.setText("");
			tf_email.setText("");
		}

		super.setVisible(b);
	}

	private void closeGUI() {
		this.setVisible(false);
		loginGUI.setVisible(true);

	}

	private void initComponent() {
		setSize(350, 438);
		setModal(true);
		setResizable(false);
		setLocationRelativeTo(null);
		setTitle("Inscription");
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ControllerAction.CANCELLATION.name().equals(e.getActionCommand())) {
			closeGUI();
		}
		else if (ControllerAction.SUBSCRIPTION.name().equals(e.getActionCommand())) {
			String pwd = new String(pf_password.getPassword());
			String pwdConfirm = new String(pf_passwordConfirm.getPassword());

			if (pwd.equals(pwdConfirm)) {
				ImageIcon country = (ImageIcon) cb_nationalite.getSelectedItem();

				try {
					NetworkClient.getInstance().subscribe(tf_pseudo.getText(), pwd, tf_email.getText(), country.getDescription());
					closeGUI();
				}
				catch (GUIErrorsException e1) {
					e1.showErrors(this);
				}
			}
			else {
				JOptionPane.showMessageDialog(guiThis, "Les mots de passe sont différents !", pwdConfirm, JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	@Override
	public void handleNetworkEvent(NetworkEvent obj) {
		Authentication auth = (Authentication) obj.getSource();

		if (auth.isSubscription()) {
			JOptionPane.showMessageDialog(this, "Inscription validée !", "Inscription", JOptionPane.INFORMATION_MESSAGE);
			closeGUI();
		}
	}
}
