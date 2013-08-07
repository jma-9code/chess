package gui.config;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import main.Config;
import model.chessmens.Bishop;
import model.chessmens.King;
import model.chessmens.Knight;
import model.chessmens.Pawn;
import model.chessmens.Queen;
import model.chessmens.Rook;

import org.apache.log4j.Logger;

import tools.Sprites;

import commun.EColor;

public class GraphicsConfigPnl extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4402949592828399708L;

	private static final Logger logger = Logger.getLogger(GraphicsConfigPnl.class);
	private JPanel pnl_colors;
	private JList jlist_color;
	private JLabel lblColor;
	private JButton bt_Changer;
	private JPanel pnl_style;
	private JList jlist_style;
	private JPanel pnl_chessmens;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;

	/**
	 * Create the dialog.
	 */
	public GraphicsConfigPnl() {
		setSize(new Dimension(400, 300));
		this.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Configuration des graphismes", TitledBorder.LEADING,
				TitledBorder.TOP, null, new Color(0, 70, 213)));
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);

		pnl_style = new JPanel();
		springLayout.putConstraint(SpringLayout.NORTH, pnl_style, 0, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, pnl_style, 0, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, pnl_style, -150, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, pnl_style, 0, SpringLayout.EAST, this);
		pnl_style.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Pi\u00E8ces", TitledBorder.LEADING, TitledBorder.TOP, null,
				new Color(0, 0, 255)));
		add(pnl_style);
		pnl_chessmens = new JPanel();

		pnl_colors = new JPanel();
		springLayout.putConstraint(SpringLayout.NORTH, pnl_colors, 11, SpringLayout.SOUTH, pnl_style);
		springLayout.putConstraint(SpringLayout.WEST, pnl_colors, 0, SpringLayout.WEST, pnl_style);
		SpringLayout sl_pnl_style = new SpringLayout();
		sl_pnl_style.putConstraint(SpringLayout.NORTH, pnl_chessmens, 0, SpringLayout.NORTH, pnl_style);
		sl_pnl_style.putConstraint(SpringLayout.EAST, pnl_chessmens, -6, SpringLayout.EAST, pnl_style);
		pnl_style.setLayout(sl_pnl_style);
		pnl_chessmens.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", TitledBorder.LEADING, TitledBorder.TOP, null,
				new Color(0, 0, 0)));
		pnl_style.add(pnl_chessmens);
		pnl_chessmens.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		scrollPane = new JScrollPane();
		sl_pnl_style.putConstraint(SpringLayout.WEST, pnl_chessmens, 6, SpringLayout.EAST, scrollPane);
		sl_pnl_style.putConstraint(SpringLayout.SOUTH, pnl_chessmens, 0, SpringLayout.SOUTH, scrollPane);
		sl_pnl_style.putConstraint(SpringLayout.NORTH, scrollPane, 0, SpringLayout.NORTH, pnl_style);
		sl_pnl_style.putConstraint(SpringLayout.WEST, scrollPane, 0, SpringLayout.WEST, pnl_style);
		sl_pnl_style.putConstraint(SpringLayout.SOUTH, scrollPane, 0, SpringLayout.SOUTH, pnl_style);
		sl_pnl_style.putConstraint(SpringLayout.EAST, scrollPane, 100, SpringLayout.WEST, pnl_style);
		pnl_style.add(scrollPane);

		jlist_style = new JList();
		scrollPane.setViewportView(jlist_style);
		sl_pnl_style.putConstraint(SpringLayout.WEST, jlist_style, 48, SpringLayout.EAST, bt_Changer);
		sl_pnl_style.putConstraint(SpringLayout.EAST, jlist_style, 204, SpringLayout.WEST, pnl_style);
		jlist_style.setSelectedIndex(Config.CHESSMEN_STYLE / 2);
		jlist_style.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				Config.CHESSMEN_STYLE = jlist_style.getSelectedIndex() * 2;
				refreshChessmens();
			}
		});
		jlist_style.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jlist_style.setModel(new AbstractListModel() {
			String[] values = new String[] { "aole", "chessmaster", "magnet", "medival", "marroq", "emeline" };

			@Override
			public int getSize() {
				return values.length;
			}

			@Override
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		jlist_style.setSelectedIndex(0);
		jlist_style.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		springLayout.putConstraint(SpringLayout.SOUTH, pnl_colors, 0, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, pnl_colors, 0, SpringLayout.EAST, this);
		add(pnl_colors);
		pnl_colors.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Couleurs", TitledBorder.LEADING, TitledBorder.TOP, null,
				Color.BLUE));
		SpringLayout sl_pnl_colors = new SpringLayout();
		pnl_colors.setLayout(sl_pnl_colors);
		lblColor = new JLabel("");
		sl_pnl_colors.putConstraint(SpringLayout.NORTH, lblColor, 0, SpringLayout.NORTH, pnl_colors);
		sl_pnl_colors.putConstraint(SpringLayout.SOUTH, lblColor, -96, SpringLayout.SOUTH, pnl_colors);
		sl_pnl_colors.putConstraint(SpringLayout.EAST, lblColor, -204, SpringLayout.EAST, pnl_colors);

		lblColor.setFont(new Font("Tahoma", Font.ITALIC, 8));
		lblColor.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		lblColor.setOpaque(true);
		lblColor.setBackground(Color.WHITE);
		pnl_colors.add(lblColor);

		bt_Changer = new JButton("Changer");
		sl_pnl_colors.putConstraint(SpringLayout.NORTH, bt_Changer, 5, SpringLayout.SOUTH, lblColor);
		sl_pnl_colors.putConstraint(SpringLayout.WEST, bt_Changer, 0, SpringLayout.WEST, lblColor);
		bt_Changer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Color color = JColorChooser.showDialog(null, "Choisir la couleur", Color.WHITE);
				switch (jlist_color.getSelectedIndex()) {
					case 0:
						Config.BOARD_SQUARE_SELECT = color;
						break;
					case 1:
						Config.BOARD_POSSIBILITY = color;
						break;
					case 2:
						Config.BOARD_CHECK = color;
						break;
					case 3:
						Config.BOARD_CHECKMATE = color;
						break;
					case 4:
						Config.BOARD_SQUARE_BLACK = color;
						break;
					case 5:
						Config.BOARD_SQUARE_WHITE = color;
						break;
					default:
						break;
				}
				refreshLblColor();
			}
		});
		pnl_colors.add(bt_Changer);

		scrollPane_1 = new JScrollPane();
		sl_pnl_colors.putConstraint(SpringLayout.WEST, lblColor, 8, SpringLayout.EAST, scrollPane_1);
		sl_pnl_colors.putConstraint(SpringLayout.NORTH, scrollPane_1, 0, SpringLayout.NORTH, pnl_colors);
		sl_pnl_colors.putConstraint(SpringLayout.WEST, scrollPane_1, 0, SpringLayout.WEST, pnl_colors);
		sl_pnl_colors.putConstraint(SpringLayout.SOUTH, scrollPane_1, 0, SpringLayout.SOUTH, pnl_colors);
		sl_pnl_colors.putConstraint(SpringLayout.EAST, scrollPane_1, 100, SpringLayout.WEST, pnl_colors);
		pnl_colors.add(scrollPane_1);
		sl_pnl_style.putConstraint(SpringLayout.NORTH, jlist_style, -71, SpringLayout.NORTH, jlist_color);

		jlist_color = new JList();
		scrollPane_1.setViewportView(jlist_color);
		sl_pnl_colors.putConstraint(SpringLayout.NORTH, jlist_color, 0, SpringLayout.NORTH, lblColor);
		jlist_color.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		jlist_color.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				refreshLblColor();
			}
		});
		jlist_color.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jlist_color.setModel(new AbstractListModel() {
			/**
				 * 
				 */
			private static final long serialVersionUID = 1134249334138349130L;
			String[] values = new String[] { "Case séléctionnée", "Coup possibles", "Echec", "Echec et mat", "Case noire", "Case blanche" };

			@Override
			public int getSize() {
				return values.length;
			}

			@Override
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		jlist_color.setSelectedIndex(0);
		sl_pnl_colors.putConstraint(SpringLayout.WEST, jlist_color, 14, SpringLayout.EAST, bt_Changer);
	}

	public void refreshLblColor() {
		switch (jlist_color.getSelectedIndex()) {
			case 0:
				lblColor.setBackground(Config.BOARD_SQUARE_SELECT);
				break;
			case 1:
				lblColor.setBackground(Config.BOARD_POSSIBILITY);
				break;
			case 2:
				lblColor.setBackground(Config.BOARD_CHECK);
				break;
			case 3:
				lblColor.setBackground(Config.BOARD_CHECKMATE);
				break;
			case 4:
				lblColor.setBackground(Config.BOARD_SQUARE_BLACK);
				break;
			case 5:
				lblColor.setBackground(Config.BOARD_SQUARE_WHITE);
				break;
			default:
				break;
		}
	}

	public void refreshChessmens() {
		pnl_chessmens.removeAll();
		final JLabel img_pawn = new JLabel("Pion", new ImageIcon(Sprites.getInstance().getChessmen(Pawn.class, EColor.BLACK, true)), JLabel.CENTER);
		final JLabel img_rook = new JLabel("Tour", new ImageIcon(Sprites.getInstance().getChessmen(Rook.class, EColor.BLACK, true)), JLabel.CENTER);
		final JLabel img_knight = new JLabel("Cavalier", new ImageIcon(Sprites.getInstance().getChessmen(Knight.class, EColor.BLACK, true)),
				JLabel.CENTER);
		final JLabel img_bishop = new JLabel("Fou", new ImageIcon(Sprites.getInstance().getChessmen(Bishop.class, EColor.BLACK, true)), JLabel.CENTER);
		final JLabel img_queen = new JLabel("Reine", new ImageIcon(Sprites.getInstance().getChessmen(Queen.class, EColor.BLACK, true)), JLabel.CENTER);
		final JLabel img_king = new JLabel("Roi", new ImageIcon(Sprites.getInstance().getChessmen(King.class, EColor.BLACK, true)), JLabel.CENTER);
		pnl_chessmens.add(img_pawn);
		pnl_chessmens.add(img_rook);
		pnl_chessmens.add(img_knight);
		pnl_chessmens.add(img_bishop);
		pnl_chessmens.add(img_queen);
		pnl_chessmens.add(img_king);
		pnl_chessmens.validate();
		pnl_chessmens.repaint();
	}
}
