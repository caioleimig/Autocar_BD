package com.veiculos.controller;

import com.veiculos.dao.ClienteDAO;
import com.veiculos.dao.VeiculoDAO;
import com.veiculos.model.Cliente;
import com.veiculos.model.Veiculo;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainApp extends Application {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final VeiculoDAO veiculoDAO = new VeiculoDAO();

    // Cliente fields
    private final TextField tfCpf    = new TextField();
    private final TextField tfNome   = new TextField();
    private final TextField tfRua    = new TextField();
    private final TextField tfBairro = new TextField();
    private final TextField tfNumero = new TextField();
    private final ObservableList<Cliente> clienteData = FXCollections.observableArrayList();
    private TableView<Cliente> tabelaCliente;

    // Veiculo fields
    private final TextField tfChassi  = new TextField();
    private final TextField tfPlaca   = new TextField();
    private final TextField tfCor     = new TextField();
    private final TextField tfAno     = new TextField();
    private final TextField tfIdMarca = new TextField();
    private final TextField tfCnpj    = new TextField();
    private final ObservableList<Veiculo> veiculoData = FXCollections.observableArrayList();
    private TableView<Veiculo> tabelaVeiculo;

    @Override
    public void start(Stage stage) {
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getTabs().addAll(
            new Tab("Clientes", buildClienteTab()),
            new Tab("Veículos",  buildVeiculoTab())
        );

        stage.setScene(new Scene(tabs, 950, 620));
        stage.setTitle("Sistema de Veículos e Contratos");
        stage.show();

        carregarClientes();
        carregarVeiculos();
    }

    // ── CLIENTE ──────────────────────────────────────────────
    private VBox buildClienteTab() {
        tabelaCliente = new TableView<>(clienteData);
        tabelaCliente.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        addCol(tabelaCliente, "CPF",    "cpf");
        addCol(tabelaCliente, "Nome",   "nome");
        addCol(tabelaCliente, "Rua",    "rua");
        addCol(tabelaCliente, "Bairro", "bairro");
        addCol(tabelaCliente, "Número", "numero");

        tabelaCliente.setOnMouseClicked(e -> {
            Cliente s = tabelaCliente.getSelectionModel().getSelectedItem();
            if (s != null) { tfCpf.setText(s.getCpf()); tfNome.setText(s.getNome());
                tfRua.setText(s.getRua()); tfBairro.setText(s.getBairro()); tfNumero.setText(s.getNumero()); }
        });

        GridPane form = form(
            new String[]{"CPF:", "Nome:", "Rua:", "Bairro:", "Número:"},
            new TextField[]{tfCpf, tfNome, tfRua, tfBairro, tfNumero}
        );

        Button salvar  = btn("Salvar",  () -> salvarCliente());
        Button deletar = btn("Deletar", () -> deletarCliente());
        Button limpar  = btn("Limpar",  () -> { tfCpf.clear(); tfNome.clear(); tfRua.clear(); tfBairro.clear(); tfNumero.clear(); });

        HBox bts = new HBox(8, salvar, deletar, limpar);
        VBox layout = new VBox(10, form, bts, tabelaCliente);
        layout.setPadding(new Insets(12));
        VBox.setVgrow(tabelaCliente, Priority.ALWAYS);
        return layout;
    }

    private void salvarCliente() {
        try {
            Cliente c = new Cliente(tfCpf.getText().trim(), tfNome.getText().trim(),
                tfRua.getText().trim(), tfBairro.getText().trim(), tfNumero.getText().trim());
            if (clienteDAO.buscarPorCpf(c.getCpf()) == null) clienteDAO.inserir(c);
            else clienteDAO.atualizar(c);
            carregarClientes();
            info("Cliente salvo com sucesso!");
        } catch (Exception ex) { erro(ex.getMessage()); }
    }

    private void deletarCliente() {
        String cpf = tfCpf.getText().trim();
        if (cpf.isEmpty()) { info("Selecione um cliente."); return; }
        try { clienteDAO.deletar(cpf); carregarClientes(); info("Cliente removido!"); }
        catch (Exception ex) { erro(ex.getMessage()); }
    }

    private void carregarClientes() {
        try { clienteData.setAll(clienteDAO.listarTodos()); }
        catch (Exception ex) { erro(ex.getMessage()); }
    }

    // ── VEICULO ──────────────────────────────────────────────
    private VBox buildVeiculoTab() {
        tabelaVeiculo = new TableView<>(veiculoData);
        tabelaVeiculo.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Veiculo,String> cChassi = new TableColumn<>("Chassi");
        TableColumn<Veiculo,String> cPlaca  = new TableColumn<>("Placa");
        TableColumn<Veiculo,String> cCor    = new TableColumn<>("Cor");
        TableColumn<Veiculo,String> cAno    = new TableColumn<>("Ano");
        TableColumn<Veiculo,String> cMarca  = new TableColumn<>("Id Marca");
        TableColumn<Veiculo,String> cSeg    = new TableColumn<>("CNPJ Seg.");

        cChassi.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getChassi()));
        cPlaca.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue().getPlaca()));
        cCor.setCellValueFactory(d    -> new SimpleStringProperty(d.getValue().getCor()));
        cAno.setCellValueFactory(d    -> new SimpleStringProperty(String.valueOf(d.getValue().getAnoFabricacao())));
        cMarca.setCellValueFactory(d  -> new SimpleStringProperty(String.valueOf(d.getValue().getIdMarca())));
        cSeg.setCellValueFactory(d    -> new SimpleStringProperty(d.getValue().getCnpjSeguradora()));

        tabelaVeiculo.getColumns().addAll(cChassi, cPlaca, cCor, cAno, cMarca, cSeg);
        tabelaVeiculo.setOnMouseClicked(e -> {
            Veiculo s = tabelaVeiculo.getSelectionModel().getSelectedItem();
            if (s != null) { tfChassi.setText(s.getChassi()); tfPlaca.setText(s.getPlaca());
                tfCor.setText(s.getCor()); tfAno.setText(String.valueOf(s.getAnoFabricacao()));
                tfIdMarca.setText(String.valueOf(s.getIdMarca())); tfCnpj.setText(s.getCnpjSeguradora()); }
        });

        GridPane form = form(
            new String[]{"Chassi:", "Placa:", "Cor:", "Ano Fab.:", "Id Marca:", "CNPJ Seg.:"},
            new TextField[]{tfChassi, tfPlaca, tfCor, tfAno, tfIdMarca, tfCnpj}
        );

        Button salvar  = btn("Salvar",  () -> salvarVeiculo());
        Button deletar = btn("Deletar", () -> deletarVeiculo());
        Button limpar  = btn("Limpar",  () -> { tfChassi.clear(); tfPlaca.clear(); tfCor.clear();
            tfAno.clear(); tfIdMarca.clear(); tfCnpj.clear(); });

        HBox bts = new HBox(8, salvar, deletar, limpar);
        VBox layout = new VBox(10, form, bts, tabelaVeiculo);
        layout.setPadding(new Insets(12));
        VBox.setVgrow(tabelaVeiculo, Priority.ALWAYS);
        return layout;
    }

    private void salvarVeiculo() {
        try {
            Veiculo v = new Veiculo(tfChassi.getText().trim(), tfPlaca.getText().trim(),
                tfCor.getText().trim(), Integer.parseInt(tfAno.getText().trim()),
                Integer.parseInt(tfIdMarca.getText().trim()), tfCnpj.getText().trim());
            if (veiculoDAO.buscarPorChassi(v.getChassi()) == null) veiculoDAO.inserir(v);
            else veiculoDAO.atualizar(v);
            carregarVeiculos();
            info("Veículo salvo com sucesso!");
        } catch (NumberFormatException ex) { erro("Ano e Id Marca devem ser números inteiros.");
        } catch (Exception ex) { erro(ex.getMessage()); }
    }

    private void deletarVeiculo() {
        String chassi = tfChassi.getText().trim();
        if (chassi.isEmpty()) { info("Selecione um veículo."); return; }
        try { veiculoDAO.deletar(chassi); carregarVeiculos(); info("Veículo removido!"); }
        catch (Exception ex) { erro(ex.getMessage()); }
    }

    private void carregarVeiculos() {
        try { veiculoData.setAll(veiculoDAO.listarTodos()); }
        catch (Exception ex) { erro(ex.getMessage()); }
    }

    // ── UTILS ─────────────────────────────────────────────────
    private <T> void addCol(TableView<T> tv, String label, String prop) {
        TableColumn<T,String> col = new TableColumn<>(label);
        col.setCellValueFactory(new PropertyValueFactory<>(prop));
        tv.getColumns().add(col);
    }

    private GridPane form(String[] labels, TextField[] fields) {
        GridPane g = new GridPane();
        g.setHgap(8); g.setVgap(6);
        for (int i = 0; i < labels.length; i++) {
            g.add(new Label(labels[i]), 0, i);
            fields[i].setPrefWidth(300);
            g.add(fields[i], 1, i);
        }
        return g;
    }

    private Button btn(String label, Runnable action) {
        Button b = new Button(label);
        b.setOnAction(e -> action.run());
        return b;
    }

    private void info(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }

    private void erro(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText("Erro"); a.setContentText(msg); a.showAndWait();
    }

    public static void main(String[] args) { launch(args); }
}
