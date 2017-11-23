package org.ju.cse.gobinda;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Receiver implements Runnable {

	private String fileWhereToSave;

	public Receiver() {

	}

	@Override
	public void run() {

		try {

			ShareIt.makeGoToMainPanelFromReceiverBtnEnabled(false);
			ShareIt.setValuesToRP1(0, "STARTING");
			ShareIt.setValuesToRP2(0, "STARTING");
			ShareIt.setValuesToRP3(0, "STARTING");

			fileWhereToSave = "C:/Users/" + System.getProperty("user.name") + "/Documents/ShareIt/"
					+ new Date().toString().replace(":", "_");
			// System.out.println(fileWhereToSave);

			ServerSocket serverSocket = null;
			Socket socket = null;

			for (int i = 1; i <= 100 && socket == null; i++) {
				try {
					serverSocket = new ServerSocket(ShareIt.RECEIVER_PORT);
					serverSocket.setSoTimeout(1000);
					socket = serverSocket.accept();
				} catch (Exception e) {
					ShareIt.setValuesToRP1(i, String.valueOf(i) + "%");
					serverSocket.close();
				}
			}

			if (socket == null) {
				ShareIt.setValuesToRP1(0, "No Sender Found");
				ShareIt.makeGoToMainPanelFromReceiverBtnEnabled(true);
				return;
			}

			ShareIt.setValuesToRP1(0, "Connected!");

			DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

			// ShareIt.showThisPanelIntoReceiverPanel("receiverTransmitePanel");

			try {

				// receive total file number
				int totalFileNmber = dataInputStream.readInt();

				String fileNumberString = "";
				for (int i = 0; i < totalFileNmber; i++) {

					fileNumberString = String.valueOf(i + 1) + "/" + String.valueOf(totalFileNmber);
					ShareIt.setValuesToRP2(0, fileNumberString);
					ShareIt.setValuesToRP3(0, String.valueOf(0));

					// receive code for file or directory
					boolean thisIsFile = dataInputStream.readBoolean();

					// receive file name
					String nowFileName = dataInputStream.readUTF();

					if (thisIsFile == false) {
						nowFileName = fileWhereToSave + "/" + nowFileName;
						new File(nowFileName).mkdirs();
						ShareIt.setValuesToSP3(100, "Completed");
						continue;
					}

					// receive file size
					long nowFileSize = dataInputStream.readLong();

					File nowFile = new File(fileWhereToSave + "/" + nowFileName);
					nowFile.getParentFile().mkdirs();
					FileOutputStream fos = new FileOutputStream(nowFile);
					byte[] buffer = new byte[4096];

					int read = 0;
					long remaining = nowFileSize;

					double perCycleCopy = 100.00 / (nowFileSize / 4096);
					double incrementor = 0.0;
					int value = (int) incrementor;

					while ((read = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, remaining))) > 0) {

						value = Math.min(100, (int) incrementor);
						ShareIt.setValuesToRP3(value, String.valueOf(value) + "%");

						remaining -= read;
						fos.write(buffer, 0, read);

						incrementor += perCycleCopy;
					}

					fos.close();
					ShareIt.setValuesToSP3(100, "Completed");

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			socket.close();
			serverSocket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		ShareIt.setValuesToRP1(100, "COMPLETE");
		ShareIt.setValuesToRP2(100, "COMPLETE");
		ShareIt.setValuesToRP3(100, "COMPLETE");

		ShareIt.makeGoToMainPanelFromReceiverBtnEnabled(true);

	}

}
