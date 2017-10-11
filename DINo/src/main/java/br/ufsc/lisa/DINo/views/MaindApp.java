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
import java.awt.Font;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTextArea;

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
		
		JPanel panelPrincipal = new JPanel();
		panelPrincipal.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent arg0) {
				
			}
		});
		frame.getContentPane().add(panelPrincipal, BorderLayout.CENTER);
		panelPrincipal.setLayout(null);
		
		JLabel lblSource = new JLabel("Source");
		lblSource.setFont(new Font("Dialog", Font.BOLD, 14));
		lblSource.setBounds(96, 12, 78, 27);
		panelPrincipal.add(lblSource);
		
		JTabbedPane tabbedPaneSource = new JTabbedPane(JTabbedPane.TOP);
		tabbedPaneSource.setBounds(12, 41, 272, 234);
		panelPrincipal.add(tabbedPaneSource);
		
		JPanel panelServer = new JPanel();
		tabbedPaneSource.addTab("Server", null, panelServer, null);
		panelServer.setLayout(null);
		
		JLabel lblConnection = new JLabel("Connection");
		lblConnection.setFont(new Font("Dialog", Font.BOLD, 13));
		lblConnection.setBounds(12, 12, 101, 15);
		panelServer.add(lblConnection);
		
		JLabel lblHost = new JLabel("Host: ");
		lblHost.setBounds(12, 58, 50, 15);
		panelServer.add(lblHost);
		
		JLabel lblPort = new JLabel("Port: ");
		lblPort.setBounds(12, 85, 70, 15);
		panelServer.add(lblPort);
		
		JLabel lblUsername = new JLabel("Username: ");
		lblUsername.setBounds(12, 112, 101, 15);
		panelServer.add(lblUsername);
		
		JLabel lblPassword = new JLabel("Password: ");
		lblPassword.setBounds(12, 139, 101, 15);
		panelServer.add(lblPassword);
		
		textFieldHost = new JTextField();
		textFieldHost.setBounds(92, 56, 163, 19);
		panelServer.add(textFieldHost);
		textFieldHost.setColumns(10);
		
		textFieldPort = new JTextField();
		textFieldPort.setBounds(92, 83, 163, 19);
		panelServer.add(textFieldPort);
		textFieldPort.setColumns(10);
		
		textFieldUsername = new JTextField();
		textFieldUsername.setBounds(92, 112, 163, 19);
		panelServer.add(textFieldUsername);
		textFieldUsername.setColumns(10);
		
		textFieldPassword = new JTextField();
		textFieldPassword.setBounds(92, 139, 163, 19);
		panelServer.add(textFieldPassword);
		textFieldPassword.setColumns(10);
		
		JButton btnTest = new JButton("Test");
		btnTest.setBounds(12, 166, 117, 25);
		panelServer.add(btnTest);
		
		JButton btnConnect = new JButton("Connect");
		btnConnect.setBounds(141, 166, 117, 25);
		panelServer.add(btnConnect);
		
		JPanel panelDatabase = new JPanel();
		tabbedPaneSource.addTab("Database", null, panelDatabase, null);
		panelDatabase.setLayout(null);
		
		JList list = new JList();
		list.setBounds(40, 12, 173, 141);
		panelDatabase.add(list);
		
		JTextArea textAreaDbSelecionado = new JTextArea();
		textAreaDbSelecionado.setBounds(257, 181, -122, -10);
		panelDatabase.add(textAreaDbSelecionado);
		
		JLabel lblDbSelecionado = new JLabel("DB Selecionado");
		lblDbSelecionado.setBounds(12, 169, 110, 15);
		panelDatabase.add(lblDbSelecionado);
		
		JPanel panelTables = new JPanel();
		tabbedPaneSource.addTab("Tables", null, panelTables, null);
		
		JLabel lblTables = new JLabel("Target");
		lblTables.setFont(new Font("Dialog", Font.BOLD, 14));
		lblTables.setBounds(419, 18, 78, 21);
		panelPrincipal.add(lblTables);
		
		JTabbedPane tabbedPaneTarget = new JTabbedPane(JTabbedPane.TOP);
		tabbedPaneTarget.setBounds(317, 41, 272, 234);
		panelPrincipal.add(tabbedPaneTarget);
		
		JPanel panelCollection = new JPanel();
		tabbedPaneTarget.addTab("Collection", null, panelCollection, null);
	}
}
