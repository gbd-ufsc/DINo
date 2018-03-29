package br.ufsc.lisa.DINo.views;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.DefaultCaret;

import br.ufsc.lisa.DINo.presenter.Importer;
import br.ufsc.lisa.DINo.util.CassadraConnector;
import br.ufsc.lisa.DINo.util.Connector;
import br.ufsc.lisa.DINo.util.MongoConnector;
import br.ufsc.lisa.DINo.util.PostgresDB;
import br.ufsc.lisa.DINo.util.RedisConnector;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class MaindApp {

	private JFrame frame;
	private JTextField textFieldHost;
	private JTextField textFieldPort;
	private JTextField textFieldUsername;
	private JPasswordField textFieldPassword;
	private JTextField textFieldImport;
	private PostgresDB postgresDb;
	private Connector targetDb;
	private JList listDb;
	private String tempUrl;
	private JTextField textFieldPrefixo;
	private JTextPane textFieldConsole;
	private JTextField textFieldTargetHost;
	private JTextField textFieldTargetPort;
	private JTextField textFieldTargetPassword;
	private JPanel panelDatabase;
	private JList listColumns;
	private JTabbedPane tabbedPaneTarget;
	private JButton btnGenerateSql;
	private JButton btnGetSelectedColumnsPK;
	private JList listValue;
	private JList listPk;
	private JList listTable;
	private JButton btnGetSelectedColumnsVALUE;
	private JButton btnImport;
	private JComboBox<Connector> comboBoxTargets;
	private JTextField textFieldTargetUser;
	private JTextField textFieldTargetDbName;
	private JProgressBar progressBar;
	private MaindApp app;
	private Double progressValue;

	public DefaultListModel<String> listToListModel(List<String> itens) {
		DefaultListModel<String> newList = new DefaultListModel<String>();
		itens.forEach(item -> {
			newList.addElement(item);
		});
		return newList;
	}

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

	public void writeLog(String message) {
		SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");
		this.textFieldConsole
				.setText(this.textFieldConsole.getText() + simpleTimeFormat.format(new Date()) + "\t" + message + "\n");
		
	}
	
	public String exportedTableName() {
		return (String) this.listTable.getSelectedValue();
	}
	
	public void importBloqued(boolean value) {
		this.btnImport.setEnabled(!value);
	}

	public void updateProgressBar(Double value) {
		progressBar.setStringPainted(true);
		if (value < 0) {
			this.progressBar.setValue(100);
			progressBar.setString("100 %");
		} else if (value.equals(0)) {
			this.progressBar.setValue(0);
			progressBar.setString("0 %");
		} else {
			this.progressValue += value / 2;
			if (this.progressValue > 0) {
				Double newValue = this.progressValue * 100;
				this.progressBar.setValue(newValue.intValue());
				progressBar.setString(String.format("%.2f", newValue) + " %");
			}
		}
		this.progressBar.repaint();
	}

	public MaindApp() throws ClassNotFoundException, SQLException {
		initialize();
		postgresDb = null;
		targetDb = null;
		this.app = this;
	}

	private void initialize() throws ClassNotFoundException, SQLException {
		frame = new JFrame();
		frame.setBounds(100, 100, 660, 654);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);

		panelDatabase = new JPanel();
		listColumns = new JList();
		tabbedPaneTarget = new JTabbedPane(JTabbedPane.TOP);

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

		final JTabbedPane tabbedPaneSource = new JTabbedPane(JTabbedPane.TOP);
		tabbedPaneSource.setBounds(12, 22, 313, 323);
		mainPanel.add(tabbedPaneSource);

		JPanel panelServer = new JPanel();
		tabbedPaneSource.addTab("Server", null, panelServer, null);
		panelServer.setLayout(null);

		JLabel lblConnection = new JLabel("Connection Postgres");
		lblConnection.setFont(new Font("Dialog", Font.BOLD, 13));
		lblConnection.setBounds(22, 12, 153, 17);
		panelServer.add(lblConnection);

		JLabel lblHost = new JLabel("Host: ");
		lblHost.setBounds(22, 58, 50, 15);
		panelServer.add(lblHost);

		JLabel lblPort = new JLabel("Port: ");
		lblPort.setBounds(22, 85, 70, 15);
		panelServer.add(lblPort);

		JLabel lblUsername = new JLabel("Username: ");
		lblUsername.setBounds(22, 112, 101, 15);
		panelServer.add(lblUsername);

		JLabel lblPassword = new JLabel("Password: ");
		lblPassword.setBounds(22, 139, 101, 15);
		panelServer.add(lblPassword);

		textFieldHost = new JTextField();
		textFieldHost.setText("localhost");
		textFieldHost.setBounds(102, 56, 163, 19);
		panelServer.add(textFieldHost);
		textFieldHost.setColumns(10);

		textFieldPort = new JTextField();
		textFieldPort.setText("5432");
		textFieldPort.setBounds(102, 83, 163, 19);
		panelServer.add(textFieldPort);
		textFieldPort.setColumns(10);

		textFieldUsername = new JTextField();
		textFieldUsername.setText("postgres");
		textFieldUsername.setBounds(102, 112, 163, 19);
		panelServer.add(textFieldUsername);
		textFieldUsername.setColumns(10);

		textFieldPassword = new JPasswordField();
		textFieldPassword.setBounds(102, 139, 163, 19);
		panelServer.add(textFieldPassword);
		textFieldPassword.setColumns(10);

		JScrollPane scrollPane_5 = new JScrollPane();
		scrollPane_5.setBounds(12, 501, 617, 116);
		mainPanel.add(scrollPane_5);

		textFieldConsole = new JTextPane();
		scrollPane_5.setViewportView(textFieldConsole);
		DefaultCaret caret = (DefaultCaret) textFieldConsole.getCaret();
        caret.setUpdatePolicy(DefaultCaret.UPDATE_WHEN_ON_EDT);
		textFieldConsole.setEditable(false);
		

		JButton btnTest = new JButton("Connect");
		btnTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				postgresDb = new PostgresDB();
				postgresDb.connect(textFieldHost.getText(), textFieldPort.getText(), textFieldUsername.getText(),
						String.valueOf(textFieldPassword.getPassword()), "?");
				try {
					listDb.setModel(listToListModel(postgresDb.listDatabases()));
					tabbedPaneSource.setEnabledAt(1, true);
					tabbedPaneSource.setSelectedIndex(1);
					panelDatabase.setEnabled(true);
					writeLog("[info] Conexão estável, favor selecionar um banco de dados.");
				} catch (Exception s) {
					writeLog("[erro] Banco não encontrado");
				}
			}
		});
		btnTest.setBounds(22, 166, 243, 25);
		panelServer.add(btnTest);

		tabbedPaneSource.addTab("Database", null, panelDatabase, null);
		tabbedPaneSource.setEnabledAt(1, false);

		panelDatabase.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(23, 39, 239, 234);
		panelDatabase.add(scrollPane);

		// JScrollBar scrollBar = new JScrollBar();
		// scrollBar.setBounds(204, 38, 17, 146);
		// panelDatabase.add(scrollBar);

		listDb = new JList();
		scrollPane.setViewportView(listDb);
		listDb.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (listDb.getSelectedValue() != null) {
					postgresDb.connect(textFieldHost.getText(), textFieldPort.getText(), textFieldUsername.getText(),
							String.valueOf(textFieldPassword.getPassword()), listDb.getSelectedValue().toString());
					tabbedPaneSource.setEnabledAt(2, true);
					tabbedPaneSource.setSelectedIndex(2);
					writeLog("[info] Conexão estável com o banco de dados " + listDb.getSelectedValue().toString());
				}
			}
		});
		listDb.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listDb.setVisibleRowCount(6);
		listDb.setLayoutOrientation(JList.VERTICAL);

		JLabel lblSelectDb = new JLabel("Select a database");
		lblSelectDb.setBounds(74, 12, 129, 15);
		panelDatabase.add(lblSelectDb);

		JPanel panelTables = new JPanel();

		tabbedPaneSource.addTab("Tables", null, panelTables, null);
		tabbedPaneSource.setEnabledAt(2, false);
		panelTables.setLayout(null);

		JLabel lblTables_1 = new JLabel("Tables");
		lblTables_1.setFont(new Font("Dialog", Font.BOLD, 14));
		lblTables_1.setBounds(111, 12, 70, 15);
		panelTables.add(lblTables_1);
		try {
		} catch (Exception n) {

		}

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(24, 29, 252, 241);
		panelTables.add(scrollPane_1);

		listTable = new JList();
		listTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					listColumns
							.setModel(listToListModel(postgresDb.listColumns(listTable.getSelectedValue().toString())));
					writeLog("[action] Selecione as colunas desejadas para Primary Key e Value");
					tabbedPaneSource.setEnabledAt(3, true);
					tabbedPaneSource.setSelectedIndex(3);
					tabbedPaneTarget.setEnabled(true);
					textFieldPrefixo.setEnabled(true);
					listPk.setEnabled(true);
					btnGetSelectedColumnsPK.setEnabled(true);
					listValue.setEnabled(true);
					btnGetSelectedColumnsVALUE.setEnabled(true);
					btnGenerateSql.setEnabled(true);

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		scrollPane_1.setViewportView(listTable);
		listTable.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				writeLog("[info] Selecionada a tabela " + listTable.getSelectedValue().toString());
			}
		});

		listTable.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				try {
					listTable.setModel(listToListModel(postgresDb.listTables()));
					writeLog("[action] Selecione uma tabela");
				} catch (Exception t) {
				}
			}
		});
		listTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// listTable.setModel(postgresDb.listTables());

		JPanel panelColumns = new JPanel();
		tabbedPaneSource.addTab("Columns", null, panelColumns, null);
		tabbedPaneSource.setEnabledAt(3, false);
		panelColumns.setLayout(null);

		JLabel lblColumn = new JLabel("Column");
		lblColumn.setBounds(114, 5, 79, 17);
		panelColumns.add(lblColumn);
		lblColumn.setFont(new Font("Dialog", Font.BOLD, 14));

		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(25, 21, 251, 242);
		panelColumns.add(scrollPane_2);

		scrollPane_2.setViewportView(listColumns);
		listColumns.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				// try {
				// listColumns.setModel(listToListModel(postgresDb.listColumns(listTable.getSelectedValue().toString())));
				// writeLog("Selecione as colunas desejadas para Primary Key e Value");
				// } catch (Exception z) {
				// }
			}
		});

		JLabel lblTables = new JLabel("Target");
		lblTables.setBounds(484, -3, 78, 21);
		lblTables.setFont(new Font("Dialog", Font.BOLD, 14));
		mainPanel.add(lblTables);

		tabbedPaneTarget.setEnabled(false);
		tabbedPaneTarget.setBounds(348, 22, 293, 323);
		mainPanel.add(tabbedPaneTarget);

		JPanel panelCollection = new JPanel();
		tabbedPaneTarget.addTab("Collection", null, panelCollection, null);
		tabbedPaneTarget.setEnabledAt(0, true);
		panelCollection.setLayout(null);

		JLabel lblProperties = new JLabel("Prefixo chave");
		lblProperties.setFont(new Font("Dialog", Font.BOLD, 12));
		lblProperties.setBounds(12, 9, 103, 26);
		panelCollection.add(lblProperties);

		JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setBounds(58, 36, 199, 90);
		panelCollection.add(scrollPane_3);

		listPk = new JList();
		listPk.setEnabled(false);
		scrollPane_3.setViewportView(listPk);
		listPk.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JLabel lblPk = new JLabel("PK");
		lblPk.setBounds(12, 37, 28, 15);
		panelCollection.add(lblPk);

		JScrollPane scrollPane_4 = new JScrollPane();
		scrollPane_4.setBounds(58, 166, 199, 82);
		panelCollection.add(scrollPane_4);

		listValue = new JList();
		listValue.setEnabled(false);
		scrollPane_4.setViewportView(listValue);
		listValue.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JLabel lblValue = new JLabel("Value");
		lblValue.setBounds(12, 167, 58, 15);
		panelCollection.add(lblValue);

		btnGetSelectedColumnsPK = new JButton("Get selected row");
		btnGetSelectedColumnsPK.setEnabled(false);
		btnGetSelectedColumnsPK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				DefaultListModel listPkTemp = new DefaultListModel();
				List<String> selecionadosPk = listColumns.getSelectedValuesList();
				for (String n : selecionadosPk) {
					listPkTemp.addElement(n);
				}
				listPk.setModel(listPkTemp);
				writeLog("[info] Selecionadas a colunas " + listPkTemp.toString() + " como Primary Keys");
			}

		});
		btnGetSelectedColumnsPK.setBounds(58, 134, 199, 25);
		panelCollection.add(btnGetSelectedColumnsPK);

		btnGetSelectedColumnsVALUE = new JButton("Get selected row");
		btnGetSelectedColumnsVALUE.setEnabled(false);
		btnGetSelectedColumnsVALUE.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DefaultListModel listValueTemp = new DefaultListModel();
				List<String> selecionadosValues = listColumns.getSelectedValuesList();
				for (String n : selecionadosValues) {
					listValueTemp.addElement(n);
				}
				listValue.setModel(listValueTemp);
				writeLog("[info] Selecionadas as colunas " + listValueTemp.toString() + " como Values");
			}
		});
		btnGetSelectedColumnsVALUE.setBounds(58, 259, 199, 25);
		panelCollection.add(btnGetSelectedColumnsVALUE);

		textFieldPrefixo = new JTextField();
		textFieldPrefixo.setEnabled(false);
		textFieldPrefixo.setBounds(111, 9, 144, 26);
		panelCollection.add(textFieldPrefixo);
		textFieldPrefixo.setColumns(10);

		JPanel panelTargetNoSQL = new JPanel();
		panelTargetNoSQL.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent arg0) {
				comboBoxTargets.removeAllItems();
				comboBoxTargets.addItem(new RedisConnector());
				comboBoxTargets.addItem(new MongoConnector());
				comboBoxTargets.addItem(new CassadraConnector());
			}
		});
		panelTargetNoSQL.setLayout(null);
		tabbedPaneTarget.addTab("NoSQL DB", null, panelTargetNoSQL, null);
		tabbedPaneTarget.setEnabledAt(1, true);

		JLabel labelRedis = new JLabel("NoSQL");
		labelRedis.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				writeLog("[action] Forneça detalhes para conexão com Redis");
			}
		});
		labelRedis.setFont(new Font("Dialog", Font.BOLD, 13));
		labelRedis.setBounds(12, 12, 101, 15);
		panelTargetNoSQL.add(labelRedis);

		JLabel labelRedisHost = new JLabel("Host: ");
		labelRedisHost.setBounds(12, 58, 50, 15);
		panelTargetNoSQL.add(labelRedisHost);

		JLabel labelRedisPort = new JLabel("Port: ");
		labelRedisPort.setBounds(12, 85, 70, 15);
		panelTargetNoSQL.add(labelRedisPort);

		JLabel labelRedisPassword = new JLabel("Password: ");
		labelRedisPassword.setBounds(12, 117, 101, 15);
		panelTargetNoSQL.add(labelRedisPassword);

		textFieldTargetHost = new JTextField();
		textFieldTargetHost.setText("localhost");
		textFieldTargetHost.setColumns(10);
		textFieldTargetHost.setBounds(92, 56, 163, 19);
		panelTargetNoSQL.add(textFieldTargetHost);

		textFieldTargetPort = new JTextField();
		textFieldTargetPort.setText("6379");
		textFieldTargetPort.setColumns(10);
		textFieldTargetPort.setBounds(92, 83, 163, 19);
		panelTargetNoSQL.add(textFieldTargetPort);

		textFieldTargetPassword = new JTextField();
		textFieldTargetPassword.setColumns(10);
		textFieldTargetPassword.setBounds(92, 117, 163, 19);
		panelTargetNoSQL.add(textFieldTargetPassword);

		JButton buttonRedisConnect = new JButton("Connect");
		buttonRedisConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				targetDb = (Connector) comboBoxTargets.getSelectedItem();

				if (targetDb.connect(textFieldTargetHost.getText().toString(), textFieldTargetPort.getText(),
						textFieldTargetUser.getText(), textFieldTargetPassword.getText(),
						textFieldTargetDbName.getText())) {
					btnImport.setEnabled(true);
					textFieldImport.setEnabled(true);
					writeLog("[info] Conexão estável com " + targetDb.toString());
				} else {
					writeLog("[erro] Conexão com " + targetDb.toString() + " falhou!");
				}
			}
		});

		buttonRedisConnect.setBounds(12, 251, 243, 25);
		panelTargetNoSQL.add(buttonRedisConnect);

		comboBoxTargets = new JComboBox();
		comboBoxTargets.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textFieldTargetHost.setText("");
				textFieldTargetPort.setText("");
				textFieldTargetPassword.setText("");
				textFieldTargetDbName.setText("");
				textFieldTargetUser.setText("");
				
				if (comboBoxTargets.getItemCount() > 0) {
					if (comboBoxTargets.getSelectedItem() instanceof MongoConnector) {
						textFieldTargetHost.setEnabled(true);
						textFieldTargetPort.setEnabled(true);
						textFieldTargetPassword.setEnabled(true);
						textFieldTargetDbName.setEnabled(true);
						textFieldTargetUser.setEnabled(true);
						
						textFieldTargetHost.setText("127.0.0.1");
						textFieldTargetPort.setText("27017");
						textFieldTargetPassword.setText("");
						textFieldTargetDbName.setText("test");
						textFieldTargetUser.setText("");
						
					} else if (comboBoxTargets.getSelectedItem() instanceof RedisConnector) {
						textFieldTargetHost.setEnabled(true);
						textFieldTargetPort.setEnabled(true);
						textFieldTargetPassword.setEnabled(true);
						textFieldTargetDbName.setEnabled(false);
						textFieldTargetUser.setEnabled(false);
						
						textFieldTargetHost.setText("localhost");
						textFieldTargetPort.setText("6379");
						textFieldTargetPassword.setText("");
						textFieldTargetDbName.setText("");
						textFieldTargetUser.setText("");
					} else if (comboBoxTargets.getSelectedItem() instanceof CassadraConnector) {
						textFieldTargetHost.setEnabled(true);
						textFieldTargetPort.setEnabled(true);
						textFieldTargetPassword.setEnabled(true);
						textFieldTargetDbName.setEnabled(true);
						textFieldTargetUser.setEnabled(true);
						
						textFieldTargetHost.setText("localhost");
						textFieldTargetPort.setText("9000");
						textFieldTargetPassword.setText("");
						textFieldTargetDbName.setText("");
						textFieldTargetUser.setText("system");
					}
				} else {
					textFieldTargetHost.setEnabled(false);
					textFieldTargetPort.setEnabled(false);
					textFieldTargetPassword.setEnabled(false);
					textFieldTargetDbName.setEnabled(false);
					textFieldTargetUser.setEnabled(false);
				}
			}
		});
		comboBoxTargets.setBounds(72, 7, 183, 24);
		panelTargetNoSQL.add(comboBoxTargets);

		JLabel lblDbName = new JLabel("User");
		lblDbName.setBounds(12, 151, 101, 15);
		panelTargetNoSQL.add(lblDbName);

		textFieldTargetUser = new JTextField();
		textFieldTargetUser.setColumns(10);
		textFieldTargetUser.setBounds(92, 151, 163, 19);
		panelTargetNoSQL.add(textFieldTargetUser);

		JLabel label = new JLabel("DB name");
		label.setBounds(12, 186, 101, 15);
		panelTargetNoSQL.add(label);

		textFieldTargetDbName = new JTextField();
		textFieldTargetDbName.setColumns(10);
		textFieldTargetDbName.setBounds(92, 186, 163, 19);
		panelTargetNoSQL.add(textFieldTargetDbName);

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
		textFieldImport.setEnabled(false);
		textFieldImport.setBounds(12, 42, 448, 34);
		panelExecute.add(textFieldImport);
		textFieldImport.setColumns(10);

		btnGenerateSql = new JButton("Generate sql");
		btnGenerateSql.setEnabled(false);
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
						+ listTable.getSelectedValue().toString());
				writeLog("[info] Gerado comando SQL para importação de dados");
			}
		});

		btnGenerateSql.setBounds(185, 7, 126, 25);
		panelExecute.add(btnGenerateSql);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.setEnabled(false);
		btnCancel.setBounds(477, 394, 117, 25);
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mainPanel.add(btnCancel);

		JLabel lblConsole = new JLabel("LOGs");
		lblConsole.setBounds(285, 482, 40, 15);
		mainPanel.add(lblConsole);

		btnImport = new JButton("Import");
		btnImport.setEnabled(false);
		btnImport.setBounds(477, 357, 117, 25);
		mainPanel.add(btnImport);

		progressBar = new JProgressBar();
		progressBar.setBounds(12, 444, 617, 26);
		mainPanel.add(progressBar);
		btnImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					progressValue = 0.;
					updateProgressBar(0.);
					importBloqued(true);
					writeLog("[info] Iniciando a importação para o " + targetDb.toString());
					Importer impo = new Importer(postgresDb, targetDb);
					impo.importData(textFieldImport.getText().toString(), listTable.getSelectedValue().toString(), app);
				} catch (SQLException e1) {
					writeLog("[erro] Error SQL Exception");
				} catch (JedisConnectionException e2) {
					writeLog("[erro] Favor inserir um host valido para banco Redis");
				}

			}
		});
	}
}
