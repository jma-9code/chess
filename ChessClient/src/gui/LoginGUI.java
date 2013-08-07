package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;

import main.Config;
import message.Authentication;
import network.NetworkClient;
import network.NetworkEvent;
import network.NetworkEventHandler;
import network.NetworkManager;

import org.apache.log4j.Logger;

public class LoginGUI extends JFrame implements NetworkEventHandler {
	private static final Logger logger = Logger.getLogger(LoginGUI.class);
	private static final long serialVersionUID = -295860552164986370L;
	private JPanel contentPane;
	private JTextField tf_name;
	private JLabel lblPseudo;
	private JLabel lblMotDePasse;
	private JPasswordField tf_pwd;
	private JCheckBox chckbxModeHorsligne;
	private LoginGUI himself;
	private SubscribeGUI subscribe = new SubscribeGUI(this);

	/**
	 * Create the frame.
	 */
	public LoginGUI() {
		initComponent();
		himself = this;

		contentPane = new JPanel();
		contentPane.setBorder(new TitledBorder(null, "Identification", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);

		tf_name = new JTextField();
		sl_contentPane.putConstraint(SpringLayout.WEST, tf_name, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, tf_name, -10, SpringLayout.EAST, contentPane);
		contentPane.add(tf_name);
		tf_name.setColumns(10);

		lblPseudo = new JLabel("Pseudonyme");
		sl_contentPane.putConstraint(SpringLayout.NORTH, tf_name, 6, SpringLayout.SOUTH, lblPseudo);
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblPseudo, 0, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblPseudo, 10, SpringLayout.WEST, contentPane);
		contentPane.add(lblPseudo);

		lblMotDePasse = new JLabel("Mot de passe");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblMotDePasse, 10, SpringLayout.SOUTH, tf_name);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblMotDePasse, 0, SpringLayout.WEST, tf_name);
		contentPane.add(lblMotDePasse);

		tf_pwd = new JPasswordField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, tf_pwd, 6, SpringLayout.SOUTH, lblMotDePasse);
		sl_contentPane.putConstraint(SpringLayout.WEST, tf_pwd, 0, SpringLayout.WEST, tf_name);
		sl_contentPane.putConstraint(SpringLayout.EAST, tf_pwd, -10, SpringLayout.EAST, contentPane);
		tf_pwd.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() != KeyEvent.VK_ENTER) {
					return;
				}

				connection();
			}
		});
		contentPane.add(tf_pwd);

		JButton btnConnexion = new JButton("Connexion");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnConnexion, 10, SpringLayout.WEST, contentPane);
		btnConnexion.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				connection();
			}
		});
		contentPane.add(btnConnexion);

		JButton btnInscription = new JButton("Inscription");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnInscription, 0, SpringLayout.NORTH, btnConnexion);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnInscription, 0, SpringLayout.EAST, tf_name);
		btnInscription.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				himself.setVisible(false);
				subscribe.setVisible(true);
			}
		});
		contentPane.add(btnInscription);

		chckbxModeHorsligne = new JCheckBox("Mode hors-ligne");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnConnexion, 6, SpringLayout.SOUTH, chckbxModeHorsligne);
		sl_contentPane.putConstraint(SpringLayout.NORTH, chckbxModeHorsligne, 6, SpringLayout.SOUTH, tf_pwd);
		sl_contentPane.putConstraint(SpringLayout.WEST, chckbxModeHorsligne, 0, SpringLayout.WEST, tf_name);
		chckbxModeHorsligne.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox src = (JCheckBox) e.getSource();
				MainGUI.getInstance().setOfflineMode(src.isSelected());
				tf_pwd.setEnabled(!src.isSelected());
			}
		});
		contentPane.add(chckbxModeHorsligne);
		// DEBUG pour autoco
		/*
		 * if (
		 * NetworkClient.getInstance().getStatut().equals(ENetworkClient.CONNECTED
		 * ) ) {
		 * goMainGUI();
		 * }
		 * else {
		 * setVisible(true);
		 * }
		 */
		setVisible(true);
	}

	private void initComponent() {
		setSize(235, 235);
		setLocationRelativeTo(null);
		setResizable(false);
		setTitle("Identification");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * Méthode de connexion commune au mode hors ligne et en ligne.
	 */
	private void connection() {
		// mode hors ligne
		if (chckbxModeHorsligne.isSelected()) {
			Config.NAME = tf_name.getText();

			// go main
			goMainGUI();
		}

		// mode en ligne
		else {
			try {
				NetworkClient.getInstance().login(tf_name.getText(), new String(tf_pwd.getPassword()));
				// next in handler
			}
			catch (GUIErrorsException e) {
				e.showErrors(this);
			}
		}
	}

	/**
	 * Changemetn de fenetre vers la principale
	 */
	public void goMainGUI() {
		setVisible(false);
		MainGUI.getInstance().setVisible(true);
	}

	@Override
	public void handleNetworkEvent(NetworkEvent obj) {
		Authentication auth = (Authentication) obj.getSource();

		if (auth.isLogin()) {
			logger.info("Authentication successed.");

			// go main
			goMainGUI();
		}
	}

	@Override
	public void setVisible(boolean b) {
		if (b) {
			// test.charAt(1);
			// abonnement à la notification de la réception des objets
			// Authentication
			NetworkManager.getInstance().addHandler(this, Authentication.class);
		}
		else {
			// NetworkManager.getInstance().removeHandler(this);
			tf_name.setText("");
			tf_pwd.setText("");
		}

		super.setVisible(b);
	}

	public JLabel getLblPseudo() {
		return lblPseudo;
	}

	public void setLblPseudo(JLabel lblPseudo) {
		this.lblPseudo = lblPseudo;
	}

	public JLabel getLblMotDePasse() {
		return lblMotDePasse;
	}

	public void setLblMotDePasse(JLabel lblMotDePasse) {
		this.lblMotDePasse = lblMotDePasse;
	}

	public JTextField getTf_name() {
		return tf_name;
	}

	public void setTf_name(JTextField tf_name) {
		this.tf_name = tf_name;
	}

	public JPasswordField getTf_pwd() {
		return tf_pwd;
	}

	public void setTf_pwd(JPasswordField tf_pwd) {
		this.tf_pwd = tf_pwd;
	}
}
