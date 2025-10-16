# Script para executar a aplicação com MySQL
# Configurar Java
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"

# Executar com perfil MySQL
Write-Host "Iniciando aplicação com perfil MySQL..."
.\mvnw.cmd spring-boot:run "-Dspring.profiles.active=mysql"



