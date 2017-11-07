package br.ufsc.lisa.DINo.views;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import javax.swing.JComboBox;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;

import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;
import java.awt.SystemColor;
import java.awt.Color;
import br.ufsc.lisa.DINo.util.PostgresDB;
import br.ufsc.lisa.DINo.util.RedisConnector;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.InputMethodEvent;
import javax.swing.JScrollBar;

public class MaindApp {

	private JFrame frame;
	private JTextField textFieldHost;
	private JTextField textFieldPort;
	private JTextField textFieldUsername;
	private JTextField textFieldPassword;
	private JTextField textFieldImport;
	private PostgresDB postgresDb;
	private RedisConnector redisDb;
	private JList listDb;
	private String tempUrl;
	private JTextField textFieldPrefixo;

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

public MaindApp() throws ClassNotFoundException, SQLException {
	initialize();
	postgresDb = new PostgresDB();
	redisDb = new RedisConnector();
}

private void initialize() throws ClassNotFoundException, SQLException {
	frame = new JFrame();
	frame.setBounds(100, 100, 607, 475);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	JPanel mainPanel = new JPanel();
	mainPanel.addComponentListener(new ComponentAdapter() {
		@Override
		public void componentShown(ComponentEvent arg0) {

		}
	});
	frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
	mainPanel.setLayout(null);

	JLabel lblSource = new JLabel("Source");
	lblSource.setFont(new Font("Dialog", Font.BOLD, 14));
	lblSource.setBounds(97, -3, 78, 27);
	mainPanel.add(lblSource);

	JTabbedPane tabbedPaneSource = new JTabbedPane(JTabbedPane.TOP);
	tabbedPaneSource.setBounds(12, 22, 293, 269);
	mainPanel.add(tabbedPaneSource);

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
	textFieldHost.setText("localhost");
	textFieldHost.setBounds(92, 56, 163, 19);
	panelServer.add(textFieldHost);
	textFieldHost.setColumns(10);

	textFieldPort = new JTextField();
	textFieldPort.setText("5432");
	textFieldPort.setBounds(92, 83, 163, 19);
	panelServer.add(textFieldPort);
	textFieldPort.setColumns(10);

	textFieldUsername = new JTextField();
	textFieldUsername.setText("postgres");
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
			postgresDb.connect(textFieldHost.getText(), textFieldPort.getText(), textFieldUsername.getText(), textFieldPassword.getText(), "?");
			try {
				listDb.setModel(postgresDb.listDatabases());
			}catch(Exception s) {}
		}
	});
	btnTest.setBounds(12, 166, 117, 25);
	panelServer.add(btnTest);

	JButton btnConnect = new JButton("Connect");
	btnConnect.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		}
	});
	btnConnect.setBounds(141, 166, 117, 25);
	panelServer.add(btnConnect);

	JPanel panelDatabase = new JPanel();
	tabbedPaneSource.addTab("Database", null, panelDatabase, null);
	panelDatabase.setLayout(null);

	//		JScrollBar scrollBar = new JScrollBar();
	//		scrollBar.setBounds(204, 38, 17, 146);
	//		panelDatabase.add(scrollBar);

	listDb = new JList();
	listDb.addListSelectionListener(new ListSelectionListener() {
		public void valueChanged(ListSelectionEvent e) {
			if(listDb.getSelectedValue() != null) {
				postgresDb.connect(textFieldHost.getText(), textFieldPort.getText(), textFieldUsername.getText(), textFieldPassword.getText(), listDb.getSelectedValue().toString());
			}
		}
	});
	listDb.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	listDb.setVisibleRowCount(6);
	listDb.setBounds(23, 39, 239, 145);
	panelDatabase.add(listDb);


	JLabel lblSelectDb = new JLabel("Select a database");
	lblSelectDb.setBounds(74, 12, 129, 15);
	panelDatabase.add(lblSelectDb);



	JPanel panelTables = new JPanel();
	
	tabbedPaneSource.addTab("Tables", null, panelTables, null);
	panelTables.setLayout(null);

	JLabel lblTables_1 = new JLabel("Tables");
	lblTables_1.setFont(new Font("Dialog", Font.BOLD, 14));
	lblTables_1.setBounds(111, 12, 70, 15);
	panelTables.add(lblTables_1);

	final JList listTable = new JList();
	listTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	try {
	listTable.setModel(postgresDb.listTables());
	} catch (Exception n) {
		
	}
	listTable.setBounds(12, 29, 264, 137);
	panelTables.add(listTable);

	JButton btnRefresh = new JButton("Refresh");
	btnRefresh.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			try {
				listTable.setModel(postgresDb.listTables());
			} catch (Exception t){					
			}				
		}
	});
	btnRefresh.setBounds(12, 178, 264, 15);
	panelTables.add(btnRefresh);

	JPanel panelColumns = new JPanel();
	tabbedPaneSource.addTab("Columns", null, panelColumns, null);
	panelColumns.setLayout(null);

	JLabel lblColumn = new JLabel("Column");
	lblColumn.setBounds(114, 5, 59, 17);
	panelColumns.add(lblColumn);
	lblColumn.setFont(new Font("Dialog", Font.BOLD, 14));
	
	final JList listColumns = new JList();
	listColumns.setBounds(12, 21, 264, 144);
	panelColumns.add(listColumns);
	
	JButton btnNewButton = new JButton("Refresh");
	btnNewButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			try {
				listColumns.setModel(postgresDb.listColumns(listTable.getSelectedValue().toString()));
			} catch (Exception z) {}
		}
	});
	btnNewButton.setBounds(12, 177, 264, 16);
	panelColumns.add(btnNewButton);

	JLabel lblTables = new JLabel("Target");
	lblTables.setFont(new Font("Dialog", Font.BOLD, 14));
	lblTables.setBounds(422, 0, 78, 21);
	mainPanel.add(lblTables);

	JTabbedPane tabbedPaneTarget = new JTabbedPane(JTabbedPane.TOP);
	tabbedPaneTarget.setBounds(317, 22, 272, 315);
	mainPanel.add(tabbedPaneTarget);

	JPanel panelCollection = new JPanel();
	tabbedPaneTarget.addTab("Collection", null, panelCollection, null);
	panelCollection.setLayout(null);

	JLabel lblProperties = new JLabel("Prefixo chave");
	lblProperties.setFont(new Font("Dialog", Font.BOLD, 12));
	lblProperties.setBounds(12, 9, 103, 26);
	panelCollection.add(lblProperties);

	final JList listPk = new JList();
	listPk.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	listPk.setBounds(56, 47, 199, 98);
	panelCollection.add(listPk);
	
	JLabel lblPk = new JLabel("PK");
	lblPk.setBounds(12, 48, 28, 15);
	panelCollection.add(lblPk);
	
	final JList listValue = new JList();
	listValue.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	listValue.setBounds(56, 175, 199, 90);
	
	panelCollection.add(listValue);
	
	JLabel lblValue = new JLabel("Value");
	lblValue.setBounds(12, 175, 58, 15);
	panelCollection.add(lblValue);
	
	JButton btnGetSelectedColumnsPK = new JButton("Get selected row");
	btnGetSelectedColumnsPK.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			
			DefaultListModel listPkTemp = new DefaultListModel();
			List <String> selecionadosPk =  listColumns.getSelectedValuesList();
			for (String n: selecionadosPk ) {
				listPkTemp.addElement(n);
			}				
			listPk.setModel(listPkTemp);
		}
	});
	btnGetSelectedColumnsPK.setBounds(56, 150, 199, 20);
	panelCollection.add(btnGetSelectedColumnsPK);
	
	JButton btnGetSelectedColumnsVALUE = new JButton("Get selected row");
	btnGetSelectedColumnsVALUE.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			DefaultListModel listValueTemp = new DefaultListModel();
			List <String> selecionadosValues =  listColumns.getSelectedValuesList();
			for (String n: selecionadosValues ) {
				listValueTemp.addElement(n);
			}				
			listValue.setModel(listValueTemp);
		}
	});
	btnGetSelectedColumnsVALUE.setBounds(56, 268, 199, 20);
	panelCollection.add(btnGetSelectedColumnsVALUE);
	
	textFieldPrefixo = new JTextField();
	textFieldPrefixo.setBounds(111, 9, 144, 26);
	panelCollection.add(textFieldPrefixo);
	textFieldPrefixo.setColumns(10);

	JPanel panelExecute = new JPanel();
	panelExecute.setBounds(12, 343, 390, 88);
	mainPanel.add(panelExecute);
	panelExecute.setLayout(null);

	JLabel lblExecute = new JLabel("Execute");
	lblExecute.setFont(new Font("Dialog", Font.BOLD, 14));
	lblExecute.setBounds(12, 12, 70, 15);
	panelExecute.add(lblExecute);

	JLabel lblStatus = new JLabel("Status:");
	lblStatus.setBounds(12, 35, 70, 15);
	panelExecute.add(lblStatus);

	textFieldImport = new JTextField();
	textFieldImport.setBounds(12, 52, 255, 33);
	panelExecute.add(textFieldImport);
	textFieldImport.setColumns(10);

	JButton btnImport = new JButton("Import");
	btnImport.setBounds(279, 52, 97, 33);
	panelExecute.add(btnImport);
	
	JButton btnGenerateSql = new JButton("Generate sql");
	btnGenerateSql.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			String tempPk = "";	
			String tempPkVirgula = "";
			for(int x = 0; x < listPk.getModel().getSize(); x++) {
				if(x == 0) {
					tempPk = tempPk + listPk.getModel().getElementAt(x) + "::text||";
					tempPkVirgula = tempPkVirgula + listPk.getModel().getElementAt(x);					
				}
				if (x == listPk.getModel().getSize() - 1){
				tempPk = tempPk +"'_'||" + listPk.getModel().getElementAt(x)+"::text";
				tempPkVirgula = tempPkVirgula +", "+ listPk.getModel().getElementAt(x);
				}
				if (x != listPk.getModel().getSize() - 1 && x > 0){
					tempPk = tempPk +"'_'||" + listPk.getModel().getElementAt(x)+"::text||";
					tempPkVirgula = tempPkVirgula +", "+ listPk.getModel().getElementAt(x);
				}
			}
			String tempValue = "";
			for(int x = 0; x < listValue.getModel().getSize(); x++) {
				if(x == 0) {
					tempValue = tempValue + listValue.getModel().getElementAt(x);
				} else {
				tempValue = tempValue +", "+ listValue.getModel().getElementAt(x);					
				}
			}
			textFieldImport.setText("SELECT '"+ textFieldPrefixo.getText() +"_'||"+tempPk+" AS key, row_to_json(n) AS value from (SELECT "+tempPkVirgula+", "+tempValue+" FROM "+ listTable.getSelectedValue().toString() + " WHERE tags->'name' <> '' LIMIT 2 ) n;");
			// BUG QUANDO UMA PK É VALUE TAMBEM, APRESENTA AMBIGUIDADE NO SEGUNDO SELECT
			
			}
		}
	);
	
	btnGenerateSql.setBounds(252, 7, 126, 33);
	panelExecute.add(btnGenerateSql);

	JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnCancel.setBounds(472, 397, 117, 34);
		mainPanel.add(btnCancel);
	}
}
