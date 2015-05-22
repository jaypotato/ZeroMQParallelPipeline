package divideconquer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import org.zeromq.ZMQ;

public class Worker {
	public static void main(String[] args) throws Exception {
		ZMQ.Context context = ZMQ.context(1);

		ZMQ.Socket receiver = context.socket(ZMQ.PULL);
		receiver.connect("tcp://localhost:5557");

		ZMQ.Socket sender = context.socket(ZMQ.PUSH);
		sender.connect("tcp://localhost:5558");

		// Process image
		while (!Thread.currentThread().isInterrupted()) {;
			byte[] imageByteArray;
			
			// Receive data from ventilator
			String fileName = receiver.recvStr();
			String fileExtensions = receiver.recvStr();
			imageByteArray = receiver.recv();
			
			System.out.println(imageByteArray.length);
		
			BufferedImage imageIn = ImageIO.read(new ByteArrayInputStream(imageByteArray));
			
			// Get image attributes
			int height = imageIn.getHeight();
			int width = imageIn.getWidth();
			
			BufferedImage imageAttributes = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
			
			// Convert to grayscale
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					Color c = new Color(imageIn.getRGB(x, y));
					int temp = (int) (c.getRed() * 0.299 + c.getGreen() * 0.587 + c.getBlue() * 0.114);
					imageAttributes.setRGB(x, y, temp << 16 | temp << 8 | temp);
				}
			}
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(imageAttributes, fileExtensions, baos);
			
			byte[] imageOut = baos.toByteArray();
            
			sender.send(fileName);
			sender.send(fileExtensions);
			sender.send(imageOut);
		}

		sender.close();
		receiver.close();
		context.term();
	}

}
