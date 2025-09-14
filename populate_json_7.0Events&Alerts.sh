#!/bin/bash

echo "Starting data population for Error Code Management System..."

# Create Product
# echo "Creating product MetroNode..."
# curl -s -X POST http://localhost:8080/api/products -H "Content-Type: application/json" -d '{"name":"MetroNode"}' > /dev/null

# Create Version
echo "Creating version versionOne for MetroNode (Product ID: 1)..."
curl -s -X POST http://localhost:8080/api/versions -H "Content-Type: application/json" -d '{"versionNumber":"7.0EventsAndAlerts","productId":1}' > /dev/null

# Read and Process JSON File
JSON_FILE="7.0EventsandAlerts.json"
BASE_URL="http://localhost:8080/api/errorCodes"
VERSION_ID=4

echo "Parsing Error Codes from JSON file and creating them in the system..."

count=0

jq -c '.[] | select(. != null)' "$JSON_FILE" | while read -r errorCode; do
  # Extract fields dynamically for payload
  errorCodeId=$(echo "$errorCode" | jq -r '.["ID"] // empty')
  conditionId=$(echo "$errorCode" | jq -r '.["Condition ID"] // empty')
  component=$(echo "$errorCode" | jq -r '.["Component"] // empty')
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
  response=$(curl -s -w "%{http_code}" -o /tmp/errcode_resp.json -X POST "$BASE_URL" -H "Content-Type: application/json" -d "$payload")
  if [ "$response" -eq 200 ] || [ "$response" -eq 201 ]; then
    count=$((count+1))
  else
    echo "Failed to create Error Code $errorCodeId (HTTP $response):"
    cat /tmp/errcode_resp.json
    continue
  fi

  # Associate the error code with the version
  echo "Associating Error Code $errorCodeId with Version versionOne (ID $VERSION_ID)..."
  curl -s -X POST "http://localhost:8080/api/versions/$VERSION_ID/errorCodes/$errorCodeId" > /dev/null

done

echo "Data population completed."
echo "Total error codes ingested: $count"