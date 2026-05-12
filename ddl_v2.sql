-- =============================================================
-- DDL - Sistema de Gerenciamento de Veiculos e Contratos
-- MySQL 8+ | Entrega 03
-- =============================================================

CREATE DATABASE IF NOT EXISTS veiculos_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE veiculos_db;

SET FOREIGN_KEY_CHECKS = 0;

-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Cliente (
  CPF    CHAR(11)     NOT NULL,
  Nome   VARCHAR(100) NOT NULL,
  Rua    VARCHAR(100) DEFAULT 'Nao informado',
  Bairro VARCHAR(60)  DEFAULT 'Nao informado',
  Numero VARCHAR(10)  DEFAULT 'S/N',
  CONSTRAINT pk_cliente PRIMARY KEY (CPF),
  CONSTRAINT chk_cliente_cpf CHECK (CHAR_LENGTH(CPF) = 11)
);

-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Telefone_Cliente (
  Telefone    VARCHAR(15) NOT NULL,
  CPF_Cliente CHAR(11)    NOT NULL,
  CONSTRAINT pk_telefone_cliente PRIMARY KEY (Telefone, CPF_Cliente),
  CONSTRAINT fk_tel_cliente FOREIGN KEY (CPF_Cliente)
    REFERENCES Cliente(CPF)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Seguradora (
  CNPJ                CHAR(14)     NOT NULL,
  Nome_Fantasia       VARCHAR(100) NOT NULL,
  Telefone_Emergencia VARCHAR(15)  DEFAULT NULL,
  CONSTRAINT pk_seguradora PRIMARY KEY (CNPJ),
  CONSTRAINT uq_seguradora_nome UNIQUE (Nome_Fantasia),
  CONSTRAINT chk_seguradora_cnpj CHECK (CHAR_LENGTH(CNPJ) = 14)
);

-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Marca (
  ID_Marca     INT          NOT NULL AUTO_INCREMENT,
  Nome_Marca   VARCHAR(50)  NOT NULL,
  Pais_Origem  VARCHAR(50)  DEFAULT 'Nao informado',
  Ano_Fundacao YEAR         DEFAULT NULL,
  Site_Oficial VARCHAR(100) DEFAULT NULL,
  Segmento     VARCHAR(50)  DEFAULT 'Passeio',
  CONSTRAINT pk_marca PRIMARY KEY (ID_Marca),
  CONSTRAINT uq_marca_nome UNIQUE (Nome_Marca)
);

-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Veiculo (
  Chassi          VARCHAR(17) NOT NULL,
  Placa           CHAR(7)     NOT NULL,
  Cor             VARCHAR(30) DEFAULT 'Nao informado',
  Ano_Fabricacao  YEAR        NOT NULL,
  Id_Marca        INT         NOT NULL,
  CNPJ_Seguradora CHAR(14)    DEFAULT NULL,
  CONSTRAINT pk_veiculo PRIMARY KEY (Chassi),
  CONSTRAINT uq_veiculo_placa UNIQUE (Placa),
  CONSTRAINT chk_veiculo_chassi CHECK (CHAR_LENGTH(Chassi) = 17),
  CONSTRAINT fk_veiculo_marca FOREIGN KEY (Id_Marca)
    REFERENCES Marca(ID_Marca)
    ON UPDATE CASCADE,
  CONSTRAINT fk_veiculo_seguradora FOREIGN KEY (CNPJ_Seguradora)
    REFERENCES Seguradora(CNPJ)
    ON UPDATE CASCADE
    ON DELETE SET NULL
);

-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Carro (
  Chassi             VARCHAR(17)  NOT NULL,
  Qtd_Portas         TINYINT      NOT NULL DEFAULT 4,
  Volume_Porta_Malas DECIMAL(6,2) DEFAULT NULL,
  CONSTRAINT pk_carro PRIMARY KEY (Chassi),
  CONSTRAINT fk_carro_veiculo FOREIGN KEY (Chassi)
    REFERENCES Veiculo(Chassi)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT chk_carro_portas CHECK (Qtd_Portas IN (2, 4))
);

-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Caminhonete (
  Chassi           VARCHAR(17)   NOT NULL,
  Capacidade_Carga DECIMAL(10,2) NOT NULL,
  Tipo_Tracao      VARCHAR(20)   DEFAULT '4x2',
  CONSTRAINT pk_caminhonete PRIMARY KEY (Chassi),
  CONSTRAINT fk_caminhonete_veiculo FOREIGN KEY (Chassi)
    REFERENCES Veiculo(Chassi)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT chk_caminhonete_carga CHECK (Capacidade_Carga > 0)
);

-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Funcionario (
  Matricula            INT          NOT NULL,
  Nome                 VARCHAR(100) NOT NULL,
  Salario              DECIMAL(10,2) NOT NULL DEFAULT 1412.00,
  Cargo                VARCHAR(50)  NOT NULL,
  Matricula_Supervisor INT          DEFAULT NULL,
  CONSTRAINT pk_funcionario PRIMARY KEY (Matricula),
  CONSTRAINT chk_funcionario_salario CHECK (Salario >= 1412.00),
  CONSTRAINT fk_func_supervisor FOREIGN KEY (Matricula_Supervisor)
    REFERENCES Funcionario(Matricula)
    ON DELETE SET NULL
);

-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Dependente (
  Id_Dependente   INT          NOT NULL AUTO_INCREMENT,
  Nome_Dependente VARCHAR(100) NOT NULL,
  Data_Nascimento DATE         DEFAULT NULL,
  Grau_Parentesco VARCHAR(30)  DEFAULT NULL,
  Matricula_Func  INT          NOT NULL,
  CONSTRAINT pk_dependente PRIMARY KEY (Id_Dependente),
  CONSTRAINT fk_dep_funcionario FOREIGN KEY (Matricula_Func)
    REFERENCES Funcionario(Matricula)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Contrato (
  ID_Contrato           INT      NOT NULL AUTO_INCREMENT,
  Data_Inicio           DATE     NOT NULL,
  Data_Fim              DATE     NOT NULL,
  CPF_Cliente           CHAR(11) NOT NULL,
  Matricula_Funcionario INT      NOT NULL,
  CONSTRAINT pk_contrato PRIMARY KEY (ID_Contrato),
  CONSTRAINT chk_contrato_datas CHECK (Data_Fim > Data_Inicio),
  CONSTRAINT fk_contrato_cliente FOREIGN KEY (CPF_Cliente)
    REFERENCES Cliente(CPF)
    ON UPDATE CASCADE,
  CONSTRAINT fk_contrato_funcionario FOREIGN KEY (Matricula_Funcionario)
    REFERENCES Funcionario(Matricula)
);

-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Contrato_Veiculo (
  ID_Contrato    INT         NOT NULL,
  Chassi_Veiculo VARCHAR(17) NOT NULL,
  CONSTRAINT pk_contrato_veiculo PRIMARY KEY (ID_Contrato, Chassi_Veiculo),
  CONSTRAINT fk_cv_contrato FOREIGN KEY (ID_Contrato)
    REFERENCES Contrato(ID_Contrato)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT fk_cv_veiculo FOREIGN KEY (Chassi_Veiculo)
    REFERENCES Veiculo(Chassi)
    ON UPDATE CASCADE
);

-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Aluguel (
  Id_Aluguel     INT           NOT NULL AUTO_INCREMENT,
  Data_Retirada  DATE          NOT NULL,
  Data_Devolucao DATE          DEFAULT NULL,
  Valor_Total    DECIMAL(10,2) DEFAULT NULL,
  Km_Saida       DECIMAL(10,2) NOT NULL DEFAULT 0,
  Km_Chegada     DECIMAL(10,2) DEFAULT NULL,
  CPF_Cliente    CHAR(11)      NOT NULL,
  Chassi_Veiculo VARCHAR(17)   NOT NULL,
  CONSTRAINT pk_aluguel PRIMARY KEY (Id_Aluguel),
  CONSTRAINT chk_aluguel_km CHECK (Km_Chegada IS NULL OR Km_Chegada >= Km_Saida),
  CONSTRAINT chk_aluguel_valor CHECK (Valor_Total IS NULL OR Valor_Total >= 0),
  CONSTRAINT fk_aluguel_cliente FOREIGN KEY (CPF_Cliente)
    REFERENCES Cliente(CPF)
    ON UPDATE CASCADE,
  CONSTRAINT fk_aluguel_veiculo FOREIGN KEY (Chassi_Veiculo)
    REFERENCES Veiculo(Chassi)
    ON UPDATE CASCADE
);

-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Manutencao (
  Id_Manutencao  INT           NOT NULL AUTO_INCREMENT,
  Data_Entrada   DATE          NOT NULL,
  Data_Saida     DATE          DEFAULT NULL,
  Tipo_Servico   VARCHAR(80)   NOT NULL,
  Custo          DECIMAL(10,2) DEFAULT 0.00,
  Chassi_Veiculo VARCHAR(17)   NOT NULL,
  CONSTRAINT pk_manutencao PRIMARY KEY (Id_Manutencao),
  CONSTRAINT chk_manutencao_custo CHECK (Custo >= 0),
  CONSTRAINT chk_manutencao_datas CHECK (Data_Saida IS NULL OR Data_Saida >= Data_Entrada),
  CONSTRAINT fk_manutencao_veiculo FOREIGN KEY (Chassi_Veiculo)
    REFERENCES Veiculo(Chassi)
    ON UPDATE CASCADE
);

CREATE OR REPLACE VIEW vw_historico_alugueis_cliente AS
SELECT
    a.Id_Aluguel,
    c.CPF,
    c.Nome AS Nome_Cliente,
    v.Placa,
    m.Nome_Marca,
    v.Ano_Fabricacao,
    a.Data_Retirada,
    a.Data_Devolucao,
    (a.Km_Chegada - a.Km_Saida) AS Km_Percorrido,
    a.Valor_Total
FROM Aluguel a
INNER JOIN Cliente c    ON a.CPF_Cliente     = c.CPF
INNER JOIN Veiculo v    ON a.Chassi_Veiculo  = v.Chassi
INNER JOIN Marca m      ON v.Id_Marca        = m.ID_Marca
WHERE a.Data_Devolucao IS NOT NULL;

CREATE OR REPLACE VIEW vw_veiculos_nunca_alugados AS
SELECT
    v.Chassi,
    v.Placa,
    v.Cor,
    v.Ano_Fabricacao,
    m.Nome_Marca,
    m.Segmento
FROM Veiculo v
INNER JOIN Marca m ON v.Id_Marca = m.ID_Marca
WHERE v.Chassi NOT IN (
    SELECT DISTINCT Chassi_Veiculo
    FROM Aluguel
);

SET FOREIGN_KEY_CHECKS = 1;
