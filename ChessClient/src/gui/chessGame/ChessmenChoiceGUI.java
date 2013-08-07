package gui.chessGame;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import model.chessmens.Bishop;
import model.chessmens.Chessmen;
import model.chessmens.Knight;
import model.chessmens.Queen;
import model.chessmens.Rook;

import commun.EColor;

public class ChessmenChoiceGUI extends JDialog {
	private JButton bt_Queen = new JButton("Reine");
	private JButton bt_Bishop = new JButton("Fou");
	private JButton bt_Knight = new JButton("Cavalier");
	private JButton bt_Rook = new JButton("Tour");
	private Chessmen chessmen;
	private final JPanel contentPane = new JPanel();

	/**
	 * Create the dialog.
	 */
	public ChessmenChoiceGUI ( Frame frame, final EColor color ) {
		super(frame, "Promotion d'une pièce", true);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		setSize(320, 70);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout(0, 0));

		getContentPane().add(contentPane);
		contentPane.add(bt_Queen);
		contentPane.add(bt_Bishop);
		contentPane.add(bt_Knight);
		contentPane.add(bt_Rook);

		bt_Rook.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setChessmen(new Rook(color));
				destroy();
			}
		});

		bt_Knight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setChessmen(new Knight(color));
				destroy();
			}
		});

		bt_Bishop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setChessmen(new Bishop(color));
				destroy();
			}
		});
		bt_Queen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setChessmen(new Queen(color));
				destroy();
			}
		});
		setVisible(true);
	}

	public void destroy() {
		setVisible(false);
		setEnabled(false);
	}

	public Chessmen getChessmen() {
		return chessmen;
	}

	public void setChessmen(Chessmen chessmen) {
		this.chessmen = chessmen;
	}
}
