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
import java.util.LinkedList;
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
import br.ufsc.lisa.DINo.util.MySQLDB;
import br.ufsc.lisa.DINo.util.OracleDB;
import br.ufsc.lisa.DINo.util.PostgresDB;
import br.ufsc.lisa.DINo.util.RedisConnector;
import br.ufsc.lisa.DINo.util.RelationalDB;
import redis.clients.jedis.exceptions.JedisConnectionException;
import java.awt.Frame;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;

public class MaindApp {

	private JFrame frame;
	private JTextField textFieldHost;
	private JTextField textFieldPort;
	private JTextField textFieldUsername;
	private JPasswordField textFieldPassword;
	private RelationalDB rDb;
	private Connector targetDb;
	private JList<String> listDb;
	private JTextField textFieldPrefixo;
	private JTextPane textFieldConsole;
	private JTextPane textSQLPane;
	private JTextField textFieldTargetHost;
	private JTextField textFieldTargetPort;
	private JTextField textFieldTargetPassword;
	private JPanel panelDatabase;
	private JList<String> listColumns;
	private JButton btnGenerateSql;
	private JButton btnGetSelectedColumnsPK;
	private JList<String> listValue;
	private JList<String> listPk;
	private JList<String> listTable;
	private JButton btnGetSelectedColumnsVALUE;
	private JButton btnImport;
	private JComboBox<Connector> comboBoxTargets;
	private JTextField textFieldTargetUser;
	private JTextField textFieldTargetDbName;
	private JProgressBar progressBar;
	private MaindApp app;
	private Double progressValue;
	private final ButtonGroup buttonGroup = new ButtonGroup();

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

	private String exportedDbName() {
		return (String) this.listDb.getSelectedValue();
	}

	public List<String> exportedPks() {
		List<String> result = new LinkedList<String>();

		for (int i = 0; i < this.listPk.getModel().getSize(); i++) {
			result.add((String) this.listPk.getModel().getElementAt(i));
		}

		return result;
	}

	public List<String> exportedValues() {
		List<String> result = new LinkedList<String>();

		for (int i = 0; i < this.listValue.getModel().getSize(); i++) {
			result.add((String) this.listValue.getModel().getElementAt(i));
		}

		return result;
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
		rDb = null;
		targetDb = null;
		this.app = this;
	}

	private void initialize() throws ClassNotFoundException, SQLException {
		frame = new JFrame();
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setBounds(100, 100, 660, 654);
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

		JPanel panelServer = new JPanel();
		panelServer.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Source Connection",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
		panelServer.setBounds(12, 12, 491, 183);
		mainPanel.add(panelServer);
		panelServer.setLayout(null);

		JLabel lblHost = new JLabel("Host: ");
		lblHost.setBounds(32, 38, 50, 15);
		panelServer.add(lblHost);

		JLabel lblPort = new JLabel("Port: ");
		lblPort.setBounds(32, 65, 70, 15);
		panelServer.add(lblPort);

		JLabel lblUsername = new JLabel("Username: ");
		lblUsername.setBounds(32, 92, 101, 15);
		panelServer.add(lblUsername);

		JLabel lblPassword = new JLabel("Password: ");
		lblPassword.setBounds(32, 119, 101, 15);
		panelServer.add(lblPassword);

		textFieldHost = new JTextField();
		textFieldHost.setText("localhost");
		textFieldHost.setBounds(112, 36, 163, 19);
		panelServer.add(textFieldHost);
		textFieldHost.setColumns(10);

		textFieldPort = new JTextField();
		textFieldPort.setText("5432");
		textFieldPort.setBounds(112, 63, 163, 19);
		panelServer.add(textFieldPort);
		textFieldPort.setColumns(10);

		textFieldUsername = new JTextField();
		textFieldUsername.setText("postgres");
		textFieldUsername.setBounds(112, 92, 163, 19);
		panelServer.add(textFieldUsername);
		textFieldUsername.setColumns(10);
		
		
		JLabel lblAvaliableSources = new JLabel("Avaliable Sources");
		lblAvaliableSources.setBounds(338, 23, 126, 15);
		panelServer.add(lblAvaliableSources);
		
		JRadioButton rdbtnPostgrresql = new JRadioButton("PostgrreSQL");
		buttonGroup.add(rdbtnPostgrresql);
		rdbtnPostgrresql.setSelected(true);
		rdbtnPostgrresql.setBounds(338, 46, 144, 23);
		panelServer.add(rdbtnPostgrresql);
		
		JRadioButton rdbtnMysql = new JRadioButton("MySQL");
		buttonGroup.add(rdbtnMysql);
		rdbtnMysql.setBounds(338, 69, 144, 23);
		panelServer.add(rdbtnMysql);
		
		JRadioButton rdbtnOracle = new JRadioButton("Oracle");
		buttonGroup.add(rdbtnOracle);
		rdbtnOracle.setBounds(338, 92, 144, 23);
		panelServer.add(rdbtnOracle);

		textFieldPassword = new JPasswordField();
		textFieldPassword.setBounds(112, 119, 163, 19);
		panelServer.add(textFieldPassword);
		textFieldPassword.setColumns(10);

		JButton btnTest = new JButton("Connect");
		btnTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (rdbtnOracle.isSelected()) {
					rDb = new OracleDB();
				}else if (rdbtnMysql.isSelected()) {
					rDb = new MySQLDB();
				}else {
					rDb = new PostgresDB();	
				}
				rDb.connect(textFieldHost.getText(), textFieldPort.getText(), textFieldUsername.getText(),
						String.valueOf(textFieldPassword.getPassword()), "?");
				try {
					listDb.setModel(listToListModel(rDb.listDatabases()));
					// tabbedPaneSource.setEnabledAt(1, true);
					// tabbedPaneSource.setSelectedIndex(1);
					panelDatabase.setEnabled(true);
					writeLog("[info] Conexão estável, favor selecionar um banco de dados.");
				} catch (Exception s) {
					writeLog("[erro] Banco não encontrado");
				}
			}
		});
		btnTest.setBounds(32, 146, 243, 25);
		panelServer.add(btnTest);

		JPanel panelTables = new JPanel();
		panelTables
				.setBorder(new TitledBorder(null, "Select Table", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelTables.setBounds(254, 203, 190, 296);
		mainPanel.add(panelTables);
		panelTables.setLayout(null);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(12, 24, 163, 260);
		panelTables.add(scrollPane_1);

		listTable = new JList<String>();
		listTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (listTable.getSelectedValue() == null)
					return;
				try {
					listColumns
							.setModel(listToListModel(rDb.listColumns(listTable.getSelectedValue().toString())));
					writeLog("[action] Selecione as colunas desejadas para Primary Key e Value");
					// tabbedPaneSource.setEnabledAt(3, true);
					// tabbedPaneSource.setSelectedIndex(3);
					textFieldPrefixo.setEnabled(true);
					listPk.setEnabled(true);
					btnGetSelectedColumnsPK.setEnabled(true);
					listValue.setEnabled(true);
					btnGetSelectedColumnsVALUE.setEnabled(true);
					btnGenerateSql.setEnabled(false);

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
				if (listTable.getSelectedValue() != null)
					writeLog("[info] Selecionada a tabela " + listTable.getSelectedValue().toString());
			}
		});

		listTable.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				// try {
				// listTable.setModel(listToListModel(postgresDb.listTables()));
				// writeLog("[action] Selecione uma tabela");
				// } catch (Exception t) {
				// }
			}
		});
		listTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		panelDatabase = new JPanel();
		panelDatabase.setBorder(
				new TitledBorder(null, "Select a database", TitledBorder.LEFT, TitledBorder.TOP, null, null));
		panelDatabase.setBounds(12, 203, 230, 296);
		mainPanel.add(panelDatabase);

		panelDatabase.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(23, 22, 195, 262);
		panelDatabase.add(scrollPane);

		// JScrollBar scrollBar = new JScrollBar();
		// scrollBar.setBounds(204, 38, 17, 146);
		// panelDatabase.add(scrollBar);

		listDb = new JList<String>();
		scrollPane.setViewportView(listDb);
		listDb.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (listDb.getSelectedValue() != null) {
					rDb.connect(textFieldHost.getText(), textFieldPort.getText(), textFieldUsername.getText(),
							String.valueOf(textFieldPassword.getPassword()), listDb.getSelectedValue().toString());

					writeLog("[info] Conexão estável com o banco de dados " + listDb.getSelectedValue().toString());

					try {
						listTable.setModel(listToListModel(rDb.listTables()));
						writeLog("[action] Selecione uma tabela");
					} catch (Exception t) {
						writeLog("[ERROR] Problema ao obter as colunas!");
					}
				}
			}
		});
		listDb.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listDb.setVisibleRowCount(6);
		listDb.setLayoutOrientation(JList.VERTICAL_WRAP);

		JScrollPane scrollPane_5 = new JScrollPane();
		scrollPane_5.setBounds(737, 452, 617, 215);
		mainPanel.add(scrollPane_5);

		textFieldConsole = new JTextPane();
		scrollPane_5.setViewportView(textFieldConsole);
		DefaultCaret caret = (DefaultCaret) textFieldConsole.getCaret();
		caret.setUpdatePolicy(DefaultCaret.UPDATE_WHEN_ON_EDT);
		textFieldConsole.setEditable(false);
		try {
		} catch (Exception n) {

		}

		JPanel panelTargetNoSQL = new JPanel();
		panelTargetNoSQL
				.setBorder(new TitledBorder(null, "NoSQL Target", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelTargetNoSQL.setBounds(726, 26, 313, 323);
		mainPanel.add(panelTargetNoSQL);
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

		JLabel labelRedis = new JLabel("NoSQL");
		labelRedis.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				writeLog("[action] Forneça detalhes para conexão com Redis");
			}
		});
		labelRedis.setFont(new Font("Dialog", Font.BOLD, 13));
		labelRedis.setBounds(12, 31, 101, 15);
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
					btnGenerateSql.setEnabled(true);
					textSQLPane.setEnabled(true);
					writeLog("[info] Conexão estável com " + targetDb.toString());
				} else {
					writeLog("[erro] Conexão com " + targetDb.toString() + " falhou!");
				}
			}
		});

		buttonRedisConnect.setBounds(12, 251, 243, 25);
		panelTargetNoSQL.add(buttonRedisConnect);

		comboBoxTargets = new JComboBox<>();
		comboBoxTargets.addItem(new RedisConnector());
		comboBoxTargets.addItem(new MongoConnector());
		comboBoxTargets.addItem(new CassadraConnector());
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
						textFieldTargetDbName.setText(exportedDbName());
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
						textFieldTargetPort.setText("9042");
						textFieldTargetPassword.setText("");
						textFieldTargetDbName.setText(exportedDbName());
						textFieldTargetUser.setText("");
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
		comboBoxTargets.setBounds(72, 26, 183, 24);
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

		JPanel panelCollection = new JPanel();
		panelCollection
				.setBorder(new TitledBorder(null, "Collection", TitledBorder.LEFT, TitledBorder.TOP, null, null));
		panelCollection.setBounds(1066, 26, 288, 319);
		mainPanel.add(panelCollection);
		panelCollection.setLayout(null);

		JLabel lblProperties = new JLabel("Prefixo chave");
		lblProperties.setFont(new Font("Dialog", Font.BOLD, 12));
		lblProperties.setBounds(12, 9, 103, 26);
		panelCollection.add(lblProperties);

		JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setBounds(58, 36, 199, 90);
		panelCollection.add(scrollPane_3);

		listPk = new JList<String>();
		listPk.setEnabled(false);
		scrollPane_3.setViewportView(listPk);
		listPk.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JLabel lblPk = new JLabel("PK");
		lblPk.setBounds(12, 37, 28, 15);
		panelCollection.add(lblPk);

		JScrollPane scrollPane_4 = new JScrollPane();
		scrollPane_4.setBounds(58, 166, 199, 82);
		panelCollection.add(scrollPane_4);

		listValue = new JList<String>();
		listValue.setEnabled(false);
		scrollPane_4.setViewportView(listValue);
		listValue.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JLabel lblValue = new JLabel("Value");
		lblValue.setBounds(12, 200, 58, 15);
		panelCollection.add(lblValue);

		btnGetSelectedColumnsPK = new JButton("Get selected row");
		btnGetSelectedColumnsPK.setEnabled(false);
		btnGetSelectedColumnsPK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				DefaultListModel<String> listPkTemp = new DefaultListModel<String>();
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
				DefaultListModel<String> listValueTemp = new DefaultListModel<String>();
				List<String> selecionadosValues = listColumns.getSelectedValuesList();
				for (String n : selecionadosValues) {
					listValueTemp.addElement(n);
				}
				listValue.setModel(listValueTemp);
				writeLog("[info] Selecionadas as colunas " + listValueTemp.toString() + " como Values");
			}
		});
		btnGetSelectedColumnsVALUE.setBounds(58, 282, 199, 25);
		panelCollection.add(btnGetSelectedColumnsVALUE);

		textFieldPrefixo = new JTextField();
		textFieldPrefixo.setEnabled(false);
		textFieldPrefixo.setBounds(111, 9, 144, 26);
		panelCollection.add(textFieldPrefixo);
		textFieldPrefixo.setColumns(10);
		listColumns = new JList<String>();
		// listTable.setModel(postgresDb.listTables());

		JPanel panelColumns = new JPanel();
		panelColumns.setBorder(
				new TitledBorder(null, "Select Columns", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelColumns.setBounds(451, 203, 179, 296);
		mainPanel.add(panelColumns);
		panelColumns.setLayout(null);

		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(12, 23, 155, 261);
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

		JPanel panelExecute = new JPanel();
		panelExecute.setBorder(
				new TitledBorder(null, "SQL Source (Generetaded)", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelExecute.setBounds(12, 554, 604, 153);
		mainPanel.add(panelExecute);
		panelExecute.setLayout(null);
		
		JScrollPane scrollPane_6 = new JScrollPane();
		scrollPane_6.setBounds(12, 26, 580, 115);
		panelExecute.add(scrollPane_6);
		
		textSQLPane = new JTextPane();
		scrollPane_6.setViewportView(textSQLPane);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.setEnabled(false);
		btnCancel.setBounds(1140, 374, 117, 25);
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mainPanel.add(btnCancel);

		JLabel lblConsole = new JLabel("LOGs");
		lblConsole.setBounds(1035, 430, 40, 15);
		mainPanel.add(lblConsole);

		btnImport = new JButton("Import");
		btnImport.setEnabled(false);
		btnImport.setBounds(865, 374, 117, 25);
		mainPanel.add(btnImport);

		progressBar = new JProgressBar();
		progressBar.setBounds(737, 681, 617, 26);
		mainPanel.add(progressBar);

		btnGenerateSql = new JButton("Generate sql");
		btnGenerateSql.setBounds(226, 517, 126, 25);
		mainPanel.add(btnGenerateSql);
		btnGenerateSql.setEnabled(false);
		btnGenerateSql.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String tempPk = "";
				String tempPkVirgula = "";

				if (comboBoxTargets.getSelectedItem() instanceof CassadraConnector) {

					for (int x = 0; x < listPk.getModel().getSize(); x++) {
						tempPkVirgula += listPk.getModel().getElementAt(x) + ", ";
					}
					for (int x = 0; x < listValue.getModel().getSize() - 1; x++) {
						tempPkVirgula += listValue.getModel().getElementAt(x) + ", ";
					}
					tempPkVirgula += listValue.getModel().getElementAt(listValue.getModel().getSize() - 1);

					textSQLPane
							.setText("SELECT " + tempPkVirgula + " FROM " + listTable.getSelectedValue().toString());

				} else {

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
					textSQLPane.setText("SELECT '" + textFieldPrefixo.getText() + "_'||" + tempPk
							+ ", (SELECT row_to_json(_) FROM (SELECT " + tempValue + ") AS _ ) AS VALUE FROM "
							+ listTable.getSelectedValue().toString());
				}
				writeLog("[info] Gerado comando SQL para importação de dados");
			}
		});
		btnImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					progressValue = 0.;
					updateProgressBar(0.);
					importBloqued(true);
					writeLog("[info] Iniciando a importação para o " + targetDb.toString());
					Importer impo = new Importer(rDb, targetDb);
					impo.importData(textSQLPane.getText().toString(), listTable.getSelectedValue().toString(), app);
				} catch (SQLException e1) {
					writeLog("[erro] Error SQL Exception");
				} catch (JedisConnectionException e2) {
					writeLog("[erro] Favor inserir um host valido para banco Redis");
				}

			}
		});
	}
}
