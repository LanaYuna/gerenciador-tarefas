package com.example.avaliacao_01;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView lista;
    EditText ed_categoria;
    ArrayList<Categoria> categorias = new ArrayList<>();
    ArrayAdapter<Categoria> adapter;

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
        ed_categoria = findViewById(R.id.ed_categoria);
        adapter = new CategoriaAdapter(this, categorias);
        lista.setAdapter(adapter);

    }

    public void adicionar(View v){
        String categoria = ed_categoria.getText().toString();

        if(categoria.isBlank()){
            Toast.makeText(this, "Informe a categoria",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Categoria ic = new Categoria();
        ic.setDescricao(categoria);

        categorias.add(ic);
        adapter.notifyDataSetChanged();

        ed_categoria.setText("");

    }
}