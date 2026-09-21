package com.example.avaliacao_01;

import java.io.Serializable;
import java.util.Date;

public class Conta implements Serializable {
    private String descricao;
    private Date vencimento;
    private double valor;
    private boolean paga = false;
    private Categoria categoria;


    public Conta(String descricao, double valor, Date vencimento, Categoria categoria) {
        this.descricao = descricao;
        this.vencimento = vencimento;
        this.valor = valor;
        this.categoria = categoria;
        this.paga = false;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public boolean isPaga() {
        return paga;
    }

    public void setPaga(boolean paga) { this.paga = paga; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

}
