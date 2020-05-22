package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.StrictMode;

import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.util.encoders.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class Votation extends AppCompatActivity {

    int user;
    int token;
    int votation;
    String titulo;
    String op1;
    String op2;
    
    private static final String[]	protocols		= new String[] {
            "TLSv1.2"
    };
    private static final String[]	cipher_suites	= new String[] {
            "TLS_AES_128_GCM_SHA256"
    };

    // Setup Server information
    protected static String server = "192.168.1.105";
    protected static int port = 8088;

    EditText voto;
    EditText tokenIn;
    TextView txt;
    TextView opcion1;
    TextView opcion2;

    public static PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String pk = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvvDZHoi0VNfb8uWI+u2Tp/qvP76Gst/ZCDTDueAUl1c1slBd43Wk2t/WSwbkoQp2Gqk2v0/3f5rK7N4pJ0oTkh2QC0tqShxWLfhWy8mH1z4DGXET5jKJBYgxhOJmPMl9ptJDPSIexd5tKoaNrwHX/K2NMn5LyPAPNRK+K8/+7s/4/MQ7dFKVMBDOvzMdB3rYSuYP149Woz+O9ja8qRCO1NTkHHz8v+M8CfLYe8zsyVgXpsTZclWUa1H6lPBjDa9t4R+MAEuJxoZIbcMfg2gcOZU4Wso88mFaqe6ifAQYltIRdId4jE1X7TK1BRf3ntLpnMV7YA+TJJ82K779xzs0dQIDAQAB";
        byte[] publicBytes = Base64.decode(pk);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey pubKey = keyFactory.generatePublic(keySpec);
        System.out.println(pubKey);
        return pubKey;
    }

    public static KeyPair getRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();
        return kp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_votation);

        voto = (EditText) findViewById(R.id.voto);
        tokenIn = (EditText) findViewById(R.id.token);
        user = getIntent().getIntExtra("user", 0);
        titulo = getIntent().getStringExtra("titulo");
        op1 = getIntent().getStringExtra("op1");
        op2 = getIntent().getStringExtra("op2");
        token = getIntent().getIntExtra("token", 0);
        votation = getIntent().getIntExtra("votation", 0);

        txt = findViewById(R.id.textViewTitulo);
        txt.setText(titulo);

        opcion1 = findViewById(R.id.textViewOp1);
        opcion1.setText(op1);

        opcion2 = findViewById(R.id.textViewOp2);
        opcion2.setText(op2);

        // Capturamos el boton de Enviar
        View button = findViewById(R.id.vot_send);

        // Llama al listener del boton Enviar
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    // Creación de un cuadro de dialogo para confirmar pedido
    private void showDialog() throws Resources.NotFoundException {
        if(new Integer(tokenIn.getText().toString())==token) {
            new AlertDialog.Builder(this)
                    .setTitle("Enviar")
                    .setMessage("Se va a proceder al envio")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                // Catch ok button and send information
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    // 1. Extraer los datos de la vista
                                    String vot = voto.getText().toString();
                                    String mensaje = user + "," + votation + "," + token + "," + vot;
                                    String mensajeenviar = "";
                                    // 2. Firmar los datos
                                    try {

                                        KeyPair kp = getRSAKeyPair();
                                        PrivateKey privateKey = kp.getPrivate();


                                        Signature sg = Signature.getInstance("SHA256withRSA");
                                        sg.initSign(privateKey);
                                        sg.update(mensaje.getBytes());
                                        byte[] firma = sg.sign();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    // 3. Enviar los datos
                                    BigInteger mu = null;
                                    BigInteger m = null;


                                    try {
                                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                                .permitAll().build();
                                        StrictMode.setThreadPolicy(policy);
                                        SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

                                        RSAPublicKey rsaPublicKey = (RSAPublicKey) getPublicKey();
                                        BigInteger r = null;

                                        String message = "";
                                        try {
                                            message = DigestUtils.sha1Hex(vot); //calculate SHA1 hash over message;
                                            System.out.println("Mensaje: " + message);

                                            byte[] msg = message.getBytes("UTF8"); //get the bytes of the hashed message

                                            m = new BigInteger(msg);  //create a BigInteger object based on the extracted bytes of the message

                                            BigInteger e = rsaPublicKey.getPublicExponent(); //get the public exponent 'e' of Alice's key pair

                                            BigInteger N = rsaPublicKey.getModulus(); // get modulus 'N' of the key pair

                                            // Generate a random number so that it belongs to Z*n and is >1 and therefore r is invertible in Z*n
                                            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

                                            byte[] randomBytes = new byte[10]; //create byte array to store the r

                                            BigInteger one = new BigInteger("1"); // make BigInteger object equal to 1, so we can compare it later with the r produced to verify r>1

                                            BigInteger gcd = null; // initialise variable gcd to null


                                            do {
                                                random.nextBytes(randomBytes); //generate random bytes using the SecureRandom function

                                                r = new BigInteger(randomBytes); //make a BigInteger object based on the generated random bytes representing the number r

                                                gcd = r.gcd(rsaPublicKey.getModulus()); //calculate the gcd for random number r and the  modulus of the keypair

                                            }
                                            while (!gcd.equals(one) || r.compareTo(N) >= 0 || r.compareTo(one) <= 0); //repeat until getting an r that satisfies all the conditions and belongs to Z*n and >1

                                            //now that we got an r that satisfies the restrictions described we can proceed with calculation of mu

                                            mu = ((r.modPow(e, N)).multiply(m)).mod(N); //Bob computes mu = H(msg) * r^e mod N


                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }


                                        //Hago un bigInteger to String, y lo envío por el socket de abajo


                                        SSLSocket socket = (SSLSocket) socketFactory.createSocket(server, 8089);

                                        socket.setEnabledProtocols(protocols);
                                        socket.setEnabledCipherSuites(cipher_suites);
                                        PrintWriter output = new PrintWriter(new OutputStreamWriter(
                                                socket.getOutputStream()));

                                        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));


                                        output.println(mu.toString() + "-" + mensaje); //Aquí va el biginteger
                                        output.flush();


                                        String muprime_string = input.readLine();

                                        System.out.println("muprime: " + muprime_string);

                                        BigInteger muprime = new BigInteger(muprime_string);

                                        String signature = "";
                                        try {

                                            BigInteger N = rsaPublicKey.getModulus(); //get modulus of the key pair

                                            BigInteger s = r.modInverse(N).multiply(muprime).mod(N); //Bob computes sig = mu'*r^-1 mod N, inverse of r mod N multiplied with muprime mod N, to remove the blinding factor

                                            byte[] bytes = new Base64().encode(s.toByteArray()); //encode with Base64 encoding to be able to read all the symbols

                                            signature = (new String(bytes)); //make a string based on the byte array representing the signature

                                            System.out.println("Signature produced with Blind RSA procedure for message (hashed with SHA1): " + new String(m.toByteArray()) + " is: ");

                                            System.out.println(signature);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }


                                        output.close();

                                        //Hay que recibir los datos del servidor


                                        //Hay que enviar al centro de votos el mensaje cifrado
                                        socket.close();

                                        SSLSocket socket2 = (SSLSocket) socketFactory.createSocket(server, 8091);

                                        socket.setEnabledProtocols(protocols);
                                        socket.setEnabledCipherSuites(cipher_suites);
                                        PrintWriter output2 = new PrintWriter(new OutputStreamWriter(
                                                socket2.getOutputStream()));

                                        BufferedReader input2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));

                                        output2.println(signature);
                                        output2.println(vot);
                                        output2.println("1");
                                        output2.flush();
                                        output2.close();
                                        input2.close();
                                        socket2.close();
                                    } catch (Exception ioException) {
                                        ioException.printStackTrace();
                                    }
                                    Toast.makeText(Votation.this, "Votacion enviada correctamente", Toast.LENGTH_SHORT).show();
                                }
                            }

                    )
                    .

                            setNegativeButton(android.R.string.no, null)

                    .

                            show();
        } else{
            Toast.makeText(Votation.this, "Token incorrecto", Toast.LENGTH_SHORT).show();
        }
    }

}
