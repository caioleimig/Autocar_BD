# Sistema de Veículos e Contratos
### Entrega 03 — Banco de Dados | CESAR School

Interface JavaFX com CRUD para **Cliente** e **Veículo**, integrada ao MySQL via JDBC puro.

---

## Pré-requisitos

| Ferramenta | Download |
|---|---|
| JDK 17+ | https://adoptium.net |
| JavaFX SDK 21 | https://gluonhq.com/products/javafx/ |
| MySQL 8+ | já instalado |
| MySQL Connector/J | https://dev.mysql.com/downloads/connector/j/ |
| IntelliJ IDEA Community | já instalado |

---

## 1. Configurar o banco

1. Abra o **MySQL Workbench**
2. Clique em **File > Open SQL Script** → selecione `ddl_v2.sql` → execute (⚡)
3. Clique em **File > Open SQL Script** → selecione `dml_v2.sql` → execute (⚡)

---

## 2. Abrir o projeto no IntelliJ

1. **File > Open** → selecione a pasta `veiculos_javafx`
2. **File > Project Structure (Ctrl+Alt+Shift+S)**
   - Em **Project**: selecione o JDK 17+
   - Em **Modules > Dependencies > (+) > JARs**: adicione o `mysql-connector-j-x.x.x.jar`
   - Em **Libraries > (+) > Java**: adicione a pasta `lib` do JavaFX SDK
3. Edite a senha em `src/com/veiculos/util/DBConnection.java`:
   ```java
   private static final String PASS = "sua_senha_aqui";
   ```

---

## 3. Configurar Run Configuration

1. **Run > Edit Configurations > (+) > Application**
2. **Main class:** `com.veiculos.controller.MainApp`
3. **VM Options:**
```
--module-path "C:\caminho\javafx-sdk\lib" --add-modules javafx.controls,javafx.fxml
```
4. Clique em OK e rode com **Shift+F10**

---

## 4. Usando a interface

### Aba Clientes
- **Inserir:** preencha os campos → Salvar
- **Editar:** clique na linha da tabela → altere os campos → Salvar
- **Deletar:** clique na linha da tabela → Deletar

### Aba Veículos
- **Inserir:** preencha os campos → Salvar
- **Editar:** clique na linha da tabela → altere os campos → Salvar
- **Deletar:** clique na linha da tabela → Deletar

> O campo **Id Marca** deve ser um número inteiro de uma Marca existente no banco (1=Toyota, 2=Ford, etc.)

---

## Estrutura do projeto

```
veiculos_javafx/
├── ddl_v2.sql                          ← Criação das tabelas
├── dml_v2.sql                          ← Inserção de dados (30+ por tabela)
├── README.md
└── src/com/veiculos/
    ├── controller/
    │   └── MainApp.java                ← Interface JavaFX
    ├── dao/
    │   ├── ClienteDAO.java             ← CRUD Cliente (JDBC puro)
    │   └── VeiculoDAO.java             ← CRUD Veículo (JDBC puro)
    ├── model/
    │   ├── Cliente.java
    │   └── Veiculo.java
    └── util/
        └── DBConnection.java           ← Conexão MySQL
```

---

## Constraints aplicadas no DDL

| Constraint | Onde foi aplicada |
|---|---|
| `PRIMARY KEY` | Todas as tabelas |
| `FOREIGN KEY` | Todas as relações |
| `ON UPDATE CASCADE` | Telefone_Cliente, Veiculo, Carro, Caminhonete, Dependente, Contrato, Aluguel, Manutencao |
| `ON DELETE SET NULL` | Veiculo → Seguradora, Funcionario → Supervisor |
| `ON DELETE CASCADE` | Telefone_Cliente, Carro, Caminhonete, Dependente, Contrato_Veiculo |
| `CHECK` | CPF (11 chars), CNPJ (14 chars), Chassi (17 chars), Qtd_Portas (2 ou 4), Salario (>= 1412), Custo (>= 0), KM, datas |
| `DEFAULT` | Rua, Bairro, Numero, Segmento, Tipo_Tracao, Salario, Km_Saida, Custo |
| `UNIQUE` | Placa (Veiculo), Nome_Marca, Nome_Fantasia |
