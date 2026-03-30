package com.veiculos.model;

public class Veiculo {
    private String chassi, placa, cor, cnpjSeguradora;
    private int anoFabricacao, idMarca;

    public Veiculo() {}
    public Veiculo(String chassi, String placa, String cor, int anoFabricacao, int idMarca, String cnpjSeguradora) {
        this.chassi = chassi; this.placa = placa; this.cor = cor;
        this.anoFabricacao = anoFabricacao; this.idMarca = idMarca;
        this.cnpjSeguradora = cnpjSeguradora;
    }

    public String getChassi()         { return chassi; }
    public String getPlaca()          { return placa; }
    public String getCor()            { return cor; }
    public int    getAnoFabricacao()  { return anoFabricacao; }
    public int    getIdMarca()        { return idMarca; }
    public String getCnpjSeguradora() { return cnpjSeguradora; }

    public void setChassi(String v)          { chassi = v; }
    public void setPlaca(String v)           { placa = v; }
    public void setCor(String v)             { cor = v; }
    public void setAnoFabricacao(int v)      { anoFabricacao = v; }
    public void setIdMarca(int v)            { idMarca = v; }
    public void setCnpjSeguradora(String v)  { cnpjSeguradora = v; }
}
