package com.apps.chaves.lucas.bluetooth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.apps.chaves.lucas.bluetooth.Bluetooth.BluetoothCheckActivity;
import com.apps.chaves.lucas.bluetooth.Bluetooth.BluetoothClienteActivity;
import com.apps.chaves.lucas.bluetooth.Bluetooth.BuscaEnderecoActivity;
import com.apps.chaves.lucas.bluetooth.Bluetooth.BuscarDispositivosActivity;
import com.apps.chaves.lucas.bluetooth.Bluetooth.ListaPareadosActivity;
import com.apps.chaves.lucas.bluetooth.Bluetooth.ReceberMensagemActivity;

public class PrincipalActivity extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
    }

    public void Verifica(View v) {
        intent = new Intent(this, BluetoothCheckActivity.class);
        startActivityForResult(intent, 0);
    }

    public void Pareados(View v) {
        intent = new Intent(this, ListaPareadosActivity.class);
        startActivityForResult(intent, 1);
    }

    public void EncontraConhecido(View v) {
        intent = new Intent(this, BuscarDispositivosActivity.class);
        startActivityForResult(intent, 2);
    }

    public void Buscar(View v) {
        intent = new Intent(this, BuscaEnderecoActivity.class);
        startActivityForResult(intent, 3);
    }

    public void Visivel(View v) {
    }

    public void Server(View v) {
        intent = new Intent(this, ReceberMensagemActivity.class);
        startActivityForResult(intent, 5);
    }

    public void Cliente(View v) {
        intent = new Intent(this, BluetoothClienteActivity.class);
        startActivityForResult(intent, 6);
    }

}
