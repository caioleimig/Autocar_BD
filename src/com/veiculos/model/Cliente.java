package com.veiculos.model;

public class Cliente {
    private String cpf, nome, rua, bairro, numero;

    public Cliente() {}
    public Cliente(String cpf, String nome, String rua, String bairro, String numero) {
        this.cpf = cpf; this.nome = nome; this.rua = rua;
        this.bairro = bairro; this.numero = numero;
    }

    public String getCpf()    { return cpf; }
    public String getNome()   { return nome; }
    public String getRua()    { return rua; }
    public String getBairro() { return bairro; }
    public String getNumero() { return numero; }

    public void setCpf(String v)    { cpf = v; }
    public void setNome(String v)   { nome = v; }
    public void setRua(String v)    { rua = v; }
    public void setBairro(String v) { bairro = v; }
    public void setNumero(String v) { numero = v; }
}
