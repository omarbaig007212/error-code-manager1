#!/bin/bash

echo "Starting data population for Error Code Management System..."

# -------------------------
# Variables
# -------------------------
PRODUCT_NAME="MetroNode"
BASE_URL="http://localhost:8080/api"
PRODUCT_ID=1   # assume product ID 1 for MetroNode
VERSION_START_ID=1 # Starting Version ID (increment for each JSON)
# -------------------------

# Step 1: Create Product (only once)
echo "Ensuring product $PRODUCT_NAME exists..."
curl -s -X POST "$BASE_URL/products" \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"$PRODUCT_NAME\"}" > /dev/null

# Step 2: Define versions and their JSON files
declare -A versions
versions["8.0EventsAndAlerts"]="8.0Events&Alerts.json"
versions["9.0EventsAndAlerts"]="9.0Events&Alerts.json"
versions["7.1EventsAndAlerts"]="7.1EventsandAlerts.json"
versions["7.0EventsAndAlerts"]="7.0EventsandAlerts.json"

# Step 3: Loop through all versions
version_id=$VERSION_START_ID
for version_number in "${!versions[@]}"; do
  JSON_FILE="${versions[$version_number]}"
  
  echo "-----------------------------------------"
  echo "Creating version $version_number for $PRODUCT_NAME (Product ID: $PRODUCT_ID)..."
  curl -s -X POST "$BASE_URL/versions" \
    -H "Content-Type: application/json" \
    -d "{\"versionNumber\":\"$version_number\",\"productId\":$PRODUCT_ID}" > /dev/null

  echo "Parsing Error Codes from $JSON_FILE and creating them in the system..."
  count=0

  jq -c '.[] | select(. != null)' "$JSON_FILE" | while read -r errorCode; do
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

    echo "Creating Error Code with ID: $errorCodeId..."
    response=$(curl -s -w "%{http_code}" -o /tmp/errcode_resp.json \
      -X POST "$BASE_URL/errorCodes" \
      -H "Content-Type: application/json" -d "$payload")

    if [ "$response" -eq 200 ] || [ "$response" -eq 201 ]; then
      count=$((count+1))
    else
      echo "Failed to create Error Code $errorCodeId (HTTP $response):"
      cat /tmp/errcode_resp.json
      continue
    fi

    echo "Associating Error Code $errorCodeId with Version $version_number (ID $version_id)..."
    curl -s -X POST "$BASE_URL/versions/$version_id/errorCodes/$errorCodeId" > /dev/null

  done

  echo "Data population completed for $version_number."
  echo "Total error codes ingested: $count"
  version_id=$((version_id+1))
done
