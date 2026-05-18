USE veiculos_db;

SET GLOBAL log_bin_trust_function_creators = 1;

DROP FUNCTION IF EXISTS fn_classificar_aluguel;
DROP FUNCTION IF EXISTS fn_total_gasto_cliente;

DELIMITER $$

CREATE FUNCTION fn_classificar_aluguel(
  p_km_saida      DECIMAL(10,2),
  p_km_chegada    DECIMAL(10,2),
  p_diaria_base   DECIMAL(10,2)
)
RETURNS DECIMAL(10,2)
DETERMINISTIC
BEGIN
  DECLARE v_km_rodados  DECIMAL(10,2);
  DECLARE v_valor_total DECIMAL(10,2);

  SET v_km_rodados = p_km_chegada - p_km_saida;

  IF v_km_rodados <= 100 THEN
    SET v_valor_total = p_diaria_base;
  ELSEIF v_km_rodados <= 300 THEN
    SET v_valor_total = p_diaria_base * 1.5;
  ELSE
    SET v_valor_total = p_diaria_base * 2.0;
  END IF;

  RETURN v_valor_total;
END$$

CREATE FUNCTION fn_total_gasto_cliente(
  p_cpf CHAR(11)
)
RETURNS DECIMAL(10,2)
DETERMINISTIC READS SQL DATA
BEGIN
  DECLARE v_total DECIMAL(10,2);

  SELECT COALESCE(SUM(Valor_Total), 0.00)
    INTO v_total
    FROM Aluguel
   WHERE CPF_Cliente = p_cpf
     AND Valor_Total IS NOT NULL;

  RETURN v_total;
END$$

DELIMITER ;

SELECT fn_classificar_aluguel(0, 80, 150.00)  AS Economico;
SELECT fn_classificar_aluguel(0, 200, 150.00) AS Padrao;
SELECT fn_classificar_aluguel(0, 500, 150.00) AS Premium;
SELECT fn_total_gasto_cliente('12345678901')  AS Total_Gasto;