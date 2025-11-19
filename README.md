Script de Creación de Base de Datos 'biblioteca' en MySQL

Este repositorio contiene un script SQL diseñado para crear y poblar la base de datos 'biblioteca' con un esquema que implementa una relación uno a uno (1:1) entre las tablas de libros y fichas bibliográficas. El script genera hasta 100,000 registros de prueba.

---------------------------------------------------------
PRERREQUISITOS
---------------------------------------------------------

1. Servidor MySQL/MariaDB: Debe estar instalado y en funcionamiento.
2. Cliente MySQL: Una herramienta para ejecutar comandos SQL (MySQL Workbench, DBeaver, etc.).
3. Tener instalado XAMPP para correr el servidor mysql
---------------------------------------------------------
PASOS PARA LA EJECUCIÓN
---------------------------------------------------------

### 1. Crear la Base de Datos

Ejecuta el siguiente comando SQL:

CREATE DATABASE IF NOT EXISTS biblioteca;

### 2. Ejecutar el Script de Creación y Llenado

El script es un proceso completo que incluye limpieza, creación de estructura y llenado de datos.

1.  Conéctate a tu servidor MySQL.
2.  Selecciona la base de datos 'biblioteca'.
3.  Copia y pega el contenido completo del script SQL (el código que comienza con 'USE biblioteca;') y ejecútalo.

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


-------------------------------------------------------------------------
-------------------------------------------------------------------------

* Limpieza: Elimina tablas previas ('libro', 'fichabibliografica') y temporales.
* Estructura: Crea las tablas 'fichabibliografica' y 'libro'.
* Relación 1:1: Asegura la unicidad de la relación mediante la restricción UNIQUE en 'fichaBibliografica_id' en la tabla 'libro'.
* Generación de Datos: Inserta hasta 100,000 registros con datos de prueba generados aleatoriamente.

---------------------------------------------------------
VERIFICACIÓN DE RESULTADOS
---------------------------------------------------------

Una vez finalizada la ejecución, puedes verificar los datos con estas consultas:

### Contar Registros (Deben ser iguales):

SELECT COUNT(*) AS total_fichas FROM fichabibliografica;
SELECT COUNT(*) AS total_libros FROM libro;

### Verificar la Unicidad de la Relación 1:1:

SELECT COUNT(DISTINCT fichaBibliografica_id) AS fichas_unicas FROM libro;


------------------------------------------------------------
LINK DEL VIDEO 
------------------------------------------------------------

YouTube: https://youtu.be/R5ldjLjvv6k

Drive: https://drive.google.com/file/d/1jPIUrwNYk4o0Tc_7BqZa7Dw7oZaPfkzU/view?usp=sharing
