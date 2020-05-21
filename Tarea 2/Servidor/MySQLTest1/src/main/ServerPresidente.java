
package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import javax.net.ServerSocketFactory;

public class ServerPresidente {

	public static void main(final String[] args) throws Exception {
		// espera conexiones del cliente y comprueba login
		ServerSocketFactory socketFactory = ServerSocketFactory.getDefault();

		// crea Socket de la factor
		ServerSocket serverSocket = socketFactory.createServerSocket(8088);
		while (true) {

			try {
				System.err.println("Esperando conexiones en 192.168.1.134:8088 ..");

				Socket socket = serverSocket.accept();

				// abre BufferedReader para leer datos del cliente
				BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				// abre PrintWriter para enviar datos al cliente
				PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
				String mensajeenviar = input.readLine();
				String[] sp1 = mensajeenviar.split("/");
				String pubkey = sp1[1];
				String values = sp1[0];

				String[] sp2 = values.split(",");
				String idUser = sp2[0];
				String idVotacion = sp2[1];

				MySQLAccess_T2 dao = new MySQLAccess_T2();
				
				Integer token = dao.getToken(idUser, idVotacion);
				
				if (token != -1)
				{
					output.println(token);
					output.flush();
					
					Map <String, String> votacion = dao.getVotacion(idVotacion);
					String titulo = votacion.get("titulo");
					String op1 = votacion.get("op1");
					String op2 = votacion.get("op2");
					
					System.out.println(titulo);
					output.println(titulo);
					output.println(op1);
					output.println(op2);
					output.flush();
				} else {
					output.println("No ha sido posible encontrar un token para esa combinación");
					output.flush();
				}
				System.out.println(token);
				output.println(token);
				output.flush();

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
