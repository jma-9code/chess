package gui.salon;

import gui.SalonGUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SpringLayout;

import message.party.JoinParty;
import message.party.ListParties;
import message.party.NewParty;
import network.NetworkClient;
import network.NetworkEvent;
import network.NetworkEventHandler;
import network.NetworkManager;

import org.apache.log4j.Logger;

import tools.TableParty;

import commun.AbsPartyInfo;

public class PlayroomListPartiesGUI extends JPanel implements ActionListener, NetworkEventHandler {

	private static final Logger logger = Logger.getLogger(PlayroomListPartiesGUI.class);

	private ArrayList<AbsPartyInfo> listParties = null;
	private TableParty tableModelParties;
	private JTable jtableParties;

	private JButton btnJoin;
	private JButton btnNewParty;
	private JButton btnRafrachir;

	/**
	 * Link vers panel parent
	 */
	private SalonGUI salonGui;

	public PlayroomListPartiesGUI(SalonGUI salonGui) {
		this.salonGui = salonGui;
		initGUI();
	}

	private void initGUI() {
		// handler network
		NetworkManager.getInstance().addHandler(this, ListParties.class);

		SpringLayout sl_salonPartys = new SpringLayout();
		this.setLayout(sl_salonPartys);

		listParties = new ArrayList<AbsPartyInfo>();
		tableModelParties = new TableParty(listParties);

		jtableParties = new JTable(tableModelParties);
		jtableParties.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		jtableParties.setFillsViewportHeight(true);
		jtableParties.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					joinPartySelected();
				}
			}
		});

		JScrollPane scrollPanePartys = new JScrollPane(jtableParties);
		sl_salonPartys.putConstraint(SpringLayout.NORTH, scrollPanePartys, 0, SpringLayout.NORTH, this);
		sl_salonPartys.putConstraint(SpringLayout.WEST, scrollPanePartys, 0, SpringLayout.WEST, this);
		sl_salonPartys.putConstraint(SpringLayout.SOUTH, scrollPanePartys, -38, SpringLayout.SOUTH, this);
		sl_salonPartys.putConstraint(SpringLayout.EAST, scrollPanePartys, 0, SpringLayout.EAST, this);
		this.add(scrollPanePartys);

		btnJoin = new JButton("Rejoindre");
		sl_salonPartys.putConstraint(SpringLayout.NORTH, btnJoin, 6, SpringLayout.SOUTH, scrollPanePartys);
		btnJoin.addActionListener(this);
		this.add(btnJoin);

		btnNewParty = new JButton("Nouvelle partie");
		sl_salonPartys.putConstraint(SpringLayout.EAST, btnJoin, -6, SpringLayout.WEST, btnNewParty);
		btnNewParty.addActionListener(this);
		sl_salonPartys.putConstraint(SpringLayout.NORTH, btnNewParty, 6, SpringLayout.SOUTH, scrollPanePartys);
		sl_salonPartys.putConstraint(SpringLayout.WEST, btnNewParty, -135, SpringLayout.EAST, this);
		sl_salonPartys.putConstraint(SpringLayout.EAST, btnNewParty, -10, SpringLayout.EAST, this);
		this.add(btnNewParty);

		btnRafrachir = new JButton("Rafraîchir");
		sl_salonPartys.putConstraint(SpringLayout.NORTH, btnRafrachir, 6, SpringLayout.SOUTH, scrollPanePartys);
		sl_salonPartys.putConstraint(SpringLayout.WEST, btnRafrachir, 10, SpringLayout.WEST, scrollPanePartys);
		btnRafrachir.addActionListener(this);
		add(btnRafrachir);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// bouton rejoindre
		if (e.getSource() == btnJoin) {
			joinPartySelected();
		}

		// nouvelle partie
		else if (e.getSource() == btnNewParty) {
			NetworkClient.getInstance().sendObject(new NewParty(""));
		}

		// rafraichir liste partie
		else if (e.getSource() == btnRafrachir) {
			NetworkClient.getInstance().sendObject(new ListParties());
		}
	}

	@Override
	public void handleNetworkEvent(NetworkEvent obj) {
		if (obj.getSource() instanceof ListParties) {
			ListParties msg = (ListParties) obj.getSource();
			// listParty.clear();
			// listParty.addAll(msg.getListOfParties());
			listParties = msg.getListOfParties();
			logger.info(listParties.size() + " parties received");
			tableModelParties.refresh(msg.getListOfParties());
		}
	}

	/**
	 * Réinitialise les différents éléments (nettoyage)
	 */
	public void reset() {
		tableModelParties.refresh(new ArrayList());
	}

	public void joinPartySelected() {
		if (jtableParties.getSelectedRow() != -1) {
			try {
				int col_id;
				for (col_id = 0; col_id < jtableParties.getColumnCount(); col_id++) {
					if ("id".equalsIgnoreCase(jtableParties.getColumnName(col_id))) {
						break;
					}
				}
				int id = (Integer) jtableParties.getValueAt(jtableParties.getSelectedRow(), col_id);
				NetworkClient.getInstance().sendObject(new JoinParty(id));
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
