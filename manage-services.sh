#!/bin/bash

# ==============================================================================
# Script para Gerenciar o Serviço 'api-gateway'
# ==============================================================================
#
# Este script gerencia o ciclo de vida do serviço 'api-gateway'
# definido no docker-compose.yml do diretório atual.
#
# ==============================================================================

# --- CONFIGURAÇÃO DO PROJETO ---
# Configurações fixas para o serviço 'api-gateway'
SERVICE_NAME="gateway"
CONTAINER_NAME="gateway"

# --- Cores para o Output ---
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # Sem Cor

# --- Funções ---

# Função para mostrar como usar o script
usage() {
  echo -e "${YELLOW}Uso:${NC}"
  echo "  $0 <comando>"
  echo ""
  echo -e "${YELLOW}Exemplos:${NC}"
  echo "  $0 start                  # Inicia o serviço se não estiver rodando"
  echo "  $0 restart                # Para, reconstrói e inicia o serviço"
  echo "  $0 reload                 # Apenas reinicia o contêiner do serviço"
  echo ""
  echo -e "${YELLOW}Comandos disponíveis:${NC} start, restart, reload"
  exit 1
}

# Função para gerar o schema e compilar a aplicação
create_schema() {
  echo -e "${YELLOW}Executando o build do Gradle para gerar artefatos...${NC}"
  ./gradlew generateGraphQLSchema
  echo -e "${GREEN}Artefatos gerados com sucesso.${NC}"
}

# Função para verificar e iniciar o serviço
check_and_start() {
  echo -e "Verificando o status do contêiner '${YELLOW}${CONTAINER_NAME}${NC}'..."
  RUNNING_CONTAINER_ID=$(docker ps -q -f "name=^/${CONTAINER_NAME}$")

  if [ -z "$RUNNING_CONTAINER_ID" ]; then
    echo -e "${YELLOW}Contêiner não está rodando. Iniciando o serviço...${NC}"
    docker compose up -d "$SERVICE_NAME"
    echo -e "${GREEN}Serviço '${SERVICE_NAME}' iniciado com sucesso.${NC}"
  else
    echo -e "${GREEN}Contêiner '${CONTAINER_NAME}' já está em execução. Nenhuma ação necessária.${NC}"
  fi
}

# Função para forçar uma reinicialização completa com build
force_restart() {
#  create_schema
  echo -e "${YELLOW}Reiniciando o ambiente com reconstrução de imagem...${NC}"
  echo "Parando o serviço..."
  docker compose down
  echo "Iniciando o serviço com reconstrução de imagem..."
  docker compose up --build -d
  echo -e "${GREEN}Reinicialização completa concluída.${NC}"
}

# Função para reiniciar apenas o contêiner
reload_container() {
#  create_schema
  echo -e "${YELLOW}Reiniciando o contêiner do serviço '${SERVICE_NAME}'...${NC}"
  docker compose restart "$SERVICE_NAME"
  echo -e "${GREEN}Contêiner reiniciado com sucesso.${NC}"
}

# --- Lógica Principal do Script ---

# Verifica se exatamente um argumento (o comando) foi fornecido
if [ "$#" -ne 1 ]; then
  echo -e "${RED}Erro: É necessário fornecer um comando.${NC}"
  usage
fi

COMMAND_ARG=$1

# Executa a ação baseada no comando
case "$COMMAND_ARG" in
  start)
    check_and_start
    ;;
  restart)
    force_restart
    ;;
  reload)
    reload_container
    ;;
  *)
    echo -e "${RED}Erro: Comando inválido '$COMMAND_ARG'${NC}"
    usage
    ;;
esac

echo -e "\n${GREEN}Operação concluída.${NC}"