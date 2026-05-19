-- =============================================================
-- PROCEDURES - Sistema Autocar
-- =============================================================

USE veiculos_db;

-- =============================================================
-- PROCEDURE 1: Registrar devolução de aluguel
-- Justificativa: Quando um cliente devolve o veículo, o sistema
-- precisa atualizar o aluguel com data de devolução, KM de
-- chegada e valor total (R$1,50/km + R$80,00/dia).
-- Um simples UPDATE avulso não garante o cálculo correto do
-- valor; o procedimento centraliza e automatiza essa lógica.
-- =============================================================
DELIMITER $$

CREATE PROCEDURE sp_registrar_devolucao(
    IN p_id_aluguel    INT,
    IN p_data_dev      DATE,
    IN p_km_chegada    DECIMAL(10,2)
)
BEGIN
    DECLARE v_km_saida      DECIMAL(10,2);
    DECLARE v_data_ret      DATE;
    DECLARE v_valor_total   DECIMAL(10,2);
    DECLARE v_dias          INT;
    DECLARE v_km_percorrida DECIMAL(10,2);

    SELECT Km_Saida, Data_Retirada
    INTO   v_km_saida, v_data_ret
    FROM   Aluguel
    WHERE  Id_Aluguel = p_id_aluguel
      AND  Data_Devolucao IS NULL;

    SET v_dias          = DATEDIFF(p_data_dev, v_data_ret);
    SET v_km_percorrida = p_km_chegada - v_km_saida;
    SET v_valor_total   = (v_dias * 80.00) + (v_km_percorrida * 1.50);

    UPDATE Aluguel
    SET    Data_Devolucao = p_data_dev,
           Km_Chegada     = p_km_chegada,
           Valor_Total    = v_valor_total
    WHERE  Id_Aluguel = p_id_aluguel;

    SELECT CONCAT('Devolucao registrada. Valor total: R$ ',
                  FORMAT(v_valor_total, 2)) AS resultado;
END$$

DELIMITER ;

-- Exemplo de uso:
-- CALL sp_registrar_devolucao(1, '2025-06-10', 1350.00);


-- =============================================================
-- PROCEDURE 2: Reajuste salarial escalonado por cargo (CURSOR)
-- Justificativa: cada funcionário recebe um percentual diferente
-- conforme o cargo (Gerente=15%, Atendente=10%, demais=5%) e
-- um teto máximo por faixa. Isso exige avaliar cada linha
-- individualmente; não é possível expressar com um UPDATE único,
-- pois a lógica condicional depende do estado de cada registro
-- e de variáveis calculadas por linha.
-- =============================================================
DELIMITER $$

CREATE PROCEDURE sp_reajuste_salarial()
BEGIN
    DECLARE v_matricula  INT;
    DECLARE v_cargo      VARCHAR(50);
    DECLARE v_salario    DECIMAL(10,2);
    DECLARE v_novo_sal   DECIMAL(10,2);
    DECLARE v_percentual DECIMAL(5,2);
    DECLARE v_teto       DECIMAL(10,2);
    DECLARE v_fim        BOOLEAN DEFAULT FALSE;

    DECLARE cur_func CURSOR FOR
        SELECT Matricula, Cargo, Salario
        FROM   Funcionario
        ORDER  BY Matricula;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_fim = TRUE;

    OPEN cur_func;

    loop_func: LOOP
        FETCH cur_func INTO v_matricula, v_cargo, v_salario;
        IF v_fim THEN LEAVE loop_func; END IF;

        CASE
            WHEN v_cargo = 'Gerente'   THEN SET v_percentual = 0.15; SET v_teto = 15000.00;
            WHEN v_cargo = 'Atendente' THEN SET v_percentual = 0.10; SET v_teto = 5000.00;
            ELSE                            SET v_percentual = 0.05; SET v_teto = 8000.00;
        END CASE;

        SET v_novo_sal = v_salario + (v_salario * v_percentual);

        IF v_novo_sal > v_teto THEN
            SET v_novo_sal = v_teto;
        END IF;

        UPDATE Funcionario
        SET    Salario = v_novo_sal
        WHERE  Matricula = v_matricula;

    END LOOP;

    CLOSE cur_func;

    SELECT 'Reajuste salarial aplicado com sucesso.' AS resultado;
END$$

DELIMITER ;

-- Exemplo de uso:
-- CALL sp_reajuste_salarial();