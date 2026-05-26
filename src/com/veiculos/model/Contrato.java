package com.veiculos.model;

import java.time.LocalDate;

public class Contrato {

    private int idContrato;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String cpfCliente;
    private int matriculaFuncionario;

    public Contrato() {}

    public Contrato(int idContrato, LocalDate dataInicio, LocalDate dataFim,
                    String cpfCliente, int matriculaFuncionario) {
        this.idContrato = idContrato;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.cpfCliente = cpfCliente;
        this.matriculaFuncionario = matriculaFuncionario;
    }

    public int getIdContrato()                  { return idContrato; }
    public void setIdContrato(int idContrato)   { this.idContrato = idContrato; }

    public LocalDate getDataInicio()                    { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio)     { this.dataInicio = dataInicio; }

    public LocalDate getDataFim()                       { return dataFim; }
    public void setDataFim(LocalDate dataFim)           { this.dataFim = dataFim; }

    public String getCpfCliente()                       { return cpfCliente; }
    public void setCpfCliente(String cpfCliente)        { this.cpfCliente = cpfCliente; }

    public int getMatriculaFuncionario()                        { return matriculaFuncionario; }
    public void setMatriculaFuncionario(int matriculaFuncionario) { this.matriculaFuncionario = matriculaFuncionario; }

    @Override
    public String toString() {
        return "Contrato{id=" + idContrato + ", cliente=" + cpfCliente +
               ", inicio=" + dataInicio + ", fim=" + dataFim + "}";
    }
}
