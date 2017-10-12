package com.apps.chaves.lucas.bluetooth.Bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.chaves.lucas.bluetooth.R;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothClienteActivity extends AppCompatActivity {
    private static final String TAG = "bluetooth";
    //Precisa abrir o mesmo UUID que o servidor utilizou para abrir o socket servidor
    private static final UUID uuid = UUID.fromString("d643d111-f8b5-422f-a2ed-4fbcbdf26252");
    private BluetoothDevice device;
    private TextView tvMsg;
    private OutputStream out;
    private BluetoothSocket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_cliente);
        tvMsg = (TextView) findViewById(R.id.tvMsg);
        //Device selecionado na lista
        device = getIntent().getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        TextView tvNome = (TextView) findViewById(R.id.tvNomeDispositivo);
        findViewById(R.id.btnConectar).setOnClickListener(onClickConectar());
        findViewById(R.id.btnEnviar).setOnClickListener(onClickEnviar());
    }

    private View.OnClickListener onClickConectar() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //Faz conexao utilizando o mesmo UUID que o servidor utilizou
                    socket = device.createRfcommSocketToServiceRecord(uuid);
                    socket.connect();
                    out = socket.getOutputStream();
                    //Se chegou aqui é porque conectou
                    if (out != null) {
                        //Habilita o botão para enviar mensagens
                        findViewById(R.id.btnConectar).setEnabled(false);
                        findViewById(R.id.btnEnviar).setEnabled(true);
                    }
                } catch (IOException e) {
                    error(e);
                    Log.e(TAG, "Erro ao conectar: " + e.getMessage(), e);
                }
            }
        };
    }

    private View.OnClickListener onClickEnviar() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = tvMsg.getText().toString();
                try {
                    if (out != null) {
                        out.write(msg.getBytes());
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Erro ao escrever: " + e.getMessage(), e);
                    error(e);
                }
            }
        };
    }

    private void error(final IOException e) {
        Log.e(TAG, "Erro no client: " + e.getMessage(), e);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
        }
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
        }
    }
}
