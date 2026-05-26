package com.veiculos.dao;

import com.veiculos.model.Contrato;
import com.veiculos.util.DBConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ContratoDAO {

    public void inserir(Contrato c) throws SQLException {
        String sql = "INSERT INTO Contrato (Data_Inicio, Data_Fim, CPF_Cliente, Matricula_Funcionario) " +
                     "VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(c.getDataInicio()));
            ps.setDate(2, Date.valueOf(c.getDataFim()));
            ps.setString(3, c.getCpfCliente());
            ps.setInt(4, c.getMatriculaFuncionario());
            ps.executeUpdate();
        }
    }

    public void atualizar(Contrato c) throws SQLException {
        String sql = "UPDATE Contrato SET Data_Inicio=?, Data_Fim=?, CPF_Cliente=?, " +
                     "Matricula_Funcionario=? WHERE ID_Contrato=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(c.getDataInicio()));
            ps.setDate(2, Date.valueOf(c.getDataFim()));
            ps.setString(3, c.getCpfCliente());
            ps.setInt(4, c.getMatriculaFuncionario());
            ps.setInt(5, c.getIdContrato());
            ps.executeUpdate();
        }
    }

    public void deletar(int idContrato) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "DELETE FROM Contrato WHERE ID_Contrato=?")) {
            ps.setInt(1, idContrato);
            ps.executeUpdate();
        }
    }

    public List<Contrato> listarTodos() throws SQLException {
        List<Contrato> lista = new ArrayList<>();
        String sql = "SELECT * FROM Contrato ORDER BY Data_Inicio DESC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                lista.add(mapear(rs));
        }
        return lista;
    }

    public Contrato buscarPorId(int id) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT * FROM Contrato WHERE ID_Contrato=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public List<Contrato> buscarPorCliente(String cpf) throws SQLException {
        List<Contrato> lista = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT * FROM Contrato WHERE CPF_Cliente=? ORDER BY Data_Inicio DESC")) {
            ps.setString(1, cpf);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    // ----------------------------------------------------------------
    private Contrato mapear(ResultSet rs) throws SQLException {
        LocalDate inicio = rs.getDate("Data_Inicio") != null
                ? rs.getDate("Data_Inicio").toLocalDate() : null;
        LocalDate fim = rs.getDate("Data_Fim") != null
                ? rs.getDate("Data_Fim").toLocalDate() : null;
        return new Contrato(
                rs.getInt("ID_Contrato"),
                inicio,
                fim,
                rs.getString("CPF_Cliente"),
                rs.getInt("Matricula_Funcionario")
        );
    }
}
