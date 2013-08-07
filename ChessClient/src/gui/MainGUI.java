package gui;

import gui.chessGame.PartyGUI;
import gui.chessGame.PartyNetworkGUI;
import gui.chessGame.PartyReplayGUI;
import gui.config.ConfigGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import main.Config;
import main.Main;
import message.party.StateOfParty;
import model.Chessboard.EChessInfoPlateau;
import model.Party;
import model.PartyNetwork;
import network.NetworkClient;
import network.NetworkClient.ENetworkClient;
import network.NetworkManager;
import network.NetworkStateListener;

import org.apache.log4j.Logger;

import tools.PGNReader;

import commun.EColor;
import commun.Player;

public class MainGUI extends JFrame implements NetworkStateListener {

	private static final Logger logger = Logger.getLogger(MainGUI.class);
	private static final long serialVersionUID = 1L;
	private JMenuBar menuBar;
	private JMenu mnGame;
	private JMenu mnOptions;
	private JMenuItem mntmConfigTouches;
	private JMenu mnNetwork;
	private JMenuItem mntmJoinSalon;
	private SalonGUI salonGUI;
	private ProfileGUI profilGUI;
	private ConfigGUI configGUI = new ConfigGUI();
	private static MainGUI instance = null;
	private Party partyModel;
	private JPanel pnl_game;
	private JLabel lbl_state;
	private JMenuItem mntmReplay;
	private JMenuItem mntmSave;
	private JMenuItem mntmStart;
	private JMenuItem mntmAfficherLeProfil;
	// TODO REPASSER A TRUE APRES AVOIR ENLEVER LAUTOLOGIN
	private boolean offlineMode = false;
	private PartyReplayGUI gameReplay;
	private PartyGUI partyGui;
	private JMenu mnDebug;
	private JMenuItem mntmEnvoyerEndpartywin;
	private JMenuItem mntmEnvoyerEndpartylose;

	/**
	 * Create the applet.
	 */
	private MainGUI() {
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		setTitle("Jeu d'Echec");

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		mnGame = new JMenu("Jeu");
		menuBar.add(mnGame);

		mntmStart = new JMenuItem("2 joueurs");
		mntmStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				destroyCurrentParty();
				partyModel = new Party();
				partyModel.addPlayer(new Player(Config.NAME, 1000, EColor.WHITE));
				partyModel.addPlayer(new Player("Black", 1000, EColor.BLACK));
				startParty(partyModel);
			}
		});
		mnGame.add(mntmStart);

		mntmReplay = new JMenuItem("Rejouer une partie");
		mntmReplay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				replayParty();
			}
		});

		mntmSave = new JMenuItem("Sauvegarder la partie");
		mntmSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (partyModel != null) {
					JFileChooser jfile = new JFileChooser();
					int ret = jfile.showSaveDialog(instance);
					if (ret == JFileChooser.APPROVE_OPTION) {
						Party.saveParty(partyModel, jfile.getSelectedFile().getAbsolutePath());
					}
				}
				else {
					new GUIErrorsException("Impossible de sauvegarder la partie", "Pas de partie en cours !").showErrors(instance);
				}
			}
		});
		mnGame.add(mntmSave);
		mnGame.add(mntmReplay);

		mnNetwork = new JMenu("R\u00E9seau");
		menuBar.add(mnNetwork);

		mntmJoinSalon = new JMenuItem("Rejoindre le salon");
		mntmJoinSalon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (salonGUI == null) {
					salonGUI = new SalonGUI();
				}
				salonGUI.setVisible(true);
			}
		});
		mnNetwork.add(mntmJoinSalon);

		mntmAfficherLeProfil = new JMenuItem("Afficher le profil");
		mntmAfficherLeProfil.setEnabled(true);
		mntmAfficherLeProfil.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (profilGUI == null) {
					profilGUI = new ProfileGUI();
				}
				profilGUI.setVisible(true, Config.NAME);
			}
		});
		mnNetwork.add(mntmAfficherLeProfil);

		mnOptions = new JMenu("Options");
		menuBar.add(mnOptions);

		mntmConfigTouches = new JMenuItem("Configurations");
		mntmConfigTouches.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				configGUI.setVisible(true);
			}
		});
		mnOptions.add(mntmConfigTouches);

		if (Config.DEBUG) {
			mnDebug = new JMenu("Debug");
			mnDebug.addMenuListener(new MenuListener() {

				@Override
				public void menuSelected(MenuEvent e) {
					boolean active = partyModel != null && partyModel instanceof PartyNetwork
							&& ((PartyNetwork) partyModel).getStatut().equals(StateOfParty.RUNNING);
					logger.debug("test " + partyModel);
					mntmEnvoyerEndpartywin.setEnabled(active);
					mntmEnvoyerEndpartylose.setEnabled(active);
				}

				@Override
				public void menuDeselected(MenuEvent e) {
				}

				@Override
				public void menuCanceled(MenuEvent e) {
				}
			});
			menuBar.add(mnDebug);

			mntmEnvoyerEndpartywin = new JMenuItem("Envoyer EndParty(WIN)");
			mntmEnvoyerEndpartywin.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (partyModel != null && NetworkClient.getInstance().isConnected()) {
						logger.debug("DEBUG MODE : sent EndParty(StateOfPlayer.WIN)");
						PartyNetwork pN = (PartyNetwork) partyModel;

						if (pN.getCurrentPlayer().getColor().equals(EColor.BLACK)) {
							pN.getPlateau().setBlackIsEchecMAT(true);
						}
						else {
							pN.getPlateau().setWhiteIsEchecMAT(true);
						}
						pN.getPlateau().endGame();
						pN.getPlateau().updateObservers(EChessInfoPlateau.CHECKMATE);
					}
				}
			});
			mnDebug.add(mntmEnvoyerEndpartywin);

			mntmEnvoyerEndpartylose = new JMenuItem("Envoyer EndParty(LOSE)");
			mntmEnvoyerEndpartylose.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (partyModel != null && NetworkClient.getInstance().isConnected()) {
						logger.debug("DEBUG MODE : sent EndParty(StateOfPlayer.LOSE)");
						PartyNetwork pN = (PartyNetwork) partyModel;

						if (pN.getCurrentPlayer().getColor().equals(EColor.BLACK)) {
							pN.getPlateau().setWhiteIsEchecMAT(true);
						}
						else {
							pN.getPlateau().setBlackIsEchecMAT(true);
						}

						pN.getPlateau().endGame();
						pN.getPlateau().updateObservers(EChessInfoPlateau.CHECKMATE);
					}
				}
			});
			mnDebug.add(mntmEnvoyerEndpartylose);

		}

		getContentPane().setLayout(new BorderLayout(0, 0));

		pnl_game = new JPanel();
		getContentPane().add(pnl_game, BorderLayout.CENTER);

		lbl_state = new JLabel("Connexion au serveur...");
		lbl_state.setIcon(new ImageIcon(Main.class.getResource("/res/images/load.gif")));
		lbl_state.setBorder(new LineBorder(new Color(0, 0, 0), 1, false));
		getContentPane().add(lbl_state, BorderLayout.SOUTH);

		// abonnement aux évènements de changement d'état du réseau
		NetworkManager.getInstance().addNetworkStateListener(this);
		NetworkManager.getInstance().forceNetworkStateNotification();
	}

	public static MainGUI getInstance() {
		if (instance == null) {
			instance = new MainGUI();
		}
		return instance;
	}

	@Override
	public void setVisible(boolean b) {
		if (!isOfflineMode()) {
			if (b) {
				// abonnement aux évènements de changement d'état du réseau
				NetworkManager.getInstance().addNetworkStateListener(this);
			}
			else {
				NetworkManager.getInstance().removeNetworkStateListener(this);
			}
			mnNetwork.setVisible(true);
			lbl_state.setVisible(true);
		}
		else {
			mnNetwork.setVisible(false);
			lbl_state.setVisible(false);
			NetworkManager.getInstance().removeNetworkStateListener(this);
		}
		super.setVisible(b);
	}

	public void destroyCurrentParty() {
		if (gameReplay != null) {
			gameReplay.destroyReplay();
		}
		if (partyModel != null) {
			partyModel.stopGame();
			partyModel = null;
		}
		pnl_game.removeAll();
		pnl_game.repaint();
	}

	public void startParty(Party p) {
		if (p instanceof PartyNetwork) {
			partyGui = new PartyNetworkGUI((PartyNetwork) p);
		}
		else {
			partyGui = new PartyGUI(p);
		}

		this.partyModel = p;

		pnl_game.removeAll();
		pnl_game.add(partyGui);
		validate();
		repaint();
		pack();
		requestFocus();
	}

	public void replayParty() {
		JFileChooser jfile = new JFileChooser();
		int ret = jfile.showOpenDialog(this);
		if (ret == JFileChooser.APPROVE_OPTION) {
			File file = jfile.getSelectedFile();
			destroyCurrentParty();
			Party party = null;
			if (file.getName().toLowerCase().matches(".*pgn")) {
				party = PGNReader.convertPGN(file.getAbsolutePath());
			}
			else {
				try {
					party = Party.loadParty(file.getAbsolutePath());
				}
				catch (Exception e) {
				}
			}
			if (party == null) {
				new GUIErrorsException("Problème de lecture de la partie", "Impossible de trouver une partie !");
			}
			else {
				gameReplay = new PartyReplayGUI(party);
				pnl_game.add(gameReplay);
				validate();
				repaint();
				pack();
			}
		}
	}

	@Override
	public void networkStateChanged(Object obj) {
		logger.debug("State network changed : " + obj.toString());
		switch ((ENetworkClient) obj) {
			case DISCONNECTED:
				if (salonGUI != null) {
					salonGUI.setVisible(false);
				}
				if (profilGUI != null) {
					profilGUI.setVisible(false);
				}
				mntmJoinSalon.setEnabled(false);
				mntmAfficherLeProfil.setEnabled(false);
				lbl_state.setText("Connection au serveur...");
				lbl_state.setIcon(new ImageIcon(Main.class.getResource("/res/images/load.gif")));
				break;

			case WAITING_IDENTIFICATION:
				// TODO VOIR LES ENABLES A FAIRE POUR L'INTERFACE !!
				mntmJoinSalon.setEnabled(false);
				lbl_state.setText("En attente d'identification...");
				break;

			case NOT_AVAILABLE:
				lbl_state.setText("Tentative de reconnexion...");
				lbl_state.setIcon(new ImageIcon(Main.class.getResource("/res/images/load.gif")));
				if (salonGUI != null) {
					salonGUI.setVisible(false);
				}
				if (profilGUI != null) {
					profilGUI.setVisible(false);
				}
				mntmJoinSalon.setEnabled(false);
				mntmAfficherLeProfil.setEnabled(false);
				break;

			case CONNECTED:
				if (salonGUI != null) {
					salonGUI.setVisible(false);
				}
				if (profilGUI != null) {
					profilGUI.setVisible(false);
				}
				mntmJoinSalon.setEnabled(true);
				mntmAfficherLeProfil.setEnabled(true);
				lbl_state.setText("Connecté au serveur");
				lbl_state.setIcon(null);
				lbl_state.validate();
				lbl_state.repaint();
				break;
		}
	}

	public Party getPartyModel() {
		return partyModel;
	}

	public boolean isOfflineMode() {
		return offlineMode;
	}

	public void setOfflineMode(boolean offlineMode) {
		this.offlineMode = offlineMode;
	}

	public SalonGUI getSalonGUI() {
		if (salonGUI == null) {
			salonGUI = new SalonGUI();
		}
		return salonGUI;
	}

	public ProfileGUI getProfileGUI() {
		if (profilGUI == null) {
			profilGUI = new ProfileGUI();
		}
		return profilGUI;
	}

}
