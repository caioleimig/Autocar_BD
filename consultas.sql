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