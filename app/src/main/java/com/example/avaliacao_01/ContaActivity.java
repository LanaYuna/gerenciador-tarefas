package com.example.avaliacao_01;

import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.avaliacao_01.Conta;
import com.example.avaliacao_01.ContaAdapter;
import com.example.avaliacao_01.R;

import java.util.ArrayList;

public class ContaActivity extends AppCompatActivity {

    private ArrayList<Conta> listaContas = new ArrayList<>();
    private ContaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conta);

        EditText etDescricao = findViewById(R.id.etDescricao);
        EditText etValor = findViewById(R.id.etValor);
        EditText etVencimento = findViewById(R.id.etVencimento);
        Button btnOk = findViewById(R.id.btnOk);
        ListView lvDespesas = findViewById(R.id.lvDespesas);

        adapter = new ContaAdapter(this, listaContas);
        lvDespesas.setAdapter(adapter);

        btnOk.setOnClickListener(v -> {
            String descricao = etDescricao.getText().toString().trim();
            String valorText = etValor.getText().toString().trim();
            String vencimento = etVencimento.getText().toString().trim();

            if (!descricao.isEmpty() && !valorText.isEmpty() && !vencimento.isEmpty()) {
                double valor = Double.parseDouble(valorText);

                Conta novaConta = new Conta(descricao, valor, vencimento, false);
                listaContas.add(novaConta);
                adapter.notifyDataSetChanged();

                etDescricao.setText("");
                etValor.setText("");
                etVencimento.setText("");
            } else {
                Toast.makeText(ContaActivity.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_conta, menu);
        return true;
    }
}