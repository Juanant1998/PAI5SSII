
package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.net.ServerSocketFactory;

import org.bouncycastle.util.encoders.Base64;


public class ServerMesaElectoral {

	
	public static PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
		String pk = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvvDZHoi0VNfb8uWI+u2Tp/qvP76Gst/ZCDTDueAUl1c1slBd43Wk2t/WSwbkoQp2Gqk2v0/3f5rK7N4pJ0oTkh2QC0tqShxWLfhWy8mH1z4DGXET5jKJBYgxhOJmPMl9ptJDPSIexd5tKoaNrwHX/K2NMn5LyPAPNRK+K8/+7s/4/MQ7dFKVMBDOvzMdB3rYSuYP149Woz+O9ja8qRCO1NTkHHz8v+M8CfLYe8zsyVgXpsTZclWUa1H6lPBjDa9t4R+MAEuJxoZIbcMfg2gcOZU4Wso88mFaqe6ifAQYltIRdId4jE1X7TK1BRf3ntLpnMV7YA+TJJ82K779xzs0dQIDAQAB";
		 byte[] publicBytes = Base64.decode(pk);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey pubKey = keyFactory.generatePublic(keySpec);
        System.out.println(pubKey);
        return pubKey;
	}
	
	public static PrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
		String pk = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQC+8NkeiLRU19vy5Yj67ZOn+q8/voay39kINMO54BSXVzWyUF3jdaTa39ZLBuShCnYaqTa/T/d/msrs3iknShOSHZALS2pKHFYt+FbLyYfXPgMZcRPmMokFiDGE4mY8yX2m0kM9Ih7F3m0qho2vAdf8rY0yfkvI8A81Er4rz/7uz/j8xDt0UpUwEM6/Mx0HethK5g/Xj1ajP472NrypEI7U1OQcfPy/4zwJ8th7zOzJWBemxNlyVZRrUfqU8GMNr23hH4wAS4nGhkhtwx+DaBw5lThayjzyYVqp7qJ8BBiW0hF0h3iMTVftMrUFF/ee0umcxXtgD5MknzYrvv3HOzR1AgMBAAECggEAIk5lxD2toNzT0PV6whLzh6fb2ukhjHv5o6bPT9M5/+MUa8BSzi5x+z1iZEKNy310sN+cTY1Rm6S1Nw/Hdx6xG3yiR9U5+KzUsxe+iIjiFkM3DVgqyh5kThElTInc8qkFIXb/y7kYrFaFaLGaPUpogyavitX0SdsP8Go4ruiFFmUJiw3y6pMUJ/6yIdSpY2KGiUB74VhLqHSHw3U8pqprCKrUrH9yCdokDkYOeB/lGPkYKojxWQBqtmzm2TJnYcUKPuJFeyPrKmWVAxZz2wVfyAKQM8Oa9JHDswGBlqqYou6toAqPav9UoplTdSnkHeKTABJIn/t64rsmcJaj+RCztQKBgQD4eLgGiU4/kYnuOudUlRrybs+/QkkOqGuGu75AE3Ycio4G2j6VOydNi42V9a9vcAsEbo+1x7Whm3p9lj4a77yu+ONIEm0G1K/at2O5r1NPbfYh5smc9PEbNnlRsmmeLsWkWv54va+9TpZWBYT6dVdXcwFMpwxGs0kcCoBcuMD1AwKBgQDEueOuomb1FnQL7AxnPepeLdrDbDO23TwUZ1KAt6Bd1Hy1kwfYN51uV+ldFmFhwyzqyhZQK+baHhiNlMTV/qzvZCXSGzDAP1Pzux+yrCjqKLsFLwXDDeyZs+pxar5YHAN07r6wgXDNxxJWHgwlMSn6xEX2BDZvriUjPrlB/VpLJwKBgHvDhcabtgIr+ExVwsx6yMVhNNHLrqBCe+zMr2MzTm3BBiWbp/ilUlpp3MiJbC1R3esDN0oQhARPcaAEqkK4j3+IMY3Av9XbMwz6tA3VquWxnBwe3OX1i/NGGv/6omlMWt4XBRIXSeY9stx+O1KWCD9i5Y7M1myQ+SWihXWqAVMJAoGAFZ2bP9gGWg2yiJDSOBHci6acL/bWo9QhQtirfwsuKsErRsQ2C3Lo8HPmZ10LLDPG3rF/zCVWw0alSgE7s4u9MrydTz2/mJAcyF4aOIakJD5/di1Zg7om8iiLrRSef43sb0/AUiKW43VpNV8t1HRXeX9RdJu344ON/xZoQrD01+cCgYAm3epNh9fu9JJ0rHxiH7sR8D1D8YhP1AhIbcwQ38QgRVMGbAvEt5UurCTSr4UPHTrtVc4Ye0U0g99W7tmrfaNIQKYCPiK4xZY3sKeqGU1dUUIU45qUnWErR3fw4Q8aUsQFTMgUBWUQG6HebdROZ2zPx/FV1Q1EMwM+uXioLN2yiw==";
		byte [] pkcs8EncodedBytes = Base64.decode(pk);
		PKCS8EncodedKeySpec keySpec1 = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privKey = kf.generatePrivate(keySpec1);
        System.out.println(privKey);	
        return privKey;
	}
	
	
	public static void main(final String[] args) throws Exception {
		// espera conexiones del cliente y comprueba login
		ServerSocketFactory socketFactory = ServerSocketFactory.getDefault();

		// crea Socket de la factor
		ServerSocket serverSocket = socketFactory.createServerSocket(8089);
		String tokenValido = "12345"; //TODO: Recuperar esto de la base de datos
		while (true) {

			try {
				System.err.println("Esperando conexiones en 192.168.1.134:8089 ..");

				Socket socket = serverSocket.accept();

				// abre BufferedReader para leer datos del cliente
				BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
				String voto = input.readLine();
				System.out.println(voto);
				String token = "12345";
				//Tokenvalido = llamar a base de datos con los otros 2 ID
				
				BigInteger muprime = null;
				if (token.equals(tokenValido)) {
					
					try
			        {
						
						BigInteger mu = new BigInteger(voto);
						RSAPublicKey rsaPublicKey = (RSAPublicKey) getPublicKey();
						RSAPrivateCrtKey rsaPrivateKey = (RSAPrivateCrtKey) getPrivateKey();
						
			            BigInteger N = rsaPublicKey.getModulus(); //get modulus N

			            BigInteger P = rsaPrivateKey.getPrimeP(); //get the prime number p used to produce the key pair

			            BigInteger Q = rsaPrivateKey.getPrimeQ(); //get the prime number q used to produce the key pair

			            //We split the mu^d modN in two , one mode p , one mode q

			            BigInteger PinverseModQ = P.modInverse(Q); //calculate p inverse modulo q

			            BigInteger QinverseModP = Q.modInverse(P); //calculate q inverse modulo p

			            BigInteger d = rsaPrivateKey.getPrivateExponent(); //get private exponent d

			            //We split the message mu in to messages m1, m2 one mod p, one mod q

			            BigInteger m1 = mu.modPow(d, N).mod(P); //calculate m1=(mu^d modN)modP

			            BigInteger m2 = mu.modPow(d, N).mod(Q); //calculate m2=(mu^d modN)modQ

			            //We combine the calculated m1 and m2 in order to calculate muprime
			            //We calculate muprime: (m1*Q*QinverseModP + m2*P*PinverseModQ) mod N where N =P*Q
			            
			            muprime = ((m1.multiply(Q).multiply(QinverseModP)).add(m2.multiply(P).multiply(PinverseModQ))).mod(N);


			        }
			        catch (Exception e)
			        {
			            e.printStackTrace();
			        }

					String votoFirmado = muprime.toString();
					
					System.out.println("Voto: " + votoFirmado);
					
					output.println(votoFirmado);
					
					output.flush();
					
					
				} else {
					System.out.println("Token invalido, " + token);
				}
				input.close();
				output.close();
				socket.close();

			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		} // end while
	}
	//serverSocket.close();

}
