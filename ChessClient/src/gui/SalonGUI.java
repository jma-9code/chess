package gui;

import gui.salon.PlayroomListPartiesGUI;
import gui.salon.PlayroomListPlayersGUI;
import gui.salon.PlayroomPartyGUI;

import java.awt.BorderLayout;
import java.awt.Dialog;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import network.NetworkClient.ENetworkClient;
import network.NetworkManager;
import network.NetworkStateListener;

import org.apache.log4j.Logger;

public class SalonGUI extends JDialog implements NetworkStateListener {

	private static final long serialVersionUID = -4853133381971109472L;
	private static final Logger logger = Logger.getLogger(SalonGUI.class);

	private JTabbedPane tabbedPane;
	private PlayroomListPlayersGUI tabListPlayers;
	private PlayroomListPartiesGUI tabListParties;
	private PlayroomPartyGUI tabParty;
	private boolean notAvailableReceive = false;

	public SalonGUI() {
		this(null, true);
	}

	/**
	 * Create the frame.
	 */
	public SalonGUI(Dialog dial, boolean modal) {
		super(dial, modal);

		NetworkManager.getInstance().addNetworkStateListener(this);
		setSize(600, 400);
		setResizable(false);
		setLocationRelativeTo(null);
		setTitle("Salon");

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		// tabbedPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.add(tabbedPane);
		setContentPane(contentPane);

		tabListPlayers = new PlayroomListPlayersGUI(this);
		tabbedPane.addTab("Salon des joueurs", null, tabListPlayers, null);

		tabListParties = new PlayroomListPartiesGUI(this);
		tabbedPane.addTab("Salon des parties", null, tabListParties, null);

		tabParty = new PlayroomPartyGUI(this);
		tabbedPane.addTab("Partie", null, tabParty, null);
		tabbedPane.setEnabledAt(2, false);
	}

	@Override
	public void setVisible(boolean b) {
		if (b) {
			NetworkManager.getInstance().flushCache();
		}

		super.setVisible(b);
	}

	public void removeHandlers() {
		NetworkManager.getInstance().removeHandler(tabListPlayers);
		NetworkManager.getInstance().removeHandler(tabListParties);
		NetworkManager.getInstance().removeHandler(tabParty);
		NetworkManager.getInstance().removeNetworkStateListener(this);
	}

	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	public PlayroomListPlayersGUI getTabListPlayers() {
		return tabListPlayers;
	}

	public PlayroomListPartiesGUI getTabListParties() {
		return tabListParties;
	}

	public PlayroomPartyGUI getTabParty() {
		return tabParty;
	}

	@Override
	public void networkStateChanged(Object obj) {
		if (!notAvailableReceive && obj.equals(ENetworkClient.NOT_AVAILABLE)) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(MainGUI.getInstance(), "Le serveur n'est plus joignable.", "Erreur du serveur",
							JOptionPane.ERROR_MESSAGE);
				}
			});

			tabParty.reset();
			tabListPlayers.reset();
			tabListParties.reset();
			reset();

			// pour éviter le repassage sur ce code à chaque tentative
			notAvailableReceive = true;
		}
		else if (obj.equals(ENetworkClient.CONNECTED)) {
			notAvailableReceive = false;
		}
	}

	/**
	 * Réinitialisation du salon
	 */
	private void reset() {
		tabbedPane.setEnabledAt(2, false);
		tabbedPane.setEnabledAt(1, true);
		tabbedPane.setSelectedIndex(1);
	}

}
