package com.example.avaliacao_01;

import java.io.Serializable;
import java.util.ArrayList;

public class Categoria implements Serializable {

    private String descricao;
    private ArrayList<Conta> contas = new ArrayList<>();

    @Override
    public String toString() {
        return descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public ArrayList<Conta> getContas() {
        return contas;
    }
}