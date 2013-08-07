package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;

import message.Error;
import message.ProfileEdit;
import network.NetworkClient;
import network.NetworkEvent;
import network.NetworkEventHandler;
import network.NetworkManager;

public class ChangePwdGUI extends JDialog implements ActionListener, NetworkEventHandler {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2516162762813906514L;
	private ChangePwdGUI himself;
	private JPanel contentPane;
	private JLabel lblAncienMotDe;
	private JPasswordField pwdOldpwd;
	private JLabel lblNouveauMotDe;
	private JPasswordField pwdNewpwd;
	private JLabel lblConfirmerNouveauMot;
	private JPasswordField pwdConfnewpwd;
	private JButton btnValider;
	private ProfileGUI profileGUI;

	public enum ControllerAction {
		VALIDATE;
	}

	public ChangePwdGUI(ProfileGUI profileGUI) {
		initComponent();
		setLocationRelativeTo(null);
		setModal(true);

		himself = this;
		this.profileGUI = profileGUI;

		contentPane = new JPanel();
		contentPane.setBorder(new TitledBorder(null, "Changement de mot de passe", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);

		lblAncienMotDe = new JLabel("Ancien mot de passe");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblAncienMotDe, 10, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblAncienMotDe, 10, SpringLayout.WEST, contentPane);
		contentPane.add(lblAncienMotDe);

		pwdOldpwd = new JPasswordField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, pwdOldpwd, 6, SpringLayout.SOUTH, lblAncienMotDe);
		sl_contentPane.putConstraint(SpringLayout.WEST, pwdOldpwd, 0, SpringLayout.WEST, lblAncienMotDe);
		sl_contentPane.putConstraint(SpringLayout.EAST, pwdOldpwd, 180, SpringLayout.WEST, contentPane);
		contentPane.add(pwdOldpwd);

		lblNouveauMotDe = new JLabel("Nouveau mot de passe");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblNouveauMotDe, 6, SpringLayout.SOUTH, pwdOldpwd);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblNouveauMotDe, 0, SpringLayout.WEST, lblAncienMotDe);
		contentPane.add(lblNouveauMotDe);

		pwdNewpwd = new JPasswordField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, pwdNewpwd, 6, SpringLayout.SOUTH, lblNouveauMotDe);
		sl_contentPane.putConstraint(SpringLayout.WEST, pwdNewpwd, 0, SpringLayout.WEST, lblAncienMotDe);
		sl_contentPane.putConstraint(SpringLayout.EAST, pwdNewpwd, 180, SpringLayout.WEST, contentPane);
		contentPane.add(pwdNewpwd);

		lblConfirmerNouveauMot = new JLabel("Confirmer nouveau mot de passe");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblConfirmerNouveauMot, 6, SpringLayout.SOUTH, pwdNewpwd);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblConfirmerNouveauMot, 0, SpringLayout.WEST, lblAncienMotDe);
		contentPane.add(lblConfirmerNouveauMot);

		pwdConfnewpwd = new JPasswordField();
		sl_contentPane.putConstraint(SpringLayout.NORTH, pwdConfnewpwd, 6, SpringLayout.SOUTH, lblConfirmerNouveauMot);
		sl_contentPane.putConstraint(SpringLayout.WEST, pwdConfnewpwd, 0, SpringLayout.WEST, lblAncienMotDe);
		sl_contentPane.putConstraint(SpringLayout.EAST, pwdConfnewpwd, 180, SpringLayout.WEST, contentPane);
		contentPane.add(pwdConfnewpwd);

		btnValider = new JButton("Valider");
		btnValider.addActionListener(this);
		btnValider.setActionCommand(ControllerAction.VALIDATE.name());
		sl_contentPane.putConstraint(SpringLayout.WEST, btnValider, 61, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnValider, -10, SpringLayout.SOUTH, contentPane);
		contentPane.add(btnValider);
	}

	private void initComponent() {
		setSize(220, 265);
		setLocationRelativeTo(null);
		setResizable(false);
		setTitle("Changement du mot de passe");
		setDefaultCloseOperation(HIDE_ON_CLOSE);
	}

	@Override
	public void setVisible(boolean b) {
		if (b) {
			NetworkManager.getInstance().addHandler(this, ProfileEdit.class);
			NetworkManager.getInstance().addHandler(this, Error.class);
		}
		else {
			pwdOldpwd.setText("");
			pwdNewpwd.setText("");
			pwdConfnewpwd.setText("");
			NetworkManager.getInstance().removeHandler(this, ProfileEdit.class);
			NetworkManager.getInstance().removeHandler(this, Error.class);
		}
		super.setVisible(b);
	}

	@Override
	public void handleNetworkEvent(NetworkEvent obj) {
		if (obj.getSource() instanceof ProfileEdit) {
			ProfileEdit edit = (ProfileEdit) obj.getSource();
			if (edit.isChgPwd()) {
				JOptionPane.showMessageDialog(this, "Changements réalisés !", "Changement du mot de passe", JOptionPane.INFORMATION_MESSAGE);
				setVisible(false);
			}
		}
		else if (obj.getSource() instanceof Error) {
			GUIErrorsException error = new GUIErrorsException("Changement du mot de passe", (Error) obj.getSource());
			error.showErrors(this);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ControllerAction.VALIDATE.name().equals(e.getActionCommand())) {
			String oldPwd = new String(pwdOldpwd.getPassword());
			String newPwd = new String(pwdNewpwd.getPassword());
			String newPwdConfirm = new String(pwdConfnewpwd.getPassword());
			if (oldPwd.isEmpty() && newPwd.isEmpty() && newPwdConfirm.isEmpty()) {
				setVisible(false);
			}
			else {
				if (!oldPwd.isEmpty()) {
					if (!newPwd.isEmpty()) {
						if (newPwd.equals(newPwdConfirm)) {
							ProfileEdit profileEdit = new ProfileEdit(oldPwd, newPwd, "", profileGUI.getProfilePartiesList().getPlayerName(), "");
							profileEdit.setChgPwd(true);
							NetworkClient.getInstance().sendObject(profileEdit);
						}
						else {
							JOptionPane.showMessageDialog(himself, "Les mots de passe sont différents !", newPwdConfirm, JOptionPane.ERROR_MESSAGE);
						}
					}
					else {
						JOptionPane.showMessageDialog(himself, "Nouveau mot de passe vide !", newPwd, JOptionPane.ERROR_MESSAGE);
					}
				}
				else {
					JOptionPane.showMessageDialog(himself, "Ancien mot de passe vide !", oldPwd, JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
}
