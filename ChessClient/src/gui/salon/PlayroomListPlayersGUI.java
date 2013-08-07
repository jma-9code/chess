package gui.salon;

import gui.MainGUI;
import gui.SalonGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import message.ListPlayers;
import message.Talk2All;
import network.NetworkClient;
import network.NetworkEvent;
import network.NetworkEventHandler;
import network.NetworkManager;

import org.apache.log4j.Logger;

import tools.CountryRenderer;
import tools.TablePlayer;

import commun.Player;

public class PlayroomListPlayersGUI extends JPanel implements NetworkEventHandler {

	private static final Logger logger = Logger.getLogger(PlayroomListPlayersGUI.class);

	private ArrayList<Player> listPlayers = null;
	private TablePlayer tableModelPlayers;
	private JTable jtablePlayers;

	private JSplitPane splitPane;
	private JTextArea txtA_salon;

	private JPanel leftpan;
	private JPanel rightpan;
	private JTextField tf_send;

	private SalonGUI salonGui;

	public PlayroomListPlayersGUI(SalonGUI salonGui) {
		this.salonGui = salonGui;
		this.setLayout(new BorderLayout(0, 0));

		// handlers réseau
		NetworkManager.getInstance().addHandler(this, Talk2All.class);
		NetworkManager.getInstance().addHandler(this, ListPlayers.class);

		splitPane = new JSplitPane();
		this.add(splitPane);

		leftpan = new JPanel();
		leftpan.setMinimumSize(new Dimension(200, 10));
		leftpan.setLayout(new BorderLayout(0, 0));
		splitPane.setLeftComponent(leftpan);

		listPlayers = new ArrayList<Player>();
		tableModelPlayers = new TablePlayer(listPlayers);

		jtablePlayers = new JTable(tableModelPlayers);
		jtablePlayers.setDefaultRenderer(Locale.class, new CountryRenderer());
		jtablePlayers.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		jtablePlayers.setFillsViewportHeight(true);
		jtablePlayers.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					playerSelected();
				}
			}
		});
		leftpan.add(new JScrollPane(jtablePlayers));

		rightpan = new JPanel();
		rightpan.setMinimumSize(new Dimension(200, 10));
		rightpan.setLayout(new BorderLayout(0, 0));
		splitPane.setRightComponent(rightpan);

		Border line = BorderFactory.createLineBorder(Color.DARK_GRAY);
		Border empty = new EmptyBorder(3, 3, 3, 3);
		Border border = new CompoundBorder(line, empty);

		txtA_salon = new JTextArea();
		txtA_salon.setEditable(false);
		txtA_salon.setWrapStyleWord(true);
		txtA_salon.setLineWrap(true);

		JScrollPane scrollPaneTxtArea = new JScrollPane(txtA_salon);
		scrollPaneTxtArea.setBorder(line);
		scrollPaneTxtArea.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				e.getAdjustable().setValue(e.getAdjustable().getMaximum());
			}
		});

		rightpan.add(scrollPaneTxtArea, BorderLayout.CENTER);

		tf_send = new JTextField();
		tf_send.setBorder(border);
		tf_send.setColumns(10);
		tf_send.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER && !tf_send.getText().trim().isEmpty()) {
					NetworkClient.getInstance().sendObject(new Talk2All(tf_send.getText()));
					tf_send.setText("");
				}
			}
		});

		rightpan.add(tf_send, BorderLayout.SOUTH);

	}

	protected void playerSelected() {
		if (jtablePlayers.getSelectedRow() != -1) {
			try {
				int col_id;
				for (col_id = 0; col_id < jtablePlayers.getColumnCount(); col_id++) {
					if ("Nom".equalsIgnoreCase(jtablePlayers.getColumnName(col_id))) {
						break;
					}
				}
				String name = (String) jtablePlayers.getValueAt(jtablePlayers.getSelectedRow(), col_id);
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
		txtA_salon.setText("");
		tf_send.setText("");
		tableModelPlayers.refresh(new ArrayList());
	}

	@Override
	public void handleNetworkEvent(NetworkEvent obj) {
		if (obj.getSource() instanceof Talk2All) {
			Talk2All msg = (Talk2All) obj.getSource();
			txtA_salon.setText(txtA_salon.getText() + "\n" + msg.getMessage());
			txtA_salon.repaint();
		}
		else if (obj.getSource() instanceof ListPlayers) {
			ListPlayers msg = (ListPlayers) obj.getSource();
			// listPlayer.clear();
			// listPlayer.addAll(msg.getListPlayers());
			listPlayers = msg.getListPlayers();
			tableModelPlayers.refresh(msg.getListPlayers());
		}
	}
}
