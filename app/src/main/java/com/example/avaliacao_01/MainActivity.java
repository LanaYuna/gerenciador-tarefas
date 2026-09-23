package com.example.avaliacao_01;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView lista;
    EditText edCategoria;
    ArrayList<Categoria> categorias = new ArrayList<>();
    ArrayAdapter<Categoria> adapter;
    private int posicaoSelecionada = AdapterView.INVALID_POSITION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lista = findViewById(R.id.lista);
        edCategoria = findViewById(R.id.edCategoria);
        adapter = new CategoriaAdapter(this, categorias);
        lista.setAdapter(adapter);

        androidx.appcompat.widget.Toolbar toolbar =
                findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        registerForContextMenu(lista);

        lista.setOnItemClickListener((parent, view, position, id) -> {
            posicaoSelecionada = position;
            Categoria categoria = categorias.get(position);
            abrirContaActivity(categoria);
        });

        lista.setOnItemLongClickListener((parent, view, position, id) -> {
            posicaoSelecionada = position;
            return false; // Retorna false para permitir que o Menu de Contexto abra
        });
    }

    public void adicionar(View v) {
        String categoria = edCategoria.getText().toString();

        if (categoria.isBlank()) {
            Toast.makeText(this, R.string.informeCategoria, Toast.LENGTH_SHORT).show();
            return;
        }

        Categoria ic = new Categoria();
        ic.setDescricao(categoria);

        categorias.add(ic);
        adapter.notifyDataSetChanged();

        edCategoria.setText("");
    }

    private void abrirContaActivity(Categoria categoria) {
        Intent intent = new Intent(MainActivity.this, ContaActivity.class);
        intent.putExtra("CATEGORIA", categoria);
        contaActivityLauncher.launch(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuDetalharCategoria) {

            detalharCategoria();
            return true;

        } else if (id == R.id.menuEditarCategoria) {

            editarCategoria();
            return true;

        } else if (id == R.id.menuRemoverCategoria) {

            removerCategoria();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(R.string.opcoesCategoria);
        menu.add(0, 1, 0, R.string.detalharCategoria);
        menu.add(0, 2, 1, R.string.editarCategoria);
        menu.add(0, 3, 2, R.string.removerCategoria);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (info == null) return super.onContextItemSelected(item);

        Categoria categoria = categorias.get(info.position);

        switch (item.getItemId()) {
            case 1: // Detalhar
                abrirContaActivity(categoria);
                return true;

            case 2: // Editar
                mostrarDialogoEditar(categoria, info.position);
                return true;

            case 3: // Remover
                mostrarDialogoRemover(info.position);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    private void mostrarDialogoEditar(Categoria categoria, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.editarCategoria);

        final EditText input = new EditText(this);
        input.setText(categoria.getDescricao());
        builder.setView(input);

        builder.setPositiveButton(R.string.salvar, (dialog, which) -> {
            String novoNome = input.getText().toString().trim();
            if (!novoNome.isEmpty()) {
                categoria.setDescricao(novoNome);
                adapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, R.string.categoriaAtualizada, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, R.string.valorInvalido, Toast.LENGTH_SHORT).show(); // se for nulo
            }
        });

        builder.setNegativeButton(R.string.cancelar, (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void mostrarDialogoRemover(int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirmarRemocao);

        builder.setPositiveButton(R.string.sim, (dialog, which) -> {
            if (position >= 0 && position < categorias.size()) {
                categorias.remove(position);
                adapter.notifyDataSetChanged();

                // Reseta a seleção para não apontar para o item errado
                lista.clearChoices();
                posicaoSelecionada = AdapterView.INVALID_POSITION;

                Toast.makeText(this, R.string.categoriaRemovida, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(R.string.nao, (dialog, which) -> dialog.cancel());
        builder.show();

    }
    private void detalharCategoria() {
        if (categorias.isEmpty()) {
            Toast.makeText(this, R.string.nenhumaCategoria, Toast.LENGTH_SHORT).show();
            return;
        }

        String[] nomes = new String[categorias.size()];
        for (int i = 0; i < categorias.size(); i++) {
            nomes[i] = categorias.get(i).getDescricao();
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.selecionarCategoria)
                .setItems(nomes, (dialog, which) -> {
                    posicaoSelecionada = which;
                    Categoria categoriaSelecionada = categorias.get(which);
                    abrirContaActivity(categoriaSelecionada);
                })
                .show();
    }

    private void editarCategoria() {
        if (categorias.isEmpty()) {
            Toast.makeText(this, R.string.nenhumaCategoria, Toast.LENGTH_SHORT).show();
            return;
        }

        String[] nomes = new String[categorias.size()];
        for (int i = 0; i < categorias.size(); i++) {
            nomes[i] = categorias.get(i).getDescricao();
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.editarCategoria)
                .setItems(nomes, (dialog, which) -> {
                    Categoria categoriaSelecionada = categorias.get(which);

                    mostrarDialogoEditar(categoriaSelecionada, which);
                })
                .show();
    }

    private void removerCategoria() {
        if (categorias.isEmpty()) {
            Toast.makeText(this, R.string.nenhumaCategoria, Toast.LENGTH_SHORT).show();
            return;
        }

        String[] nomes = new String[categorias.size()];
        for (int i = 0; i < categorias.size(); i++) {
            nomes[i] = categorias.get(i).getDescricao();
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.removerCategoria)
                .setItems(nomes, (dialog, which) -> {
                    mostrarDialogoRemover(which);
                })
                .show();
    }

    private ActivityResultLauncher<Intent> contaActivityLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == RESULT_OK
                                && result.getData() != null) {

                            Categoria categoriaAtualizada =
                                    (Categoria) result.getData()
                                            .getSerializableExtra("CATEGORIA_ATUALIZADA");

                            if (categoriaAtualizada != null
                                    && posicaoSelecionada != AdapterView.INVALID_POSITION) {

                                categorias.set(
                                        posicaoSelecionada,
                                        categoriaAtualizada
                                );

                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
            );

}