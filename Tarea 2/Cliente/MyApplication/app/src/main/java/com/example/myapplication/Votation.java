package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;

import javax.net.SocketFactory;

public class Votation extends AppCompatActivity {

    int user;
    int token;
    int votation;

    // Setup Server information
    protected static String server = "192.168.1.105";
    protected static int port = 8088;

    EditText voto;

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
        user = getIntent().getIntExtra("user", 0);
        token = getIntent().getIntExtra("token", 0);
        votation = getIntent().getIntExtra("votation", 0);
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
        new AlertDialog.Builder(this)
                .setTitle("Enviar")
                .setMessage("Se va a proceder al envio")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            // Catch ok button and send information
                            public void onClick(DialogInterface dialog, int whichButton) {

                                // 1. Extraer los datos de la vista
                                Integer vot = Integer.parseInt(voto.getText().toString());
                                String mensaje = user + "," + votation + "," + token + "," + vot;
                                String mensajeenviar = "";
                                // 2. Firmar los datos
                                try{

                                    KeyPair kp = getRSAKeyPair();
                                    PrivateKey privateKey = kp.getPrivate();


                                    Signature sg = Signature.getInstance("SHA256withRSA");
                                    sg.initSign(privateKey);
                                    sg.update(mensaje.getBytes());
                                    byte[] firma = sg.sign();

                                    String firmaMensaje = Base64.encodeToString(firma, Base64.DEFAULT);

                                    mensajeenviar = mensaje+"/"+firmaMensaje;
                                }catch(Exception e){
                                    e.printStackTrace();
                                }

                                // 3. Enviar los datos
                                try {
                                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                            .permitAll().build();
                                    StrictMode.setThreadPolicy(policy);
                                    SocketFactory socketFactory = (SocketFactory) SocketFactory.getDefault();

                                    Socket socket = (Socket) socketFactory.createSocket(server, port);
                                    PrintWriter output = new PrintWriter(new OutputStreamWriter(
                                            socket.getOutputStream()));

                                    output.println(mensajeenviar);
                                    output.flush();

                                    output.close();
                                    socket.close();

                                } catch (Exception ioException) {
                                    ioException.printStackTrace();
                                }
                                Toast.makeText(Votation.this, "Votacion realizada correctamente", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Votation.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }

                )
                .

                        setNegativeButton(android.R.string.no, null)

                .

                        show();
    }

}
