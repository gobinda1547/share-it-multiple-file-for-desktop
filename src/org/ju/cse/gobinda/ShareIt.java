package org.ju.cse.gobinda;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.CardLayout;

import javax.swing.JButton;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;

import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JProgressBar;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import java.awt.Color;

public class ShareIt extends JFrame {

	private static final long serialVersionUID = 1L;

	public static int RECEIVER_PORT = 55555;

	private static JPanel contentPane;
	private static JPanel senderPanel;
	private static JPanel receiverPanel;

	private JTextField enterReceiverIpAddressTextField;
	private JTextField txtTotalFileSend;
	private JTextField txtTotalTransmittedFile;
	private JTextField txtTotalFileReceive;
	private JTextField txtTotalFileSize;

	private static JButton goToMainPanelFromSender;
	private static JButton goToMainPanelFromReceiver;

	private static CardLayout mainPanelCardLayout;
	private static CardLayout senderPanelCardLayout;
	private static CardLayout receiverPanelCardlayout;

	private JTextArea dragAndDropTextArea;

	private static JProgressBar sp1;
	private static JProgressBar sp2;
	private static JProgressBar sp3;

	private static JProgressBar rp1;
	private static JProgressBar rp2;
	private static JProgressBar rp3;

	private Sender sender;
	private Receiver receiver;
	private JTextField textField;
	private JTextField txtProcessingFel;

	/**
	 * Create the frame.
	 */
	public ShareIt() {

		sender = new Sender();
		receiver = new Receiver();

		DropTargetHandler dropTargetHandler = new DropTargetHandler();

		mainPanelCardLayout = new CardLayout();
		senderPanelCardLayout = new CardLayout();
		receiverPanelCardlayout = new CardLayout();

		setTitle("Share IT");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 282, 445);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(mainPanelCardLayout);

		JPanel optionPanel = new JPanel();
		contentPane.add(optionPanel, "optionPanel");
		optionPanel.setLayout(null);

		JButton showSenderPanelBtn = new JButton("Send");
		showSenderPanelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				showThisPanelIntoMainPanel("senderPanel");
				showThisPanelIntoSenderPanel("senderSelectFilePanel");
			}
		});
		showSenderPanelBtn.setFont(new Font("Tahoma", Font.PLAIN, 18));
		showSenderPanelBtn.setBounds(62, 128, 135, 43);
		optionPanel.add(showSenderPanelBtn);

		JButton showReceiverPanelBtn = new JButton("Receive");
		showReceiverPanelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				showThisPanelIntoMainPanel("receiverPanel");
				receiver = new Receiver();
				new Thread(receiver).start();
			}
		});
		showReceiverPanelBtn.setFont(new Font("Tahoma", Font.PLAIN, 18));
		showReceiverPanelBtn.setBounds(62, 182, 135, 43);
		optionPanel.add(showReceiverPanelBtn);

		JLabel lblGobindaPaul = new JLabel("Gobinda Paul [01923055489]");
		lblGobindaPaul.setForeground(new Color(0, 128, 128));
		lblGobindaPaul.setBounds(10, 381, 246, 14);
		optionPanel.add(lblGobindaPaul);

		senderPanel = new JPanel();
		contentPane.add(senderPanel, "senderPanel");
		senderPanel.setLayout(senderPanelCardLayout);

		JPanel senderSelectFilePanel = new JPanel();
		senderPanel.add(senderSelectFilePanel, "senderSelectFilePanel");
		senderSelectFilePanel.setLayout(null);

		JPanel dragAndDropPanel = new JPanel();
		dragAndDropPanel.setBounds(10, 43, 246, 320);
		senderSelectFilePanel.add(dragAndDropPanel);
		dragAndDropPanel.setLayout(new BorderLayout(0, 0));

		JScrollPane senderFileListScrollPane = new JScrollPane();
		dragAndDropPanel.add(senderFileListScrollPane, BorderLayout.CENTER);

		dragAndDropTextArea = new JTextArea();
		dragAndDropTextArea.setEditable(false);
		dragAndDropTextArea.setText("Drag and Drop File Here");
		senderFileListScrollPane.setViewportView(dragAndDropTextArea);

		dragAndDropTextArea.setDropTarget(dropTargetHandler);

		JButton sendSelectedFileBtn = new JButton("Send");
		sendSelectedFileBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				sender.setReceiverIpAddress(enterReceiverIpAddressTextField.getText().trim());
				boolean recevierIpAddresssValidnessCheck = sender.isIpAddressValid();
				if (recevierIpAddresssValidnessCheck == false) {
					JOptionPane.showMessageDialog(null, "ENTER A VALID IP ADDRESS!");
					return;
				}

				boolean selectedFileValidCheck = sender.isSelectedFileValid();
				if (selectedFileValidCheck == false) {
					JOptionPane.showMessageDialog(null, "FILE SELECTION ERROR!");
					return;
				}

				makeGoToMainPanelFromSenderBtnEnabled(false);
				showThisPanelIntoSenderPanel("senderTransmiteProgressPanel");
				new Thread(sender).start();

			}
		});
		sendSelectedFileBtn.setBounds(176, 374, 80, 23);
		senderSelectFilePanel.add(sendSelectedFileBtn);

		enterReceiverIpAddressTextField = new JTextField();
		enterReceiverIpAddressTextField.setText("Enter Receiver Ip Address");
		enterReceiverIpAddressTextField.setBounds(10, 11, 194, 23);
		senderSelectFilePanel.add(enterReceiverIpAddressTextField);
		enterReceiverIpAddressTextField.setColumns(10);

		JButton clearReceiverIpAddressButton = new JButton("XX");
		clearReceiverIpAddressButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				enterReceiverIpAddressTextField.setText("");
			}
		});
		clearReceiverIpAddressButton.setBounds(211, 11, 45, 23);
		senderSelectFilePanel.add(clearReceiverIpAddressButton);

		JButton goBackToOptionPanelFromSenderPanelBtn = new JButton("Clear");
		goBackToOptionPanelFromSenderPanelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				clearSenderInformation();
			}
		});
		goBackToOptionPanelFromSenderPanelBtn.setBounds(90, 374, 76, 23);
		senderSelectFilePanel.add(goBackToOptionPanelFromSenderPanelBtn);

		JButton clearDragAndDropFileListBtn = new JButton("Back");
		clearDragAndDropFileListBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearSenderInformation();
				showThisPanelIntoMainPanel("optionPanel");
			}
		});
		clearDragAndDropFileListBtn.setBounds(10, 374, 76, 23);
		senderSelectFilePanel.add(clearDragAndDropFileListBtn);

		JPanel senderTransmiteProgressPanel = new JPanel();
		senderPanel.add(senderTransmiteProgressPanel, "senderTransmiteProgressPanel");
		senderTransmiteProgressPanel.setLayout(null);

		txtTotalFileSend = new JTextField();
		txtTotalFileSend.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtTotalFileSend.setEditable(false);
		txtTotalFileSend.setText("Waiting For Receiver");
		txtTotalFileSend.setHorizontalAlignment(SwingConstants.CENTER);
		txtTotalFileSend.setColumns(10);
		txtTotalFileSend.setBounds(10, 66, 246, 34);
		senderTransmiteProgressPanel.add(txtTotalFileSend);

		sp1 = new JProgressBar();
		sp1.setBounds(10, 105, 246, 26);
		sp1.setStringPainted(true);
		senderTransmiteProgressPanel.add(sp1);

		textField = new JTextField();
		textField.setText("Total Transmitted");
		textField.setHorizontalAlignment(SwingConstants.CENTER);
		textField.setFont(new Font("Tahoma", Font.BOLD, 11));
		textField.setEditable(false);
		textField.setColumns(10);
		textField.setBounds(10, 150, 246, 35);
		senderTransmiteProgressPanel.add(textField);

		sp2 = new JProgressBar();
		sp2.setStringPainted(true);
		sp2.setBounds(10, 190, 246, 26);
		senderTransmiteProgressPanel.add(sp2);

		txtTotalTransmittedFile = new JTextField();
		txtTotalTransmittedFile.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtTotalTransmittedFile.setEditable(false);
		txtTotalTransmittedFile.setText("Total Transmitted");
		txtTotalTransmittedFile.setHorizontalAlignment(SwingConstants.CENTER);
		txtTotalTransmittedFile.setColumns(10);
		txtTotalTransmittedFile.setBounds(10, 234, 246, 35);
		senderTransmiteProgressPanel.add(txtTotalTransmittedFile);

		sp3 = new JProgressBar();
		sp3.setBounds(10, 274, 246, 26);
		sp3.setStringPainted(true);
		senderTransmiteProgressPanel.add(sp3);

		goToMainPanelFromSender = new JButton("Go To Main Panel");
		goToMainPanelFromSender.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setValuesToSP1(0, String.valueOf("0") + "%");
				setValuesToSP2(0, String.valueOf("0") + "%");
				setValuesToSP3(0, String.valueOf("0") + "%");
				clearSenderInformation();
				showThisPanelIntoMainPanel("optionPanel");
			}
		});
		goToMainPanelFromSender.setBounds(10, 327, 246, 34);
		senderTransmiteProgressPanel.add(goToMainPanelFromSender);

		receiverPanel = new JPanel();
		contentPane.add(receiverPanel, "receiverPanel");
		receiverPanel.setLayout(receiverPanelCardlayout);

		JPanel receiverTransmitePanel = new JPanel();
		receiverPanel.add(receiverTransmitePanel, "receiverTransmitePanel");
		receiverTransmitePanel.setLayout(null);

		txtTotalFileReceive = new JTextField();
		txtTotalFileReceive.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtTotalFileReceive.setText("Waiting For Sender");
		txtTotalFileReceive.setHorizontalAlignment(SwingConstants.CENTER);
		txtTotalFileReceive.setEditable(false);
		txtTotalFileReceive.setColumns(10);
		txtTotalFileReceive.setBounds(10, 71, 246, 29);
		receiverTransmitePanel.add(txtTotalFileReceive);

		rp1 = new JProgressBar();
		rp1.setStringPainted(true);
		rp1.setBounds(10, 105, 246, 29);
		receiverTransmitePanel.add(rp1);

		txtProcessingFel = new JTextField();
		txtProcessingFel.setText("Processing Fel");
		txtProcessingFel.setHorizontalAlignment(SwingConstants.CENTER);
		txtProcessingFel.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtProcessingFel.setEditable(false);
		txtProcessingFel.setColumns(10);
		txtProcessingFel.setBounds(10, 156, 246, 29);
		receiverTransmitePanel.add(txtProcessingFel);

		rp2 = new JProgressBar();
		rp2.setStringPainted(true);
		rp2.setBounds(10, 189, 246, 29);
		receiverTransmitePanel.add(rp2);

		txtTotalFileSize = new JTextField();
		txtTotalFileSize.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtTotalFileSize.setText("Total Received");
		txtTotalFileSize.setHorizontalAlignment(SwingConstants.CENTER);
		txtTotalFileSize.setEditable(false);
		txtTotalFileSize.setColumns(10);
		txtTotalFileSize.setBounds(10, 241, 246, 29);
		receiverTransmitePanel.add(txtTotalFileSize);

		rp3 = new JProgressBar();
		rp3.setStringPainted(true);
		rp3.setBounds(10, 274, 246, 29);
		receiverTransmitePanel.add(rp3);

		goToMainPanelFromReceiver = new JButton("Go To Main Panel");
		goToMainPanelFromReceiver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setValuesToRP1(0, String.valueOf("0") + "%");
				setValuesToRP2(0, String.valueOf("0") + "%");
				setValuesToRP3(0, String.valueOf("0") + "%");
				showThisPanelIntoMainPanel("optionPanel");
			}
		});
		goToMainPanelFromReceiver.setBounds(10, 324, 246, 29);
		receiverTransmitePanel.add(goToMainPanelFromReceiver);

		JButton showIpAddressBtn = new JButton("Show Your Ip Address");
		showIpAddressBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {

					JOptionPane.showMessageDialog(null, showAllTheIpAddress());
					// JOptionPane.showMessageDialog(null,
					// InetAddress.getLocalHost().getHostAddress().toString());
				} catch (HeadlessException e1) {
					JOptionPane.showMessageDialog(null, "CAN NOT ACCESS IP ADDRESS");
				}
			}

			public String showAllTheIpAddress() {

				String overAllAnswer = "";

				try {
					Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();

					for (NetworkInterface netint : Collections.list(nets)) {

						String netName = netint.getName();
						int len = netName.length();

						if (netName.contains("wlan") || netName.contains("eth") || netName.contains("net")) {
							Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
							for (InetAddress inetAddress : Collections.list(inetAddresses)) {
								netName = netName + ":" + inetAddress;
								break;
							}
						}

						if (netName.length() == len) {

						} else if (netName.contains(".") == false) {

						} else {
							overAllAnswer = overAllAnswer + netName + "\n";
						}
					}

				} catch (SocketException e) {
					e.printStackTrace();
				}

				return overAllAnswer;

			}
		});
		showIpAddressBtn.setBounds(10, 356, 246, 29);
		receiverTransmitePanel.add(showIpAddressBtn);
	}

	public static void setValuesToRP1(int percentage, String message) {
		rp1.setString(message);
		rp1.setValue(percentage);
	}

	public static void setValuesToRP2(int percentage, String message) {
		rp2.setString(message);
		rp2.setValue(percentage);
	}

	public static void setValuesToRP3(int percentage, String message) {
		rp3.setString(message);
		rp3.setValue(percentage);
	}

	public static void setValuesToSP1(int percentage, String message) {
		sp1.setString(message);
		sp1.setValue(percentage);
	}

	public static void setValuesToSP2(int percentage, String message) {
		sp2.setString(message);
		sp2.setValue(percentage);
	}

	public static void setValuesToSP3(int percentage, String message) {
		sp3.setString(message);
		sp3.setValue(percentage);
	}

	public void clearSenderInformation() {
		dragAndDropTextArea.setText("Drag and Drop File Here");
		enterReceiverIpAddressTextField.setText("Enter Receiver Ip Address");
		sender.clearAllInformation();
	}

	public static void showThisPanelIntoMainPanel(String panelName) {
		mainPanelCardLayout.show(contentPane, panelName);
	}

	public static void showThisPanelIntoSenderPanel(String panelName) {
		senderPanelCardLayout.show(senderPanel, panelName);
	}

	public static void showThisPanelIntoReceiverPanel(String panelName) {
		receiverPanelCardlayout.show(receiverPanel, panelName);
	}

	public static void makeGoToMainPanelFromSenderBtnEnabled(boolean tof) {
		goToMainPanelFromSender.setEnabled(tof);
	}

	public static void makeGoToMainPanelFromReceiverBtnEnabled(boolean tof) {
		goToMainPanelFromReceiver.setEnabled(tof);
	}

	class DropTargetHandler extends DropTarget {

		private static final long serialVersionUID = 1L;

		public synchronized void drop(DropTargetDropEvent evt) {
			if (dragAndDropTextArea.getText().startsWith("Drag")) {
				dragAndDropTextArea.setText("");
			}

			try {
				evt.acceptDrop(DnDConstants.ACTION_COPY);

				@SuppressWarnings("unchecked")
				List<File> droppedFiles = (List<File>) evt.getTransferable()
						.getTransferData(DataFlavor.javaFileListFlavor);

				for (File file : droppedFiles) {
					dragAndDropTextArea.append(file.getName() + "\n");
					sender.addThisFileToTheSelectionList(file);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}

	}

	public static void initialize() {
		new ShareIt().setVisible(true);
	}
}
