package gui.salon;

import gui.GUIErrorsException;
import gui.MainGUI;
import gui.SalonGUI;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import main.Config;
import message.Talk2Party;
import message.party.JoinParty;
import message.party.LeftParty;
import message.party.ListParties;
import message.party.NewParty;
import message.party.StartParty;
import message.party.StateOfParty;
import model.Party;
import model.PartyNetwork;
import network.NetworkClient;
import network.NetworkEvent;
import network.NetworkEventHandler;
import network.NetworkException;
import network.NetworkManager;

import org.apache.log4j.Logger;

import tools.CountryRenderer;
import tools.TablePlayer;

import commun.AbsPartyInfo;
import commun.Player;
import commun.chess.Chess_PartyInfo;
import commun.chess.Chess_PartyOption;

/**
 * Panel pour la configuration d'une partie.
 * 
 * @author Florent
 * 
 */
public class PlayroomPartyGUI extends JPanel implements ActionListener, ChangeListener, NetworkEventHandler {

	private static final Logger logger = Logger.getLogger(PlayroomPartyGUI.class);

	/**
	 * Thread d'animation du lancement d'une partie.
	 */
	private Thread threadStartingParty = null;

	/**
	 * Cache de la liste des parties
	 */
	private ArrayList<AbsPartyInfo> listParties = null;

	/**
	 * Partie courrante
	 */
	private AbsPartyInfo curPartyInfo = null;

	private TablePlayer tablePlayerParty;
	private JTable jtable_playerParty;

	private JTextArea ta_talkParty;
	private JTextField tf_talkInputParty;

	private JPanel jp_option;
	private JPanel jp_conf;
	private JSlider slider_time;
	private static final int SLIDER_TIME_DEFAULT = 10;

	private JPanel jp_btoption;
	private JButton bt_startParty;
	private JButton bt_leftParty;
	private JButton bt_ready;

	/**
	 * Link panel parent
	 */
	private SalonGUI salonGui;

	public PlayroomPartyGUI(final SalonGUI salonGui) {
		this.salonGui = salonGui;

		// handlers network
		NetworkManager.getInstance().addHandler(this, Talk2Party.class);
		NetworkManager.getInstance().addHandler(this, ListParties.class);
		NetworkManager.getInstance().addHandler(this, NewParty.class);
		NetworkManager.getInstance().addHandler(this, JoinParty.class);
		NetworkManager.getInstance().addHandler(this, StartParty.class);
		NetworkManager.getInstance().addHandler(this, Chess_PartyOption.class);

		SpringLayout sl_this = new SpringLayout();
		this.setLayout(sl_this);

		tablePlayerParty = new TablePlayer();
		jtable_playerParty = new JTable(tablePlayerParty);
		jtable_playerParty.setDefaultRenderer(Locale.class, new CountryRenderer());
		jtable_playerParty.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		jtable_playerParty.setFillsViewportHeight(true);
		jtable_playerParty.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					playerSelected();
				}
			}
		});
		JScrollPane scrollPanePlayerParty = new JScrollPane(jtable_playerParty);
		sl_this.putConstraint(SpringLayout.NORTH, scrollPanePlayerParty, 0, SpringLayout.NORTH, this);
		sl_this.putConstraint(SpringLayout.WEST, scrollPanePlayerParty, 0, SpringLayout.WEST, this);
		sl_this.putConstraint(SpringLayout.EAST, scrollPanePlayerParty, 200, SpringLayout.WEST, this);
		this.add(scrollPanePlayerParty);

		jp_option = new JPanel();
		jp_option.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		sl_this.putConstraint(SpringLayout.NORTH, jp_option, 0, SpringLayout.NORTH, this);
		sl_this.putConstraint(SpringLayout.WEST, jp_option, 0, SpringLayout.EAST, scrollPanePlayerParty);
		sl_this.putConstraint(SpringLayout.EAST, jp_option, 0, SpringLayout.EAST, this);
		this.add(jp_option);

		Border line = BorderFactory.createLineBorder(Color.DARK_GRAY);
		Border empty = new EmptyBorder(3, 3, 3, 3);
		CompoundBorder border = new CompoundBorder(line, empty);

		ta_talkParty = new JTextArea();
		ta_talkParty.setEditable(false);
		JScrollPane scrollPaneTxtAreaParty = new JScrollPane(ta_talkParty);
		scrollPaneTxtAreaParty.setBorder(line);
		scrollPaneTxtAreaParty.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				e.getAdjustable().setValue(e.getAdjustable().getMaximum());
			}
		});

		sl_this.putConstraint(SpringLayout.WEST, scrollPaneTxtAreaParty, 0, SpringLayout.WEST, this);
		sl_this.putConstraint(SpringLayout.SOUTH, scrollPanePlayerParty, 0, SpringLayout.NORTH, scrollPaneTxtAreaParty);
		sl_this.putConstraint(SpringLayout.SOUTH, jp_option, 0, SpringLayout.NORTH, scrollPaneTxtAreaParty);
		SpringLayout sl_jp_option = new SpringLayout();
		jp_option.setLayout(sl_jp_option);

		jp_btoption = new JPanel();
		sl_jp_option.putConstraint(SpringLayout.WEST, jp_btoption, 0, SpringLayout.WEST, jp_option);
		sl_jp_option.putConstraint(SpringLayout.SOUTH, jp_btoption, 0, SpringLayout.SOUTH, jp_option);
		sl_jp_option.putConstraint(SpringLayout.EAST, jp_btoption, 0, SpringLayout.EAST, jp_option);
		jp_option.add(jp_btoption);

		bt_startParty = new JButton("Lancer partie");
		jp_btoption.add(bt_startParty);
		bt_startParty.addActionListener(this);
		sl_jp_option.putConstraint(SpringLayout.NORTH, bt_startParty, 7, SpringLayout.NORTH, jp_option);
		sl_jp_option.putConstraint(SpringLayout.WEST, bt_startParty, 55, SpringLayout.WEST, jp_option);

		bt_ready = new JButton("Pret");
		jp_btoption.add(bt_ready);
		sl_jp_option.putConstraint(SpringLayout.NORTH, bt_ready, 7, SpringLayout.NORTH, jp_option);
		sl_jp_option.putConstraint(SpringLayout.WEST, bt_ready, 160, SpringLayout.WEST, jp_option);

		bt_leftParty = new JButton("Quitter partie");
		bt_leftParty.addActionListener(this);

		jp_btoption.add(bt_leftParty);
		sl_jp_option.putConstraint(SpringLayout.NORTH, bt_leftParty, 7, SpringLayout.NORTH, jp_option);
		sl_jp_option.putConstraint(SpringLayout.WEST, bt_leftParty, 215, SpringLayout.WEST, jp_option);

		jp_conf = new JPanel();
		sl_jp_option.putConstraint(SpringLayout.NORTH, jp_conf, 0, SpringLayout.NORTH, jp_option);
		sl_jp_option.putConstraint(SpringLayout.WEST, jp_conf, 0, SpringLayout.WEST, jp_option);
		sl_jp_option.putConstraint(SpringLayout.SOUTH, jp_conf, 0, SpringLayout.NORTH, jp_btoption);
		sl_jp_option.putConstraint(SpringLayout.EAST, jp_conf, 0, SpringLayout.EAST, jp_option);
		jp_option.add(jp_conf);

		slider_time = new JSlider(1, 20, SLIDER_TIME_DEFAULT);
		slider_time.setToolTipText("Durée de partie");
		slider_time.setMinorTickSpacing(1);
		slider_time.addChangeListener(this);
		slider_time.setPaintLabels(true);
		slider_time.setPaintTicks(true);
		slider_time.setMajorTickSpacing(3);
		slider_time.setBorder(new TitledBorder(new LineBorder(new Color(41, 41, 41)), slider_time.getToolTipText() + " - 10", TitledBorder.LEADING,
				TitledBorder.TOP, null, new Color(0, 0, 0)));

		jp_conf.add(slider_time);
		sl_jp_option.putConstraint(SpringLayout.NORTH, slider_time, 0, SpringLayout.NORTH, jp_option);
		sl_jp_option.putConstraint(SpringLayout.EAST, slider_time, 0, SpringLayout.EAST, jp_option);
		sl_this.putConstraint(SpringLayout.EAST, scrollPaneTxtAreaParty, 0, SpringLayout.EAST, this);
		this.add(scrollPaneTxtAreaParty);

		tf_talkInputParty = new JTextField();
		tf_talkInputParty.setBorder(border);
		tf_talkInputParty.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER && !tf_talkInputParty.getText().trim().isEmpty()) {
					NetworkClient.getInstance().sendObject(new Talk2Party(tf_talkInputParty.getText()));
					tf_talkInputParty.setText("");
				}
			}
		});
		sl_this.putConstraint(SpringLayout.WEST, tf_talkInputParty, 0, SpringLayout.WEST, this);
		sl_this.putConstraint(SpringLayout.NORTH, scrollPaneTxtAreaParty, -200, SpringLayout.SOUTH, tf_talkInputParty);
		sl_this.putConstraint(SpringLayout.SOUTH, scrollPaneTxtAreaParty, 0, SpringLayout.NORTH, tf_talkInputParty);
		sl_this.putConstraint(SpringLayout.SOUTH, tf_talkInputParty, 0, SpringLayout.SOUTH, this);
		sl_this.putConstraint(SpringLayout.EAST, tf_talkInputParty, 0, SpringLayout.EAST, this);
		this.add(tf_talkInputParty);
		tf_talkInputParty.setColumns(10);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// bouton lancer partie
		if (e.getSource() == bt_startParty) {
			if (threadStartingParty == null) {
				threadStartingParty = new Thread("PlayroomPartyGUI_StartingParty") {
					@Override
					public void run() {
						String oldText = bt_startParty.getText();
						bt_startParty.setText("Annuler lancement");

						if (Config.DEBUG) {
							NetworkClient.getInstance().sendObject(new Talk2Party("Lancement direct (Mode debug)"));
						}
						else {

							// décompte
							for (int i = 5; i > 0; i--) {
								NetworkClient.getInstance().sendObject(
										new Talk2Party("Lancement de la partie dans " + i + " seconde" + (i > 1 ? "s" : "")));
								try {
									Thread.sleep(1000);
								}
								catch (InterruptedException e) {
									// annulation
									bt_startParty.setText(oldText);
									NetworkClient.getInstance().sendObject(new Talk2Party("Lancement interrompu !"));

									return;
								}
							}
						}
						NetworkClient.getInstance().sendObject(new StartParty());
						bt_startParty.setText(oldText);
					}
				};

				threadStartingParty.start();
			}
			else {
				// interruption du lancement.
				threadStartingParty.interrupt();
			}
		}

		// bouton quitter partie
		else if (e.getSource() == bt_leftParty) {
			reset();
			MainGUI.getInstance().destroyCurrentParty();
			NetworkClient.getInstance().sendObject(new LeftParty());
			salonGui.getTabbedPane().setEnabledAt(2, false);
			salonGui.getTabbedPane().setEnabledAt(1, true);
			salonGui.getTabbedPane().setSelectedIndex(1);
		}
	}

	protected void playerSelected() {
		if (jtable_playerParty.getSelectedRow() != -1) {
			try {
				int col_id;
				for (col_id = 0; col_id < jtable_playerParty.getColumnCount(); col_id++) {
					if ("Nom".equalsIgnoreCase(jtable_playerParty.getColumnName(col_id))) {
						break;
					}
				}
				String name = (String) jtable_playerParty.getValueAt(jtable_playerParty.getSelectedRow(), col_id);
				MainGUI.getInstance().getProfileGUI().setVisible(true, name);
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Réinitialise les différents éléments (nettoyage)
	 */
	public void reset() {
		tablePlayerParty.refresh(new ArrayList());
		ta_talkParty.setText("");
		slider_time.setValue(SLIDER_TIME_DEFAULT);

		Party p = MainGUI.getInstance().getPartyModel();
		if (p != null && p instanceof PartyNetwork) {
			MainGUI.getInstance().destroyCurrentParty();
		}

		curPartyInfo = null;
		threadStartingParty = null;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		slider_time.setBorder(new TitledBorder(new LineBorder(new Color(41, 41, 41)), slider_time.getToolTipText() + " - " + slider_time.getValue(),
				TitledBorder.LEADING, TitledBorder.TOP, null, null));

		if (!slider_time.getValueIsAdjusting() && slider_time.isEnabled()) {
			Chess_PartyOption opt = new Chess_PartyOption(slider_time.getValue());
			NetworkClient.getInstance().sendObject(opt);
		}
	}

	@Override
	public void handleNetworkEvent(NetworkEvent obj) {
		if (obj.getSource() instanceof Talk2Party) {
			Talk2Party msg = (Talk2Party) obj.getSource();

			StringBuffer base = new StringBuffer();
			if (!ta_talkParty.getText().trim().isEmpty()) {
				base.append(ta_talkParty.getText());
				base.append("\n");
			}
			base.append(msg.getMessage());

			ta_talkParty.setText(base.toString());
		}
		else if (obj.getSource() instanceof ListParties) {
			ListParties msg = (ListParties) obj.getSource();
			listParties = msg.getListOfParties();

			if (curPartyInfo != null) {
				curPartyInfo = getPartyInfoById(curPartyInfo.getId());
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						refreshParty();
					}
				});
			}
		}
		else if (obj.getSource() instanceof NewParty) {
			NewParty msg = (NewParty) obj.getSource();
			curPartyInfo = getPartyInfoById(msg.getIdParty());

			if (curPartyInfo == null) {
				logger.error("No party matched for id " + msg.getIdParty());
			}

			// màj panel party
			configPartyForOwner();

			// changement onglet
			salonGui.getTabbedPane().setEnabledAt(1, false);
			salonGui.getTabbedPane().setEnabledAt(2, true);
			salonGui.getTabbedPane().setSelectedIndex(2);

			if (curPartyInfo != null) {
				refreshParty();
			}
		}
		else if (obj.getSource() instanceof JoinParty) {
			JoinParty msg = (JoinParty) obj.getSource();

			if (!msg.isPartyJoined()) {
				new GUIErrorsException("Erreur salon", "Impossible de rejoindre cette partie.").showErrors(this);
				return;
			}

			curPartyInfo = getPartyInfoById(msg.getId());

			if (curPartyInfo == null) {
				logger.error("No party matched for id " + msg.getId());
			}

			// màj panel party
			configPartyForPlayer();

			// changement onglet
			salonGui.getTabbedPane().setEnabledAt(1, false);
			salonGui.getTabbedPane().setEnabledAt(2, true);
			salonGui.getTabbedPane().setSelectedIndex(2);

			if (curPartyInfo != null) {
				refreshParty();
			}
		}
		else if (obj.getSource() instanceof StartParty) {
			curPartyInfo = ((StartParty) obj.getSource()).getPartyInfo();

			for (Player player : curPartyInfo.getPlayers()) {
				try {
					Player p = NetworkClient.getInstance().getPlayer();
					if (player.equals(p)) {
						p.setColor(player.getColor());
						break;
					}
				}
				catch (NetworkException e) {
					e.printStackTrace();
				}
			}
			PartyNetwork p = new PartyNetwork((Chess_PartyInfo) curPartyInfo);
			MainGUI.getInstance().startParty(p);
			salonGui.setVisible(false);
		}
		else if (obj.getSource() instanceof Chess_PartyOption && curPartyInfo != null) {
			Chess_PartyOption msg = (Chess_PartyOption) obj.getSource();
			if (curPartyInfo != null) {
				curPartyInfo.setOption(msg);
				refreshParty();
			}
		}

	}

	/**
	 * Rafraîchie le modèle associé à la partie
	 */
	public void refreshParty() {
		if (curPartyInfo == null) {
			return;
		}

		// options
		Chess_PartyOption opt = (Chess_PartyOption) curPartyInfo.getOption();
		if (opt != null) {
			slider_time.setValue(opt.getMaxTimePerPlayer());
		}

		// mise à jour interface par rapport au type owner ou player
		Chess_PartyInfo curChessPartyinfo = (Chess_PartyInfo) curPartyInfo;
		if (Config.NAME.equalsIgnoreCase(curChessPartyinfo.getPlayers().get(0).getName())) {
			configPartyForOwner();
		}
		else {
			configPartyForPlayer();
		}

		// tableau joueur
		tablePlayerParty.refresh(curPartyInfo.getPlayers());
	}

	public void configPartyForOwner() {
		bt_ready.setVisible(false);

		// retour jeu
		Party p = MainGUI.getInstance().getPartyModel();
		if (p != null && p.getStatut().equals(StateOfParty.FINISHED)) {
			configPartyForPlayer();
		}
		else {
			bt_startParty.setVisible(true);
			bt_startParty.setEnabled(curPartyInfo.getPlayers().size() > 1);
			slider_time.setEnabled(true);
		}
	}

	public void configPartyForPlayer() {
		bt_ready.setVisible(false);
		bt_startParty.setVisible(false);
		slider_time.setEnabled(false);
	}

	/**
	 * Retour après une fin de partie
	 */
	public void returnFromEndParty() {
		refreshParty();
	}

	/**
	 * Retourne une partie depuis un id
	 * 
	 * @param id
	 * @return
	 */
	public AbsPartyInfo getPartyInfoById(int id) {
		logger.debug("Nombre de partie : " + listParties.size());

		// synchronized (listParty) {
		for (AbsPartyInfo partyInfo : listParties) {
			if (partyInfo.getId() == id) {
				logger.debug("Party id " + partyInfo.getId() + " found");
				return partyInfo;
			}
		}

		return null;
		// }
	}

}
