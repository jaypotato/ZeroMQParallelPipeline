package divideconquer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;

import javax.imageio.ImageIO;

import org.zeromq.ZMQ;

public class Sinker {
	public static void main(String[] args) throws Exception {
		long Start = System.currentTimeMillis();
		ZMQ.Context context = ZMQ.context(1);
		ZMQ.Socket receiver = context.socket(ZMQ.PULL);
		receiver.bind("tcp://*:5558");
		
		// Receive data from worker
		String string = new String(receiver.recv(0));

		for (int i = 0; i < 50; i++) {
			int fileNumber = i + 1;
			
			String fileName = receiver.recvStr();
			String fileExtensions = receiver.recvStr();
			
			// Initialize imageOut to receive an byte array from sender
			byte[] imageOut = receiver.recv();
			
			BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageOut));
			String pathNewFile = "your/full/new/image/path";
			
			File destinationPath = new File(pathNewFile + fileNumber + "_" + fileName + "_grayscale." + fileExtensions);

			ImageIO.write(image, fileExtensions, destinationPath);
		}
		
		receiver.close();
		context.term();
		
		long Stop = System.currentTimeMillis();
		long Elapsed = Stop - Start;
		
		System.out.println("total time: " + Elapsed + " miliseconds");
	}
}
