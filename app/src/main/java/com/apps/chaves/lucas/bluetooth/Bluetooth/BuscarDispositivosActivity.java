package com.apps.chaves.lucas.bluetooth.Bluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.apps.chaves.lucas.bluetooth.R;

public class BuscarDispositivosActivity extends ListaPareadosActivity {

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_dispositivos);
        //Registra o receiver para recever as mensagens de dispositivos pareados
        this.registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        // Redistra os broadcasts depois que o discovery dor finalizado
        this.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        buscar();
    }

    private void buscar() {
        //Garante que nao existe outra busca sendo realizada
        if (btfAdapter.isDiscovering()) {
            btfAdapter.cancelDiscovery();
        }
        //Inicia a busca
        btfAdapter.startDiscovery();
        dialog = ProgressDialog.show(this, "Exemplo", "Buscando aparelhos bluetooth...", false, true);
    }

    //Receiver para receber os broadcasts do bluetooth
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        //Quantia de dispositivos encontrados
        private int count;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //Se um dispositivo foi encontrado
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //Recupera o device da intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //Apenas insere na lista os devices que ainda nao estao pareados
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    lista.add(device);
                    Toast.makeText(context, "Encontrou: " + device.getName() + ":" + device.getAddress(), Toast.LENGTH_LONG).show();
                    count++;
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //Iniciou a busca
                count = 0;
                Toast.makeText(context, "Busca iniciada.", Toast.LENGTH_SHORT).show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //Terminou a busca
                Toast.makeText(context, "Busca finalizada. " + count + " devices encontrados", Toast.LENGTH_LONG).show();
                dialog.dismiss();
                //Atualiza a listView. Agora vai possuir todos os devices pareados mais os novos que foram encotrados
                updateLista();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Garante que a busca é cancelada ao sair
        if (btfAdapter != null) {
            btfAdapter.cancelDiscovery();
        }
        //Cancela o registro do receiver
        this.unregisterReceiver(mReceiver);
    }
}
