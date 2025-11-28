#!/usr/bin/env bash

ARCHITECTURE=$(arch)
LAMBDA_NATIVE_RUNTIME="provided.al2023"

cmd_create() {
  echo "Criando função ${FUNCTION_NAME}..."
  aws lambda create-function \
    --function-name "${FUNCTION_NAME}" \
    --zip-file "${ZIP_FILE}" \
    --handler "${HANDLER}" \
    --runtime "${RUNTIME}" \
    --role "${LAMBDA_ROLE_ARN}" \
    --timeout 15 \
    --memory-size 256 \
    --architectures "${ARCHITECTURE}"
}

cmd_update() {
  echo "Atualizando código da função ${FUNCTION_NAME}..."
  aws lambda update-function-code \
    --function-name "${FUNCTION_NAME}" \
    --zip-file "${ZIP_FILE}"
}

cmd_invoke() {
  echo "Invocando função ${FUNCTION_NAME}..."
  aws lambda invoke response.txt \
    --function-name "${FUNCTION_NAME}" \
    --payload file://payload.json \
    --log-type Tail \
    --query 'LogResult' \
    --output text | base64 --decode
  echo
  cat response.txt && rm -f response.txt
}

cmd_delete() {
  echo "Deletando função ${FUNCTION_NAME}..."
  aws lambda delete-function --function-name "${FUNCTION_NAME}"
}

CMD=${1:-help}
case "$CMD" in
  create|update|invoke|delete)
    cmd_${CMD}
    ;;
  *)
    echo "Uso: $0 [create|update|invoke|delete]"
    ;;
esac
