#!/bin/bash

echo "Starting data population for Error Code Management System..."

# Create Product
# echo "Creating product MetroNode..."
# curl -X POST http://localhost:8080/api/products -H "Content-Type: application/json" -d '{"name":"MetroNode9.0"}'

# Create Version
echo "Creating version versionOne for MetroNode (Product ID: 1)..."
curl -X POST http://localhost:8080/api/versions -H "Content-Type: application/json" -d '{"versionNumber":"9.0EventsandAlerts","productId":1}'

# Read and Process JSON File
JSON_FILE="9.0Events&Alerts.json"
BASE_URL="http://localhost:8080/api/errorCodes"

echo "Parsing Error Codes from JSON file and creating them in the system..."

jq -c '.[] | select(. != null)' "$JSON_FILE" | while read -r errorCode; do
  
  # Extract fields dynamically for payload
  errorCodeId=$(echo "$errorCode" | jq -r '.["ID"] // empty')
  conditionId=$(echo "$errorCode" | jq -r '.["Condition ID"] // empty')
  component=$(echo "$errorCode" | jq -r '.["Component"] // empty')
  idValue=$(echo "$errorCode" | jq -r '.["ID"] // empty')
  severity=$(echo "$errorCode" | jq -r '.["Severity"] // empty')
  callhome=$(echo "$errorCode" | jq -r '.["Call home"] | if . == "True" or . == true then "YES" else "NO" end')
  alertName=$(echo "$errorCode" | jq -r '.["Alert name"] // empty')
  description=$(echo "$errorCode" | jq -r '.["Description"] // empty' | sed 's/"/\\"/g')
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

  # Create the Error Code
  echo "Creating Error Code with ID: $errorCodeId..."
  curl -X POST "$BASE_URL" -H "Content-Type: application/json" -d "$payload"

  # Associate the error code with versionOne (Version ID: 1)
  echo "Associating Error Code $errorCodeId with Version versionOne (ID 1)..."
  curl -X POST "http://localhost:8080/api/versions/2/errorCodes/$errorCodeId"

done

echo "Data population completed."