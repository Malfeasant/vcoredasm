package us.malfeasant.vcoredasm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Main {
	private File currentFile;
	private final JFrame frame;
	private final SpinnerNumberModel offset;
	private final SpinnerNumberModel start;
	private final SpinnerNumberModel lines;
	private final JTextArea output;
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Main();
			}
		});
	}
	private Main() {
		final JFileChooser chooser = new JFileChooser();
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final JPanel panel = new JPanel();
		frame.add(panel);
		
		ChangeListener l = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				show();
			}
		};
		final JLabel filename = new JLabel();
		offset = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 2);
		offset.addChangeListener(l);
		JSpinner offSpin = new JSpinner(offset);
		offSpin.setToolTipText("Offset to add to all addresses");
		start = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 2);
		start.addChangeListener(l);
		JSpinner startSpin = new JSpinner(start);
		startSpin.setToolTipText("Where in the file to start disassembly");
		lines = new SpinnerNumberModel(25, 1, 100, 1);
		lines.addChangeListener(l);
		JSpinner lineSpin = new JSpinner(lines);
		lineSpin.setToolTipText("How many lines to decode");
		Box hbox = Box.createHorizontalBox();
		hbox.add(filename);
		hbox.add(new JLabel("Offset:"));
		hbox.add(offSpin);
		hbox.add(new JLabel("Start:"));
		hbox.add(startSpin);
		hbox.add(new JLabel("Lines:"));
		hbox.add(lineSpin);
		
		output = new JTextArea();
		output.setEditable(false);
		JButton open = new JButton("Open...");
		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
					currentFile = chooser.getSelectedFile();
					filename.setText(currentFile.getName());
					start.setValue(0);
					start.setMaximum((int)currentFile.length());
					show();
				}
			}
		});
		Box box = Box.createVerticalBox();
		box.add(hbox);
		box.add(new JScrollPane(output));
		box.add(open);
		frame.add(box);
		frame.pack();
		frame.setVisible(true);
	}
	private void show() {
		if (currentFile == null) return;
		if (!currentFile.exists()) return;
		try (FileInputStream in = new FileInputStream(currentFile)) {
			output.setText("");
			int st = (int)start.getValue();
			int off = st + (int)offset.getValue();
			in.skip(st);
			while (output.getLineCount() < (int)lines.getValue()) {
				int inst = in.read() | (in.read() << 8);
				if (inst < 0) break;	// file ended, so done
				output.append(String.format("%08x: ", off));
				InstructionFormat fmt = InstructionFormat.identify(inst);
				int len = fmt.moreBytes;
				byte[] bytes = null;
				if (len > 0) {
					bytes = new byte[len];
					in.read(bytes);
				}
				off += fmt.moreBytes + 2;	// remember the instruction...
				output.append(fmt.format(inst, bytes));
			}
			frame.pack();
		} catch (IOException e) {
			System.err.println("Problem!");
			e.printStackTrace();
		}
	}
}
