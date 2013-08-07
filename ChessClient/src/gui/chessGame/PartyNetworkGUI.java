package gui.chessGame;

import gui.MainGUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import message.Talk2Party;
import message.party.EndParty;
import message.party.NullParty;
import message.party.StateOfPlayer;
import model.PartyNetwork;
import network.NetworkClient;
import network.NetworkEvent;
import network.NetworkEventHandler;
import network.NetworkManager;

import commun.EColor;
import commun.Player;

public class PartyNetworkGUI extends PartyGUI implements NetworkEventHandler {

	private static final long serialVersionUID = 1L;
	private JPanel pnl_discuss;
	private JScrollPane scrollPane_txt;
	private JTextArea ta_talkParty;
	private JTextField tf_talkInputParty;

	private PartyNetworkGUI himself = null;

	public PartyNetworkGUI(PartyNetwork party) {
		super(party);
		this.himself = this;
		initGUINetwork();
		initEventHandler();
	}

	public void initEventHandler() {
		NetworkManager.getInstance().addHandler(this, Talk2Party.class);
		NetworkManager.getInstance().addHandler(this, EndParty.class);
		NetworkManager.getInstance().addHandler(this, NullParty.class);
	}

	public void removeHandlers() {
		NetworkManager.getInstance().removeHandler(this);
	}

	protected void initGUINetwork() {
		SpringLayout springLayout_1 = (SpringLayout) getLayout();
		Dimension dimPnl = new Dimension(525, 527);
		setPreferredSize(dimPnl);
		pnl_discuss = new JPanel();
		springLayout_1.putConstraint(SpringLayout.NORTH, pnl_discuss, 0, SpringLayout.SOUTH, boardGUI);
		springLayout_1.putConstraint(SpringLayout.WEST, pnl_discuss, 0, SpringLayout.WEST, this);
		springLayout_1.putConstraint(SpringLayout.SOUTH, pnl_discuss, 0, SpringLayout.SOUTH, this);
		springLayout_1.putConstraint(SpringLayout.EAST, pnl_discuss, 0, SpringLayout.EAST, this);
		add(pnl_discuss);
		SpringLayout sl_pnl_discuss = new SpringLayout();
		pnl_discuss.setLayout(sl_pnl_discuss);

		Border line = BorderFactory.createLineBorder(Color.DARK_GRAY);
		Border empty = new EmptyBorder(3, 3, 3, 3);
		CompoundBorder border = new CompoundBorder(line, empty);

		scrollPane_txt = new JScrollPane((Component) null);
		scrollPane_txt.setBorder(line);
		sl_pnl_discuss.putConstraint(SpringLayout.NORTH, scrollPane_txt, 4, SpringLayout.NORTH, pnl_discuss);
		sl_pnl_discuss.putConstraint(SpringLayout.WEST, scrollPane_txt, 0, SpringLayout.WEST, pnl_discuss);
		sl_pnl_discuss.putConstraint(SpringLayout.EAST, scrollPane_txt, 0, SpringLayout.EAST, pnl_discuss);
		pnl_discuss.add(scrollPane_txt);

		ta_talkParty = new JTextArea();
		ta_talkParty.setEditable(false);
		scrollPane_txt.setViewportView(ta_talkParty);
		scrollPane_txt.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				e.getAdjustable().setValue(e.getAdjustable().getMaximum());
			}
		});

		tf_talkInputParty = new JTextField();
		tf_talkInputParty.setBorder(border);

		sl_pnl_discuss.putConstraint(SpringLayout.SOUTH, scrollPane_txt, -2, SpringLayout.NORTH, tf_talkInputParty);
		sl_pnl_discuss.putConstraint(SpringLayout.WEST, tf_talkInputParty, 0, SpringLayout.WEST, scrollPane_txt);
		sl_pnl_discuss.putConstraint(SpringLayout.SOUTH, tf_talkInputParty, 0, SpringLayout.SOUTH, pnl_discuss);
		sl_pnl_discuss.putConstraint(SpringLayout.EAST, tf_talkInputParty, 0, SpringLayout.EAST, pnl_discuss);
		pnl_discuss.add(tf_talkInputParty);

		tf_talkInputParty.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER && !tf_talkInputParty.getText().trim().isEmpty()) {
					NetworkClient.getInstance().sendObject(new Talk2Party(tf_talkInputParty.getText()));
					tf_talkInputParty.setText("");
				}
			}
		});

		PartyNetwork networkParty = (PartyNetwork) party;
		EColor colorWhoPlay = networkParty.getPlateau().getWhoColorPlay();

		if (EColor.WHITE.equals(colorWhoPlay)) {
			ta_talkParty.setText("Les blancs commençent à jouer.");
		}
		else {
			ta_talkParty.setText("Les noirs commençent à jouer.");
		}

		if (colorWhoPlay.equals(networkParty.getCurrentPlayer().getColor())) {
			ta_talkParty.setText(ta_talkParty.getText() + "\nVous jouez en premier.");
		}
	}

	@Override
	public void handleNetworkEvent(NetworkEvent obj) {
		if (obj.getSource() instanceof Talk2Party) {
			Talk2Party talk = (Talk2Party) obj.getSource();

			StringBuffer base = new StringBuffer();
			if (!ta_talkParty.getText().trim().isEmpty()) {
				base.append(ta_talkParty.getText());
				base.append("\n");
			}
			base.append(talk.getMessage());

			ta_talkParty.setText(base.toString());
		}
		else if (obj.getSource() instanceof EndParty) {
			showEndOfParty(((PartyNetwork) party).getCurrentPlayer());
			removeHandlers();
		}
		else if (obj.getSource() instanceof NullParty) {
			final NullParty nullParty = (NullParty) obj.getSource();
			if ( nullParty.isQuestion() ) {
				int val = JOptionPane.showConfirmDialog(this, "Demande de match null, acceptez vous ?",
						"Demande de match nul", JOptionPane.YES_NO_OPTION);
				NullParty reply = new NullParty();
				if ( val == JOptionPane.OK_OPTION ) {
					reply.setResponse(true);
					reply.setAccepted(true);
					party.getPlateau().requestNullAccepted();
				} else {
					reply.setResponse(true);
					reply.setAccepted(false);
				}
				NetworkClient.getInstance().sendObject(reply);
			} else {
				if ( nullParty.isAccepted() ) {
					party.getPlateau().requestNullAccepted();
					JOptionPane.showMessageDialog(this, "Partie nulle acceptée.");
				} else {
					JOptionPane.showMessageDialog(this, "Partie nulle refusée.");
				}
			}
		}
	}

	@Override
	protected void requestNull() {
		int val = JOptionPane.showConfirmDialog(this, "Voulez-vous demander le match nul ?", "Demande de match nul", JOptionPane.YES_NO_OPTION);

		if (val == JOptionPane.OK_OPTION) {
			NullParty nullParty = new NullParty();
			nullParty.setQuestion(true);
			NetworkClient.getInstance().sendObject(nullParty);
		}
	}

	private void showEndOfParty(final Player partyPlayer) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				String msg = null;
				StateOfPlayer state = plateau.getStateOfPlayer(partyPlayer.getColor());

				if ( plateau.isGameNull() ) {
					msg = "Aucun gagnant, partie nulle.";
				} else if ( StateOfPlayer.LOSE.equals(state) ) {
					msg = "Echec et mat, vous avez perdu.";
				}
				else if (StateOfPlayer.WIN.equals(state)) {
					msg = "Bravo, vous avez gagné ";
				}
				else {
					msg = "Le joueur adverse s'est déconnecté.";
				}

				int ret = JOptionPane.showOptionDialog(himself, msg, "Fin de partie", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
						null, new String[] { "Retour au salon" }, null);

				if (ret == 0) {
					MainGUI.getInstance().getSalonGUI().getTabParty().returnFromEndParty();
					MainGUI.getInstance().getSalonGUI().setVisible(true);
				}
			}
		});

	}
}
