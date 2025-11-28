#!/usr/bin/env bash

TARGET_DIR="./target"
MANAGE_SCRIPT="./custom-manage.sh"

# Variáveis que o custom-manage.sh vai consumir
export FUNCTION_NAME="FeedbackReportLambda"
export HANDLER="io.quarkus.amazon.lambda.runtime.QuarkusStreamHandler::handleRequest"
export RUNTIME="java21"
export AWS_REGION="sa-east-1"
export LAMBDA_ROLE_ARN="arn:aws:iam::992382492436:role/role-feedback-report"

export ZIP_FILE="fileb://target/function.zip"

usage() {
  echo "Uso: ./lambda-deploy.sh [create|update|invoke|delete]"
  echo "Exemplo: ./lambda-deploy.sh update"
}

CMD=${1:-help}
if [[ "$CMD" == "help" ]]; then
  usage; exit 0
fi

if [[ ! -f "${TARGET_DIR}/function.zip" ]]; then
  echo "ZIP não encontrado em ${TARGET_DIR}/function.zip. Verifique o build."
  exit 1
fi

if [[ ! -f "${MANAGE_SCRIPT}" ]]; then
  echo "${MANAGE_SCRIPT} não encontrado na raiz do projeto."
  exit 1
fi

chmod +x "${MANAGE_SCRIPT}"

echo "Executando Lambda '${CMD}' via ${MANAGE_SCRIPT}"
./custom-manage.sh "${CMD}"

echo "Operação concluída!"
