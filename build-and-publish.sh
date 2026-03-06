#!/bin/bash

# Parar a execução em caso de erro
set -e

# Função para imprimir linha com estilo
print_status() {
    local label="$1"
    local emoji="$2"
    local message="$3"
    printf "\n\e[1m%-6s\e[0m: %s \e[1m%s\e[0m\n" "$label" "$emoji" "$message"
}
ARROWS="➡️➡️➡️➡️➡️➡️➡️➡️➡️➡️ "  # 10 setas


print_status "GIT" "🔄" "$ARROWS Atualizando repositório remoto..."
git push origin main
  
print_status "MAVEN" "⚙️ " "$ARROWS Buildando o projeto..."
rm -rf target/
mvn clean package -Dmaven.test.skip=true

print_status "DOCKER" "🐳" "$ARROWS Buildando a imagem..."
docker build -t carloser7/er7_financeai-api:latest .

print_status "DOCKER" "🚀" "$ARROWS Publicando a imagem..."
docker push carloser7/er7_financeai-api:latest

print_status "SSH" "🔑" "$ARROWS Acessando servidor de aplicaçao remoto..."
ssh reis@192.168.15.20 << 'ENDSSH'
    cd projetos/er7_financeai-api/

    printf "\n\e[1m%-6s\e[0m: %s \e[1m%s\e[0m\n" "DOCKER" "🔄" "➡️➡️➡️➡️➡️➡️➡️➡️➡️➡️  Atualizando imagem da aplicaçao..."
    docker pull carloser7/er7_financeai-api:latest

    printf "\n\e[1m%-6s\e[0m: %s \e[1m%s\e[0m\n" "DOCKER" "🔄" "➡️➡️➡️➡️➡️➡️➡️➡️➡️➡️  Derrubando imagem da aplicação remota..."
    docker-compose down -v || true

    printf "\n\e[1m%-6s\e[0m: %s \e[1m%s\e[0m\n" "DOCKER" "🔄" "➡️➡️➡️➡️➡️➡️➡️➡️➡️➡️  Subindo imagem da aplicação atualizada..."
    docker-compose up -d
ENDSSH

printf "\n..."
printf "\n..."

print_status "APP" "✅" "$ARROWS Aplicação atualizada com sucesso!"

printf "\n..."
printf "\n..."