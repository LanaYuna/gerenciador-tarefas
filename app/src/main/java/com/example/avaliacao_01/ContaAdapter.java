package com.example.avaliacao_01;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import java.util.List;
import java.util.Locale;

public class ContaAdapter extends ArrayAdapter<Conta> {

    public ContaAdapter(Context context, List<Conta> contas) {
        super(context, 0, contas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_conta, parent, false);
        }

        Conta conta = getItem(position);

        TextView tvNome = itemView.findViewById(R.id.tvNomeDespesa);
        TextView tvData = itemView.findViewById(R.id.tvDataVencimento);
        TextView tvValor = itemView.findViewById(R.id.tvValorDespesa);
        CheckBox cbPaga = itemView.findViewById(R.id.cbPaga);

        if (conta != null) {
            tvNome.setText(conta.getDescricao());
            tvData.setText(conta.getVencimento());
            tvValor.setText(String.format(Locale.getDefault(), "R$ %.2f", conta.getValor()));

            cbPaga.setOnCheckedChangeListener(null);
            cbPaga.setChecked(conta.isPaga());

            cbPaga.setOnCheckedChangeListener((buttonView, isChecked) -> conta.setPaga(isChecked));
        }

        return itemView;
    }
}