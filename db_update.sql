-- =========================================================================
-- Script de actualización para métodos de pago (Tabla: metodopago)
-- =========================================================================

-- 1. Asegurar Efectivo
UPDATE metodopago SET nombre = 'Efectivo', estado = 1 WHERE idmetodo = 1;
INSERT INTO metodopago (idmetodo, nombre, estado) 
SELECT 1, 'Efectivo', 1 WHERE NOT EXISTS (SELECT 1 FROM metodopago WHERE idmetodo = 1);

-- 2. Asegurar Yape/Plin (Presencial)
UPDATE metodopago SET nombre = 'Yape/Plin (Presencial)', estado = 1 WHERE idmetodo = 2;
INSERT INTO metodopago (idmetodo, nombre, estado) 
SELECT 2, 'Yape/Plin (Presencial)', 1 WHERE NOT EXISTS (SELECT 1 FROM metodopago WHERE idmetodo = 2);

-- 3. Asegurar Transferencia Bancaria / Depósito en Agente
UPDATE metodopago SET nombre = 'Transferencia Bancaria / Depósito en Agente', estado = 1 WHERE idmetodo = 3;
INSERT INTO metodopago (idmetodo, nombre, estado) 
SELECT 3, 'Transferencia Bancaria / Depósito en Agente', 1 WHERE NOT EXISTS (SELECT 1 FROM metodopago WHERE idmetodo = 3);

-- 4. Asegurar Tarjeta de Crédito / Débito (POS Físico - Niubiz/Izipay)
UPDATE metodopago SET nombre = 'Tarjeta de Crédito / Débito (POS Físico - Niubiz/Izipay)', estado = 1 WHERE idmetodo = 4;
INSERT INTO metodopago (idmetodo, nombre, estado) 
SELECT 4, 'Tarjeta de Crédito / Débito (POS Físico - Niubiz/Izipay)', 1 WHERE NOT EXISTS (SELECT 1 FROM metodopago WHERE idmetodo = 4);

-- 5. Asegurar Yape (E-commerce / Pasarela Web)
UPDATE metodopago SET nombre = 'Yape (E-commerce / Pasarela Web)', estado = 1 WHERE idmetodo = 5;
INSERT INTO metodopago (idmetodo, nombre, estado) 
SELECT 5, 'Yape (E-commerce / Pasarela Web)', 1 WHERE NOT EXISTS (SELECT 1 FROM metodopago WHERE idmetodo = 5);

-- 6. Agregar columna para el código de comprobación / número de operación en la tabla venta
ALTER TABLE venta ADD COLUMN nro_operacion VARCHAR(45) NULL;
