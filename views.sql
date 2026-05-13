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