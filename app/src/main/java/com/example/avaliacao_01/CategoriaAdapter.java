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

        TextView tvDescricao = convertView.findViewById(R.id.tv_descricao_categoria);
        TextView tvQuantidade = convertView.findViewById(R.id.tv_quantidade);
        TextView tvValorTotal = convertView.findViewById(R.id.tv_valor_total);
        TextView tvTotalPago = convertView.findViewById(R.id.tv_total_pago);
        TextView tvRestante = convertView.findViewById(R.id.tv_restante);

        tvDescricao.setText(categoria.getDescricao());

        tvQuantidade.setText("Quantidade: " + categoria.getQuantidadeContas());
        tvValorTotal.setText("Total: R$ " + categoria.getValorTotal());
        tvTotalPago.setText( "Pago: R$ " + categoria.getTotalPago());
        tvRestante.setText("Restante: R$ " + categoria.getRestante());

        return convertView;
    }
}