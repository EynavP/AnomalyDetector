package test;


import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;
import java.io.PrintWriter;
import test.Commands.DefaultIO;
import test.Server.ClientHandler;

public class AnomalyDetectionHandler implements ClientHandler{

	Scanner in;
	PrintWriter out;
	
	public void communicate(InputStream inFromClient,OutputStream outToClient)
	{
		this.in = new Scanner(inFromClient);
		this.out = new PrintWriter(outToClient);
		
		SocketIO s = new SocketIO();
		CLI c = new CLI(s);
		c.start();
		out.println("bye");
		out.close();
		in.close();
	}
	
	public class SocketIO implements DefaultIO{
		
		public String readText() {
			return in.nextLine();
		}
		public void write(String text) {
			out.print(text);
		}
		public float readVal() {
			return in.nextFloat();
		}
		public void write(float val) {
			out.print(val);
		}
	}



}
