-- Remoção das tabelas se existirem
DROP TABLE IF EXISTS positions;
DROP TABLE IF EXISTS person;
DROP TABLE IF EXISTS levels;

-- Tabela de níveis (junior, pleno, etc)
CREATE TABLE levels (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL UNIQUE,
                        description VARCHAR(500)
);

-- Tabela de posições (dev front, analista, etc)
CREATE TABLE positions (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           name VARCHAR(255) NOT NULL UNIQUE,
                           description VARCHAR(500),
                           level_id BIGINT NOT NULL,
                           CONSTRAINT fk_position_level FOREIGN KEY (level_id) REFERENCES levels(id)
);

-- Tabela de pessoas
CREATE TABLE person (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        email VARCHAR(255) NOT NULL UNIQUE,
                        password VARCHAR(255) NOT NULL,
                        cpf VARCHAR(14) NOT NULL UNIQUE,
                        phone VARCHAR(20) UNIQUE,
                        address VARCHAR(255)
);
