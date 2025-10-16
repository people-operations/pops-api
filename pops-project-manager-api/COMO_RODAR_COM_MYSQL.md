# Como Rodar a Aplicação com MySQL

## 🎯 **Comandos para Rodar com MySQL**

### **Opção 1: Usando Perfil MySQL (Recomendado)**
```powershell
# 1. Ir para o diretório
cd pops-project-manager-api

# 2. Configurar Java
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"

# 3. Rodar com perfil MySQL
.\mvnw.cmd spring-boot:run -Dspring.profiles.active=mysql
```

### **Opção 2: Usando Variáveis de Ambiente**
```powershell
# 1. Ir para o diretório
cd pops-project-manager-api

# 2. Configurar variáveis
$env:IPV4_PRIVATE="localhost"
$env:MYSQL_DATABASE="pops_project_manager"
$env:MYSQL_USER="root"
$env:MYSQL_PASSWORD="gyulia06*"
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"

# 3. Rodar aplicação
.\mvnw.cmd spring-boot:run
```

## 🔧 **Configurações Disponíveis**

### **H2 (Desenvolvimento) - Padrão**
- **Arquivo:** `application.properties`
- **Banco:** H2 em memória
- **Console:** http://localhost:8081/api/h2-console

### **MySQL (Produção)**
- **Arquivo:** `application-mysql.properties`
- **Banco:** MySQL local
- **Perfil:** `mysql`

## 📊 **Verificar Conexão**

### **1. Health Check**
```
http://localhost:8081/api/actuator/health
```

### **2. Swagger UI**
```
http://localhost:8081/api/swagger-ui.html
```

### **3. Listar Skills**
```
http://localhost:8081/api/skills
```

## 🗄️ **Banco MySQL**

### **Configuração Atual:**
- **Host:** localhost
- **Porta:** 3306
- **Database:** pops_project_manager
- **Usuário:** root
- **Senha:** gyulia06*

### **Script SQL:**
Execute o arquivo `database/setup.sql` no MySQL antes de rodar a aplicação.

## ⚠️ **Solução de Problemas**

### **Erro de Conexão MySQL:**
1. Verifique se o MySQL está rodando
2. Confirme as credenciais
3. Execute o script `database/setup.sql`

### **Aplicação não inicia:**
1. Verifique se a porta 8081 está livre
2. Confirme se o Java 17+ está instalado
3. Verifique os logs de erro



