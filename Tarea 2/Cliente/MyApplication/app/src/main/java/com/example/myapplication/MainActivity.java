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

import androidx.appcompat.app.AppCompatActivity;

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

    String idUsuario;
    EditText idUsuarioInput;

    String idVotacion;
    EditText idVotacionInput;

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

        idUsuario = (EditText) findViewById(R.id.idUsuarioInput);
        idVotacion = (EditText) findViewById(R.id.idVotacionInput);

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

    // Creación de un cuadro de dialogo para confirmar datos
    private void showDialog() throws Resources.NotFoundException {
        CheckBox sabanas = (CheckBox) findViewById(R.id.checkBox_sabanas);

        if (!sabanas.isChecked()) {
            // Mostramos un mensaje emergente;
            Toast.makeText(getApplicationContext(), "Selecciona al menos un elemento", Toast.LENGTH_SHORT).show();
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

                                    idUsuario = idUsuarioInput.getText().toString();
                                    idVotacion = idVotacionInput.getText().toString();

                                    String mensaje = idUsuario+", "+idVotacion;

                                    // 2. Firmar los datos

                                    try{

                                        KeyPair kp = getRSAKeyPair();
                                        PrivateKey privateKey = kp.getPrivate();


                                        Signature sg = Signature.getInstance("SHA256withRSA");
                                        sg.initSign(privateKey);
                                        sg.update(mensaje.getBytes());
                                        byte[] firma = sg.sign();

                                        String firmaMensaje = Base64.encodeToString(firma, Base64.DEFAULT);

                                        String mensajeenviar = mensaje+"/"+firmaMensaje;
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

                                    Toast.makeText(MainActivity.this, "Datos enviados correctamente", Toast.LENGTH_SHORT).show();
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
