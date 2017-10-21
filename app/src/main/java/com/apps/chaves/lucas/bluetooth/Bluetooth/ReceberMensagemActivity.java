package com.apps.chaves.lucas.bluetooth.Bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.apps.chaves.lucas.bluetooth.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class ReceberMensagemActivity extends BluetoothCheckActivity {
    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private boolean running;
    private BluetoothServerSocket serverSocket;
    private BluetoothSocket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receber_mensagem);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Se o bluetooth está ligado, vamos iniciar a Thead do servidor
        if (btfAdapter != null && btfAdapter.isEnabled()) {
            new ThreadServidor().start();
            running = true;
        }
    }

    //Thread para controlar a conexao e nao travar a tela
    public class ThreadServidor extends Thread {
        private Tag TAG;
        @Override
        public void run() {
            super.run();
            try {
                //Abre o socket servidor (quem for conectar precisa utilizar o mesmo UUID)
                BluetoothServerSocket serverSocket = btfAdapter.listenUsingRfcommWithServiceRecord("Bluetooth", uuid);
                //Fica aguardando alguem conectar (Chamada bloqueante)
                try {
                    //Aguarda conexoes
                    socket = serverSocket.accept();
                } catch (Exception e) {
                    //Em caso de erro encerrar aqui
                    return;
                }

                if (socket != null) {
                    //Alguem conectou enstao encerra-se o socket server
                    serverSocket.close();
                    //O socket possui a InputStream e OutputStram para ler e escrever
                    InputStream in = socket.getInputStream();
                    //Recupera o dispositivo cliente que fez a conexao
                    BluetoothDevice device = socket.getRemoteDevice();
                    updateViewConectou(device);
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
                Log.e(String.valueOf(TAG), "Erro no servidor: " + e.getMessage(), e);
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
            if (socket != null) {
                socket.close();
            }
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {

        }
    }
}