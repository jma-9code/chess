package gui;

import java.awt.Component;

import javax.swing.JOptionPane;

import message.Error;

public class GUIErrorsException extends Exception {
	private String title = null;

	public GUIErrorsException(String title, String msg) {
		super(msg);
		this.title = title;
	}

	public GUIErrorsException(String title, Error err) {
		super(err.toString());
		this.title = title;
	}

	public void showErrors(Component parent) {
		JOptionPane.showMessageDialog(parent, getMessage(), title, JOptionPane.ERROR_MESSAGE);
	}
}
