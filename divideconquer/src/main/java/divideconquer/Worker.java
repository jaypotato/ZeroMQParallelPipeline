package divideconquer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.rmi.RemoteException;

import javax.imageio.ImageIO;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageConverter;

import org.zeromq.ZMQ;

public class Worker {
	public static void main(String[] args) throws Exception {
		ZMQ.Context context = ZMQ.context(1);

		ZMQ.Socket receiver = context.socket(ZMQ.PULL);
		receiver.connect("tcp://localhost:5557");

		ZMQ.Socket sender = context.socket(ZMQ.PUSH);
		sender.connect("tcp://localhost:5558");


		// Process image
		while (!Thread.currentThread().isInterrupted()) {
			ByteBuffer recData = ByteBuffer.allocate(4096);
			int buffer = 0;
			byte[] tmpData ;
			OutputStream out = null;
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
			tmpData = receiver.recv();
			System.out.println(tmpData.length);
		
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(tmpData));
			int x, y;
			int height = img.getHeight();
			int width = img.getWidth();
			BufferedImage imgOut = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

			for (y = 0; y < height; y++) {
				for (x = 0; x < width; x++) {
	
					Color c = new Color(img.getRGB(x, y));
					int temp = (int) (c.getRed() * 0.299 + c.getGreen() * 0.587 + c
							.getBlue() * 0.114);
					imgOut.setRGB(x, y, temp << 16 | temp << 8 | temp);
				}
			}
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(imgOut, "png", baos);
			byte[] sendImg = baos.toByteArray();
            sender.send(sendImg);
		}

		sender.close();
		receiver.close();
		context.term();
	}

}
