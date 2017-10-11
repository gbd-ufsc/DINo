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
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;
import java.awt.SystemColor;
import java.awt.Color;

public class MaindApp {

	private JFrame frame;
	private JTextField textFieldHost;
	private JTextField textFieldPort;
	private JTextField textFieldUsername;
	private JTextField textFieldPassword;
	private JTextField textField;

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
		btnTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//chama um metodo no controlador que executa os testes, os textFields serao passados como parametro do método. 
			}
		});
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
		panelTables.setLayout(null);
		
		JLabel lblTables_1 = new JLabel("Tables");
		lblTables_1.setFont(new Font("Dialog", Font.BOLD, 14));
		lblTables_1.setBounds(92, 12, 70, 15);
		panelTables.add(lblTables_1);
		
		JList listTable = new JList();
		listTable.setBounds(12, 22, 243, 73);
		panelTables.add(listTable);
		
		JLabel lblColumn = new JLabel("Column");
		lblColumn.setFont(new Font("Dialog", Font.BOLD, 14));
		lblColumn.setBounds(92, 96, 70, 15);
		panelTables.add(lblColumn);
		
		JList listColumn = new JList();
		listColumn.setBounds(12, 114, 243, 81);
		panelTables.add(listColumn);
		
		JLabel lblTables = new JLabel("Target");
		lblTables.setFont(new Font("Dialog", Font.BOLD, 14));
		lblTables.setBounds(419, 18, 78, 21);
		panelPrincipal.add(lblTables);
		
		JTabbedPane tabbedPaneTarget = new JTabbedPane(JTabbedPane.TOP);
		tabbedPaneTarget.setBounds(317, 41, 272, 234);
		panelPrincipal.add(tabbedPaneTarget);
		
		JPanel panelCollection = new JPanel();
		tabbedPaneTarget.addTab("Collection", null, panelCollection, null);
		panelCollection.setLayout(null);
		
		JLabel lblProperties = new JLabel("Properties");
		lblProperties.setFont(new Font("Dialog", Font.BOLD, 13));
		lblProperties.setBounds(92, 12, 94, 24);
		panelCollection.add(lblProperties);
		
		JList listProperties = new JList();
		listProperties.setBounds(45, 40, 173, 137);
		panelCollection.add(listProperties);
		
		JPanel panel = new JPanel();
		panel.setBounds(12, 303, 388, 128);
		panelPrincipal.add(panel);
		panel.setLayout(null);
		
		JLabel lblExecute = new JLabel("Execute");
		lblExecute.setFont(new Font("Dialog", Font.BOLD, 14));
		lblExecute.setBounds(12, 12, 70, 15);
		panel.add(lblExecute);
		
		JLabel lblStatus = new JLabel("Status:");
		lblStatus.setBounds(12, 56, 70, 15);
		panel.add(lblStatus);
		
		textField = new JTextField();
		textField.setBounds(12, 83, 255, 33);
		panel.add(textField);
		textField.setColumns(10);
		
		JButton btnImport = new JButton("Import");
		btnImport.setBounds(279, 83, 97, 33);
		panel.add(btnImport);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setBounds(472, 386, 117, 34);
		panelPrincipal.add(btnCancel);
	}
}
