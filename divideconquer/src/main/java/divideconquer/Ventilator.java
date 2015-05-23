package divideconquer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.imageio.ImageIO;

import org.zeromq.ZMQ;

public class Ventilator {
	public static void main(String[] args) throws Exception {
		ZMQ.Context context = ZMQ.context(1);

		ZMQ.Socket sender = context.socket(ZMQ.PUSH);
		sender.bind("tcp://*:5557");

		ZMQ.Socket sink = context.socket(ZMQ.PUSH);
		sink.connect("tcp://localhost:5558");

		System.out.println("Press Enter when the workers are ready: ");
		System.in.read();
		System.out.println("Sending tasks to workers\n");

		sink.send("0", 0);

		// Full image file path
		String imagePath = "your/full/image/path";
		File path = new File(imagePath);
		
		// File list
		String[] files = path.list();
		
		// Number of files
		int numOfFiles = files.length;
		
		// Initialize byte array for image
		byte[] images;

		for (int i = 0; i < numOfFiles; i++) {
			// Image file will send to worker
			String fileIndex = files[i];
			File sendFile = new File(imagePath + "/" + fileIndex);
			
			// Using regex to remove file extensions
        	String fileName = fileIndex.replaceFirst("[.][^.]+$", "");
        	
        	// Get file extesions
        	String fileExtension = fileIndex.substring(fileIndex.lastIndexOf('.')+1);
        	
			System.out.println("transfering " + fileIndex);

			BufferedImage img = ImageIO.read(sendFile);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(img, fileExtension, baos);
			images = new byte[baos.size()];
			images = baos.toByteArray();
	
			sender.send(fileName);
			sender.send(fileExtension);
			sender.send(images);
			
			int fileNumber = i + 1;
			
			// Printing message reply as log
			System.out.println("length of byte array: " + images.length);
			System.out.println("length of file: " + sendFile.length() + " bytes");
			System.out.println(fileNumber + ". " + fileName + " is sended succesfully\n");
		}

		Thread.sleep(1000); 

		sink.close();
		sender.close();
		context.term();
	}
}
