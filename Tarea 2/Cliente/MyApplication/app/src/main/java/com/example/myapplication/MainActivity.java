package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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


public class MainActivity extends AppCompatActivity {

    // Setup Server information
    protected static String server = "192.168.1.105";
    protected static int port = 8088;

    EditText idUser;
    EditText idVotation;

    public static KeyPair getRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();
        return kp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        idUser = (EditText) findViewById(R.id.idUserIn);
        idVotation = (EditText) findViewById(R.id.idVotationIn);
        // Capturamos el boton de Enviar
        View button = findViewById(R.id.button_send);

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
                                    Integer user = Integer.parseInt(idUser.getText().toString());
                                    Integer votation = Integer.parseInt(idVotation.getText().toString());
                                    String mensaje = user + "," + votation;
                                    String mensajeenviar = "";
                                    Integer token = 0;
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
                                        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                        PrintWriter output = new PrintWriter(new OutputStreamWriter(
                                                socket.getOutputStream()));

                                        output.println(mensajeenviar);
                                        output.flush();
                                        token = Integer.valueOf(input.readLine());
                                        System.out.println(token);
                                        output.close();
                                        input.close();
                                        socket.close();

                                    } catch (Exception ioException) {
                                        ioException.printStackTrace();
                                    }
                                    Toast.makeText(MainActivity.this, "Datos enviados correctamente, token: " + token, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainActivity.this, Votation.class);
                                    intent.putExtra("token", token);
                                    intent.putExtra("votation", votation);
                                    intent.putExtra("user", user);
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
