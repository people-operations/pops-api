# Como Importar a Collection no Insomnia

## 📥 Importando a Collection

### Passo 1: Abrir o Insomnia
1. Abra o aplicativo **Insomnia**
2. Clique em **"Create"** ou **"New Request"**

### Passo 2: Importar a Collection
1. Clique no menu **"Application"** → **"Preferences"** (ou `Ctrl+,`)
2. Vá para a aba **"Data"**
3. Clique em **"Import Data"**
4. Selecione **"From File"**
5. Navegue até o arquivo: `pops-project-manager-api/insomnia/POPS_Project_Manager_API.json`
6. Clique em **"Import"**

### Passo 3: Verificar a Importação
1. A collection **"POPS Project Manager API"** deve aparecer na barra lateral
2. Você verá as seguintes pastas:
   - **Skills** - Endpoints para gerenciamento de skills
   - **Projects** - Endpoints para gerenciamento de projetos
   - **Health & Documentation** - Endpoints de saúde e documentação

## 🔧 Configurando o Ambiente

### Passo 1: Configurar a URL Base
1. Clique no dropdown de ambiente (canto superior direito)
2. Selecione **"Manage Environments"**
3. Clique em **"Base Environment"**
4. Verifique se a variável `base_url` está configurada como:
   ```
   http://localhost:8081/api
   ```

### Passo 2: Criar Ambiente de Produção (Opcional)
1. Clique em **"Create Environment"**
2. Nome: `Production`
3. Adicione a variável:
   ```
   base_url: https://seu-servidor.com/api
   ```

## 🚀 Testando os Endpoints

### Ordem Recomendada de Testes:

#### 1. Health Check
- Execute: **Health & Documentation** → **Health Check**
- Deve retornar status `UP`

#### 2. Skills
1. **Listar Skills** - Ver todas as skills cadastradas
2. **Criar Skill** - Adicionar uma nova skill
3. **Buscar Skill por ID** - Buscar skill específica
4. **Listar Skills por Tipo** - Filtrar por HARD ou SOFT
5. **Atualizar Skill** - Modificar uma skill existente
6. **Desabilitar/Habilitar Skill** - Controle de status

#### 3. Projects
1. **Listar Projetos Ativos** - Ver todos os projetos
2. **Criar Projeto** - Adicionar novo projeto (com skills)
3. **Buscar Projeto por ID** - Buscar projeto específico
4. **Listar Projetos por Status** - Filtrar por status
5. **Atualizar Projeto** - Modificar projeto existente
6. **Desabilitar/Habilitar Projeto** - Controle de status

## 📝 Exemplos de Payloads

### Criar Skill
```json
{
  "name": "React",
  "description": "Biblioteca JavaScript para interfaces de usuário",
  "type": "HARD"
}
```

### Criar Projeto
```json
{
  "name": "Sistema de E-commerce",
  "type": "Desenvolvimento Web",
  "description": "Sistema completo de e-commerce",
  "status": "PLANNING",
  "budget": 150000.00,
  "startDate": "2024-11-01",
  "endDate": "2025-06-30",
  "area": "Tecnologia",
  "requiredSkills": [
    {"id": 1},
    {"id": 2},
    {"id": 3}
  ]
}
```

## 🔍 Verificando Respostas

### Respostas de Sucesso:
- **200 OK** - Operação realizada com sucesso
- **201 Created** - Recurso criado com sucesso
- **204 No Content** - Exclusão realizada com sucesso

### Respostas de Erro:
- **400 Bad Request** - Dados inválidos
- **404 Not Found** - Recurso não encontrado
- **500 Internal Server Error** - Erro interno do servidor

## 🐛 Solução de Problemas

### Erro de Conexão
```
Could not get any response
```
**Solução:**
1. Verifique se a aplicação está rodando
2. Confirme a URL base no ambiente
3. Teste o Health Check primeiro

### Erro 404
```
404 Not Found
```
**Solução:**
1. Verifique se o endpoint está correto
2. Confirme se o ID do recurso existe
3. Verifique se o recurso não foi excluído

### Erro 400
```
400 Bad Request
```
**Solução:**
1. Verifique o formato do JSON
2. Confirme se todos os campos obrigatórios estão preenchidos
3. Verifique se os tipos de dados estão corretos

## 📊 Dados de Teste Disponíveis

Após executar o script SQL, você terá:
- **12 Skills** (6 HARD + 6 SOFT)
- **5 Projetos** com diferentes status
- **Relacionamentos** entre projetos e skills

Use esses dados para testar os endpoints de busca e atualização.

