package com.apps.chaves.lucas.bluetooth.Bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.apps.chaves.lucas.bluetooth.R;

public class BluetoothCheckActivity extends Activity {

    protected BluetoothAdapter btfAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_chack);
        //Bluetooth adapter
        btfAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btfAdapter == null){
            Toast.makeText(this, "Bluetooth não disponível neste aparelho.", Toast.LENGTH_LONG).show();
            //Fecha a activity neste caso
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Se o bluetooth não está ligado
        if (btfAdapter.isEnabled()){
            Toast.makeText(this, "Bluetooth ligado!", Toast.LENGTH_LONG).show();
        }else {
            //Necessita a permissão (BLUETOOTH_ADMIN) no manifest para funcionar
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Confere se o usuário ativou o bluetooth ou não
        if (resultCode != Activity.RESULT_OK){
            Toast.makeText(this, "Bluetooth não ativado!", Toast.LENGTH_LONG).show();
        }
    }
}
