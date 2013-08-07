package gui.config;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import main.Config;

import org.apache.log4j.Logger;

public class LocalConfigPnl extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4402949592828399708L;
	
	private static final Logger logger = Logger.getLogger(LocalConfigPnl.class);
	private JSlider slider_time;
	private JSlider slider_mvtTime;

	/**
	 * Create the dialog.
	 */
	public LocalConfigPnl () {
		setSize(new Dimension(400, 300));
		this.setBorder(new TitledBorder(null, "Configuration des options locales", TitledBorder.LEADING,
				TitledBorder.TOP, null, new Color(0, 70, 213)));
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);

		slider_time = new JSlider();
		slider_time.setPaintTicks(true);
		springLayout.putConstraint(SpringLayout.EAST, slider_time, 229, SpringLayout.WEST, this);
		slider_time.setBorder(new TitledBorder(null,
				"R\u00E9glage du temps de partie - " + Config.PARTY_TIME / 60 + " mn",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		slider_time.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				Config.PARTY_TIME = slider_time.getValue() * 60;
				slider_time.setBorder(new TitledBorder(null,
						"R\u00E9glage du temps de partie - " + Config.PARTY_TIME / 60 + " mn",
						TitledBorder.LEADING, TitledBorder.TOP, null, null));
			}
		});
		slider_time.setValue(Config.PARTY_TIME / 60);
		slider_time.setMinimum(1);
		slider_time.setPaintLabels(true);
		springLayout.putConstraint(SpringLayout.WEST, slider_time, 10, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, slider_time, -199, SpringLayout.SOUTH, this);
		add(slider_time);
		slider_time.setMajorTickSpacing(19);
		slider_time.setMinorTickSpacing(1);
		slider_time.setMaximum(60);

		slider_mvtTime = new JSlider();
		springLayout.putConstraint(SpringLayout.NORTH, slider_mvtTime, 6, SpringLayout.SOUTH, slider_time);
		springLayout.putConstraint(SpringLayout.WEST, slider_mvtTime, 0, SpringLayout.WEST, slider_time);
		springLayout.putConstraint(SpringLayout.EAST, slider_mvtTime, 0, SpringLayout.EAST, slider_time);
		slider_mvtTime.setPaintTicks(true);
		slider_mvtTime.setPaintLabels(true);
		slider_mvtTime.setMinorTickSpacing(100);
		slider_mvtTime.setMaximum(1000);
		slider_mvtTime.setMajorTickSpacing(200);
		slider_mvtTime.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
				"Vitesse d'un d\u00E9placement - " + Config.MOVE_RAPIDITY + " ms", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		slider_mvtTime.setValue(Config.MOVE_RAPIDITY);
		slider_mvtTime.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				Config.MOVE_RAPIDITY = slider_mvtTime.getValue();
				slider_mvtTime.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
						"Vitesse d'un d\u00E9placement - " + Config.MOVE_RAPIDITY + " ms", TitledBorder.LEADING,
						TitledBorder.TOP, null, null));
			}
		});
		add(slider_mvtTime);
	}
}
