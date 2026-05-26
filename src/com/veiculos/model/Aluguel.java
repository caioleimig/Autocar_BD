package com.veiculos.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Aluguel {

    private int idAluguel;
    private LocalDate dataRetirada;
    private LocalDate dataDevolucao;   // pode ser null (ainda não devolvido)
    private BigDecimal valorTotal;     // pode ser null
    private BigDecimal kmSaida;
    private BigDecimal kmChegada;      // pode ser null
    private String cpfCliente;
    private String chassiVeiculo;

    public Aluguel() {}

    public Aluguel(int idAluguel, LocalDate dataRetirada, LocalDate dataDevolucao,
                   BigDecimal valorTotal, BigDecimal kmSaida, BigDecimal kmChegada,
                   String cpfCliente, String chassiVeiculo) {
        this.idAluguel = idAluguel;
        this.dataRetirada = dataRetirada;
        this.dataDevolucao = dataDevolucao;
        this.valorTotal = valorTotal;
        this.kmSaida = kmSaida;
        this.kmChegada = kmChegada;
        this.cpfCliente = cpfCliente;
        this.chassiVeiculo = chassiVeiculo;
    }

    public int getIdAluguel()                       { return idAluguel; }
    public void setIdAluguel(int idAluguel)         { this.idAluguel = idAluguel; }

    public LocalDate getDataRetirada()                      { return dataRetirada; }
    public void setDataRetirada(LocalDate dataRetirada)     { this.dataRetirada = dataRetirada; }

    public LocalDate getDataDevolucao()                     { return dataDevolucao; }
    public void setDataDevolucao(LocalDate dataDevolucao)   { this.dataDevolucao = dataDevolucao; }

    public BigDecimal getValorTotal()                       { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal)        { this.valorTotal = valorTotal; }

    public BigDecimal getKmSaida()                          { return kmSaida; }
    public void setKmSaida(BigDecimal kmSaida)              { this.kmSaida = kmSaida; }

    public BigDecimal getKmChegada()                        { return kmChegada; }
    public void setKmChegada(BigDecimal kmChegada)          { this.kmChegada = kmChegada; }

    public String getCpfCliente()                           { return cpfCliente; }
    public void setCpfCliente(String cpfCliente)            { this.cpfCliente = cpfCliente; }

    public String getChassiVeiculo()                        { return chassiVeiculo; }
    public void setChassiVeiculo(String chassiVeiculo)      { this.chassiVeiculo = chassiVeiculo; }

    @Override
    public String toString() {
        return "Aluguel{id=" + idAluguel + ", cliente=" + cpfCliente +
               ", veiculo=" + chassiVeiculo + ", retirada=" + dataRetirada + "}";
    }
}
