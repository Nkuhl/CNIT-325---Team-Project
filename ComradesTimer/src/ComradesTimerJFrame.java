import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Font;
import javax.swing.SwingConstants;


public class ComradesTimerJFrame extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ComradesTimerJFrame frame = new ComradesTimerJFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ComradesTimerJFrame() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 300, 100);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JLabel lblNewLabel = new JLabel("  Player 1                                                              Player 2");
		contentPane.add(lblNewLabel, BorderLayout.NORTH);
		
		JButton btnPause = new JButton("| |");
		btnPause.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnPause.setForeground(Color.RED);
		contentPane.add(btnPause, BorderLayout.CENTER);
		
		textField = new JTextField();
		textField.setHorizontalAlignment(SwingConstants.CENTER);
		textField.setBackground(Color.WHITE);
		textField.setEditable(false);
		contentPane.add(textField, BorderLayout.WEST);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setHorizontalAlignment(SwingConstants.CENTER);
		textField_1.setBackground(Color.WHITE);
		textField_1.setEditable(false);
		contentPane.add(textField_1, BorderLayout.EAST);
		textField_1.setColumns(10);
	}

}
