package com.example.avaliacao_01;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.avaliacao_01.Categoria;

import java.util.ArrayList;

public class CategoriaAdapter extends ArrayAdapter<Categoria> {

    public CategoriaAdapter(Context context, ArrayList<Categoria> categorias) {
        super(context, 0, categorias);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_categoria, parent, false);
        }

        Categoria categoria = getItem(position);

        TextView tvDescricao = convertView.findViewById(R.id.tvDescricaoCategoria);
        TextView tvQuantidade = convertView.findViewById(R.id.tvQuantidade);
        TextView tvValorTotal = convertView.findViewById(R.id.tvValorTotal);
        TextView tvTotalPago = convertView.findViewById(R.id.tvTotalPago);
        TextView tvRestante = convertView.findViewById(R.id.tvRestante);

        if(categoria != null){
            tvDescricao.setText(categoria.getDescricao());
            Context context = parent.getContext();

            tvQuantidade.setText(context.getString(R.string.quantidadeAdapter, categoria.getQuantidadeContas()));
            tvValorTotal.setText(context.getString(R.string.valorAdapter, categoria.getValorTotal()));
            tvTotalPago.setText(context.getString(R.string.pagoAdapter, categoria.getTotalPago()));
            tvRestante.setText(context.getString(R.string.restanteAdapter, categoria.getRestante()));
        }

        return convertView;
    }
}