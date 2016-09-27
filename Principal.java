package com.usac.lesterod.ejemploblue;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class Principal extends AppCompatActivity {

    Button btnenviar;
    EditText txtmensaje;
    private ProgressDialog progress;


    String address = "30:14:12:04:32:75";
    BluetoothAdapter miDispositivo = null;
    BluetoothSocket btSocket = null;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        btnenviar=(Button)findViewById(R.id.button);
        txtmensaje = (EditText)findViewById(R.id.editText);
        btnenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarMsg(txtmensaje.getText().toString());
                txtmensaje.setText("");
            }
        });

    }

    private void enviarMsg(String s)
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write(s.getBytes());
            }
            catch (IOException e)
            {
                msg("Error al enviar mensaje");
            }
        }
        else
        {
            msg("Establezca conexion con dispositivo");
        }
    }

    private void desconectar()
    {
        if (btSocket!=null)
        {
            try {
                btSocket.close();
                btSocket=null;
                msg("Conexion Cerrada");
            } catch (IOException e) {
                msg("Error al desconectar");
            }
        }
    }

    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }



    private class ConexionBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(Principal.this, "Conectando...", " Espere un momento");
        }

        @Override
        protected Void doInBackground(Void... devices)
        {
            try
            {
                if (btSocket == null)
                {
                    miDispositivo = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = miDispositivo.getRemoteDevice(address);
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;
                btSocket = null;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            if (!ConnectSuccess) {
                msg("No se puede conectar dispositivo");
            }
            else
            {
                msg("Dispositivo listo");
            }
            progress.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.conectar) {
            new ConexionBT().execute();
        }
        else if( id == R.id.desconectar){
            desconectar();
        }

        return super.onOptionsItemSelected(item);
    }



}
