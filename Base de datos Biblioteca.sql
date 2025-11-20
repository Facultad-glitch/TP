USE biblioteca;

-- 1) Limpieza por si hay restos de ejecuciones previas
DROP TEMPORARY TABLE IF EXISTS tmp_numeros;
DROP TEMPORARY TABLE IF EXISTS autores_temp;
DROP TEMPORARY TABLE IF EXISTS editoriales_temp;

DROP TABLE IF EXISTS libro;
DROP TABLE IF EXISTS fichabibliografica;

-- 2) Crear tablas con relación 1→1 unidireccional (libro → ficha)
CREATE TABLE IF NOT EXISTS fichabibliografica (
    id INT AUTO_INCREMENT PRIMARY KEY,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    isbn VARCHAR(20) UNIQUE NOT NULL,
    clasificacionDewey VARCHAR(10),
    estanteria VARCHAR(10),
    idioma CHAR(2)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS libro (
    id INT AUTO_INCREMENT PRIMARY KEY,
    eliminado BOOLEAN NOT NULL DEFAULT FALSE,
    titulo VARCHAR(255),
    autor VARCHAR(100),
    editorial VARCHAR(100),
    anioEdicion INT,
    fichaBibliografica_id INT UNIQUE, -- 🔹 1→1 asegurada
    FOREIGN KEY (fichaBibliografica_id) REFERENCES fichabibliografica(id)
) ENGINE=InnoDB;

-- 3) Generador de números (para crear muchos registros)
CREATE TEMPORARY TABLE tmp_numeros (n INT) ENGINE=Memory;

SET @rownum := 0;
INSERT INTO tmp_numeros (n)
SELECT @rownum := @rownum + 1
FROM information_schema.columns a, information_schema.columns b
LIMIT 100000; -- Cambia aquí si querés más filas

-- Verificar
SELECT COUNT(*) AS total_tmp FROM tmp_numeros;

-- 4) Insertar fichas bibliográficas
INSERT INTO fichabibliografica (eliminado, isbn, clasificacionDewey, estanteria, idioma)
SELECT
    (RAND() < 0.02) AS eliminado,
    CONCAT(
        LPAD(FLOOR(RAND() * 999999999999), 12, '0'),
        LPAD(n, 5, '0')
    ) AS isbn,
    CASE 
        WHEN n % 10 IN (0,1,2) THEN '800'
        WHEN n % 10 IN (3,4,5) THEN '900'
        WHEN n % 10 = 6 THEN '500'
        WHEN n % 10 = 7 THEN '100'
        ELSE '000'
    END AS clasificacionDewey,
    CONCAT('E', LPAD(FLOOR(1 + RAND() * 30), 2, '0')) AS estanteria,
    CASE
        WHEN RAND() < 0.70 THEN 'ES'
        WHEN RAND() < 0.85 THEN 'EN'
        WHEN RAND() < 0.93 THEN 'FR'
        ELSE 'PT'
    END AS idioma
FROM tmp_numeros;

-- 5) Tablas temporales de autores y editoriales
CREATE TEMPORARY TABLE autores_temp (
    aid INT,
    nombre VARCHAR(200)
) ENGINE=Memory;

INSERT INTO autores_temp (aid, nombre) VALUES
(0, 'Gabriel García Márquez'),
(1, 'Jane Austen'),
(2, 'J.R.R. Tolkien'),
(3, 'Isabel Allende'),
(4, 'Haruki Murakami'),
(5, 'George Orwell'),
(6, 'Julio Cortázar'),
(7, 'Ernest Hemingway'),
(8, 'Miguel de Cervantes'),
(9, 'Stephen King'),
(10, 'Ursula K. Le Guin'),
(11, 'Mario Vargas Llosa'),
(12, 'Agatha Christie');

CREATE TEMPORARY TABLE editoriales_temp (
    eid INT,
    nombre VARCHAR(200)
) ENGINE=Memory;

INSERT INTO editoriales_temp (eid, nombre) VALUES
(0, 'Planeta'),
(1, 'Alfaguara'),
(2, 'Penguin Random House'),
(3, 'Anagrama'),
(4, 'Seix Barral'),
(5, 'HarperCollins'),
(6, 'Tusquets'),
(7, 'Debolsillo');

-- 6) Calcular tamaños
SELECT @cnt_autores := COUNT(*) FROM autores_temp;
SELECT @cnt_editoriales := COUNT(*) FROM editoriales_temp;

-- 7) Insertar libros (1 libro ↔ 1 ficha)
INSERT INTO libro (eliminado, titulo, autor, editorial, anioEdicion, fichaBibliografica_id)
SELECT
    F.eliminado,
    CONCAT('Libro ', F.id) AS titulo,
    A.nombre AS autor,
    E.nombre AS editorial,
    CASE
        WHEN RAND() < 0.6 THEN FLOOR(2000 + RAND() * 25)
        WHEN RAND() < 0.9 THEN FLOOR(1980 + RAND() * 20)
        ELSE FLOOR(1950 + RAND() * 30)
    END AS anioEdicion,
    F.id AS fichaBibliografica_id
FROM fichabibliografica F
JOIN autores_temp A      ON A.aid = ((F.id - 1) % @cnt_autores)
JOIN editoriales_temp E  ON E.eid = ((F.id - 1) % @cnt_editoriales)
LIMIT 100000;

-- 8) Verificaciones finales
SELECT COUNT(*) AS total_fichas FROM fichabibliografica;
SELECT COUNT(*) AS total_libros FROM libro;

-- Cada libro debe tener una ficha distinta (1→1)
SELECT COUNT(DISTINCT fichaBibliografica_id) AS fichas_unicas FROM libro;