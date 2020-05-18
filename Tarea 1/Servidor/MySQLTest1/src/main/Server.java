package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;

public class Server {

	public static void main(String[] args) throws Exception {
// espera conexiones del cliente y comprueba login
ServerSocketFactory socketFactory = (ServerSocketFactory) ServerSocketFactory.getDefault(); 

// crea Socket de la factor�a
ServerSocket serverSocket = (ServerSocket) socketFactory.createServerSocket(8088);

while (true) {

	try {
		System.err.println("Esperando conexiones en 192.168.1.134:8088 ..");

		Socket socket = serverSocket.accept();

		// abre BufferedReader para leer datos del cliente
		BufferedReader input = new BufferedReader(
				new InputStreamReader(socket.getInputStream()));
		// abre PrintWriter para enviar datos al cliente
		PrintWriter output = new PrintWriter(new OutputStreamWriter(
		socket.getOutputStream()));
		String mensajeenviar = input.readLine();
		
		String[] sp1 = mensajeenviar.split("/");
		String pubkey = sp1[1];
		String values = sp1[0];
		
		String[] allvalues = values.split(",");
		Integer mesas = Integer.parseInt(allvalues[0]);
		Integer sillas = Integer.parseInt(allvalues[1]);
		Integer sillones = Integer.parseInt(allvalues[2]);
		Integer camas = Integer.parseInt(allvalues[3]);
		Integer usuario = Integer.parseInt(allvalues[4]);
		
		System.out.println(mensajeenviar);
		
		MySQLAccess dao = new MySQLAccess();
	    //dao.readDataBase();
	    dao.insertPedido(mesas, sillas, sillones, camas, usuario);
	    
	    

		output.close();
		input.close();
		socket.close();

	} catch (IOException ioException) {
		ioException.printStackTrace();
	}
} // end while
}
//serverSocket.close();

}
