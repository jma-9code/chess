package gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import main.Config;
import message.ListPlayers;
import message.ProfilePartiesList;
import message.party.ListParties;
import network.NetworkClient;
import network.NetworkEvent;
import network.NetworkEventHandler;
import network.NetworkManager;

import org.apache.log4j.Logger;

import tools.CountryRenderer;
import tools.PlayerRenderer;
import tools.Sprites;
import tools.StateRenderer;
import tools.TableProfile;
import tools.TableProfileParties;

import commun.AbsPartyInfo;
import commun.EPlayerState;
import commun.Player;
import commun.ProfilePartyInfos;
import commun.ProfilePlayerInfos;

public class ProfileGUI extends JDialog implements NetworkEventHandler {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8964547121201285903L;

	private final static Logger logger = Logger.getLogger(ProfileGUI.class);
	private ProfilePartiesList profilePartiesList = new ProfilePartiesList();
	private String playerName;
	private ArrayList<Player> playersList;
	private ArrayList<AbsPartyInfo> partiesList;
	private TableProfile tableProfile;
	private TableProfileParties tableProfileParties;
	private JPanel contentPane;
	private JLabel lblPlayerMail;
	private JLabel lblPlayerName;
	private JLabel lblPlayerScore;
	private JLabel lblPseudo;
	private JLabel lblPays;
	private JLabel lblEmail;
	private JLabel lblInscritDepuisLe;
	private JLabel lblDerniereConnexion;
	private JLabel lblDrap;
	private JLabel lblDateinscription;
	private JLabel lblDateconnexion;
	private JScrollPane scrollPaneParties;
	private JPanel partiesPanel;
	private JLabel lblScore;
	private JPanel informationsPanel;
	private JTabbedPane tabbedPane;
	private JLabel lblChangerMotDe;
	private JPanel panelOpponent;
	private JScrollPane scrollPaneOpponent;
	private JTable profileTable;
	private JTable partiesTable;
	private JLabel lblEditerLeProfil;
	private JPanel statsPanel;
	private JPanel panelParties;
	private EditGUI editGUI;
	private ChangePwdGUI cpwdGUI;
	private JLabel lblRefresh;
	private ReAddComponents components;
	private JLabel lblLoading;
	private JButton btnRevoirPartie;
	private JLabel lblPartiesGagnes;
	private JLabel lblGagnees;
	private JLabel lblPartiesNulles;
	private JLabel lblNulles;
	private JLabel lblPartiesPerdues;
	private JLabel lblPerdues;
	private JLabel lblScoreMoyenDes;
	private JLabel lblScore_1;
	private JLabel lblPlusGrandAdversaire;
	private JLabel lblAdversaire;
	private JLabel lblPartiesJoues;
	private JLabel lblJouees;

	private enum ReAddComponents {
		NEED, NONEED;
	}

	public ProfileGUI() {
		setBounds(100, 100, 372, 325);
		setLocationRelativeTo(null);
		setModal(true);

		components = ReAddComponents.NONEED;
		tableProfile = new TableProfile();
		tableProfileParties = new TableProfileParties();
		editGUI = new EditGUI(this);
		cpwdGUI = new ChangePwdGUI(this);

		contentPane = new JPanel();
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		sl_contentPane.putConstraint(SpringLayout.NORTH, tabbedPane, 0, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, tabbedPane, 0, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, tabbedPane, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, tabbedPane, 0, SpringLayout.EAST, contentPane);
		contentPane.add(tabbedPane, BorderLayout.CENTER);

		informationsPanel = new JPanel();
		tabbedPane.addTab("Général", null, informationsPanel, null);
		SpringLayout sl_informationsPanel = new SpringLayout();
		informationsPanel.setLayout(sl_informationsPanel);

		lblPlayerName = new JLabel("name");
		informationsPanel.add(lblPlayerName);

		lblPlayerMail = new JLabel("mail");
		informationsPanel.add(lblPlayerMail, BorderLayout.WEST);

		lblPlayerScore = new JLabel("score");
		sl_informationsPanel.putConstraint(SpringLayout.EAST, lblPlayerScore, -10, SpringLayout.EAST, informationsPanel);
		sl_informationsPanel.putConstraint(SpringLayout.NORTH, lblPlayerName, 0, SpringLayout.NORTH, lblPlayerScore);
		lblPlayerScore.setVerticalAlignment(SwingConstants.TOP);
		informationsPanel.add(lblPlayerScore, BorderLayout.WEST);

		lblScore = new JLabel("Score :");
		sl_informationsPanel.putConstraint(SpringLayout.EAST, lblScore, -5, SpringLayout.WEST, lblPlayerScore);
		sl_informationsPanel.putConstraint(SpringLayout.NORTH, lblPlayerScore, 0, SpringLayout.NORTH, lblScore);
		informationsPanel.add(lblScore);

		lblPseudo = new JLabel("Nom :");
		sl_informationsPanel.putConstraint(SpringLayout.WEST, lblPlayerName, 6, SpringLayout.EAST, lblPseudo);
		sl_informationsPanel.putConstraint(SpringLayout.NORTH, lblScore, 0, SpringLayout.NORTH, lblPseudo);
		sl_informationsPanel.putConstraint(SpringLayout.NORTH, lblPseudo, 6, SpringLayout.NORTH, informationsPanel);
		sl_informationsPanel.putConstraint(SpringLayout.WEST, lblPseudo, 10, SpringLayout.WEST, informationsPanel);
		informationsPanel.add(lblPseudo);

		lblPays = new JLabel("Pays :");
		sl_informationsPanel.putConstraint(SpringLayout.NORTH, lblPays, 6, SpringLayout.SOUTH, lblPseudo);
		sl_informationsPanel.putConstraint(SpringLayout.WEST, lblPays, 10, SpringLayout.WEST, informationsPanel);
		informationsPanel.add(lblPays);

		lblEmail = new JLabel("Email :");
		sl_informationsPanel.putConstraint(SpringLayout.NORTH, lblPlayerMail, 0, SpringLayout.NORTH, lblEmail);
		sl_informationsPanel.putConstraint(SpringLayout.WEST, lblPlayerMail, 6, SpringLayout.EAST, lblEmail);
		sl_informationsPanel.putConstraint(SpringLayout.NORTH, lblEmail, 6, SpringLayout.SOUTH, lblPays);
		sl_informationsPanel.putConstraint(SpringLayout.WEST, lblEmail, 0, SpringLayout.WEST, lblPseudo);
		informationsPanel.add(lblEmail);

		lblInscritDepuisLe = new JLabel("Inscrit depuis le");
		sl_informationsPanel.putConstraint(SpringLayout.NORTH, lblInscritDepuisLe, 6, SpringLayout.SOUTH, lblEmail);
		sl_informationsPanel.putConstraint(SpringLayout.WEST, lblInscritDepuisLe, 0, SpringLayout.WEST, lblPseudo);
		informationsPanel.add(lblInscritDepuisLe);

		lblDerniereConnexion = new JLabel("Dernière connexion le");
		sl_informationsPanel.putConstraint(SpringLayout.NORTH, lblDerniereConnexion, 6, SpringLayout.SOUTH, lblInscritDepuisLe);
		sl_informationsPanel.putConstraint(SpringLayout.WEST, lblDerniereConnexion, 0, SpringLayout.WEST, lblPseudo);
		informationsPanel.add(lblDerniereConnexion);

		lblDrap = new JLabel("drap");
		sl_informationsPanel.putConstraint(SpringLayout.WEST, lblDrap, 6, SpringLayout.EAST, lblPays);
		sl_informationsPanel.putConstraint(SpringLayout.SOUTH, lblDrap, -6, SpringLayout.NORTH, lblPlayerMail);
		informationsPanel.add(lblDrap);

		lblDateinscription = new JLabel("dateInscription");
		sl_informationsPanel.putConstraint(SpringLayout.WEST, lblDateinscription, 6, SpringLayout.EAST, lblInscritDepuisLe);
		sl_informationsPanel.putConstraint(SpringLayout.SOUTH, lblDateinscription, 0, SpringLayout.SOUTH, lblInscritDepuisLe);
		informationsPanel.add(lblDateinscription);

		lblDateconnexion = new JLabel("dateConnexion");
		sl_informationsPanel.putConstraint(SpringLayout.NORTH, lblDateconnexion, 0, SpringLayout.NORTH, lblDerniereConnexion);
		sl_informationsPanel.putConstraint(SpringLayout.WEST, lblDateconnexion, 6, SpringLayout.EAST, lblDerniereConnexion);
		informationsPanel.add(lblDateconnexion);

		lblChangerMotDe = new JLabel("<html><u>Changer mot de passe</u></html>");
		lblChangerMotDe.setCursor(new Cursor(Cursor.HAND_CURSOR));
		lblChangerMotDe.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (playerName.equalsIgnoreCase(Config.NAME)) {
					cpwdGUI.setVisible(true);
				}
			}
		});
		sl_informationsPanel.putConstraint(SpringLayout.SOUTH, lblChangerMotDe, -5, SpringLayout.SOUTH, informationsPanel);
		lblChangerMotDe.setFont(new Font("SansSerif", Font.PLAIN, 10));
		sl_informationsPanel.putConstraint(SpringLayout.WEST, lblChangerMotDe, 0, SpringLayout.WEST, lblPseudo);
		informationsPanel.add(lblChangerMotDe);

		panelOpponent = new JPanel();
		sl_informationsPanel.putConstraint(SpringLayout.EAST, panelOpponent, -8, SpringLayout.EAST, informationsPanel);
		SpringLayout sl_panelOpponent = new SpringLayout();
		panelOpponent.setLayout(sl_panelOpponent);
		sl_informationsPanel.putConstraint(SpringLayout.NORTH, panelOpponent, 6, SpringLayout.SOUTH, lblDerniereConnexion);
		sl_informationsPanel.putConstraint(SpringLayout.WEST, panelOpponent, 8, SpringLayout.WEST, informationsPanel);
		sl_informationsPanel.putConstraint(SpringLayout.SOUTH, panelOpponent, -6, SpringLayout.NORTH, lblChangerMotDe);
		panelOpponent.setBorder(new TitledBorder(null, "Derniers adversaires", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		informationsPanel.add(panelOpponent);

		scrollPaneOpponent = new JScrollPane();
		sl_panelOpponent.putConstraint(SpringLayout.NORTH, scrollPaneOpponent, 0, SpringLayout.NORTH, panelOpponent);
		sl_panelOpponent.putConstraint(SpringLayout.WEST, scrollPaneOpponent, 0, SpringLayout.WEST, panelOpponent);
		sl_panelOpponent.putConstraint(SpringLayout.SOUTH, scrollPaneOpponent, 0, SpringLayout.SOUTH, panelOpponent);
		sl_panelOpponent.putConstraint(SpringLayout.EAST, scrollPaneOpponent, 0, SpringLayout.EAST, panelOpponent);
		panelOpponent.add(scrollPaneOpponent);

		profileTable = new JTable(tableProfile);
		profileTable.setShowGrid(false);
		profileTable.setShowVerticalLines(false);
		profileTable.setShowHorizontalLines(false);
		profileTable.setFillsViewportHeight(true);
		profileTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		profileTable.setDefaultRenderer(Locale.class, new CountryRenderer());
		profileTable.setDefaultRenderer(EPlayerState.class, new StateRenderer());
		profileTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		profileTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					removeAllComponents();
					profileSelected();
				}
			}
		});
		scrollPaneOpponent.setViewportView(profileTable);

		lblEditerLeProfil = new JLabel("<html><u>Editer le profil</u></html>");
		lblEditerLeProfil.setFont(new Font("SansSerif", Font.PLAIN, 10));
		lblEditerLeProfil.setCursor(new Cursor(Cursor.HAND_CURSOR));
		lblEditerLeProfil.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (playerName.equalsIgnoreCase(Config.NAME)) {
					editGUI.setVisible(true);
				}
			}
		});
		sl_informationsPanel.putConstraint(SpringLayout.SOUTH, lblEditerLeProfil, 0, SpringLayout.SOUTH, lblChangerMotDe);
		sl_informationsPanel.putConstraint(SpringLayout.EAST, lblEditerLeProfil, 0, SpringLayout.EAST, lblPlayerScore);
		informationsPanel.add(lblEditerLeProfil);

		lblRefresh = new JLabel();
		sl_informationsPanel.putConstraint(SpringLayout.NORTH, lblRefresh, -15, SpringLayout.NORTH, panelOpponent);
		sl_informationsPanel.putConstraint(SpringLayout.WEST, lblRefresh, -25, SpringLayout.EAST, informationsPanel);
		sl_informationsPanel.putConstraint(SpringLayout.EAST, lblRefresh, -10, SpringLayout.EAST, informationsPanel);
		lblRefresh.setIcon(Sprites.getInstance().getRefresh());
		lblRefresh.setToolTipText("refresh");
		lblRefresh.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				lblRefresh.setIcon(Sprites.getInstance().getRefreshlive());
				repaint();
				askFullProfile();
			}
		});
		informationsPanel.add(lblRefresh);

		lblLoading = new JLabel();
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblLoading, 0, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblLoading, 0, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblLoading, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblLoading, 0, SpringLayout.EAST, contentPane);
		lblLoading.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoading.setIcon(Sprites.getInstance().getLoading());
		lblLoading.setVisible(false);
		sl_informationsPanel.putConstraint(SpringLayout.NORTH, lblLoading, 0, SpringLayout.NORTH, contentPane);
		sl_informationsPanel.putConstraint(SpringLayout.WEST, lblLoading, 0, SpringLayout.WEST, contentPane);
		sl_informationsPanel.putConstraint(SpringLayout.SOUTH, lblLoading, 0, SpringLayout.SOUTH, contentPane);
		sl_informationsPanel.putConstraint(SpringLayout.EAST, lblLoading, 0, SpringLayout.EAST, contentPane);
		contentPane.add(lblLoading);

		statsPanel = new JPanel();
		tabbedPane.addTab("Statistiques", null, statsPanel, null);
		SpringLayout sl_statsPanel = new SpringLayout();
		statsPanel.setLayout(sl_statsPanel);

		lblPartiesGagnes = new JLabel("Parties gagnées :");
		sl_statsPanel.putConstraint(SpringLayout.WEST, lblPartiesGagnes, 10, SpringLayout.WEST, statsPanel);
		statsPanel.add(lblPartiesGagnes);

		lblGagnees = new JLabel("gagnees");
		sl_statsPanel.putConstraint(SpringLayout.NORTH, lblGagnees, 0, SpringLayout.NORTH, lblPartiesGagnes);
		sl_statsPanel.putConstraint(SpringLayout.WEST, lblGagnees, 6, SpringLayout.EAST, lblPartiesGagnes);
		statsPanel.add(lblGagnees);

		lblPartiesNulles = new JLabel("Parties nulles :");
		sl_statsPanel.putConstraint(SpringLayout.NORTH, lblPartiesNulles, 6, SpringLayout.SOUTH, lblPartiesGagnes);
		sl_statsPanel.putConstraint(SpringLayout.WEST, lblPartiesNulles, 0, SpringLayout.WEST, lblPartiesGagnes);
		statsPanel.add(lblPartiesNulles);

		lblNulles = new JLabel("nulles");
		sl_statsPanel.putConstraint(SpringLayout.NORTH, lblNulles, 0, SpringLayout.NORTH, lblPartiesNulles);
		sl_statsPanel.putConstraint(SpringLayout.WEST, lblNulles, 6, SpringLayout.EAST, lblPartiesNulles);
		statsPanel.add(lblNulles);

		lblPartiesPerdues = new JLabel("Parties perdues :");
		sl_statsPanel.putConstraint(SpringLayout.NORTH, lblPartiesPerdues, 6, SpringLayout.SOUTH, lblPartiesNulles);
		sl_statsPanel.putConstraint(SpringLayout.WEST, lblPartiesPerdues, 0, SpringLayout.WEST, lblPartiesGagnes);
		statsPanel.add(lblPartiesPerdues);

		lblPerdues = new JLabel("perdues");
		sl_statsPanel.putConstraint(SpringLayout.NORTH, lblPerdues, 0, SpringLayout.NORTH, lblPartiesPerdues);
		sl_statsPanel.putConstraint(SpringLayout.WEST, lblPerdues, 0, SpringLayout.WEST, lblGagnees);
		statsPanel.add(lblPerdues);

		lblScoreMoyenDes = new JLabel("Score moyen des adversaires :");
		sl_statsPanel.putConstraint(SpringLayout.NORTH, lblScoreMoyenDes, 6, SpringLayout.SOUTH, lblPartiesPerdues);
		sl_statsPanel.putConstraint(SpringLayout.WEST, lblScoreMoyenDes, 0, SpringLayout.WEST, lblPartiesGagnes);
		statsPanel.add(lblScoreMoyenDes);

		lblScore_1 = new JLabel("score");
		sl_statsPanel.putConstraint(SpringLayout.NORTH, lblScore_1, 0, SpringLayout.NORTH, lblScoreMoyenDes);
		sl_statsPanel.putConstraint(SpringLayout.WEST, lblScore_1, 6, SpringLayout.EAST, lblScoreMoyenDes);
		statsPanel.add(lblScore_1);

		lblPlusGrandAdversaire = new JLabel("Meilleur adversaire :");
		sl_statsPanel.putConstraint(SpringLayout.NORTH, lblPlusGrandAdversaire, 6, SpringLayout.SOUTH, lblScoreMoyenDes);
		sl_statsPanel.putConstraint(SpringLayout.WEST, lblPlusGrandAdversaire, 0, SpringLayout.WEST, lblPartiesGagnes);
		statsPanel.add(lblPlusGrandAdversaire);

		lblAdversaire = new JLabel("adversaire");
		sl_statsPanel.putConstraint(SpringLayout.NORTH, lblAdversaire, 6, SpringLayout.SOUTH, lblScoreMoyenDes);
		sl_statsPanel.putConstraint(SpringLayout.WEST, lblAdversaire, 6, SpringLayout.EAST, lblPlusGrandAdversaire);
		statsPanel.add(lblAdversaire);

		lblPartiesJoues = new JLabel("Parties jouées :");
		sl_statsPanel.putConstraint(SpringLayout.NORTH, lblPartiesGagnes, 6, SpringLayout.SOUTH, lblPartiesJoues);
		sl_statsPanel.putConstraint(SpringLayout.NORTH, lblPartiesJoues, 10, SpringLayout.NORTH, statsPanel);
		sl_statsPanel.putConstraint(SpringLayout.WEST, lblPartiesJoues, 10, SpringLayout.WEST, statsPanel);
		statsPanel.add(lblPartiesJoues);

		lblJouees = new JLabel("jouees");
		sl_statsPanel.putConstraint(SpringLayout.WEST, lblJouees, 6, SpringLayout.EAST, lblPartiesJoues);
		sl_statsPanel.putConstraint(SpringLayout.SOUTH, lblJouees, -6, SpringLayout.NORTH, lblPartiesGagnes);
		statsPanel.add(lblJouees);

		partiesPanel = new JPanel();
		tabbedPane.addTab("Parties récentes", null, partiesPanel, null);
		SpringLayout sl_partiesPanel = new SpringLayout();
		partiesPanel.setLayout(sl_partiesPanel);

		panelParties = new JPanel();
		sl_partiesPanel.putConstraint(SpringLayout.NORTH, panelParties, 0, SpringLayout.NORTH, partiesPanel);
		sl_partiesPanel.putConstraint(SpringLayout.WEST, panelParties, 0, SpringLayout.WEST, partiesPanel);
		sl_partiesPanel.putConstraint(SpringLayout.SOUTH, panelParties, 215, SpringLayout.NORTH, partiesPanel);
		sl_partiesPanel.putConstraint(SpringLayout.EAST, panelParties, 0, SpringLayout.EAST, partiesPanel);
		SpringLayout sl_panelParties = new SpringLayout();
		panelParties.setLayout(sl_panelParties);
		partiesPanel.add(panelParties);

		scrollPaneParties = new JScrollPane();
		sl_panelParties.putConstraint(SpringLayout.NORTH, scrollPaneParties, 0, SpringLayout.NORTH, panelParties);
		sl_panelParties.putConstraint(SpringLayout.WEST, scrollPaneParties, 0, SpringLayout.WEST, panelParties);
		sl_panelParties.putConstraint(SpringLayout.SOUTH, scrollPaneParties, 0, SpringLayout.SOUTH, panelParties);
		sl_panelParties.putConstraint(SpringLayout.EAST, scrollPaneParties, 0, SpringLayout.EAST, panelParties);
		sl_partiesPanel.putConstraint(SpringLayout.NORTH, scrollPaneParties, 0, SpringLayout.NORTH, partiesPanel);
		sl_partiesPanel.putConstraint(SpringLayout.WEST, scrollPaneParties, 0, SpringLayout.WEST, partiesPanel);
		sl_partiesPanel.putConstraint(SpringLayout.SOUTH, scrollPaneParties, 0, SpringLayout.SOUTH, partiesPanel);
		sl_partiesPanel.putConstraint(SpringLayout.EAST, scrollPaneParties, 0, SpringLayout.EAST, partiesPanel);
		panelParties.add(scrollPaneParties);

		partiesTable = new JTable(tableProfileParties);
		partiesTable.setRowSelectionAllowed(true);
		partiesTable.setCellSelectionEnabled(true);
		partiesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		partiesTable.setDefaultRenderer(ProfilePlayerInfos.class, new PlayerRenderer());
		partiesTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		partiesTable.setFillsViewportHeight(true);
		partiesTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getClickCount() == 2) {
					if (partiesTable.getSelectedRow() != -1) {

					}
				}
			}
		});
		scrollPaneParties.setViewportView(partiesTable);

		btnRevoirPartie = new JButton("Revoir partie");
		btnRevoirPartie.setEnabled(false);
		sl_partiesPanel.putConstraint(SpringLayout.NORTH, btnRevoirPartie, 6, SpringLayout.SOUTH, panelParties);
		sl_partiesPanel.putConstraint(SpringLayout.WEST, btnRevoirPartie, 123, SpringLayout.WEST, partiesPanel);
		partiesPanel.add(btnRevoirPartie);
	}

	@Override
	public void handleNetworkEvent(NetworkEvent obj) {
		if (obj.getSource() instanceof ProfilePartiesList) {
			profilePartiesList = (ProfilePartiesList) obj.getSource();
			NetworkManager.getInstance().removeHandler(this, ProfilePartiesList.class);
			updateProfile();
		}
		else if (obj.getSource() instanceof ListPlayers) {
			ListPlayers List = (ListPlayers) obj.getSource();
			NetworkManager.getInstance().removeHandler(this, ListPlayers.class);
			playersList = List.getListPlayers();
		}
		else if (obj.getSource() instanceof ListParties) {
			ListParties List = (ListParties) obj.getSource();
			NetworkManager.getInstance().removeHandler(this, ListParties.class);
			partiesList = List.getListOfParties();
		}
	}

	private void updateJTable() {
		ArrayList<ProfilePartyInfos> listParties = profilePartiesList.getPartiesList();
		tableProfileParties.refresh(listParties);
		ArrayList<Player> lastOpponents = new ArrayList<Player>();
		Iterator<ProfilePartyInfos> it = listParties.iterator();
		while (it.hasNext() && lastOpponents.size() < 5) {
			ProfilePartyInfos info = it.next();
			if (info.getBlackPlayer().getPlayerName().equals(playerName)) {
				if (!isInPlayerList(lastOpponents, info.getWhitePlayer().getPlayerName())) {
					if (isInPlayerList(playersList, info.getWhitePlayer().getPlayerName())) {
						if (isInPartyList(partiesList, info.getWhitePlayer().getPlayerName())) {
							lastOpponents.add(new Player(info.getWhitePlayer().getPlayerName(), info.getWhitePlayer().getPlayerScore(), info
									.getWhitePlayer().getPlayerCountry(), EPlayerState.BUSY));
						}
						else {
							lastOpponents.add(new Player(info.getWhitePlayer().getPlayerName(), info.getWhitePlayer().getPlayerScore(), info
									.getWhitePlayer().getPlayerCountry(), EPlayerState.ONLINE));
						}
					}
					else {
						lastOpponents.add(new Player(info.getWhitePlayer().getPlayerName(), info.getWhitePlayer().getPlayerScore(), info
								.getWhitePlayer().getPlayerCountry(), EPlayerState.OFFLINE));
					}
				}
			}
			else if (info.getWhitePlayer().getPlayerName().equals(playerName)) {
				if (!isInPlayerList(lastOpponents, info.getBlackPlayer().getPlayerName())) {
					if (isInPlayerList(playersList, info.getBlackPlayer().getPlayerName())) {
						if (isInPartyList(partiesList, info.getBlackPlayer().getPlayerName())) {
							lastOpponents.add(new Player(info.getBlackPlayer().getPlayerName(), info.getBlackPlayer().getPlayerScore(), info
									.getBlackPlayer().getPlayerCountry(), EPlayerState.BUSY));
						}
						else {
							lastOpponents.add(new Player(info.getBlackPlayer().getPlayerName(), info.getBlackPlayer().getPlayerScore(), info
									.getBlackPlayer().getPlayerCountry(), EPlayerState.ONLINE));
						}
					}
					else {
						lastOpponents.add(new Player(info.getBlackPlayer().getPlayerName(), info.getBlackPlayer().getPlayerScore(), info
								.getBlackPlayer().getPlayerCountry(), EPlayerState.OFFLINE));
					}
				}
			}
		}
		tableProfile.refresh(lastOpponents);
	}

	private boolean isInPlayerList(ArrayList<Player> list, String name) {
		Iterator<Player> it = list.iterator();
		while (it.hasNext()) {
			Player temp = it.next();
			if (temp.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	private boolean isInPartyList(ArrayList<AbsPartyInfo> list, String name) {
		Iterator<AbsPartyInfo> it = list.iterator();
		while (it.hasNext()) {
			AbsPartyInfo temp = it.next();
			if (isInPlayerList(temp.getPlayers(), name)) {
				return true;
			}
		}
		return false;
	}

	public void setVisible(boolean b, String name) {
		if (b) {
			playerName = name;
			removeAllComponents();
			askFullProfile();
		}
		super.setVisible(b);
	}

	public void updateProfile() {
		setTitle("Profil de " + profilePartiesList.getPlayerName());

		if (playerName.equalsIgnoreCase(Config.NAME)) {
			lblChangerMotDe.setCursor(new Cursor(Cursor.HAND_CURSOR));
			lblEditerLeProfil.setCursor(new Cursor(Cursor.HAND_CURSOR));
			lblEditerLeProfil.setText("<html><u>Editer le profil</u></html>");
			lblChangerMotDe.setText("<html><u>Changer mot de passe</u></html>");
		}
		else {
			lblChangerMotDe.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			lblEditerLeProfil.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			lblEditerLeProfil.setText("");
			lblChangerMotDe.setText("");
		}

		lblPlayerName.setText(profilePartiesList.getPlayerName());

		for (ImageIcon icon : Sprites.getInstance().getCountries()) {
			if (profilePartiesList.getPlayerCountry().equals(icon.getDescription())) {
				lblDrap.setText("");
				lblDrap.setIcon(icon);
				break;
			}
		}

		lblPlayerMail.setText(profilePartiesList.getPlayerEmail());

		lblPlayerScore.setText(Integer.toString(profilePartiesList.getPlayerScore()));
		lblPlayerScore.setIcon(Sprites.getInstance().getStar());

		lblDateinscription.setText(mefDateInscription(profilePartiesList.getSubscriptionDate()));

		if (playerName.equals(Config.NAME)) {
			lblDerniereConnexion.setText("");
			lblDateconnexion.setText("");
			lblDateconnexion.setIcon(null);
			lblDateconnexion.setToolTipText(null);
		}
		else {
			if (isInPlayerList(playersList, playerName)) {
				lblDerniereConnexion.setText("Dernière connexion :");
				lblDateconnexion.setText("");
				lblDateconnexion.setIcon(Sprites.getInstance().getOnline());
				lblDateconnexion.setToolTipText("Online");
			}
			else {
				lblDerniereConnexion.setText("Dernière connexion :");
				lblDateconnexion.setText(mefDateConnection(profilePartiesList.getConnectionDate()));
				lblDateconnexion.setIcon(null);
				lblDateconnexion.setToolTipText(null);
			}
		}

		lblRefresh.setIcon(Sprites.getInstance().getRefresh());

		updateJTable();
		createStats();

		if (components.equals(ReAddComponents.NEED)) {
			AddAllComponents();
		}

		validate();
		repaint();
	}

	private String mefDateConnection(String string) {
		Date date;
		try {
			date = new SimpleDateFormat("yyyyMMdd HHmmss").parse(string);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			String dateString = sdf.format(date);
			dateString.replaceAll(" ", " à ");

			return dateString;
		}
		catch (ParseException e) {
			e.printStackTrace();
			return "";
		}
	}

	private String mefDateInscription(String string) {
		Date date;
		try {
			date = new SimpleDateFormat("yyyyMMdd").parse(string);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			return sdf.format(date);
		}
		catch (ParseException e) {
			e.printStackTrace();
			return "";
		}
	}

	public void askFullProfile() {
		NetworkManager.getInstance().addHandler(this, ListParties.class);
		NetworkManager.getInstance().addHandler(this, ListPlayers.class);
		NetworkManager.getInstance().addHandler(this, ProfilePartiesList.class);
		NetworkClient.getInstance().sendObject(new ListPlayers());
		NetworkClient.getInstance().sendObject(new ListParties());
		profilePartiesList.setPlayerName(playerName);
		NetworkClient.getInstance().sendObject(profilePartiesList);
	}

	public void profileSelected() {
		if (profileTable.getSelectedRow() != -1) {
			try {
				int col_id;
				for (col_id = 0; col_id < profileTable.getColumnCount(); col_id++) {
					if ("Nom".equalsIgnoreCase(profileTable.getColumnName(col_id))) {
						break;
					}
				}
				String name = (String) profileTable.getValueAt(profileTable.getSelectedRow(), col_id);
				this.playerName = name;
				askFullProfile();
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public ProfilePartiesList getProfilePartiesList() {
		return profilePartiesList;
	}

	private void removeAllComponents() {
		tabbedPane.setVisible(false);
		lblLoading.setVisible(true);
		components = ReAddComponents.NEED;
		repaint();
	}

	private void AddAllComponents() {
		components = ReAddComponents.NONEED;
		tabbedPane.setVisible(true);
		lblLoading.setVisible(false);
	}

	private void createStats() {
		ArrayList<ProfilePartyInfos> listParties = profilePartiesList.getPartiesList();
		ArrayList<String> listOpponents = new ArrayList<String>();
		String bestOppo = "Aucun";
		Iterator it = listParties.iterator();
		int total = listParties.size();
		int gagnee = 0;
		int nulle = 0;
		int score = 0;
		while (it.hasNext()) {
			ProfilePartyInfos temp = (ProfilePartyInfos) it.next();

			if (temp.getWinner().getPlayerName().equalsIgnoreCase(playerName)) {
				gagnee++;
			}
			else if (temp.getWinner().getPlayerName().equalsIgnoreCase("Partie nulle")) {
				nulle++;
			}
			else {
				listOpponents.add(temp.getWinner().getPlayerName());
			}

			if (temp.getBlackPlayer().getPlayerName().equalsIgnoreCase(playerName)) {
				score += temp.getWhitePlayer().getPlayerScore();
			}
			else {
				score += temp.getBlackPlayer().getPlayerScore();
			}
		}
		if (total == 0) {
			score = 1000;
		}
		else {
			score = score / total;
		}
		int perdue = total - gagnee;
		int instances = 0;
		it = listOpponents.iterator();
		while (it.hasNext()) {
			String name = (String) it.next();
			int temp = nbInstances(listOpponents, name);
			if (temp > instances) {
				instances = temp;
				bestOppo = name;
			}
		}
		drawStats(total, gagnee, nulle, perdue, score, bestOppo, instances);
	}

	private void drawStats(int total, int gagnee, int nulle, int perdue, int score, String bestOppo, int instances) {
		lblJouees.setText(Integer.toString(total));
		lblGagnees.setText(Integer.toString(gagnee));
		lblNulles.setText(Integer.toString(nulle));
		lblPerdues.setText(Integer.toString(perdue));
		lblScore_1.setText(Integer.toString(score));
		lblAdversaire.setText(bestOppo + " (" + Integer.toString(instances) + ")");
	}

	private int nbInstances(ArrayList<String> listOpponents, String next) {
		Iterator<String> it = listOpponents.iterator();
		int instances = 0;
		while (it.hasNext()) {
			String temp = it.next();
			if (temp.equalsIgnoreCase(next)) {
				instances++;
			}
		}
		return instances;
	}
}
