package org.ju.cse.gobinda;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Sender implements Runnable {

	private ArrayList<File> fileHaveToSend;
	private ArrayList<String> fileNameHaveToSend;

	private String receiverIpAddress;

	public Sender() {

	}

	@Override
	public void run() {

		try {
			ShareIt.makeGoToMainPanelFromSenderBtnEnabled(false);
			ShareIt.setValuesToSP1(0, "STARTING");
			ShareIt.setValuesToSP2(0, "STARTING");
			ShareIt.setValuesToSP3(0, "STARTING");

			Socket socket = null;

			boolean socketIsConnected = false;
			for (int totalConnectingStep = 1; totalConnectingStep <= 100 && !socketIsConnected; totalConnectingStep++) {

				try {
					socket = new Socket(receiverIpAddress, ShareIt.RECEIVER_PORT);
					socket.setSoTimeout(1000);
					socketIsConnected = true;
				} catch (Exception e) {
					ShareIt.setValuesToSP1(totalConnectingStep, String.valueOf(totalConnectingStep) + "%");

				}

			}

			if (socketIsConnected == false) {
				ShareIt.setValuesToSP1(0, "No Receiver Found");
				ShareIt.makeGoToMainPanelFromSenderBtnEnabled(true);
				return;
			}

			ShareIt.setValuesToSP1(0, "Connected!");

			DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

			// send total file number
			int totalFileNmber = fileHaveToSend.size();
			dataOutputStream.writeInt(totalFileNmber);
			dataOutputStream.flush();

			String fileNumberString = "";
			for (int i = 0; i < totalFileNmber; i++) {

				fileNumberString = String.valueOf(i + 1) + "/" + String.valueOf(totalFileNmber);
				ShareIt.setValuesToSP2(0, fileNumberString);
				ShareIt.setValuesToSP3(0, String.valueOf(0));

				// send code for file or directory
				boolean thisIsFile = fileHaveToSend.get(i).isFile();
				dataOutputStream.writeBoolean(thisIsFile);
				dataOutputStream.flush();

				// send file name
				dataOutputStream.writeUTF(fileNameHaveToSend.get(i));
				dataOutputStream.flush();

				if (thisIsFile == false) {
					ShareIt.setValuesToSP3(100, "Completed");
					continue;
				}

				// send file size
				long nowFileSize = fileHaveToSend.get(i).length();
				dataOutputStream.writeLong(nowFileSize);
				dataOutputStream.flush();

				// send file

				FileInputStream fis = new FileInputStream(fileHaveToSend.get(i));
				byte[] buffer = new byte[4096];

				int read = 0;
				long remaining = nowFileSize;

				double perCycleCopy = 100.00 / (nowFileSize / 4096);
				double incrementor = 0.0;
				int value = (int) incrementor;

				while ((read = fis.read(buffer, 0, (int) Math.min(buffer.length, remaining))) > 0) {
					value = Math.min(100, (int) incrementor);
					ShareIt.setValuesToSP3(value, String.valueOf(value));
					dataOutputStream.write(buffer, 0, read);
					dataOutputStream.flush();
					remaining -= read;
					incrementor += perCycleCopy;
				}
				fis.close();
				ShareIt.setValuesToSP3(100, "Completed");

			}

			socket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		ShareIt.setValuesToSP1(100, "COMPLETE");
		ShareIt.setValuesToSP2(100, "COMPLETE");
		ShareIt.setValuesToSP3(100, "COMPLETE");
		ShareIt.makeGoToMainPanelFromSenderBtnEnabled(true);

	}

	public void addThisFileToTheSelectionList(File selectedFile) {
		if (this.fileHaveToSend == null || this.fileNameHaveToSend == null) {
			this.fileHaveToSend = new ArrayList<>();
			this.fileNameHaveToSend = new ArrayList<>();
		}

		fileHaveToSend.addAll(new TraversFileTree(selectedFile).getFileList());
		fileNameHaveToSend.addAll(new TraversFileNameTree(selectedFile).getFileNameList());

	}

	public void setReceiverIpAddress(String receiverIpAddress) {
		this.receiverIpAddress = receiverIpAddress;
	}

	public boolean isSelectedFileValid() {

		if (this.fileHaveToSend == null) {
			return false;
		}

		if (this.fileNameHaveToSend == null) {
			return false;
		}

		if (this.fileHaveToSend.size() == 0) {
			return false;
		}

		if (this.fileNameHaveToSend.size() == 0) {
			return false;
		}

		return true;
	}

	public boolean isIpAddressValid() {

		if (receiverIpAddress == null) {
			return false;
		}

		for (int i = 0; i < receiverIpAddress.length(); i++) {
			char ch = receiverIpAddress.charAt(i);
			if (ch != '.' && (ch < '0' && '9' < ch)) {
				return false;
			}
		}

		String[] partsOfReceiverIpAddress = receiverIpAddress.split("\\.");
		if (partsOfReceiverIpAddress.length != 4) {
			return false;
		}

		for (int i = 0; i < 4; i++) {
			if (partsOfReceiverIpAddress[i].length() > 3) {
				return false;
			}
		}

		if (receiverIpAddress.indexOf("..") != -1) {
			return false;
		}

		if (receiverIpAddress.startsWith(".")) {
			return false;
		}

		if (receiverIpAddress.endsWith(".")) {
			return false;
		}

		return true;

	}

	public void clearAllInformation() {
		this.receiverIpAddress = null;
		this.fileHaveToSend = null;
		this.fileNameHaveToSend = null;
	}

	static class TraversFileTree {

		private ArrayList<File> files;

		public TraversFileTree(File file) {
			this.files = new ArrayList<>();
			prepareFileList(file);
		}

		private void prepareFileList(File file) {

			files.add(file);

			if (file.isFile()) {
				return;
			}

			File[] f = file.listFiles();
			for (int i = 0; i < f.length; i++) {
				prepareFileList(f[i]);
			}
		}

		public ArrayList<File> getFileList() {
			return files;
		}

	}

	static class TraversFileNameTree {

		private ArrayList<String> filesName;

		public TraversFileNameTree(File file) {
			this.filesName = new ArrayList<>();
			prepareFileNameList(file, file.getName());
		}

		private void prepareFileNameList(File file, String nowFileName) {

			filesName.add(nowFileName);

			if (file.isFile()) {
				return;
			}

			File[] f = file.listFiles();
			for (int i = 0; i < f.length; i++) {
				prepareFileNameList(f[i], nowFileName + "/" + f[i].getName());
			}
		}

		public ArrayList<String> getFileNameList() {
			return filesName;
		}

	}

}