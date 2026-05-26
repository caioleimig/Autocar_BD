package com.veiculos.dao;

import com.veiculos.model.Aluguel;
import com.veiculos.util.DBConnection;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AluguelDAO {

    public void inserir(Aluguel a) throws SQLException {
        String sql = "INSERT INTO Aluguel " +
                     "(Data_Retirada, Data_Devolucao, Valor_Total, Km_Saida, Km_Chegada, " +
                     " CPF_Cliente, Chassi_Veiculo) VALUES (?,?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(a.getDataRetirada()));
            ps.setDate(2, a.getDataDevolucao() != null ? Date.valueOf(a.getDataDevolucao()) : null);
            ps.setBigDecimal(3, a.getValorTotal());
            ps.setBigDecimal(4, a.getKmSaida());
            ps.setBigDecimal(5, a.getKmChegada());
            ps.setString(6, a.getCpfCliente());
            ps.setString(7, a.getChassiVeiculo());
            ps.executeUpdate();
        }
    }

    public void atualizar(Aluguel a) throws SQLException {
        String sql = "UPDATE Aluguel SET Data_Retirada=?, Data_Devolucao=?, Valor_Total=?, " +
                     "Km_Saida=?, Km_Chegada=?, CPF_Cliente=?, Chassi_Veiculo=? " +
                     "WHERE Id_Aluguel=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(a.getDataRetirada()));
            ps.setDate(2, a.getDataDevolucao() != null ? Date.valueOf(a.getDataDevolucao()) : null);
            ps.setBigDecimal(3, a.getValorTotal());
            ps.setBigDecimal(4, a.getKmSaida());
            ps.setBigDecimal(5, a.getKmChegada());
            ps.setString(6, a.getCpfCliente());
            ps.setString(7, a.getChassiVeiculo());
            ps.setInt(8, a.getIdAluguel());
            ps.executeUpdate();
        }
    }

    public void deletar(int idAluguel) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "DELETE FROM Aluguel WHERE Id_Aluguel=?")) {
            ps.setInt(1, idAluguel);
            ps.executeUpdate();
        }
    }

    public List<Aluguel> listarTodos() throws SQLException {
        List<Aluguel> lista = new ArrayList<>();
        String sql = "SELECT * FROM Aluguel ORDER BY Data_Retirada DESC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                lista.add(mapear(rs));
        }
        return lista;
    }

    public Aluguel buscarPorId(int id) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT * FROM Aluguel WHERE Id_Aluguel=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public List<Aluguel> buscarPorCliente(String cpf) throws SQLException {
        List<Aluguel> lista = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT * FROM Aluguel WHERE CPF_Cliente=? ORDER BY Data_Retirada DESC")) {
            ps.setString(1, cpf);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<Aluguel> listarAtivos() throws SQLException {
        List<Aluguel> lista = new ArrayList<>();
        String sql = "SELECT * FROM Aluguel WHERE Data_Devolucao IS NULL ORDER BY Data_Retirada DESC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    // ----------------------------------------------------------------
    private Aluguel mapear(ResultSet rs) throws SQLException {
        LocalDate retirada = rs.getDate("Data_Retirada") != null
                ? rs.getDate("Data_Retirada").toLocalDate() : null;
        LocalDate devolucao = rs.getDate("Data_Devolucao") != null
                ? rs.getDate("Data_Devolucao").toLocalDate() : null;
        return new Aluguel(
                rs.getInt("Id_Aluguel"),
                retirada,
                devolucao,
                rs.getBigDecimal("Valor_Total"),
                rs.getBigDecimal("Km_Saida"),
                rs.getBigDecimal("Km_Chegada"),
                rs.getString("CPF_Cliente"),
                rs.getString("Chassi_Veiculo")
        );
    }
}
