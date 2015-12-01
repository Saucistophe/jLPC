package org.saucistophe.lpc.client.display;

import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * This class holds various methods to ask user informations and interactions.
 */
public class UserDialog
{
	private static String login, password;
	// modal dialog to get user ID and password
	private static final String[] ConnectOptionNames =
	{
		"Ok", "Annuler"
	};
	private static final String ConnectTitle = "Infos de connexion";

	public static String[] getLoginInfo(Component parent)
	{
		JPanel connectionPanel;

		// Create the labels and text fields.
		JLabel urlLabel = new JLabel("Serveur : ", JLabel.RIGHT);
		JTextField urlField = new JTextField("http://saucistophe.servebeer.com:9980");
		JLabel userNameLabel = new JLabel("Login : ", JLabel.RIGHT);
		JTextField userNameField = new JTextField("");
		JLabel passwordLabel = new JLabel("Mot de passe : ", JLabel.RIGHT);
		JTextField passwordField = new JPasswordField("");

		// Create whole panel
		connectionPanel = new JPanel(false);
		connectionPanel.setLayout(new BoxLayout(connectionPanel,
				BoxLayout.X_AXIS));
		JPanel namePanel = new JPanel(false);
		namePanel.setLayout(new GridLayout(0, 1));
		namePanel.add(urlLabel);
		namePanel.add(userNameLabel);
		namePanel.add(passwordLabel);
		JPanel fieldPanel = new JPanel(false);
		fieldPanel.setLayout(new GridLayout(0, 1));
		fieldPanel.add(urlField);
		fieldPanel.add(userNameField);
		fieldPanel.add(passwordField);
		connectionPanel.add(namePanel);
		connectionPanel.add(fieldPanel);

		// Connect or quit
		if (JOptionPane.showOptionDialog(parent, connectionPanel,
				ConnectTitle,
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.INFORMATION_MESSAGE,
				null, ConnectOptionNames,
				ConnectOptionNames[0]) != 0)
		{
			return null;
		}
		else
		{
			String result[] = new String[3];
			result[0] = userNameField.getText();
			result[1] = passwordField.getText();
			result[2] = urlField.getText();
			return result;
		}
	}
}
