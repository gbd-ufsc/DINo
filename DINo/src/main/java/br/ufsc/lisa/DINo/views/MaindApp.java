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
import javax.swing.JList;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class MaindApp {

	private JFrame frame;
	private JTextField textFieldHost;
	private JTextField textFieldPort;
	private JTextField textFieldUsername;
	private JTextField textFieldPassword;
	private JTextField txtLine;

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
		
		JPanel panelMain = new JPanel();
		panelMain.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent arg0) {
				comboBox.addItem("Tabela 1");
				comboBox.addItem("Tabela 2");
				comboBox.addItem("Tabela 3");
			}
		});
		frame.getContentPane().add(panelMain, BorderLayout.CENTER);
		panelMain.setLayout(null);
		
		JTabbedPane panelSource = new JTabbedPane(JTabbedPane.TOP);
		panelSource.setBounds(12, 35, 283, 288);
		panelMain.add(panelSource);
		
		JPanel server = new JPanel();
		panelSource.addTab("Server", null, server, null);
		server.setLayout(null);
		
		JLabel lblHost = new JLabel("Host: ");
		lblHost.setBounds(12, 75, 70, 15);
		server.add(lblHost);
		
		JButton btnTest = new JButton("Test");
		btnTest.setBounds(105, 226, 65, 25);
		server.add(btnTest);
		
		textFieldHost = new JTextField();
		textFieldHost.setBounds(61, 73, 174, 19);
		server.add(textFieldHost);
		textFieldHost.setColumns(10);
		
		JLabel lblConnection = new JLabel("Connection");
		lblConnection.setBounds(12, 12, 105, 25);
		server.add(lblConnection);
		
		JLabel lblPort = new JLabel("Port: ");
		lblPort.setBounds(12, 120, 39, 15);
		server.add(lblPort);
		
		textFieldPort = new JTextField();
		textFieldPort.setBounds(61, 118, 174, 19);
		server.add(textFieldPort);
		textFieldPort.setColumns(10);
		
		JLabel lblUsername = new JLabel("Username: ");
		lblUsername.setBounds(12, 158, 92, 15);
		server.add(lblUsername);
		
		textFieldUsername = new JTextField();
		textFieldUsername.setBounds(95, 156, 140, 19);
		server.add(textFieldUsername);
		textFieldUsername.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setBounds(12, 197, 92, 15);
		server.add(lblPassword);
		
		textFieldPassword = new JTextField();
		textFieldPassword.setBounds(95, 195, 140, 19);
		server.add(textFieldPassword);
		textFieldPassword.setColumns(10);
		
		JPanel databases = new JPanel();
		panelSource.addTab("Databases", null, databases, null);
		databases.setLayout(null);
		
		JList listDbs = new JList();
		listDbs.setBounds(12, 12, 254, 268);
		databases.add(listDbs);
		
		JScrollBar scrollBar_dbs = new JScrollBar();
		scrollBar_dbs.setBounds(249, 12, 17, 268);
		databases.add(scrollBar_dbs);
		
		JPanel tables = new JPanel();
		panelSource.addTab("Tables", null, tables, null);
		tables.setLayout(null);
		
		JLabel lblTables = new JLabel("Tables");
		lblTables.setBounds(104, 12, 70, 15);
		tables.add(lblTables);
		
		JComboBox comboBoxTables = new JComboBox();
		comboBoxTables.setBounds(30, 47, 217, 24);
		tables.add(comboBoxTables);
		
		JLabel lblColumns = new JLabel("Columns");
		lblColumns.setBounds(104, 156, 70, 15);
		tables.add(lblColumns);
		
		JComboBox comboBoxColumns = new JComboBox();
		comboBoxColumns.setBounds(30, 194, 217, 24);
		tables.add(comboBoxColumns);
		
		JLabel lblSource = new JLabel("Source");
		lblSource.setBounds(106, 8, 70, 15);
		panelMain.add(lblSource);
		
		JLabel lblTarget = new JLabel("Target");
		lblTarget.setBounds(421, 8, 70, 15);
		panelMain.add(lblTarget);
		
		JPanel panelImport = new JPanel();
		panelImport.setBounds(22, 335, 567, 96);
		panelMain.add(panelImport);
		panelImport.setLayout(null);
		
		JLabel lblExecute = new JLabel("Execute");
		lblExecute.setBounds(12, 12, 70, 15);
		panelImport.add(lblExecute);
		
		JLabel lblStatus = new JLabel("Status: ");
		lblStatus.setBounds(12, 51, 70, 15);
		panelImport.add(lblStatus);
		
		txtLine = new JTextField();
		txtLine.setText("line1");
		txtLine.setBounds(73, 39, 366, 27);
		panelImport.add(txtLine);
		txtLine.setColumns(10);
		
		JButton btnNewButton = new JButton("Import");
		btnNewButton.setBounds(462, 12, 93, 72);
		panelImport.add(btnNewButton);
		
		JTabbedPane panelTarget = new JTabbedPane(JTabbedPane.TOP);
		panelTarget.setBounds(307, 35, 282, 288);
		panelMain.add(panelTarget);
		
		JPanel panelCollection = new JPanel();
		panelTarget.addTab("Collection", null, panelCollection, null);
		panelCollection.setLayout(null);
		
		JLabel label = new JLabel("Properties");
		label.setBounds(99, 12, 104, 15);
		panelCollection.add(label);
		
		JList list = new JList();
		list.setBounds(65, 39, 159, 173);
		panelCollection.add(list);
	}
}
