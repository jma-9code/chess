package gui.config;

import java.awt.BorderLayout;
import java.awt.Dialog;

import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

public class ConfigGUI extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6154448330106532942L;
	private static final Logger logger = Logger.getLogger(ConfigGUI.class);

	private JPanel configChoice;
	private JPanel configX;
	private JList listChoice;
	private GraphicsConfigPnl configGraphics = new GraphicsConfigPnl();
	private LocalConfigPnl localConfig = new LocalConfigPnl();

	String[] values = new String[] { "Graphiques", "Locale" };
	private JSplitPane splitPane;

	public ConfigGUI() {
		this(null, true);
	}

	/**
	 * Create the dialog.
	 */
	public ConfigGUI(Dialog dial, boolean modal) {
		super(dial, modal);
		setSize(600, 400);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());

		// panel general
		splitPane = new JSplitPane();
		setContentPane(splitPane);

		// panel droit des config
		configX = new JPanel();
		splitPane.setRightComponent(configX);
		configX.setLayout(null);

		// panel gauche / liste des configs dispo
		listChoice = new JList(values);
		splitPane.setLeftComponent(listChoice);
		listChoice.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		listChoice.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting() == false) {
					configX.removeAll();
					switch (listChoice.getSelectedIndex()) {
						case 0:
							configX.add(configGraphics);
							break;
						case 1:
							configX.add(localConfig);
							break;
						case 2:
							break;
						case 3:
							break;
						case 4:
							break;
						default:
							break;
					}
					configX.validate();
					configX.repaint();
				}
			}
		});

		// default config
		listChoice.setSelectedIndex(0);
	}

}
