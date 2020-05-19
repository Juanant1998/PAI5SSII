
package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;

public class ServerMesaElectoral {

	public static void main(final String[] args) throws Exception {
		// espera conexiones del cliente y comprueba login
		ServerSocketFactory socketFactory = ServerSocketFactory.getDefault();

		// crea Socket de la factor
		ServerSocket serverSocket = socketFactory.createServerSocket(8088);
		String tokenValido = "12345";
		while (true) {

			try {
				System.err.println("Esperando conexiones en 192.168.1.105:8088 ..");

				Socket socket = serverSocket.accept();

				// abre BufferedReader para leer datos del cliente
				BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
				String mensajeenviar = input.readLine();
				String[] sp1 = mensajeenviar.split("/");
				String pubkey = sp1[1];
				String values = sp1[0];

				String[] sp2 = values.split(",");
				String idUser = sp2[0];
				String idVotacion = sp2[1];
				String token = sp2[2];
				String voto = sp2[3];

				if (token.equals(tokenValido)) {
					System.out.println("OK");
					System.out.println(idUser + ", " + idVotacion + ", " + voto);
				} else {
					System.out.println("Token invalido, " + token);
				}
				input.close();
				socket.close();

			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		} // end while
	}
	//serverSocket.close();

}
