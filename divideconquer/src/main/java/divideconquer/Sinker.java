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

		String string = new String(receiver.recv(0));

		
		for (int i = 0; i < 50; i++) {
			
			byte[] input = receiver.recv();
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(input));
			String pathStr = "D:/Fathur/Kuliah/Semester 6/Sistem Terdistribusi/GrayScaleConv/";
			File path = new File(pathStr + "File " + i+1 +" grayscale.png");

			ImageIO.write(img, "png", path);

		}
		receiver.close();
		context.term();
		long Stop = System.currentTimeMillis();
		long Elapsed = Stop - Start;
		System.out.println("total time : " + Elapsed + "miliseconds");
	}
}
