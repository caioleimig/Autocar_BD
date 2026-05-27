USE veiculos_db;

-- 1. JOIN + GROUP BY + HAVING
SELECT f.Nome, COUNT(c.ID_Contrato) AS Total_Contratos
FROM funcionario f
JOIN contrato c ON f.Matricula = c.Matricula_Funcionario
GROUP BY f.Nome
HAVING COUNT(c.ID_Contrato) >= 1;

-- 2. 2 JOINs + WHERE
SELECT cl.Nome, v.Placa, v.Cor, a.Data_Retirada, a.Valor_Total
FROM aluguel a
JOIN cliente cl ON a.CPF_Cliente = cl.CPF
JOIN veiculo v ON a.Chassi_Veiculo = v.Chassi
WHERE a.Valor_Total > 0;

-- 3. ANTI JOIN
SELECT cl.CPF, cl.Nome
FROM cliente cl
LEFT JOIN aluguel a ON cl.CPF = a.CPF_Cliente
WHERE a.CPF_Cliente IS NULL;

-- 4. SUBCONSULTA
SELECT v.Chassi, v.Placa, v.Cor, m.Nome_Marca
FROM veiculo v
JOIN marca m ON v.Id_Marca = m.ID_Marca
WHERE v.Id_Marca = (
    SELECT Id_Marca
    FROM veiculo
    GROUP BY Id_Marca
    ORDER BY COUNT(*) DESC
    LIMIT 1
);
-- ── PROCEDURE: sp_registrar_devolucao ────────────────────────
DELIMITER $$

CREATE PROCEDURE sp_registrar_devolucao(
    IN p_id_aluguel INT,
    IN p_data_devolucao DATE,
    IN p_km_chegada DECIMAL(10,2)
)
BEGIN
    DECLARE v_km_saida   DECIMAL(10,2);
    DECLARE v_retirada   DATE;
    DECLARE v_dias       INT;
    DECLARE v_km_rodados DECIMAL(10,2);
    DECLARE v_valor      DECIMAL(10,2);

    SELECT Km_Saida, Data_Retirada
    INTO v_km_saida, v_retirada
    FROM Aluguel
    WHERE ID_Aluguel = p_id_aluguel;

    SET v_dias       = DATEDIFF(p_data_devolucao, v_retirada);
    SET v_km_rodados = p_km_chegada - v_km_saida;
    SET v_valor      = (v_dias * 80.00) + (v_km_rodados * 1.50);

    UPDATE Aluguel
    SET Data_Devolucao = p_data_devolucao,
        Km_Chegada     = p_km_chegada,
        Valor_Total    = v_valor
    WHERE ID_Aluguel = p_id_aluguel;

    SELECT CONCAT('Devolução registrada! Dias: ', v_dias,
                  ' | Km rodados: ', v_km_rodados,
                  ' | Valor: R$ ', v_valor) AS resultado;
END$$

DELIMITER ;

-- ── PROCEDURE: sp_reajuste_salarial ──────────────────────────
DELIMITER $$

CREATE PROCEDURE sp_reajuste_salarial()
BEGIN
    UPDATE Funcionario SET Salario = Salario * 1.15 WHERE Cargo = 'Gerente Geral';
    UPDATE Funcionario SET Salario = Salario * 1.12 WHERE Cargo IN ('Supervisora', 'Supervisor');
    UPDATE Funcionario SET Salario = Salario * 1.10 WHERE Cargo IN ('Coordenadora', 'Coordenador');
    UPDATE Funcionario SET Salario = Salario * 1.08 WHERE Cargo = 'Analista';
    UPDATE Funcionario SET Salario = Salario * 1.05 WHERE Cargo = 'Atendente';

    SELECT 'Reajuste salarial aplicado com sucesso!' AS resultado;
END$$

DELIMITER ;