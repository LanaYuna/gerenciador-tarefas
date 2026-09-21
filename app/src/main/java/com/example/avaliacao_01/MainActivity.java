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

        // Registra o ListView para ter suporte ao Menu de Contexto (clique longo)
        registerForContextMenu(lista);

        // FORMA 1: Clique simples para abrir as contas da Categoria via Intent
        lista.setOnItemClickListener((parent, view, position, id) -> {
            posicaoSelecionada = position;
            Categoria categoria = categorias.get(position);
            abrirContaActivity(categoria);
        });

        // Salva a posição selecionada quando o usuário faz clique longo
        lista.setOnItemLongClickListener((parent, view, position, id) -> {
            posicaoSelecionada = position;
            return false; // Retorna false para permitir que o Menu de Contexto abra
        });
    }

    public void adicionar(View v) {
        String categoria = edCategoria.getText().toString();

        if (categoria.isBlank()) {
            Toast.makeText(this, "Informe a categoria", Toast.LENGTH_SHORT).show();
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
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (posicaoSelecionada == AdapterView.INVALID_POSITION) {
            Toast.makeText(this, "Selecione uma categoria na lista primeiro", Toast.LENGTH_SHORT).show();

            return true;
        }

        Categoria categoria = categorias.get(posicaoSelecionada);

        if (item.getItemId() == R.id.menuDetalharCategoria) {

            abrirContaActivity(categoria);
            return true;

        } else if (item.getItemId() == R.id.menuEditarCategoria) {

            mostrarDialogoEditar(categoria, posicaoSelecionada);
            return true;

        } else if (item.getItemId() == R.id.menuRemoverCategoria) {

            mostrarDialogoRemover(posicaoSelecionada);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Opções da Categoria");
        menu.add(0, 1, 0, "Detalhar contas (Abrir)");
        menu.add(0, 2, 1, "Editar Categoria");
        menu.add(0, 3, 2, "Remover Categoria");
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
        builder.setTitle("Editar Categoria");

        final EditText input = new EditText(this);
        input.setText(categoria.getDescricao());
        builder.setView(input);

        builder.setPositiveButton("Salvar", (dialog, which) -> {
            String novoNome = input.getText().toString().trim();
            if (!novoNome.isEmpty()) {
                categoria.setDescricao(novoNome);
                adapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Categoria atualizada", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "O nome não pode ser vazio", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void mostrarDialogoRemover(int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tem certeza que deseja remover?");

        builder.setPositiveButton("Sim", (dialog, which) -> {
            categorias.remove(position);
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Categoria removida", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Não", (dialog, which) -> dialog.cancel());
        builder.show();

    }
}