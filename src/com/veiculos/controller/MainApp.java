package com.veiculos.controller;

import com.veiculos.dao.AluguelDAO;
import com.veiculos.dao.ClienteDAO;
import com.veiculos.dao.ContratoDAO;
import com.veiculos.dao.VeiculoDAO;
import com.veiculos.model.Aluguel;
import com.veiculos.model.Cliente;
import com.veiculos.model.Contrato;
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

import java.math.BigDecimal;
import java.time.LocalDate;

public class MainApp extends Application {

    private final ClienteDAO  clienteDAO  = new ClienteDAO();
    private final VeiculoDAO  veiculoDAO  = new VeiculoDAO();
    private final ContratoDAO contratoDAO = new ContratoDAO();
    private final AluguelDAO  aluguelDAO  = new AluguelDAO();

    // ── Cliente fields ────────────────────────────────────────
    private final TextField tfCpf    = new TextField();
    private final TextField tfNome   = new TextField();
    private final TextField tfRua    = new TextField();
    private final TextField tfBairro = new TextField();
    private final TextField tfNumero = new TextField();
    private final ObservableList<Cliente> clienteData = FXCollections.observableArrayList();
    private TableView<Cliente> tabelaCliente;

    // ── Veiculo fields ────────────────────────────────────────
    private final TextField tfChassi  = new TextField();
    private final TextField tfPlaca   = new TextField();
    private final TextField tfCor     = new TextField();
    private final TextField tfAno     = new TextField();
    private final TextField tfIdMarca = new TextField();
    private final TextField tfCnpj    = new TextField();
    private final ObservableList<Veiculo> veiculoData = FXCollections.observableArrayList();
    private TableView<Veiculo> tabelaVeiculo;

    // ── Contrato fields ───────────────────────────────────────
    private final TextField tfContratoId       = new TextField();
    private final TextField tfContratoInicio   = new TextField();
    private final TextField tfContratoFim      = new TextField();
    private final TextField tfContratoCpf      = new TextField();
    private final TextField tfContratoMatricula = new TextField();
    private final ObservableList<Contrato> contratoData = FXCollections.observableArrayList();
    private TableView<Contrato> tabelaContrato;

    // ── Aluguel fields ────────────────────────────────────────
    private final TextField tfAluguelId         = new TextField();
    private final TextField tfAluguelRetirada   = new TextField();
    private final TextField tfAluguelDevolucao  = new TextField();
    private final TextField tfAluguelValor      = new TextField();
    private final TextField tfAluguelKmSaida    = new TextField();
    private final TextField tfAluguelKmChegada  = new TextField();
    private final TextField tfAluguelCpf        = new TextField();
    private final TextField tfAluguelChassi     = new TextField();
    private final ObservableList<Aluguel> aluguelData = FXCollections.observableArrayList();
    private TableView<Aluguel> tabelaAluguel;

    @Override
    public void start(Stage stage) {
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getTabs().addAll(
            new Tab("Clientes",  buildClienteTab()),
            new Tab("Veículos",  buildVeiculoTab()),
            new Tab("Contratos", buildContratoTab()),
            new Tab("Aluguéis",  buildAluguelTab())
        );

        stage.setScene(new Scene(tabs, 950, 620));
        stage.setTitle("Sistema de Veículos e Contratos");
        stage.show();

        carregarClientes();
        carregarVeiculos();
        carregarContratos();
        carregarAlugueis();
    }

    // ── CLIENTE ───────────────────────────────────────────────
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
            if (s != null) {
                tfCpf.setText(s.getCpf()); tfNome.setText(s.getNome());
                tfRua.setText(s.getRua()); tfBairro.setText(s.getBairro());
                tfNumero.setText(s.getNumero());
            }
        });

        GridPane form = form(
            new String[]{"CPF:", "Nome:", "Rua:", "Bairro:", "Número:"},
            new TextField[]{tfCpf, tfNome, tfRua, tfBairro, tfNumero}
        );

        Button salvar  = btn("Salvar",  () -> salvarCliente());
        Button deletar = btn("Deletar", () -> deletarCliente());
        Button limpar  = btn("Limpar",  () -> {
            tfCpf.clear(); tfNome.clear(); tfRua.clear(); tfBairro.clear(); tfNumero.clear();
        });

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

    // ── VEICULO ───────────────────────────────────────────────
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
            if (s != null) {
                tfChassi.setText(s.getChassi()); tfPlaca.setText(s.getPlaca());
                tfCor.setText(s.getCor()); tfAno.setText(String.valueOf(s.getAnoFabricacao()));
                tfIdMarca.setText(String.valueOf(s.getIdMarca())); tfCnpj.setText(s.getCnpjSeguradora());
            }
        });

        GridPane form = form(
            new String[]{"Chassi:", "Placa:", "Cor:", "Ano Fab.:", "Id Marca:", "CNPJ Seg.:"},
            new TextField[]{tfChassi, tfPlaca, tfCor, tfAno, tfIdMarca, tfCnpj}
        );

        Button salvar  = btn("Salvar",  () -> salvarVeiculo());
        Button deletar = btn("Deletar", () -> deletarVeiculo());
        Button limpar  = btn("Limpar",  () -> {
            tfChassi.clear(); tfPlaca.clear(); tfCor.clear();
            tfAno.clear(); tfIdMarca.clear(); tfCnpj.clear();
        });

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

    // ── CONTRATO ──────────────────────────────────────────────
    private VBox buildContratoTab() {
        tabelaContrato = new TableView<>(contratoData);
        tabelaContrato.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Contrato,String> cId       = new TableColumn<>("ID");
        TableColumn<Contrato,String> cInicio   = new TableColumn<>("Início");
        TableColumn<Contrato,String> cFim      = new TableColumn<>("Fim");
        TableColumn<Contrato,String> cCpf      = new TableColumn<>("CPF Cliente");
        TableColumn<Contrato,String> cMatricula = new TableColumn<>("Matrícula Func.");

        cId.setCellValueFactory(d        -> new SimpleStringProperty(String.valueOf(d.getValue().getIdContrato())));
        cInicio.setCellValueFactory(d    -> new SimpleStringProperty(String.valueOf(d.getValue().getDataInicio())));
        cFim.setCellValueFactory(d       -> new SimpleStringProperty(String.valueOf(d.getValue().getDataFim())));
        cCpf.setCellValueFactory(d       -> new SimpleStringProperty(d.getValue().getCpfCliente()));
        cMatricula.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getMatriculaFuncionario())));

        tabelaContrato.getColumns().addAll(cId, cInicio, cFim, cCpf, cMatricula);
        tabelaContrato.setOnMouseClicked(e -> {
            Contrato s = tabelaContrato.getSelectionModel().getSelectedItem();
            if (s != null) {
                tfContratoId.setText(String.valueOf(s.getIdContrato()));
                tfContratoInicio.setText(String.valueOf(s.getDataInicio()));
                tfContratoFim.setText(String.valueOf(s.getDataFim()));
                tfContratoCpf.setText(s.getCpfCliente());
                tfContratoMatricula.setText(String.valueOf(s.getMatriculaFuncionario()));
            }
        });

        tfContratoId.setDisable(true); // ID é gerado pelo banco

        GridPane form = form(
            new String[]{"ID:", "Início (AAAA-MM-DD):", "Fim (AAAA-MM-DD):", "CPF Cliente:", "Matrícula Func.:"},
            new TextField[]{tfContratoId, tfContratoInicio, tfContratoFim, tfContratoCpf, tfContratoMatricula}
        );

        Button salvar  = btn("Salvar",  () -> salvarContrato());
        Button deletar = btn("Deletar", () -> deletarContrato());
        Button limpar  = btn("Limpar",  () -> {
            tfContratoId.clear(); tfContratoInicio.clear(); tfContratoFim.clear();
            tfContratoCpf.clear(); tfContratoMatricula.clear();
        });

        HBox bts = new HBox(8, salvar, deletar, limpar);
        VBox layout = new VBox(10, form, bts, tabelaContrato);
        layout.setPadding(new Insets(12));
        VBox.setVgrow(tabelaContrato, Priority.ALWAYS);
        return layout;
    }

    private void salvarContrato() {
        try {
            LocalDate inicio = LocalDate.parse(tfContratoInicio.getText().trim());
            LocalDate fim    = LocalDate.parse(tfContratoFim.getText().trim());
            int matricula    = Integer.parseInt(tfContratoMatricula.getText().trim());
            String cpf       = tfContratoCpf.getText().trim();

            String idStr = tfContratoId.getText().trim();
            if (idStr.isEmpty()) {
                // inserir novo
                contratoDAO.inserir(new Contrato(0, inicio, fim, cpf, matricula));
                info("Contrato inserido com sucesso!");
            } else {
                // atualizar existente
                contratoDAO.atualizar(new Contrato(Integer.parseInt(idStr), inicio, fim, cpf, matricula));
                info("Contrato atualizado com sucesso!");
            }
            carregarContratos();
        } catch (NumberFormatException ex) { erro("Matrícula deve ser um número inteiro.");
        } catch (Exception ex) { erro(ex.getMessage()); }
    }

    private void deletarContrato() {
        String idStr = tfContratoId.getText().trim();
        if (idStr.isEmpty()) { info("Selecione um contrato."); return; }
        try {
            contratoDAO.deletar(Integer.parseInt(idStr));
            carregarContratos();
            info("Contrato removido!");
        } catch (Exception ex) { erro(ex.getMessage()); }
    }

    private void carregarContratos() {
        try { contratoData.setAll(contratoDAO.listarTodos()); }
        catch (Exception ex) { erro(ex.getMessage()); }
    }

    // ── ALUGUEL ───────────────────────────────────────────────
    private VBox buildAluguelTab() {
        tabelaAluguel = new TableView<>(aluguelData);
        tabelaAluguel.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Aluguel,String> cId        = new TableColumn<>("ID");
        TableColumn<Aluguel,String> cRetirada  = new TableColumn<>("Retirada");
        TableColumn<Aluguel,String> cDevolucao = new TableColumn<>("Devolução");
        TableColumn<Aluguel,String> cValor     = new TableColumn<>("Valor (R$)");
        TableColumn<Aluguel,String> cKmS       = new TableColumn<>("Km Saída");
        TableColumn<Aluguel,String> cKmC       = new TableColumn<>("Km Chegada");
        TableColumn<Aluguel,String> cCpf       = new TableColumn<>("CPF Cliente");
        TableColumn<Aluguel,String> cChassi    = new TableColumn<>("Chassi");

        cId.setCellValueFactory(d        -> new SimpleStringProperty(String.valueOf(d.getValue().getIdAluguel())));
        cRetirada.setCellValueFactory(d  -> new SimpleStringProperty(String.valueOf(d.getValue().getDataRetirada())));
        cDevolucao.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDataDevolucao() != null ? String.valueOf(d.getValue().getDataDevolucao()) : "Em aberto"));
        cValor.setCellValueFactory(d     -> new SimpleStringProperty(d.getValue().getValorTotal() != null ? d.getValue().getValorTotal().toPlainString() : "-"));
        cKmS.setCellValueFactory(d       -> new SimpleStringProperty(d.getValue().getKmSaida() != null ? d.getValue().getKmSaida().toPlainString() : "-"));
        cKmC.setCellValueFactory(d       -> new SimpleStringProperty(d.getValue().getKmChegada() != null ? d.getValue().getKmChegada().toPlainString() : "-"));
        cCpf.setCellValueFactory(d       -> new SimpleStringProperty(d.getValue().getCpfCliente()));
        cChassi.setCellValueFactory(d    -> new SimpleStringProperty(d.getValue().getChassiVeiculo()));

        tabelaAluguel.getColumns().addAll(cId, cRetirada, cDevolucao, cValor, cKmS, cKmC, cCpf, cChassi);
        tabelaAluguel.setOnMouseClicked(e -> {
            Aluguel s = tabelaAluguel.getSelectionModel().getSelectedItem();
            if (s != null) {
                tfAluguelId.setText(String.valueOf(s.getIdAluguel()));
                tfAluguelRetirada.setText(String.valueOf(s.getDataRetirada()));
                tfAluguelDevolucao.setText(s.getDataDevolucao() != null ? String.valueOf(s.getDataDevolucao()) : "");
                tfAluguelValor.setText(s.getValorTotal() != null ? s.getValorTotal().toPlainString() : "");
                tfAluguelKmSaida.setText(s.getKmSaida() != null ? s.getKmSaida().toPlainString() : "");
                tfAluguelKmChegada.setText(s.getKmChegada() != null ? s.getKmChegada().toPlainString() : "");
                tfAluguelCpf.setText(s.getCpfCliente());
                tfAluguelChassi.setText(s.getChassiVeiculo());
            }
        });

        tfAluguelId.setDisable(true); // ID é gerado pelo banco

        GridPane form = form(
            new String[]{"ID:", "Retirada (AAAA-MM-DD):", "Devolução (AAAA-MM-DD):", "Valor Total:", "Km Saída:", "Km Chegada:", "CPF Cliente:", "Chassi Veículo:"},
            new TextField[]{tfAluguelId, tfAluguelRetirada, tfAluguelDevolucao, tfAluguelValor, tfAluguelKmSaida, tfAluguelKmChegada, tfAluguelCpf, tfAluguelChassi}
        );

        Button salvar  = btn("Salvar",  () -> salvarAluguel());
        Button deletar = btn("Deletar", () -> deletarAluguel());
        Button limpar  = btn("Limpar",  () -> {
            tfAluguelId.clear(); tfAluguelRetirada.clear(); tfAluguelDevolucao.clear();
            tfAluguelValor.clear(); tfAluguelKmSaida.clear(); tfAluguelKmChegada.clear();
            tfAluguelCpf.clear(); tfAluguelChassi.clear();
        });

        HBox bts = new HBox(8, salvar, deletar, limpar);
        VBox layout = new VBox(10, form, bts, tabelaAluguel);
        layout.setPadding(new Insets(12));
        VBox.setVgrow(tabelaAluguel, Priority.ALWAYS);
        return layout;
    }

    private void salvarAluguel() {
        try {
            LocalDate retirada   = LocalDate.parse(tfAluguelRetirada.getText().trim());
            LocalDate devolucao  = tfAluguelDevolucao.getText().trim().isEmpty() ? null
                                   : LocalDate.parse(tfAluguelDevolucao.getText().trim());
            BigDecimal valor     = tfAluguelValor.getText().trim().isEmpty() ? null
                                   : new BigDecimal(tfAluguelValor.getText().trim());
            BigDecimal kmSaida   = new BigDecimal(tfAluguelKmSaida.getText().trim());
            BigDecimal kmChegada = tfAluguelKmChegada.getText().trim().isEmpty() ? null
                                   : new BigDecimal(tfAluguelKmChegada.getText().trim());
            String cpf    = tfAluguelCpf.getText().trim();
            String chassi = tfAluguelChassi.getText().trim();

            String idStr = tfAluguelId.getText().trim();
            if (idStr.isEmpty()) {
                aluguelDAO.inserir(new Aluguel(0, retirada, devolucao, valor, kmSaida, kmChegada, cpf, chassi));
                info("Aluguel registrado com sucesso!");
            } else {
                aluguelDAO.atualizar(new Aluguel(Integer.parseInt(idStr), retirada, devolucao, valor, kmSaida, kmChegada, cpf, chassi));
                info("Aluguel atualizado com sucesso!");
            }
            carregarAlugueis();
        } catch (NumberFormatException ex) { erro("Km e Valor devem ser números válidos.");
        } catch (Exception ex) { erro(ex.getMessage()); }
    }

    private void deletarAluguel() {
        String idStr = tfAluguelId.getText().trim();
        if (idStr.isEmpty()) { info("Selecione um aluguel."); return; }
        try {
            aluguelDAO.deletar(Integer.parseInt(idStr));
            carregarAlugueis();
            info("Aluguel removido!");
        } catch (Exception ex) { erro(ex.getMessage()); }
    }

    private void carregarAlugueis() {
        try { aluguelData.setAll(aluguelDAO.listarTodos()); }
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
