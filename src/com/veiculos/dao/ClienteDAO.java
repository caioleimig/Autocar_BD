package com.veiculos.dao;

import com.veiculos.model.Cliente;
import com.veiculos.util.DBConnection;
import java.sql.*;
import java.util.*;

public class ClienteDAO {

    public void inserir(Cliente c) throws SQLException {
        String sql = "INSERT INTO Cliente (CPF, Nome, Rua, Bairro, Numero) VALUES (?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getCpf());
            ps.setString(2, c.getNome());
            ps.setString(3, c.getRua());
            ps.setString(4, c.getBairro());
            ps.setString(5, c.getNumero());
            ps.executeUpdate();
        }
    }

    public void atualizar(Cliente c) throws SQLException {
        String sql = "UPDATE Cliente SET Nome=?, Rua=?, Bairro=?, Numero=? WHERE CPF=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNome());
            ps.setString(2, c.getRua());
            ps.setString(3, c.getBairro());
            ps.setString(4, c.getNumero());
            ps.setString(5, c.getCpf());
            ps.executeUpdate();
        }
    }

    public void deletar(String cpf) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM Cliente WHERE CPF=?")) {
            ps.setString(1, cpf);
            ps.executeUpdate();
        }
    }

    public List<Cliente> listarTodos() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM Cliente ORDER BY Nome")) {
            while (rs.next())
                lista.add(new Cliente(rs.getString("CPF"), rs.getString("Nome"),
                    rs.getString("Rua"), rs.getString("Bairro"), rs.getString("Numero")));
        }
        return lista;
    }

    public Cliente buscarPorCpf(String cpf) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM Cliente WHERE CPF=?")) {
            ps.setString(1, cpf);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return new Cliente(rs.getString("CPF"), rs.getString("Nome"),
                        rs.getString("Rua"), rs.getString("Bairro"), rs.getString("Numero"));
            }
        }
        return null;
    }
}
