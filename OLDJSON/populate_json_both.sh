#!/bin/bash

echo "Starting data population for Error Code Management System..."

API_URL="http://localhost:8080/api"
PRODUCT_NAME="MetroNode"

# List JSON files and their corresponding version names in order
JSON_FILES=("8.0Events&Alerts.json" "9.0Events&Alerts.json")
VERSION_NAMES=("8.0 Events And Alerts" "9.0 Events and Alerts")

# 1. Ensure Product exists (get or create)
PRODUCT_ID=$(curl -s "$API_URL/products" | jq ".[] | select(.name==\"$PRODUCT_NAME\") | .productId" | head -n1)
if [ -z "$PRODUCT_ID" ]; then
  PRODUCT_ID=$(curl -s -X POST "$API_URL/products" -H "Content-Type: application/json" -d "{\"name\":\"$PRODUCT_NAME\"}" | jq ".productId")
  echo "Created product $PRODUCT_NAME with ID $PRODUCT_ID"
else
  echo "Product $PRODUCT_NAME already exists with ID $PRODUCT_ID"
fi

BASE_URL="$API_URL/errorCodes"

for i in "${!JSON_FILES[@]}"; do
  JSON_FILE="${JSON_FILES[$i]}"
  VERSION_NAME="${VERSION_NAMES[$i]}"

  # Ensure version exists (get or create)
  VERSION_ID=$(curl -s "$API_URL/versions/product/$PRODUCT_ID" | jq ".[] | select(.versionNumber==\"$VERSION_NAME\") | .versionId" | head -n1)
  if [ -z "$VERSION_ID" ]; then
    VERSION_ID=$(curl -s -X POST "$API_URL/versions" -H "Content-Type: application/json" -d "{\"versionNumber\":\"$VERSION_NAME\",\"productId\":$PRODUCT_ID}" | jq ".versionId")
    echo "Created version $VERSION_NAME with ID $VERSION_ID"
  else
    echo "Version $VERSION_NAME already exists with ID $VERSION_ID"
  fi

  echo "Parsing Error Codes from $JSON_FILE and creating them in the system..."

  jq -c '.[] | select(. != null)' "$JSON_FILE" | while read -r errorCode; do
    # Normalize keys for both JSON formats
    errorCodeId=$(echo "$errorCode" | jq -r '.["ID"] // .["ID "] // empty')
    [ -z "$errorCodeId" ] && continue

    # Check if error code exists
    EXISTS=$(curl -s "$BASE_URL/findByErrorCodeId/$errorCodeId" | jq -r 'if type=="array" then .[0].errorCodeId // empty else .errorCodeId // empty end')
    if [ "$EXISTS" == "$errorCodeId" ]; then
      echo "ErrorCode $errorCodeId already exists, skipping creation."
    else
      # Try both possible field names for each property
      conditionId=$(echo "$errorCode" | jq -r '.["Condition ID"] // .["Conditio n ID"] // empty')
      component=$(echo "$errorCode" | jq -r '.["Component"] // .["Compo nent"] // empty')
      idValue=$(echo "$errorCode" | jq -r '.["ID"] // .["ID "] // empty')
      severity=$(echo "$errorCode" | jq -r '.["Severity"] // .["Severity "] // empty' | sed 's/ //g')
      callhome=$(echo "$errorCode" | jq -r '.["Call home"] // .["Call hom e"] | if . == "True" or . == true then "YES" else "NO" end')
      alertName=$(echo "$errorCode" | jq -r '.["Alert name"] // .["Alert name "] // empty')
      description=$(echo "$errorCode" | jq -r '.["Description"] // .["Descriptio n"] // empty' | sed 's/"/\\"/g')
      rca=$(echo "$errorCode" | jq -r '.["RCA"] // empty' | sed 's/"/\\"/g')
      correctiveAction=$(echo "$errorCode" | jq -r '.["Corrective action"] // empty' | sed 's/"/\\"/g')
      eventSource=$(echo "$errorCode" | jq -r '.["Event source"] // empty')
      alertType=$(echo "$errorCode" | jq -r '.["Alert type"] // empty')

      # Prepare the JSON payload
      payload=$(jq -n \
         --arg errorCodeId "$errorCodeId" \
         --arg conditionId "$conditionId" \
         --arg component "$component" \
         --arg idValue "$idValue" \
         --arg severity "$severity" \
         --arg callhome "$callhome" \
         --arg alertName "$alertName" \
         --arg description "$description" \
         --arg rca "$rca" \
         --arg correctiveAction "$correctiveAction" \
         --arg eventSource "$eventSource" \
         --arg alertType "$alertType" \
         '{
            errorCodeId: $errorCodeId,
            conditionId: $conditionId,
            component: $component,
            idValue: $idValue,
            severity: $severity,
            callhome: $callhome,
            alertName: $alertName,
            description: $description,
            rca: $rca,
            correctiveAction: $correctiveAction,
            eventSource: $eventSource,
            alertType: $alertType
          }'
      )

      echo "Creating Error Code with ID: $errorCodeId..."
      curl -s -X POST "$BASE_URL" -H "Content-Type: application/json" -d "$payload" > /dev/null
    fi

    # Associate the error code only with its own version
    echo "Associating Error Code $errorCodeId with Version ID $VERSION_ID..."
    curl -s -X POST "$API_URL/versions/$VERSION_ID/errorCodes/$errorCodeId" > /dev/null

  done
done

echo "Data population completed."