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

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_conta);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
                    Toast.makeText(this, R.string.dataInvalida, Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, R.string.valorInvalido ,Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, R.string.preencherCampos, Toast.LENGTH_SHORT).show();
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
        int id = item.getItemId();

        if (id == android.R.id.home) {
            devolverCategoria();
            return true;
        } else if (id == R.id.menuEditarConta) {
            editarConta();
            return true;
        } else if (id == R.id.menuMarcarPaga) {
            marcarContaComoPaga();
            return true;
        } else if (id == R.id.menuRemoverConta) {
            removerConta();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle(R.string.opcoesConta);

        menu.add(0, 1, 0, R.string.editarConta);
        menu.add(0, 2, 1, R.string.pagarConta);
        menu.add(0, 3, 2, R.string.removerConta);
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

                Toast.makeText(this, R.string.contaPaga, Toast.LENGTH_SHORT).show();
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
        descricao.setHint(R.string.descricao);
        descricao.setText(conta.getDescricao());

        EditText valor = new EditText(this);
        valor.setHint(R.string.valor);
        valor.setInputType(android.text.InputType.TYPE_CLASS_NUMBER |
                android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        valor.setText(String.valueOf(conta.getValor()));

        EditText vencimento = new EditText(this);
        vencimento.setHint(R.string.vencimento);
        vencimento.setInputType(android.text.InputType.TYPE_CLASS_DATETIME);

        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        vencimento.setText(formato.format(conta.getVencimento()));

        layout.addView(descricao);
        layout.addView(valor);
        layout.addView(vencimento);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.editarConta);
        builder.setView(layout);

        builder.setPositiveButton(R.string.salvar, (dialog, which) -> {

            try {

                conta.setDescricao(descricao.getText().toString().trim());

                conta.setValor(Double.parseDouble(valor.getText().toString().trim()));

                conta.setVencimento(formato.parse(vencimento.getText().toString().trim()));

                adapter.notifyDataSetChanged();

                Toast.makeText(this, R.string.contaAtualizada, Toast.LENGTH_SHORT).show();

            } catch (Exception e) {

                Toast.makeText(this, R.string.dadosInvalidos, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(R.string.cancelar, (dialog, which) -> dialog.cancel()
        );

        builder.show();
    }

    public void mostrarDialogoRemover(Conta conta, int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.confirmarRemocao);

        builder.setPositiveButton(R.string.sim, (dialog, which) -> {

            listaContas.remove(position);
            adapter.notifyDataSetChanged();

            Toast.makeText(this, R.string.contaRemovida, Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton(R.string.nao, (dialog, which) -> dialog.cancel()
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
    private void editarConta() {
        if (listaContas.isEmpty()) {
            Toast.makeText(this, R.string.nenhumaConta, Toast.LENGTH_SHORT).show();
            return;
        }

        String[] nomes = new String[listaContas.size()];
        for (int i = 0; i < listaContas.size(); i++) {
            nomes[i] = listaContas.get(i).getDescricao();
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.selecionarContaEditar)
                .setItems(nomes, (dialog, which) -> {
                    Conta contaSelecionada = listaContas.get(which);
                    mostrarDialogoEditar(contaSelecionada, which);
                })
                .show();
    }

    private void marcarContaComoPaga() {
        if (listaContas.isEmpty()) {
            Toast.makeText(this, R.string.nenhumaConta, Toast.LENGTH_SHORT).show();
            return;
        }

        String[] nomes = new String[listaContas.size()];
        for (int i = 0; i < listaContas.size(); i++) {
            nomes[i] = listaContas.get(i).getDescricao();
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.selecionarContaMarcarPaga)
                .setItems(nomes, (dialog, which) -> {
                    Conta conta = listaContas.get(which);
                    conta.setPaga(true);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, R.string.contaPaga, Toast.LENGTH_SHORT).show();
                })
                .show();
    }


    private void removerConta() {
        if (listaContas.isEmpty()) {
            Toast.makeText(this, R.string.nenhumaConta, Toast.LENGTH_SHORT).show();
            return;
        }

        String[] nomes = new String[listaContas.size()];
        for (int i = 0; i < listaContas.size(); i++) {
            nomes[i] = listaContas.get(i).getDescricao();
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.selecionarContaRemover)
                .setItems(nomes, (dialog, which) -> {
                    Conta contaSelecionada = listaContas.get(which);
                    mostrarDialogoRemover(contaSelecionada, which);
                })
                .show();
    }

}