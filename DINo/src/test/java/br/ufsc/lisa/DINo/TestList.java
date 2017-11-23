package br.ufsc.lisa.DINo;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class TestList {

    public static void main(String[] args) {
        new TestList();
    }

    public TestList() {
        EventQueue.invokeLater(new Runnable() {
            
            public void run() {
            	JPanel panel = new JPanel(new BorderLayout());

                List<String> labels = new ArrayList<String>(25);
                for (int index = 0; index < 100; index++) {
                    labels.add("Item " + index);
                }

                final JList<String> listArea = new JList<String>(labels.toArray(new String[labels.size()]));
                listArea.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                listArea.setFont(new Font("Serif", Font.ITALIC, 14));
                JScrollPane listScroller = new JScrollPane();
                listScroller.setViewportView(listArea);
                listArea.setLayoutOrientation(JList.VERTICAL);
                panel.add(listScroller);

                JFrame frame = new JFrame("Testing");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(panel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

}