package us.malfeasant.vcoredasm;

import java.awt.Dimension;
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
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class Main {
	private static JTextArea output;
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
		output = new JTextArea();
//		output.setPreferredSize(new Dimension(480, 360));	// breaks scroll bars...
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
		box.add(new JScrollPane(output));
		box.add(open);
		frame.add(box);
		frame.pack();
		frame.setVisible(true);
	}
	private static void open(File f) {
		try (FileInputStream in = new FileInputStream(f)) {
			output.setText("");
			int offset = 0;
			while (true) {
				int arga;
				int argb;
				output.append(String.format("%08x: ", offset));
				int inst = in.read();
				inst |= (in.read() << 8);
				if (inst < 0) return;	// file ended, so done
				output.append(String.format("%04x ", inst));
				InstructionFormat fmt = InstructionFormat.identify(inst);
				offset += fmt.totalBytes;
				switch (fmt) {
				case Scalar16:
					output.append("\n");
					// nothing more
					break;
				case Scalar32:
					arga = in.read();
					arga |= (in.read() << 8);
					output.append(String.format("%04x\n", arga));
					break;
				case Scalar48:
					arga = in.read();
					arga |= (in.read() << 8);
					arga |= (in.read() << 16);
					arga |= (in.read() << 24);
					output.append(String.format("%08x\n", arga));
					break;
				case Vector48:
					arga = in.read() << 16;
					arga |= (in.read() << 24);
					arga |= (in.read() );
					arga |= (in.read() << 8);
					output.append(String.format("%08x\n", arga));
					break;
				case Vector80:
					arga = in.read() << 16;
					arga |= (in.read() << 24);
					arga |= (in.read() );
					arga |= (in.read() << 8);
					output.append(String.format("%08x ", arga));
					argb = in.read() << 16;
					argb |= (in.read() << 24);
					argb |= (in.read() );
					argb |= (in.read() << 8);
					output.append(String.format("%08x\n", argb));
					break;
				default:
					output.append("Polly shouldn't be!");
					break;
				}
			}
		} catch (IOException e) {
			System.err.println("Problem!");
			e.printStackTrace();
		}
	}
}
