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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final TextField tfContratoId        = new TextField();
    private final TextField tfContratoInicio    = new TextField();
    private final TextField tfContratoFim       = new TextField();
    private final TextField tfContratoCpf       = new TextField();
    private final TextField tfContratoMatricula = new TextField();
    private final ObservableList<Contrato> contratoData = FXCollections.observableArrayList();
    private TableView<Contrato> tabelaContrato;

    // ── Aluguel fields ────────────────────────────────────────
    private final TextField tfAluguelId        = new TextField();
    private final TextField tfAluguelRetirada  = new TextField();
    private final TextField tfAluguelDevolucao = new TextField();
    private final TextField tfAluguelValor     = new TextField();
    private final TextField tfAluguelKmSaida   = new TextField();
    private final TextField tfAluguelKmChegada = new TextField();
    private final TextField tfAluguelCpf       = new TextField();
    private final TextField tfAluguelChassi    = new TextField();
    private final ObservableList<Aluguel> aluguelData = FXCollections.observableArrayList();
    private TableView<Aluguel> tabelaAluguel;

    @Override
    public void start(Stage stage) {
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getTabs().addAll(
            new Tab("Clientes",     buildClienteTab()),
            new Tab("Veículos",     buildVeiculoTab()),
            new Tab("Contratos",    buildContratoTab()),
            new Tab("Aluguéis",     buildAluguelTab()),
            new Tab("Operações BD", buildOperacoesTab()),
            new Tab("Consultas e Views", buildConsultasViewsTab()),
            new Tab("Dashboard",    buildDashboardTab())
        );

        stage.setScene(new Scene(tabs, 980, 660));
        stage.setTitle("Sistema de Veículos e Contratos");
        stage.show();

        carregarClientes();
        carregarVeiculos();
        carregarContratos();
        carregarAlugueis();
    }

    // ── OPERAÇÕES BD ──────────────────────────────────────────
    private ScrollPane buildOperacoesTab() {
        VBox root = new VBox(18);
        root.setPadding(new Insets(14));

        TextArea taResultado = new TextArea();
        taResultado.setEditable(false);
        taResultado.setPrefHeight(160);
        taResultado.setWrapText(true);
        taResultado.setPromptText("Resultado das operações aparecerá aqui...");

        // ── FUNÇÕES ──────────────────────────────────────────
        TitledPane painelFuncoes = new TitledPane();
        painelFuncoes.setText("Funções");

        VBox vFuncoes = new VBox(10);
        vFuncoes.setPadding(new Insets(8));

        Label lbFn1 = new Label("fn_classificar_aluguel — calcula valor do aluguel por km rodado:");
        TextField tfKmSaidaFn = new TextField(); tfKmSaidaFn.setPromptText("Km Saída");    tfKmSaidaFn.setPrefWidth(120);
        TextField tfKmChegFn  = new TextField(); tfKmChegFn.setPromptText("Km Chegada");   tfKmChegFn.setPrefWidth(120);
        TextField tfDiariaFn  = new TextField(); tfDiariaFn.setPromptText("Diária base");  tfDiariaFn.setPrefWidth(120);
        Button btnFn1 = new Button("Calcular");
        btnFn1.setOnAction(e -> {
            try (Connection con = getConnection();
                 PreparedStatement ps = con.prepareStatement(
                         "SELECT fn_classificar_aluguel(?, ?, ?) AS resultado")) {
                ps.setBigDecimal(1, new BigDecimal(tfKmSaidaFn.getText().trim()));
                ps.setBigDecimal(2, new BigDecimal(tfKmChegFn.getText().trim()));
                ps.setBigDecimal(3, new BigDecimal(tfDiariaFn.getText().trim()));
                ResultSet rs = ps.executeQuery();
                if (rs.next()) taResultado.setText("fn_classificar_aluguel → R$ " + rs.getString("resultado"));
            } catch (Exception ex) { taResultado.setText("Erro: " + ex.getMessage()); }
        });
        HBox rowFn1 = new HBox(8, tfKmSaidaFn, tfKmChegFn, tfDiariaFn, btnFn1);
        rowFn1.setAlignment(Pos.CENTER_LEFT);

        Label lbFn2 = new Label("fn_total_gasto_cliente — total gasto em aluguéis por CPF:");
        TextField tfCpfFn2 = new TextField(); tfCpfFn2.setPromptText("CPF (11 dígitos)"); tfCpfFn2.setPrefWidth(180);
        Button btnFn2 = new Button("Consultar");
        btnFn2.setOnAction(e -> {
            try (Connection con = getConnection();
                 PreparedStatement ps = con.prepareStatement(
                         "SELECT fn_total_gasto_cliente(?) AS resultado")) {
                ps.setString(1, tfCpfFn2.getText().trim());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) taResultado.setText("fn_total_gasto_cliente → R$ " + rs.getString("resultado"));
            } catch (Exception ex) { taResultado.setText("Erro: " + ex.getMessage()); }
        });
        HBox rowFn2 = new HBox(8, tfCpfFn2, btnFn2);
        rowFn2.setAlignment(Pos.CENTER_LEFT);

        vFuncoes.getChildren().addAll(lbFn1, rowFn1, new Separator(), lbFn2, rowFn2);
        painelFuncoes.setContent(vFuncoes);

        // ── PROCEDURES ───────────────────────────────────────
        TitledPane painelProc = new TitledPane();
        painelProc.setText("Procedimentos");

        VBox vProc = new VBox(10);
        vProc.setPadding(new Insets(8));

        Label lbProc1 = new Label("sp_registrar_devolucao — registra devolução e calcula valor (R$80/dia + R$1,50/km):");
        TextField tfProcId   = new TextField(); tfProcId.setPromptText("ID Aluguel");             tfProcId.setPrefWidth(110);
        TextField tfProcData = new TextField(); tfProcData.setPromptText("Data dev. AAAA-MM-DD"); tfProcData.setPrefWidth(160);
        TextField tfProcKm   = new TextField(); tfProcKm.setPromptText("Km Chegada");             tfProcKm.setPrefWidth(110);
        Button btnProc1 = new Button("Registrar Devolução");
        btnProc1.setOnAction(e -> {
            try (Connection con = getConnection();
                 CallableStatement cs = con.prepareCall("{CALL sp_registrar_devolucao(?, ?, ?)}")) {
                cs.setInt(1, Integer.parseInt(tfProcId.getText().trim()));
                cs.setDate(2, java.sql.Date.valueOf(tfProcData.getText().trim()));
                cs.setBigDecimal(3, new BigDecimal(tfProcKm.getText().trim()));
                ResultSet rs = cs.executeQuery();
                if (rs.next()) taResultado.setText("sp_registrar_devolucao → " + rs.getString("resultado"));
                carregarAlugueis();
            } catch (Exception ex) { taResultado.setText("Erro: " + ex.getMessage()); }
        });
        HBox rowProc1 = new HBox(8, tfProcId, tfProcData, tfProcKm, btnProc1);
        rowProc1.setAlignment(Pos.CENTER_LEFT);

        Label lbProc2 = new Label("sp_reajuste_salarial — aplica reajuste escalonado por cargo em todos os funcionários:");
        Button btnProc2 = new Button("Aplicar Reajuste Salarial");
        btnProc2.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Confirma o reajuste salarial para todos os funcionários?",
                    ButtonType.YES, ButtonType.NO);
            confirm.setHeaderText(null);
            confirm.showAndWait().ifPresent(bt -> {
                if (bt == ButtonType.YES) {
                    try (Connection con = getConnection();
                         CallableStatement cs = con.prepareCall("{CALL sp_reajuste_salarial()}")) {
                        ResultSet rs = cs.executeQuery();
                        if (rs.next()) taResultado.setText("sp_reajuste_salarial → " + rs.getString("resultado"));
                    } catch (Exception ex) { taResultado.setText("Erro: " + ex.getMessage()); }
                }
            });
        });

        vProc.getChildren().addAll(lbProc1, rowProc1, new Separator(), lbProc2, btnProc2);
        painelProc.setContent(vProc);

        // ── TRIGGERS ─────────────────────────────────────────
        TitledPane painelTrig = new TitledPane();
        painelTrig.setText("Triggers — visualizar efeitos");

        VBox vTrig = new VBox(10);
        vTrig.setPadding(new Insets(8));

        Label lbTrig1 = new Label("trg_log_alteracao_salario — disparado após UPDATE de salário. Log registrado:");
        TableView<ObservableList<String>> tabelaLog = buildTabelaDinamica();
        Button btnTrig1 = new Button("Atualizar Log de Salários");
        btnTrig1.setOnAction(e -> {
            try (Connection con = getConnection();
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(
                         "SELECT Id_Log, Matricula_Func, Salario_Antigo, Salario_Novo, Data_Alteracao " +
                         "FROM Log_Salario ORDER BY Data_Alteracao DESC LIMIT 50")) {
                preencherTabela(tabelaLog, rs);
                taResultado.setText("Log_Salario carregado (" + tabelaLog.getItems().size() + " registros).");
            } catch (Exception ex) { taResultado.setText("Erro: " + ex.getMessage()); }
        });

        Label lbTrig2 = new Label("trg_bloquear_aluguel_em_manutencao — veículos bloqueados (em manutenção aberta):");
        TableView<ObservableList<String>> tabelaManut = buildTabelaDinamica();
        Button btnTrig2 = new Button("Ver Veículos Bloqueados");
        btnTrig2.setOnAction(e -> {
            try (Connection con = getConnection();
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(
                         "SELECT v.Chassi, v.Placa, v.Cor, m.Tipo_Servico, m.Data_Entrada " +
                         "FROM Manutencao m JOIN Veiculo v ON m.Chassi_Veiculo = v.Chassi " +
                         "WHERE m.Data_Saida IS NULL ORDER BY m.Data_Entrada DESC")) {
                preencherTabela(tabelaManut, rs);
                taResultado.setText("Veículos bloqueados: " + tabelaManut.getItems().size());
            } catch (Exception ex) { taResultado.setText("Erro: " + ex.getMessage()); }
        });

        vTrig.getChildren().addAll(lbTrig1, btnTrig1, tabelaLog,
                new Separator(), lbTrig2, btnTrig2, tabelaManut);
        painelTrig.setContent(vTrig);

        TitledPane painelResult = new TitledPane("Resultado", taResultado);
        painelResult.setCollapsible(false);

        root.getChildren().addAll(painelFuncoes, painelProc, painelTrig, painelResult);
        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        return scroll;
    }

    private ScrollPane buildConsultasViewsTab() {

        VBox root = new VBox(18);
        root.setPadding(new Insets(14));

        // =========================
        // TABELA DINÂMICA
        // =========================

        TableView<ObservableList<String>> tabela =
                buildTabelaDinamica();

        tabela.setPrefHeight(300);

        TextArea resultado = new TextArea();
        resultado.setEditable(false);
        resultado.setPrefHeight(100);

        // =========================
        // KPIs
        // =========================

        Label lbTotalAlugueis = new Label("Total de Aluguéis: --");
        Label lbReceita = new Label("Receita Total: --");
        Label lbClientes = new Label("Clientes Ativos: --");

        VBox indicadores = new VBox(
                8,
                lbTotalAlugueis,
                lbReceita,
                lbClientes
        );

        indicadores.setPadding(new Insets(10));
        indicadores.setStyle(
                "-fx-border-color: lightgray;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;"
        );

        // =========================
        // GRÁFICO
        // =========================

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        BarChart<String, Number> grafico =
                new BarChart<>(xAxis, yAxis);

        grafico.setTitle("Clientes que Mais Gastaram");

        // =========================
        // CONSULTA 1
        // =========================

        Label lbConsulta1 =
                new Label("Clientes que mais gastaram:");

        Button btnConsulta1 =
                new Button("Executar Consulta");

        btnConsulta1.setOnAction(e -> {

            try (
                    Connection con = getConnection();
                    Statement st = con.createStatement();

                    ResultSet rs = st.executeQuery(

                            "SELECT c.nome, " +
                                    "SUM(a.valor_total) AS total_gasto " +
                                    "FROM aluguel a " +
                                    "JOIN cliente c ON a.cpf_cliente = c.cpf " +
                                    "GROUP BY c.nome " +
                                    "ORDER BY total_gasto DESC"

                    )
            ) {

                preencherTabela(tabela, rs);

                resultado.setText(
                        "Consulta executada com sucesso!"
                );

            } catch (Exception ex) {

                resultado.setText(
                        "Erro: " + ex.getMessage()
                );

            }

            // ===== GRÁFICO =====

            grafico.getData().clear();

            XYChart.Series<String, Number> serie =
                    new XYChart.Series<>();

            try (
                    Connection con = getConnection();
                    Statement st = con.createStatement();

                    ResultSet rs = st.executeQuery(

                            "SELECT c.nome, " +
                                    "SUM(a.valor_total) AS total_gasto " +
                                    "FROM aluguel a " +
                                    "JOIN cliente c ON a.cpf_cliente = c.cpf " +
                                    "GROUP BY c.nome " +
                                    "ORDER BY total_gasto DESC"

                    )
            ) {

                while (rs.next()) {

                    serie.getData().add(

                            new XYChart.Data<>(

                                    rs.getString("nome"),
                                    rs.getDouble("total_gasto")

                            )

                    );

                }

                grafico.getData().add(serie);

            } catch (Exception ex) {

                resultado.setText(
                        "Erro gráfico: " + ex.getMessage()
                );

            }

            // ===== KPIs =====

            try (
                    Connection con = getConnection();
                    Statement st = con.createStatement()
            ) {

                ResultSet rs1 = st.executeQuery(
                        "SELECT COUNT(*) AS total FROM aluguel"
                );

                if (rs1.next()) {

                    lbTotalAlugueis.setText(
                            "Total de Aluguéis: "
                                    + rs1.getInt("total")
                    );

                }

                ResultSet rs2 = st.executeQuery(
                        "SELECT SUM(valor_total) AS receita FROM aluguel"
                );

                if (rs2.next()) {

                    lbReceita.setText(
                            "Receita Total: R$ "
                                    + rs2.getString("receita")
                    );

                }

                ResultSet rs3 = st.executeQuery(
                        "SELECT COUNT(*) AS total FROM cliente"
                );

                if (rs3.next()) {

                    lbClientes.setText(
                            "Clientes Ativos: "
                                    + rs3.getInt("total")
                    );

                }

            } catch (Exception ex) {

                resultado.setText(
                        "Erro KPIs: " + ex.getMessage()
                );

            }

        });

        VBox consulta1 = new VBox(
                10,
                lbConsulta1,
                btnConsulta1
        );

        root.getChildren().addAll(

                consulta1,
                tabela,
                grafico,
                indicadores,
                resultado

        );

        ScrollPane scroll = new ScrollPane(root);

        scroll.setFitToWidth(true);

        return scroll;
    }

    @SuppressWarnings("unchecked")
    private TableView<ObservableList<String>> buildTabelaDinamica() {
        TableView<ObservableList<String>> tv = new TableView<>();
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tv.setPrefHeight(150);
        return tv;
    }

    @SuppressWarnings("unchecked")
    private void preencherTabela(TableView<ObservableList<String>> tv, ResultSet rs) throws SQLException {
        tv.getColumns().clear();
        tv.getItems().clear();
        ResultSetMetaData meta = rs.getMetaData();
        int cols = meta.getColumnCount();
        for (int i = 1; i <= cols; i++) {
            final int idx = i - 1;
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(meta.getColumnLabel(i));
            col.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(idx)));
            tv.getColumns().add(col);
        }
        while (rs.next()) {
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 1; i <= cols; i++) row.add(rs.getString(i));
            tv.getItems().add(row);
        }
    }

    private Connection getConnection() throws SQLException {
        return com.veiculos.util.DBConnection.getConnection();
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

        TableColumn<Contrato,String> cId        = new TableColumn<>("ID");
        TableColumn<Contrato,String> cInicio    = new TableColumn<>("Início");
        TableColumn<Contrato,String> cFim       = new TableColumn<>("Fim");
        TableColumn<Contrato,String> cCpf       = new TableColumn<>("CPF Cliente");
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

        tfContratoId.setDisable(true);

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
            String idStr     = tfContratoId.getText().trim();
            if (idStr.isEmpty()) {
                contratoDAO.inserir(new Contrato(0, inicio, fim, cpf, matricula));
                info("Contrato inserido com sucesso!");
            } else {
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
        try { contratoDAO.deletar(Integer.parseInt(idStr)); carregarContratos(); info("Contrato removido!"); }
        catch (Exception ex) { erro(ex.getMessage()); }
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

        tfAluguelId.setDisable(true);

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
            String idStr  = tfAluguelId.getText().trim();
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
        try { aluguelDAO.deletar(Integer.parseInt(idStr)); carregarAlugueis(); info("Aluguel removido!"); }
        catch (Exception ex) { erro(ex.getMessage()); }
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

    // ── DASHBOARD ─────────────────────────────────────────────
    private ScrollPane buildDashboardTab() {
        VBox root = new VBox(16);
        root.setPadding(new Insets(16));

        Label titulo = new Label("Dashboard Estatístico — Autocar");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1a365d;");

        // ── Filtro de ano ──────────────────────────────────────
        ComboBox<String> cbAno = new ComboBox<>();
        cbAno.getItems().addAll("Todos", "2024", "2025", "2026");
        cbAno.setValue("2024");
        Button btnAtualizar = new Button("Atualizar Dashboard");
        btnAtualizar.setStyle("-fx-background-color: #2b6cb0; -fx-text-fill: white; -fx-cursor: hand;");

        HBox filtros = new HBox(10, new Label("Filtrar por ano:"), cbAno, btnAtualizar);
        filtros.setAlignment(Pos.CENTER_LEFT);

        // ── KPI Cards ─────────────────────────────────────────
        Label kpiClientes = kpiCard("Clientes", "--");
        Label kpiVeiculos = kpiCard("Veículos", "--");
        Label kpiAlugueis = kpiCard("Aluguéis", "--");
        Label kpiReceita  = kpiCard("Receita Total", "--");
        Label kpiManut    = kpiCard("Custo Manutenção", "--");
        Label kpiTicket   = kpiCard("Ticket Médio", "--");
        Label kpiVariancia = kpiCard("Variância", "--");

        HBox kpiBox = new HBox(10,
                kpiClientes, kpiVeiculos, kpiAlugueis,
                kpiReceita, kpiManut, kpiTicket, kpiVariancia);
        kpiBox.setAlignment(Pos.CENTER_LEFT);

        // ── Gráfico 1: Receita por mês (LineChart) ────────────
        NumberAxis lxAxis = new NumberAxis(1, 12, 1);
        NumberAxis lyAxis = new NumberAxis();
        lxAxis.setLabel("Mês"); lyAxis.setLabel("Receita (R$)");
        LineChart<Number, Number> lcReceita = new LineChart<>(lxAxis, lyAxis);
        lcReceita.setTitle("Gráfico 1 — Receita Mensal");
        lcReceita.setLegendVisible(false);
        lcReceita.setPrefSize(470, 300);

        // ── Gráfico 2: Veículos por Cor (PieChart) ───────────
        PieChart pcCores = new PieChart();
        pcCores.setTitle("Gráfico 2 — Veículos por Cor");
        pcCores.setPrefSize(370, 300);

        HBox row1 = new HBox(14, lcReceita, pcCores);

        // ── Gráfico 3: Top 5 Clientes (BarChart) ─────────────
        CategoryAxis bx3 = new CategoryAxis(); NumberAxis by3 = new NumberAxis();
        by3.setLabel("R$");
        BarChart<String, Number> bcClientes = new BarChart<>(bx3, by3);
        bcClientes.setTitle("Gráfico 3 — Top 5 Clientes por Gasto");
        bcClientes.setLegendVisible(false);
        bcClientes.setPrefSize(420, 300);

        // ── Gráfico 4: Custo de Manutenção por Tipo (BarChart) ──
        CategoryAxis bx4 = new CategoryAxis(); NumberAxis by4 = new NumberAxis();
        by4.setLabel("R$");
        BarChart<String, Number> bcManut = new BarChart<>(bx4, by4);
        bcManut.setTitle("Gráfico 4 — Custo por Tipo de Manutenção");
        bcManut.setLegendVisible(false);
        bcManut.setPrefSize(420, 300);

        HBox row2 = new HBox(14, bcClientes, bcManut);

        // ── Gráfico 5: Estatísticas (BarChart) ───────────────
        CategoryAxis bx5 = new CategoryAxis(); NumberAxis by5 = new NumberAxis();
        by5.setLabel("R$");
        BarChart<String, Number> bcStats = new BarChart<>(bx5, by5);
        bcStats.setTitle("Gráfico 5 — Estatísticas de Valor de Aluguel");
        bcStats.setLegendVisible(false);
        bcStats.setPrefSize(420, 300);

        // ── Gráfico 6: Veículos por Segmento de Marca (PieChart) ─
        PieChart pcSegmento = new PieChart();
        pcSegmento.setTitle("Gráfico 6 — Veículos por Segmento");
        pcSegmento.setPrefSize(370, 300);

        HBox row3 = new HBox(14, bcStats, pcSegmento);

        root.getChildren().addAll(titulo, filtros, kpiBox,
                new Separator(), row1,
                new Separator(), row2,
                new Separator(), row3);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);

        Runnable carregar = () -> carregarDashboard(
                cbAno.getValue(),
                kpiClientes, kpiVeiculos, kpiAlugueis, kpiReceita, kpiManut, kpiTicket, kpiVariancia,
                lcReceita, pcCores, bcClientes, bcManut, bcStats, pcSegmento);

        btnAtualizar.setOnAction(e -> carregar.run());
        carregar.run();

        return scroll;
    }

    private Label kpiCard(String titulo, String valor) {
        Label l = new Label(titulo + "\n" + valor);
        l.setStyle("-fx-background-color: #2b6cb0; -fx-text-fill: white; " +
                   "-fx-font-size: 12px; -fx-font-weight: bold; " +
                   "-fx-padding: 10 14; -fx-background-radius: 8; " +
                   "-fx-alignment: center; -fx-text-alignment: center;");
        l.setMinWidth(115); l.setMinHeight(60);
        return l;
    }

    private void carregarDashboard(
            String anoFiltro,
            Label kpiClientes, Label kpiVeiculos, Label kpiAlugueis,
            Label kpiReceita, Label kpiManut, Label kpiTicket, Label kpiVariancia,
            LineChart<Number, Number> lcReceita, PieChart pcCores,
            BarChart<String, Number> bcClientes, BarChart<String, Number> bcManut,
            BarChart<String, Number> bcStats, PieChart pcSegmento) {

        try (Connection con = getConnection()) {

            boolean filtraAno = !anoFiltro.equals("Todos");
            String whereAluguel = filtraAno ? " WHERE YEAR(Data_Retirada) = " + anoFiltro : "";
            String whereManut   = filtraAno ? " WHERE YEAR(Data_Entrada) = " + anoFiltro : "";

            // ── KPIs ──────────────────────────────────────────
            try (Statement st = con.createStatement()) {
                ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM Cliente");
                if (rs.next()) kpiClientes.setText("Clientes\n" + rs.getInt(1));

                rs = st.executeQuery("SELECT COUNT(*) FROM Veiculo");
                if (rs.next()) kpiVeiculos.setText("Veículos\n" + rs.getInt(1));

                rs = st.executeQuery("SELECT COUNT(*) FROM Aluguel" + whereAluguel);
                if (rs.next()) kpiAlugueis.setText("Aluguéis\n" + rs.getInt(1));

                rs = st.executeQuery("SELECT COALESCE(SUM(Valor_Total),0) FROM Aluguel" + whereAluguel);
                if (rs.next()) kpiReceita.setText("Receita Total\nR$ " + String.format("%.2f", rs.getDouble(1)));

                rs = st.executeQuery("SELECT COALESCE(SUM(Custo),0) FROM Manutencao" + whereManut);
                if (rs.next()) kpiManut.setText("Custo Manutenção\nR$ " + String.format("%.2f", rs.getDouble(1)));

                rs = st.executeQuery("SELECT COALESCE(AVG(Valor_Total),0) FROM Aluguel" + whereAluguel);
                if (rs.next()) kpiTicket.setText("Ticket Médio\nR$ " + String.format("%.2f", rs.getDouble(1)));
            }

            // ── Gráfico 1: Receita Mensal (LineChart) ─────────
            lcReceita.getData().clear();
            XYChart.Series<Number, Number> serieReceita = new XYChart.Series<>();
            String condAno = filtraAno ? " WHERE YEAR(Data_Retirada) = " + anoFiltro : "";
            String sqlReceita = "SELECT MONTH(Data_Retirada) AS mes, SUM(Valor_Total) AS receita " +
                    "FROM Aluguel" + condAno + " GROUP BY mes ORDER BY mes";
            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sqlReceita)) {
                while (rs.next())
                    serieReceita.getData().add(new XYChart.Data<>(rs.getInt("mes"), rs.getDouble("receita")));
            }
            lcReceita.getData().add(serieReceita);

            // ── Gráfico 2: PieChart — Veículos por Cor ────────
            pcCores.getData().clear();
            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(
                         "SELECT Cor, COUNT(*) AS qtd FROM Veiculo GROUP BY Cor ORDER BY qtd DESC")) {
                while (rs.next())
                    pcCores.getData().add(new PieChart.Data(rs.getString("Cor"), rs.getInt("qtd")));
            }

            // ── Gráfico 3: Top 5 Clientes ─────────────────────
            bcClientes.getData().clear();
            XYChart.Series<String, Number> serieClientes = new XYChart.Series<>();
            String sqlClientes = "SELECT c.Nome, COALESCE(SUM(a.Valor_Total),0) AS total " +
                    "FROM Aluguel a JOIN Cliente c ON a.CPF_Cliente = c.CPF " +
                    (filtraAno ? "WHERE YEAR(a.Data_Retirada) = " + anoFiltro + " " : "") +
                    "GROUP BY c.Nome ORDER BY total DESC LIMIT 5";
            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sqlClientes)) {
                while (rs.next()) {
                    String nome = rs.getString("Nome");
                    String primeiroNome = nome.split(" ")[0];
                    serieClientes.getData().add(new XYChart.Data<>(primeiroNome, rs.getDouble("total")));
                }
            }
            bcClientes.getData().add(serieClientes);

            // ── Gráfico 4: Custo por Tipo de Manutenção ───────
            bcManut.getData().clear();
            XYChart.Series<String, Number> serieManut = new XYChart.Series<>();
            String sqlManut = "SELECT Tipo_Servico, SUM(Custo) AS total FROM Manutencao" +
                    whereManut + " GROUP BY Tipo_Servico ORDER BY total DESC LIMIT 6";
            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sqlManut)) {
                while (rs.next()) {
                    String tipo = rs.getString("Tipo_Servico");
                    if (tipo.length() > 16) tipo = tipo.substring(0, 16) + "…";
                    serieManut.getData().add(new XYChart.Data<>(tipo, rs.getDouble("total")));
                }
            }
            bcManut.getData().add(serieManut);

            // ── Gráfico 5: Estatísticas de Aluguel ────────────
            // Busca todos os valores para calcular mediana e moda em Java
            List<Double> valores = new ArrayList<>();
            String sqlVals = "SELECT Valor_Total FROM Aluguel WHERE Valor_Total IS NOT NULL" +
                    (filtraAno ? " AND YEAR(Data_Retirada) = " + anoFiltro : "") +
                    " ORDER BY Valor_Total";
            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sqlVals)) {
                while (rs.next()) valores.add(rs.getDouble("Valor_Total"));
            }

            bcStats.getData().clear();
            if (!valores.isEmpty()) {
                double media = valores.stream().mapToDouble(d -> d).average().orElse(0);
                int n = valores.size();
                double mediana = (n % 2 == 0)
                        ? (valores.get(n / 2 - 1) + valores.get(n / 2)) / 2.0
                        : valores.get(n / 2);
                Map<Double, Long> freq = valores.stream()
                        .collect(Collectors.groupingBy(d -> d, Collectors.counting()));
                double moda = freq.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey).orElse(0.0);
                double variancia = valores.stream()
                        .mapToDouble(d -> Math.pow(d - media, 2)).average().orElse(0);
                double desvioPadrao = Math.sqrt(variancia);

                kpiVariancia.setText("Variância\n" + String.format("%.2f", variancia));

                XYChart.Series<String, Number> serieStats = new XYChart.Series<>();
                serieStats.getData().add(new XYChart.Data<>("Média", media));
                serieStats.getData().add(new XYChart.Data<>("Mediana", mediana));
                serieStats.getData().add(new XYChart.Data<>("Moda", moda));
                serieStats.getData().add(new XYChart.Data<>("Desvio Padrão", desvioPadrao));
                bcStats.getData().add(serieStats);
            }

            // ── Gráfico 6: PieChart — Veículos por Segmento ───
            pcSegmento.getData().clear();
            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(
                         "SELECT m.Segmento, COUNT(v.Chassi) AS qtd " +
                         "FROM Veiculo v JOIN Marca m ON v.Id_Marca = m.ID_Marca " +
                         "GROUP BY m.Segmento ORDER BY qtd DESC")) {
                while (rs.next())
                    pcSegmento.getData().add(new PieChart.Data(rs.getString("Segmento"), rs.getInt("qtd")));
            }

        } catch (Exception ex) {
            erro("Erro ao carregar dashboard: " + ex.getMessage());
        }
    }

    public static void main(String[] args) { launch(args); }
}
