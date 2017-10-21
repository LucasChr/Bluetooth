package com.apps.chaves.lucas.bluetooth.Bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.chaves.lucas.bluetooth.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothClienteActivity extends AppCompatActivity {
    private static final String TAG = "bluetooth";
    //Precisa abrir o mesmo UUID que o servidor utilizou para abrir o socket servidor
    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothDevice device;
    private TextView tMsg, tmsg;
    private OutputStream out;
    BluetoothSocket socket;

    Button btnConectar, btnEnviar;
    BluetoothAdapter btfAdapter = null;
    BluetoothDevice btfDevice = null;
    BluetoothSocket btfSocket = null;
    boolean conexao = false;
    private static final int SOLICITA_ATIVACAO = 1;
    private static final int SOLICITA_CONEXAO = 2;
    private static String MAC = null;
    UUID btf_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Intent intent;
    private boolean running;
    private BluetoothServerSocket serverSocket;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_cliente);

        btnConectar = (Button) findViewById(R.id.btnConectar);
        btnEnviar = (Button) findViewById(R.id.btnEnviar);
        tMsg = (TextView) findViewById(R.id.tvMsg);
        btfAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btfAdapter == null) {
            Toast.makeText(getApplicationContext(), "O dispositivo não tem bluetooth", Toast.LENGTH_LONG).show();
        } else if (!btfAdapter.isEnabled()) {
            Intent it = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(it, SOLICITA_ATIVACAO);
        }

        btnConectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (conexao) {
                    //desconectar
                    try {
                        btfSocket.close();
                        conexao = false;
                        btnConectar.setText("Conectar");
                        Toast.makeText(getApplicationContext(), "Bluetooth desconectado", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "Ocorreu um erro: " + e, Toast.LENGTH_LONG).show();
                    }
                } else {
                    //conectar
                    Intent it = new Intent(BluetoothClienteActivity.this, ListaDispositivos.class);
                    startActivityForResult(it, SOLICITA_CONEXAO);
                }
            }
        });
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tmsg = (TextView) findViewById(R.id.tvMsg);
                String msg = tmsg.toString();
                try {
                    out = socket.getOutputStream();
                    if (out != null) {
                        out.write(msg.getBytes());
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Erro ao escrever: " + e.getMessage(), e);
                    error(e);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SOLICITA_ATIVACAO:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(getApplicationContext(), "Bluetooth ativado!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth não ativado neste aparelho", Toast.LENGTH_LONG).show();
                }
                break;
            case SOLICITA_CONEXAO:
                if (resultCode == Activity.RESULT_OK) {
                    MAC = data.getExtras().getString(ListaDispositivos.ENDERECO_MAC);
                    btfDevice = btfAdapter.getRemoteDevice(MAC);
                    try {
                        btfSocket = btfDevice.createRfcommSocketToServiceRecord(btf_UUID);
                        btfSocket.connect();
                        conexao = true;
                        btnConectar.setText("Desconectar");
                        findViewById(R.id.btnEnviar).setEnabled(true);

                        if (btfAdapter != null && btfAdapter.isEnabled()) {
                            new ThreadServidor().start();
                            running = true;
                        }
                        Toast.makeText(getApplicationContext(), "Conecado com: " + MAC, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        conexao = false;
                        Toast.makeText(getApplicationContext(), "Ocorreu um erro: " + e, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Falha ao obter MAC!", Toast.LENGTH_LONG).show();
                }
        }
    }

    private View.OnClickListener onClickEnviar() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = tMsg.getText().toString();
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

    //Thread para controlar a conexao e nao travar a tela
    public class ThreadServidor extends Thread {
        private String TAG = "Control car";

        @Override
        public void run() {
            super.run();
            try {
                //Abre o socket servidor (quem for conectar precisa utilizar o mesmo UUID)
                BluetoothServerSocket serverSocket = btfAdapter.listenUsingRfcommWithServiceRecord("Bluetooth", btf_UUID);
                //Fica aguardando alguem conectar (Chamada bloqueante)
                try {
                    //Aguarda conexoes
                    btfSocket = serverSocket.accept();
                } catch (Exception e) {
                    //Em caso de erro encerrar aqui
                    return;
                }

                if (btfSocket != null) {
                    //Alguem conectou entao encerra-se o socket server
                    serverSocket.close();
                    //O socket possui a InputStream e OutputStram para ler e escrever
                    InputStream in = btfSocket.getInputStream();
                    //Recupera o dispositivo cliente que fez a conexao
                    btfDevice = btfSocket.getRemoteDevice();
                    updateViewConectou(btfDevice);
                    byte[] bytes = new byte[1024];
                    int length;
                    //Fica em loop para receber as mensagens
                    while (running) {
                        //Le a mensagem (chamada bloqueada até alguem escrever)
                        length = in.read(bytes);
                        String mensagemRecebida = new String(bytes, 0, length);
                        TextView tvMsg = (TextView) findViewById(R.id.tvMsg);
                        final String s = tvMsg.getText().toString() + mensagemRecebida + "\n";
                        updateViewMensagem(s);
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Erro no servidor: " + e.getMessage(), e);
                running = false;
            }
        }
    }

    //Exibe a mensagem na tela, informando o nome do dispositivo que fez a conexao
    private void updateViewConectou(final BluetoothDevice device) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView tvNome = (TextView) findViewById(R.id.tvNomeDispositivo);
                tvNome.setText(device.getName() + " - " + device.getAddress());
            }
        });
    }

    //Exibe a mensagem recebida na tela (Enviada pelo outro dispositivo)
    private void updateViewMensagem(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView tvMsg = (TextView) findViewById(R.id.tvMsg);
                tvMsg.setText(s);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        running = false;
        try {
            if (btfSocket != null) {
                btfSocket.close();
            }
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {

        }
    }

}
