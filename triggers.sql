USE veiculos_db;
 
CREATE TABLE IF NOT EXISTS Log_Salario (
    Id_Log         INT           NOT NULL AUTO_INCREMENT,
    Matricula_Func INT           NOT NULL,
    Salario_Antigo DECIMAL(10,2) NOT NULL,
    Salario_Novo   DECIMAL(10,2) NOT NULL,
    Data_Alteracao DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_log_salario PRIMARY KEY (Id_Log)
);
 
DELIMITER $$
 
CREATE TRIGGER trg_log_alteracao_salario
AFTER UPDATE ON Funcionario
FOR EACH ROW
BEGIN
    IF OLD.Salario <> NEW.Salario THEN
        INSERT INTO Log_Salario (Matricula_Func, Salario_Antigo, Salario_Novo)
        VALUES (OLD.Matricula, OLD.Salario, NEW.Salario);
    END IF;
END$$
 
CREATE TRIGGER trg_bloquear_aluguel_em_manutencao
BEFORE INSERT ON Aluguel
FOR EACH ROW
BEGIN
    DECLARE veiculo_em_manutencao INT;
 
    SELECT COUNT(*) INTO veiculo_em_manutencao
    FROM Manutencao
    WHERE Chassi_Veiculo = NEW.Chassi_Veiculo
      AND Data_Saida IS NULL;
 
    IF veiculo_em_manutencao > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Veiculo em manutencao e nao pode ser alugado.';
    END IF;
END$$
 
DELIMITER ;
USE veiculos_db;

-- Tabela de manutenção (se ainda não existir)
CREATE TABLE IF NOT EXISTS Manutencao (
    Id_Manutencao  INT AUTO_INCREMENT PRIMARY KEY,
    Chassi_Veiculo VARCHAR(50)    NOT NULL,
    Tipo_Servico   VARCHAR(100)   NOT NULL,
    Custo          DECIMAL(10,2)  DEFAULT 0,
    Data_Entrada   DATE           NOT NULL,
    Data_Saida     DATE           DEFAULT NULL,
    FOREIGN KEY (Chassi_Veiculo) REFERENCES Veiculo(Chassi)
);

-- Trigger que bloqueia aluguel de veículo em manutenção aberta
DELIMITER $$

CREATE TRIGGER trg_bloquear_aluguel_em_manutencao
BEFORE INSERT ON Aluguel
FOR EACH ROW
BEGIN
    DECLARE v_em_manutencao INT;

    SELECT COUNT(*) INTO v_em_manutencao
    FROM Manutencao
    WHERE Chassi_Veiculo = NEW.Chassi_Veiculo
      AND Data_Saida IS NULL;

    IF v_em_manutencao > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Veículo em manutenção! Aluguel bloqueado.';
    END IF;
END$$

DELIMITER ;
USE veiculos_db;

CREATE TABLE IF NOT EXISTS Log_Salario (
    Id_Log          INT AUTO_INCREMENT PRIMARY KEY,
    Matricula_Func  INT            NOT NULL,
    Salario_Antigo  DECIMAL(10,2)  NOT NULL,
    Salario_Novo    DECIMAL(10,2)  NOT NULL,
    Data_Alteracao  DATETIME       DEFAULT CURRENT_TIMESTAMP
);

DELIMITER $$

CREATE TRIGGER trg_log_alteracao_salario
AFTER UPDATE ON Funcionario
FOR EACH ROW
BEGIN
    IF OLD.Salario <> NEW.Salario THEN
        INSERT INTO Log_Salario (Matricula_Func, Salario_Antigo, Salario_Novo)
        VALUES (OLD.Matricula, OLD.Salario, NEW.Salario);
    END IF;
END$$

DELIMITER ;