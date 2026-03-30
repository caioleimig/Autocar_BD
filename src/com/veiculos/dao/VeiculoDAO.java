package com.veiculos.dao;

import com.veiculos.model.Veiculo;
import com.veiculos.util.DBConnection;
import java.sql.*;
import java.util.*;

public class VeiculoDAO {

    public void inserir(Veiculo v) throws SQLException {
        String sql = "INSERT INTO Veiculo (Chassi, Placa, Cor, Ano_Fabricacao, Id_Marca, CNPJ_Seguradora) VALUES (?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getChassi());
            ps.setString(2, v.getPlaca());
            ps.setString(3, v.getCor());
            ps.setInt(4, v.getAnoFabricacao());
            ps.setInt(5, v.getIdMarca());
            ps.setString(6, v.getCnpjSeguradora());
            ps.executeUpdate();
        }
    }

    public void atualizar(Veiculo v) throws SQLException {
        String sql = "UPDATE Veiculo SET Placa=?, Cor=?, Ano_Fabricacao=?, Id_Marca=?, CNPJ_Seguradora=? WHERE Chassi=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getPlaca());
            ps.setString(2, v.getCor());
            ps.setInt(3, v.getAnoFabricacao());
            ps.setInt(4, v.getIdMarca());
            ps.setString(5, v.getCnpjSeguradora());
            ps.setString(6, v.getChassi());
            ps.executeUpdate();
        }
    }

    public void deletar(String chassi) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM Veiculo WHERE Chassi=?")) {
            ps.setString(1, chassi);
            ps.executeUpdate();
        }
    }

    public List<Veiculo> listarTodos() throws SQLException {
        List<Veiculo> lista = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM Veiculo ORDER BY Placa")) {
            while (rs.next())
                lista.add(new Veiculo(rs.getString("Chassi"), rs.getString("Placa"),
                    rs.getString("Cor"), rs.getInt("Ano_Fabricacao"),
                    rs.getInt("Id_Marca"), rs.getString("CNPJ_Seguradora")));
        }
        return lista;
    }

    public Veiculo buscarPorChassi(String chassi) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM Veiculo WHERE Chassi=?")) {
            ps.setString(1, chassi);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return new Veiculo(rs.getString("Chassi"), rs.getString("Placa"),
                        rs.getString("Cor"), rs.getInt("Ano_Fabricacao"),
                        rs.getInt("Id_Marca"), rs.getString("CNPJ_Seguradora"));
            }
        }
        return null;
    }
}
