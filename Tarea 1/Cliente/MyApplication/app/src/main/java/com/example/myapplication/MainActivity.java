package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.IntRange;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
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
    protected static String server = "192.168.1.133";
    protected static int port = 7070;

    @IntRange(from=1,to=300)
    int camas;

    EditText camasInput;

    @IntRange(from=1,to=300)
    int mesas;

    EditText mesasInput;

    @IntRange(from=1,to=300)
    int sillas;

    EditText sillasInput;

    @IntRange(from=1,to=300)
    int sillones;

    EditText sillonesInput;

    int user;

    EditText userInput;

    String mensaje;

    String mensajeenviar;

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

        camasInput = (EditText) findViewById(R.id.camasInput);
        mesasInput = (EditText) findViewById(R.id.mesasInput);
        sillasInput = (EditText) findViewById(R.id.sillasInput);
        sillonesInput = (EditText) findViewById(R.id.sillonesInput);
        userInput = (EditText) findViewById(R.id.userInput);
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
        CheckBox sabanas = (CheckBox) findViewById(R.id.checkBox_sabanas);


        if (!sabanas.isChecked()) {
            // Mostramos un mensaje emergente;
            Toast.makeText(getApplicationContext(), "Debes confirmar el envío", Toast.LENGTH_SHORT).show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Enviar")
                    .setMessage("Se va a proceder al envio")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                // Catch ok button and send information
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    if (android.os.Build.VERSION.SDK_INT > 9)
                                    {
                                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                        StrictMode.setThreadPolicy(policy);
                                    }

                                    // 1. Extraer los datos de la vista

                                    camas = Integer.parseInt(camasInput.getText().toString());
                                    sillas = Integer.parseInt(sillasInput.getText().toString());
                                    mesas = Integer.parseInt(mesasInput.getText().toString());
                                    sillones = Integer.parseInt(sillonesInput.getText().toString());
                                    user = Integer.parseInt(userInput.getText().toString());

                                    mensaje = camas  + "," + sillas + "," + sillones + "," + mesas + "," + user + "";

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
                                    }

                                    catch(Exception e){
                                        e.printStackTrace();
                                    }

                                    // 3. Enviar los datos

                                    try {

                                        SocketFactory socketFactory = (SocketFactory) SocketFactory.getDefault();

                                        Socket socket = (Socket) socketFactory.createSocket("192.168.1.134", 8088);

                                        PrintWriter output = new PrintWriter(new OutputStreamWriter(
                                                socket.getOutputStream()));

                                        output.println(mensajeenviar);
                                        output.flush();


                                        output.close();
                                        socket.close();

                                    } catch (Exception ioException) {
                                        ioException.printStackTrace();
                                    }
                                    Toast.makeText(MainActivity.this, "Solicitud procesada con éxito", Toast.LENGTH_SHORT).show();
                                }
                            }

                    )
                    .

                            setNegativeButton(android.R.string.no, null)

                    .

                            show();
        }
    }


}
