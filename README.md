# Autocar

Sistema de gerenciamento de veículos e contratos desenvolvido para a disciplina de Banco de Dados da CESAR School.

---

## Tecnologias utilizadas

- Java 17+
- JavaFX (interface gráfica)
- MySQL 8+
- JDBC (conexão com o banco, sem ORM)

---

## Pré-requisitos

Antes de rodar o projeto, você precisa ter instalado:

- [JDK 17 ou superior](https://adoptium.net)
- [JavaFX SDK 21](https://gluonhq.com/products/javafx/) — baixe a versão para o seu sistema operacional
- [MySQL 8+](https://dev.mysql.com/downloads/mysql/)
- [MySQL Connector/J](https://dev.mysql.com/downloads/connector/j/) — selecione "Platform Independent" e baixe o ZIP
- [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) — a versão Community é gratuita

---

## Configurando o banco de dados

1. Abra o MySQL Workbench e conecte ao servidor local
2. Vá em **File > Open SQL Script**, selecione o arquivo `ddl_v2.sql` e execute (botão ⚡)
3. Repita o processo com o arquivo `dml_v2.sql`

Após isso, o banco `veiculos_db` estará criado com todas as tabelas e dados de exemplo.

---

## Configurando o projeto no IntelliJ

1. Abra o IntelliJ e vá em **File > Open** → selecione a pasta do projeto
2. Vá em **File > Project Structure** (`Ctrl+Alt+Shift+S`)
   - Em **Libraries**, clique em **+** → **Java** → selecione a pasta `lib` dentro do JavaFX SDK
   - Em **Libraries**, clique em **+** → **Java** → selecione o arquivo `.jar` do MySQL Connector
3. Edite o arquivo `src/com/veiculos/util/DBConnection.java` e troque a senha:
   ```java
   private static final String PASS = "sua_senha_aqui";
   ```

---

## Configurando o Run Configuration

1. Vá em **Run > Edit Configurations**
2. Clique em **+** → **Application**
3. Preencha:
   - **Name:** VeiculosApp
   - **Main class:** `com.veiculos.controller.MainApp`
4. Clique em **Modify options** → **Add VM options** e cole:
   ```
   --module-path "caminho/para/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml
   ```
   Substitua `caminho/para/javafx-sdk` pelo caminho real onde você extraiu o JavaFX SDK.
5. Clique em **OK** e rode com **Shift+F10** ou o botão ▶

---

## Como usar

### Aba Clientes
- **Inserir:** preencha os campos e clique em Salvar
- **Editar:** clique em um cliente na tabela, altere os campos e clique em Salvar
- **Deletar:** clique em um cliente na tabela e clique em Deletar

### Aba Veículos
- **Inserir:** preencha os campos e clique em Salvar
- **Editar:** clique em um veículo na tabela, altere os campos e clique em Salvar
- **Deletar:** clique em um veículo na tabela e clique em Deletar

---

## Estrutura do projeto

```
Autocar_BD/
├── ddl_v2.sql               — Criação das tabelas
├── dml_v2.sql               — Inserção de dados (30+ registros por tabela)
├── README.md
└── src/com/veiculos/
    ├── controller/
    │   └── MainApp.java     — Interface JavaFX
    ├── dao/
    │   ├── ClienteDAO.java  — Operações SQL de Cliente
    │   └── VeiculoDAO.java  — Operações SQL de Veículo
    ├── model/
    │   ├── Cliente.java
    │   └── Veiculo.java
    └── util/
        └── DBConnection.java — Conexão com o MySQL
```
