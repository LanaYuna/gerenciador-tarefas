package com.example.avaliacao_01;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.avaliacao_01.Conta;
import com.example.avaliacao_01.ContaAdapter;
import com.example.avaliacao_01.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ContaActivity extends AppCompatActivity {
    private ContaAdapter adapter;
    private Categoria categoriaAtual;
    private ArrayList<Conta> listaContas;
    private int posicaoSelecionada = AdapterView.INVALID_POSITION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conta);

        categoriaAtual = (Categoria) getIntent().getSerializableExtra("CATEGORIA");
        listaContas = categoriaAtual.getContas();

        EditText etDescricao = findViewById(R.id.etDescricao);
        EditText etValor = findViewById(R.id.etValor);
        EditText etVencimento = findViewById(R.id.etVencimento);
        Button btnOk = findViewById(R.id.btnOk);
        ListView lvDespesas = findViewById(R.id.lvDespesas);
        TextView tvTituloCategoria = findViewById(R.id.tvTituloCategoria);

        androidx.appcompat.widget.Toolbar toolbar =
                findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        tvTituloCategoria.setText(
                getString(
                        R.string.despesasDe,
                        categoriaAtual.getDescricao()
                )
        );

        adapter = new ContaAdapter(this, listaContas);
        lvDespesas.setAdapter(adapter);

        registerForContextMenu(lvDespesas);

        lvDespesas.setOnItemClickListener((parent, view, position, id) -> {
            posicaoSelecionada = position;
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                devolverCategoria();
            }
        });

        btnOk.setOnClickListener(v -> {

            String descricao = etDescricao.getText().toString().trim();
            String valorText = etValor.getText().toString().trim();
            String vencimentoText = etVencimento.getText().toString().trim();

            if (!descricao.isEmpty() && !valorText.isEmpty() && !vencimentoText.isEmpty()) {
                try {
                    double valor = Double.parseDouble(valorText);

                    SimpleDateFormat formato =
                            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                    Date vencimento = formato.parse(vencimentoText);

                    Conta novaConta = new Conta (descricao, valor, vencimento, categoriaAtual);

                    categoriaAtual.adicionarConta(novaConta);
                    adapter.notifyDataSetChanged();
                    etDescricao.setText("");
                    etValor.setText("");
                    etVencimento.setText("");

                } catch (ParseException e) {
                    Toast.makeText(this, "Data inválida. Use dd/MM/yyyy", Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Valor inválido", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_conta, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            devolverCategoria();
            return true;
        }

        if (posicaoSelecionada == AdapterView.INVALID_POSITION) {
            Toast.makeText(this, "Selecione uma conta primeiro", Toast.LENGTH_SHORT).show();
            return true;
        }

        Conta conta = listaContas.get(posicaoSelecionada);

        if (item.getItemId() == R.id.menuEditarConta) {
            mostrarDialogoEditar(conta, posicaoSelecionada);
            return true;
        } else if (item.getItemId() == R.id.menuMarcarPaga) {
            conta.setPaga(true);
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Conta marcada como paga", Toast.LENGTH_SHORT).show();
            return true;
        } else if (item.getItemId() == R.id.menuRemoverConta) {
            mostrarDialogoRemover(conta, posicaoSelecionada);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle("Opções da Conta");

        menu.add(0, 1, 0, "Editar conta");
        menu.add(0, 2, 1, "Marcar como paga");
        menu.add(0, 3, 2, "Remover conta");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if (info == null) {
            return super.onContextItemSelected(item);
        }

        Conta conta = listaContas.get(info.position);

        switch (item.getItemId()) {

            case 1:
                mostrarDialogoEditar(conta, info.position);
                return true;

            case 2:
                conta.setPaga(true);
                adapter.notifyDataSetChanged();

                Toast.makeText(this, "Conta marcada como paga", Toast.LENGTH_SHORT).show();
                return true;

            case 3:
                mostrarDialogoRemover(conta, info.position);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    public void mostrarDialogoEditar(Conta conta, int position){

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 10, 40, 10);

        EditText descricao = new EditText(this);
        descricao.setHint("Descrição");
        descricao.setText(conta.getDescricao());

        EditText valor = new EditText(this);
        valor.setHint("Valor");
        valor.setInputType(android.text.InputType.TYPE_CLASS_NUMBER |
                android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        valor.setText(String.valueOf(conta.getValor()));

        EditText vencimento = new EditText(this);
        vencimento.setHint("Vencimento");
        vencimento.setInputType(android.text.InputType.TYPE_CLASS_DATETIME);

        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        vencimento.setText(formato.format(conta.getVencimento()));

        layout.addView(descricao);
        layout.addView(valor);
        layout.addView(vencimento);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Editar Conta");
        builder.setView(layout);

        builder.setPositiveButton("Salvar", (dialog, which) -> {

            try {

                conta.setDescricao(descricao.getText().toString().trim());

                conta.setValor(Double.parseDouble(valor.getText().toString().trim()));

                conta.setVencimento(formato.parse(vencimento.getText().toString().trim()));

                adapter.notifyDataSetChanged();

                Toast.makeText(this, "Conta atualizada", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {

                Toast.makeText(this, "Dados inválidos", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel()
        );

        builder.show();
    }

    public void mostrarDialogoRemover(Conta conta, int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Tem certeza que deseja remover esta conta?");

        builder.setPositiveButton("Sim", (dialog, which) -> {

            listaContas.remove(position);
            adapter.notifyDataSetChanged();

            Toast.makeText(this, "Conta removida", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Não", (dialog, which) -> dialog.cancel()
        );

        builder.show();
    }

    private void devolverCategoria() {
        Intent intent = new Intent();

        intent.putExtra(
                "CATEGORIA_ATUALIZADA",
                categoriaAtual
        );

        setResult(RESULT_OK, intent);
        finish();
    }

}