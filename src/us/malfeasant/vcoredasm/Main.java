package us.malfeasant.vcoredasm;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class Main {
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				startup();
			}
		});
	}
	private static void startup() {
		final JFileChooser fc = new JFileChooser();
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final JPanel panel = new JPanel();
		frame.add(panel);
		JLabel filename = new JLabel();
		JTextArea output = new JTextArea();
		output.setPreferredSize(new Dimension(480, 360));
		output.setEditable(false);
		JButton open = new JButton("Open...");
		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (fc.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION) {
					open(fc.getSelectedFile());
				}
			}
		});
		Box box = Box.createVerticalBox();
		box.add(filename);
		box.add(output);
		box.add(open);
		frame.add(box);
		frame.pack();
		frame.setVisible(true);
	}
	private static void open(File f) {
		
	}
}
