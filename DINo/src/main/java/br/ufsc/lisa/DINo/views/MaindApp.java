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
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.InputMethodEvent;
import javax.swing.JScrollBar;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

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
	private JTextField textFieldConsole;
	private JTextField textFieldRedisHost;
	private JTextField textFieldRedisPort;
	private JTextField textFieldRedisUser;
	private JTextField textFieldRedisPassword;

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
		frame.setBounds(100, 100, 636, 525);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);

		JPanel mainPanel = new JPanel();
		mainPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent arg0) {

			}
		});
		frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(null);

		JLabel lblSource = new JLabel("Source");
		lblSource.setBounds(97, -3, 78, 27);
		lblSource.setFont(new Font("Dialog", Font.BOLD, 14));
		mainPanel.add(lblSource);

		JTabbedPane tabbedPaneSource = new JTabbedPane(JTabbedPane.TOP);
		tabbedPaneSource.setBounds(12, 22, 293, 315);
		mainPanel.add(tabbedPaneSource);

		JPanel panelServer = new JPanel();
		tabbedPaneSource.addTab("Server", null, panelServer, null);
		panelServer.setLayout(null);

		JLabel lblConnection = new JLabel("Connection Postgres");
		lblConnection.setFont(new Font("Dialog", Font.BOLD, 13));
		lblConnection.setBounds(12, 12, 153, 17);
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
		
		textFieldConsole = new JTextField();
		textFieldConsole.setBounds(12, 454, 448, 34);
		textFieldConsole.setEditable(false);
		mainPanel.add(textFieldConsole);
		textFieldConsole.setColumns(10);

		JButton btnTest = new JButton("Connect");
		btnTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				postgresDb.connect(textFieldHost.getText(), textFieldPort.getText(), textFieldUsername.getText(),
						textFieldPassword.getText(), "?");
				try {
					listDb.setModel(postgresDb.listDatabases());
				} catch (Exception s) {
					textFieldConsole.setText("Error: banco não encontrado");
				}
				textFieldConsole.setText("Conexão estável, favor selecionar um banco de dados.");
			}
		});
		btnTest.setBounds(12, 166, 243, 25);
		panelServer.add(btnTest);

		JPanel panelDatabase = new JPanel();
		tabbedPaneSource.addTab("Database", null, panelDatabase, null);

		// JScrollBar scrollBar = new JScrollBar();
		// scrollBar.setBounds(204, 38, 17, 146);
		// panelDatabase.add(scrollBar);

		listDb = new JList();
		listDb.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (listDb.getSelectedValue() != null) {
				postgresDb.connect(textFieldHost.getText(), textFieldPort.getText(), textFieldUsername.getText(),
						textFieldPassword.getText(), listDb.getSelectedValue().toString());
				textFieldConsole.setText("Conexão estável com o banco de dados " + listDb.getSelectedValue().toString());
				}
			}
		});
		
		panelDatabase.setLayout(null);
		listDb.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listDb.setVisibleRowCount(6);
		listDb.setBounds(23, 39, 239, 173);
//		JScrollPane listScroller = new JScrollPane();
//      listScroller.setViewportView(listDb);
        listDb.setLayoutOrientation(JList.VERTICAL);
//      panelDatabase.add(listScroller);		
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
		listTable.addListSelectionListener(new ListSelectionListener() {
			
			public void valueChanged(ListSelectionEvent e) {
				textFieldConsole.setText("Selecionado tabela "+ listTable.getSelectedValue().toString());
			}
		});
	
		listTable.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				try {
					listTable.setModel(postgresDb.listTables());
					textFieldConsole.setText("Selecione uma tabela");
					} catch (Exception t) {
				}
			}
		});
		listTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		try {
			listTable.setModel(postgresDb.listTables());
		} catch (Exception n) {

		}
		listTable.setBounds(12, 29, 264, 183);
		panelTables.add(listTable);

		JPanel panelColumns = new JPanel();
		tabbedPaneSource.addTab("Columns", null, panelColumns, null);
		panelColumns.setLayout(null);

		JLabel lblColumn = new JLabel("Column");
		lblColumn.setBounds(114, 5, 59, 17);
		panelColumns.add(lblColumn);
		lblColumn.setFont(new Font("Dialog", Font.BOLD, 14));

		final JList listColumns = new JList();
		listColumns.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				try {
					listColumns.setModel(postgresDb.listColumns(listTable.getSelectedValue().toString()));
					textFieldConsole.setText("Selecione as colunas desejadas para Primary Key e Value");
				} catch (Exception z) {
				}
			}
		});
		listColumns.setBounds(12, 21, 264, 191);
		panelColumns.add(listColumns);

		JLabel lblTables = new JLabel("Target");
		lblTables.setBounds(422, 0, 78, 21);
		lblTables.setFont(new Font("Dialog", Font.BOLD, 14));
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
		listPk.setBounds(58, 36, 199, 98);
		panelCollection.add(listPk);

		JLabel lblPk = new JLabel("PK");
		lblPk.setBounds(12, 37, 28, 15);
		panelCollection.add(lblPk);

		final JList listValue = new JList();
		listValue.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listValue.setBounds(58, 166, 199, 90);

		panelCollection.add(listValue);

		JLabel lblValue = new JLabel("Value");
		lblValue.setBounds(12, 167, 58, 15);
		panelCollection.add(lblValue);

		JButton btnGetSelectedColumnsPK = new JButton("Get selected row");
		btnGetSelectedColumnsPK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				DefaultListModel listPkTemp = new DefaultListModel();
				List<String> selecionadosPk = listColumns.getSelectedValuesList();
				for (String n : selecionadosPk) {
					listPkTemp.addElement(n);
				}
				listPk.setModel(listPkTemp);
				textFieldConsole.setText("Selecionado colunas "+ listPkTemp.toString() +" como Primary Keys");
			}
			
		});
		btnGetSelectedColumnsPK.setBounds(58, 134, 199, 25);
		panelCollection.add(btnGetSelectedColumnsPK);

		JButton btnGetSelectedColumnsVALUE = new JButton("Get selected row");
		btnGetSelectedColumnsVALUE.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultListModel listValueTemp = new DefaultListModel();
				List<String> selecionadosValues = listColumns.getSelectedValuesList();
				for (String n : selecionadosValues) {
					listValueTemp.addElement(n);
				}
				listValue.setModel(listValueTemp);
				textFieldConsole.setText("Selecionado colunas "+ listValueTemp.toString() +" como Values");
			}
		});
		btnGetSelectedColumnsVALUE.setBounds(58, 259, 199, 25);
		panelCollection.add(btnGetSelectedColumnsVALUE);

		textFieldPrefixo = new JTextField();
		textFieldPrefixo.setBounds(111, 9, 144, 26);
		panelCollection.add(textFieldPrefixo);
		textFieldPrefixo.setColumns(10);
		
		JPanel panelRedisConnector = new JPanel();
		panelRedisConnector.setLayout(null);
		tabbedPaneTarget.addTab("Redis Connector", null, panelRedisConnector, null);
		
		JLabel labelRedis = new JLabel("Redis");
		labelRedis.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				textFieldConsole.setText("Forneça detalhes para conexão com Redis");
			}
		});
		labelRedis.setFont(new Font("Dialog", Font.BOLD, 13));
		labelRedis.setBounds(12, 12, 101, 15);
		panelRedisConnector.add(labelRedis);
		
		JLabel labelRedisHost = new JLabel("Host: ");
		labelRedisHost.setBounds(12, 58, 50, 15);
		panelRedisConnector.add(labelRedisHost);
		
		JLabel labelRedisPort = new JLabel("Port: ");
		labelRedisPort.setBounds(12, 85, 70, 15);
		panelRedisConnector.add(labelRedisPort);
		
		JLabel labelRedisUser = new JLabel("Username: ");
		labelRedisUser.setBounds(12, 112, 101, 15);
		panelRedisConnector.add(labelRedisUser);
		
		JLabel labelRedisPassword = new JLabel("Password: ");
		labelRedisPassword.setBounds(12, 139, 101, 15);
		panelRedisConnector.add(labelRedisPassword);
		
		textFieldRedisHost = new JTextField();
		textFieldRedisHost.setColumns(10);
		textFieldRedisHost.setBounds(92, 56, 163, 19);
		panelRedisConnector.add(textFieldRedisHost);
		
		textFieldRedisPort = new JTextField();
		textFieldRedisPort.setColumns(10);
		textFieldRedisPort.setBounds(92, 83, 163, 19);
		panelRedisConnector.add(textFieldRedisPort);
		
		textFieldRedisUser = new JTextField();
		textFieldRedisUser.setColumns(10);
		textFieldRedisUser.setBounds(92, 112, 163, 19);
		panelRedisConnector.add(textFieldRedisUser);
		
		textFieldRedisPassword = new JTextField();
		textFieldRedisPassword.setColumns(10);
		textFieldRedisPassword.setBounds(92, 139, 163, 19);
		panelRedisConnector.add(textFieldRedisPassword);
		
		JButton buttonRedisConnect = new JButton("Connect");
		buttonRedisConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				redisDb.connect(textFieldHost.getText().toString());
				textFieldConsole.setText("Conexão estável com BD Redis");
			}			
		});
		
		buttonRedisConnect.setBounds(12, 251, 243, 25);
		panelRedisConnector.add(buttonRedisConnect);

		JPanel panelExecute = new JPanel();
		panelExecute.setBounds(0, 349, 465, 83);
		mainPanel.add(panelExecute);
		panelExecute.setLayout(null);

		JLabel lblExecute = new JLabel("Execute");
		lblExecute.setFont(new Font("Dialog", Font.BOLD, 14));
		lblExecute.setBounds(12, 3, 70, 20);
		panelExecute.add(lblExecute);

		JLabel lblStatus = new JLabel("Source SQL:");
		lblStatus.setBounds(12, 25, 85, 15);
		panelExecute.add(lblStatus);

		textFieldImport = new JTextField();
		textFieldImport.setBounds(12, 42, 448, 34);
		panelExecute.add(textFieldImport);
		textFieldImport.setColumns(10);

		JButton btnGenerateSql = new JButton("Generate sql");
		btnGenerateSql.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String tempPk = "";
				String tempPkVirgula = "";
				for (int x = 0; x < listPk.getModel().getSize(); x++) {
					if (x == 0) {
						tempPk = tempPk + listPk.getModel().getElementAt(x) + "::text";
						tempPkVirgula = tempPkVirgula + listPk.getModel().getElementAt(x);
					} else {
						tempPk = tempPk + "||'_'||" + listPk.getModel().getElementAt(x) + "::text";
						tempPkVirgula = tempPkVirgula + ", " + listPk.getModel().getElementAt(x);
					}
				}
				String tempValue = "";
				for (int x = 0; x < listValue.getModel().getSize(); x++) {
					if (x == 0) {
						tempValue = tempValue + listValue.getModel().getElementAt(x);
					} else {
						tempValue = tempValue + ", " + listValue.getModel().getElementAt(x);
					}
				}
				textFieldImport.setText("SELECT '" + textFieldPrefixo.getText() + "_'||" + tempPk
						+ ", (SELECT row_to_json(_) FROM (SELECT " + tempValue + ") AS _ ) AS VALUE FROM "
						+ listTable.getSelectedValue().toString()+" limit 5;");
				textFieldConsole.setText("Gerado comando SQL para importação de dados");
			}		
		});

		btnGenerateSql.setBounds(185, 7, 126, 25);
		panelExecute.add(btnGenerateSql);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.setBounds(472, 458, 117, 25);
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mainPanel.add(btnCancel);
		
		
		
		JLabel lblConsole = new JLabel("Console:");
		lblConsole.setBounds(12, 439, 70, 15);
		mainPanel.add(lblConsole);
		
				JButton btnImport = new JButton("Import");
				btnImport.setBounds(472, 395, 117, 25);
				mainPanel.add(btnImport);
				btnImport.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						 try {
						 postgresDb.getDataFromPSQL(textFieldImport.getText().toString(), textFieldRedisHost.getText().toString());
						 textFieldConsole.setText("Importado dados para Redis com sucesso");
						 } catch (SQLException e1) {
							 textFieldConsole.setText("Error SQL Exception");
						 } catch (JedisConnectionException e2) {
							 textFieldConsole.setText("Favor inserir um host valido para banco Redis");
						 }
						 
					}
				});
	}
}
