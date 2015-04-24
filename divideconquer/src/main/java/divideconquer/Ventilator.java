package divideconquer;

import java.util.Random;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

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

		String pathStr = "D:/Fathur/Kuliah/Semester 6/Sistem Terdistribusi/sister dataset";
		File path = new File(pathStr);
		String[] files = path.list();
		int numFiles = files.length;
		byte[] byteArr;
	

		for (int i = 0; i < numFiles; i++) {

			String fileName = files[i];
			File sentFile = new File(pathStr + "/" + fileName);
			System.out.println("transfering " + fileName);

			BufferedImage img = ImageIO.read(sentFile);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(img, "png", baos);
			byteArr = new byte[ (baos.size())];
			byteArr = baos.toByteArray();
	
			sender.send(byteArr);
			System.out.println("byteArr : " + byteArr.length);
			System.out.println("sentFile : " + sentFile.length());
			System.out.println("Finished sending " + i+1 + ". " + fileName);
		}

		Thread.sleep(1000); 

		sink.close();
		sender.close();
		context.term();
	}
}
