# Configuração de Ambiente - POPS Project Manager API

## 📋 Pré-requisitos

1. **MySQL** instalado e rodando
2. **Java 17** instalado
3. **Maven** (ou usar o wrapper incluído)

## 🗄️ Configuração do Banco de Dados

### Passo 1: Executar o Script SQL
```bash
# Conecte-se ao MySQL
mysql -u root -p

# Execute o script de criação
source pops-project-manager-api/database/setup.sql
```

### Passo 2: Verificar se o banco foi criado
```sql
SHOW DATABASES;
USE pops_project_manager;
SHOW TABLES;
```

## 🔧 Configuração das Variáveis de Ambiente

### Opção 1: Arquivo .env (Recomendado)

1. **Copie o arquivo de exemplo:**
```bash
cp env.example .env
```

2. **Edite o arquivo `.env` com suas configurações:**
```env
# Configurações do MySQL
IPV4_PRIVATE=localhost
MYSQL_DATABASE=pops_project_manager
MYSQL_USER=root
MYSQL_PASSWORD=sua_senha_aqui

# Configurações da aplicação
SPRING_PROFILES_ACTIVE=mysql
```

### Opção 2: Variáveis de Sistema (Windows)

1. **Abra o Painel de Controle** → Sistema → Configurações avançadas do sistema
2. **Clique em "Variáveis de Ambiente"**
3. **Adicione as seguintes variáveis:**

| Nome da Variável | Valor |
|------------------|-------|
| `IPV4_PRIVATE` | `localhost` |
| `MYSQL_DATABASE` | `pops_project_manager` |
| `MYSQL_USER` | `root` |
| `MYSQL_PASSWORD` | `sua_senha_do_mysql` |
| `SPRING_PROFILES_ACTIVE` | `mysql` |

### Opção 3: PowerShell (Temporário)
```powershell
$env:IPV4_PRIVATE="localhost"
$env:MYSQL_DATABASE="pops_project_manager"
$env:MYSQL_USER="root"
$env:MYSQL_PASSWORD="sua_senha_aqui"
$env:SPRING_PROFILES_ACTIVE="mysql"
```

## 🚀 Executando a Aplicação

### Desenvolvimento (H2 - Banco em memória)
```bash
# Não precisa configurar variáveis de ambiente
./mvnw spring-boot:run
```

### Produção (MySQL)
```bash
# Configure as variáveis de ambiente primeiro
./mvnw spring-boot:run -Dspring.profiles.active=mysql
```

## 🔍 Verificando se está funcionando

1. **Aplicação rodando:** http://localhost:8081/api/actuator/health
2. **Swagger UI:** http://localhost:8081/api/swagger-ui.html
3. **Console H2 (apenas desenvolvimento):** http://localhost:8081/api/h2-console

## 📊 Dados de Teste

O script SQL já inclui dados de exemplo:
- **12 Skills** (6 HARD + 6 SOFT)
- **5 Projetos** com diferentes status
- **Relacionamentos** entre projetos e skills

## 🐛 Solução de Problemas

### Erro de Conexão com MySQL
```
Could not create connection to database server
```
**Solução:**
1. Verifique se o MySQL está rodando
2. Confirme as credenciais no arquivo `.env`
3. Teste a conexão: `mysql -u root -p`

### Erro de Banco não encontrado
```
Unknown database 'pops_project_manager'
```
**Solução:**
1. Execute o script `database/setup.sql`
2. Verifique se o nome do banco está correto nas variáveis

### Erro de Porta em uso
```
Port 8081 was already in use
```
**Solução:**
1. Mude a porta no `application.properties`
2. Ou pare o processo que está usando a porta 8081

## 📝 Exemplos de Configuração

### Configuração Local (Desenvolvimento)
```env
IPV4_PRIVATE=localhost
MYSQL_DATABASE=pops_project_manager
MYSQL_USER=root
MYSQL_PASSWORD=123456
SPRING_PROFILES_ACTIVE=mysql
```

### Configuração de Produção
```env
IPV4_PRIVATE=192.168.1.100
MYSQL_DATABASE=pops_project_manager_prod
MYSQL_USER=pops_user
MYSQL_PASSWORD=senha_super_segura
SPRING_PROFILES_ACTIVE=mysql
```

