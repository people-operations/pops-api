-- Níveis
INSERT INTO levels (name, description) VALUES ('Junior', 'Início de carreira');
INSERT INTO levels (name, description) VALUES ('Pleno', 'Experiência intermediária');
INSERT INTO levels (name, description) VALUES ('Senior', 'Alto nível técnico e autonomia');
INSERT INTO levels (name, description) VALUES ('Tech Lead', 'Liderança técnica da equipe');
INSERT INTO levels (name, description) VALUES ('CTO', 'Responsável pela visão tecnológica da empresa');

-- Posições
INSERT INTO positions (name, description, level_id) VALUES ('Analista de Dados', 'Responsável por análise de dados', 1);
INSERT INTO positions (name, description, level_id) VALUES ('Desenvolvedor Front-end', 'Especialista em interfaces', 2);
INSERT INTO positions (name, description, level_id) VALUES ('Desenvolvedor Back-end', 'Especialista em APIs', 3);
INSERT INTO positions (name, description, level_id) VALUES ('Tech Lead Front-end', 'Liderança no front', 4);
INSERT INTO positions (name, description, level_id) VALUES ('CTO', 'Chefe de tecnologia', 5);

-- Pessoas
INSERT INTO person (name, email, password, cpf, phone, address) VALUES
                                                                    ('João Silva', 'joao.silva@example.com', 'senha123', '123.456.789-00', '11999998888', 'Rua das Flores, 100'),
                                                                    ('Maria Souza', 'maria.souza@example.com', 'minhasenha', '987.654.321-00', NULL, 'Av. Brasil, 200');
