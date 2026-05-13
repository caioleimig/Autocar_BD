USE veiculos_db;

CREATE INDEX idx_aluguel_cpf_cliente
    ON Aluguel (CPF_Cliente);

CREATE INDEX idx_contrato_matricula_funcionario
    ON Contrato (Matricula_Funcionario);