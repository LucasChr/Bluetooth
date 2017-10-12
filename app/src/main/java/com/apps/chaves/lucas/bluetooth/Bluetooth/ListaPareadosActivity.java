package com.apps.chaves.lucas.bluetooth.Bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.apps.chaves.lucas.bluetooth.R;

import java.util.ArrayList;
import java.util.List;

public class ListaPareadosActivity extends BluetoothCheckActivity implements OnItemClickListener {

    protected List<BluetoothDevice> lista;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_pareados);

        listView = (ListView) findViewById(R.id.listView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Bluetooth adapter é iniciado na classe mãe
        if(btfAdapter != null){
            //Lista dispositivos pareados
            lista = new ArrayList<BluetoothDevice>(btfAdapter.getBondedDevices());
            updateLista();
        }
    }

    public void updateLista(){
        //Cria um array com o nome de cada dispositivo
        List<String> nomes = new ArrayList<String>();
        for(BluetoothDevice device : lista){
            //A variável boolean sempre será true, pois esta lista é somente dos pareados
            boolean pareado = device.getBondState() == BluetoothDevice.BOND_BONDED;
            nomes.add(device.getName() +" - " + device.getAddress() + (pareado ? " *pareado" : ""));
        }
        //Cria um adapter para popular o listView
        int layout = android.R.layout.simple_list_item_1;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, layout, nomes);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int idx, long id) {
        //Recupera o dispositivo selecionado
        BluetoothDevice device = lista.get(idx);
        String msg = device.getName() + " - " + device.getAddress();
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, BluetoothClienteActivity.class);
        intent.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
        startActivity(intent);
    }
}
