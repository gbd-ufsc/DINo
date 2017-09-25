package br.ufsc.lisa.DINo.views;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JComboBox;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JTextField;

public class MaindApp {

	private JFrame frame;
	private JTextField textFieldHost;
	private JTextField textFieldPort;
	private JTextField textFieldUsername;
	private JTextField textFieldPassword;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MaindApp window = new MaindApp();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MaindApp() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 607, 475);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		panel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent arg0) {
				comboBox.addItem("Tabela 1");
				comboBox.addItem("Tabela 2");
				comboBox.addItem("Tabela 3");
			}
		});
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		JTabbedPane Abas = new JTabbedPane(JTabbedPane.TOP);
		Abas.setBounds(12, 24, 283, 319);
		panel.add(Abas);
		
		JPanel Server = new JPanel();
		Abas.addTab("Server", null, Server, null);
		Server.setLayout(null);
		
		JLabel lblHost = new JLabel("Host: ");
		lblHost.setBounds(12, 75, 70, 15);
		Server.add(lblHost);
		
		JButton btnTest = new JButton("Test");
		btnTest.setBounds(105, 255, 65, 25);
		Server.add(btnTest);
		
		textFieldHost = new JTextField();
		textFieldHost.setBounds(61, 73, 174, 19);
		Server.add(textFieldHost);
		textFieldHost.setColumns(10);
		
		JLabel lblConnection = new JLabel("Connection");
		lblConnection.setBounds(12, 12, 105, 25);
		Server.add(lblConnection);
		
		JLabel lblPort = new JLabel("Port: ");
		lblPort.setBounds(12, 120, 39, 15);
		Server.add(lblPort);
		
		textFieldPort = new JTextField();
		textFieldPort.setBounds(61, 118, 174, 19);
		Server.add(textFieldPort);
		textFieldPort.setColumns(10);
		
		JLabel lblUsername = new JLabel("Username: ");
		lblUsername.setBounds(12, 158, 92, 15);
		Server.add(lblUsername);
		
		textFieldUsername = new JTextField();
		textFieldUsername.setBounds(95, 156, 140, 19);
		Server.add(textFieldUsername);
		textFieldUsername.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setBounds(12, 197, 92, 15);
		Server.add(lblPassword);
		
		textFieldPassword = new JTextField();
		textFieldPassword.setBounds(95, 195, 140, 19);
		Server.add(textFieldPassword);
		textFieldPassword.setColumns(10);
		
		JPanel Databases = new JPanel();
		Abas.addTab("Databases", null, Databases, null);
		
		JPanel Tables = new JPanel();
		Abas.addTab("Tables", null, Tables, null);
		
		JLabel lblSource = new JLabel("Source");
		lblSource.setBounds(12, 0, 70, 15);
		panel.add(lblSource);
	}
}
