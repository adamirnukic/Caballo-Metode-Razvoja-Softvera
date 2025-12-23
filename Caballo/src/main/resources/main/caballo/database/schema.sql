CREATE DATABASE IF NOT EXISTS caballo CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE caballo;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    salt VARCHAR(64) NOT NULL,
    role ENUM('admin','user') NOT NULL DEFAULT 'user'
);

CREATE TABLE IF NOT EXISTS menu_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    naziv VARCHAR(150) NOT NULL,
    opis TEXT,
    cijena DECIMAL(10,2) NOT NULL,
    kategorija VARCHAR(100) NOT NULL,
    current_qty INT NOT NULL DEFAULT 0,
    is_active TINYINT NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS tables (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    broj_stola INT NOT NULL UNIQUE,
    broj_sjedista INT NOT NULL,
    status ENUM('slobodan','zauzet','rezervisan') NOT NULL DEFAULT 'slobodan'
);

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    datum DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ukupno DECIMAL(10,2) NOT NULL DEFAULT 0,
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    kolicina INT NOT NULL,
    CONSTRAINT fk_orderitems_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_orderitems_item FOREIGN KEY (item_id) REFERENCES menu_items(id)
);

CREATE TABLE IF NOT EXISTS reservations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    table_id BIGINT NOT NULL,
    ime_gosta VARCHAR(150) NOT NULL,
    broj_telefona VARCHAR(50),
    datum_rezervacije DATE NOT NULL,
    vrijeme_dolaska TIME NOT NULL,
    broj_osoba INT NOT NULL,
    napomena TEXT,
    CONSTRAINT fk_res_table FOREIGN KEY (table_id) REFERENCES tables(id)
);

CREATE TABLE IF NOT EXISTS item_stock_movements (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    item_id BIGINT NOT NULL,
    datum DATE NOT NULL,
    opening_qty INT NOT NULL DEFAULT 0,
    received_qty INT NOT NULL DEFAULT 0,
    physical_closing_qty INT NOT NULL DEFAULT 0,
    napomena TEXT,
    CONSTRAINT fk_item_stock_item FOREIGN KEY (item_id) REFERENCES menu_items(id),
    CONSTRAINT uq_item_stock_item_date UNIQUE (item_id, datum)
);

DELIMITER $$

CREATE PROCEDURE ensure_item_stock_for_date(IN p_date DATE)
BEGIN
    DECLARE v_prev_date DATE;
    SET v_prev_date = DATE_SUB(p_date, INTERVAL 1 DAY);

INSERT INTO item_stock_movements (item_id, datum, opening_qty, received_qty, physical_closing_qty)
SELECT
    mi.id,
    p_date AS datum,
    COALESCE(prev.physical_closing_qty, mi.current_qty) AS opening_qty,
    0 AS received_qty,
    0 AS physical_closing_qty
FROM menu_items mi
         LEFT JOIN item_stock_movements prev
                   ON prev.item_id = mi.id
                       AND prev.datum = v_prev_date
         LEFT JOIN item_stock_movements cur
                   ON cur.item_id = mi.id
                       AND cur.datum = p_date
WHERE cur.id IS NULL;
END $$

DELIMITER ;



DELIMITER $$

CREATE PROCEDURE close_day_stock(IN p_date DATE)
BEGIN
UPDATE item_stock_movements sm
    JOIN menu_items mi ON mi.id = sm.item_id
    SET sm.physical_closing_qty = mi.current_qty
WHERE sm.datum = p_date;
END$$

DELIMITER ;