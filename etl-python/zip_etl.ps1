# Caminho da pasta com os arquivos
$etlPath = "C:\grupo_pops\pops-api\etl-python"

# Lista de arquivos sem extensão
$files = @("dateHandling", "notification", "rawToTrusted", "uploadToRaw")

# Vai para o diretório
Set-Location $etlPath

foreach ($file in $files) {
    $pyFile = "$file.py"
    $zipFile = "$file.zip"

    if (Test-Path $pyFile) {
        # Remove o ZIP antigo se existir
        if (Test-Path $zipFile) {
            Remove-Item $zipFile -Force
        }

        # Cria o novo ZIP com apenas o .py
        Compress-Archive -Path $pyFile -DestinationPath $zipFile
        Write-Host "Compactado: $pyFile -> $zipFile"
    } else {
        Write-Host "Arquivo não encontrado: $pyFile"
    }
}
