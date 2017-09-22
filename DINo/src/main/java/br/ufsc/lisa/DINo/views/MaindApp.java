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

public class MaindApp {

	private JFrame frame;
	private final JComboBox comboBox = new JComboBox();

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
		comboBox.setBounds(161, 210, 158, 24);
		comboBox.setEditable(true);
		panel.add(comboBox);
		
		JLabel lblTabelas = new JLabel("Tabelas");
		lblTabelas.setBounds(85, 214, 70, 15);
		panel.add(lblTabelas);
		
		JButton btnButaum = new JButton("butaum");
		btnButaum.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		btnButaum.setBounds(176, 92, 117, 25);
		panel.add(btnButaum);
	}
}
