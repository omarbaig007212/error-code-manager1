#!/bin/bash

echo "Starting data population for MetroNode Events & Alerts..."

# # Create Product
# curl -X POST http://localhost:8080/api/products \
#   -H "Content-Type: application/json" \
#   -d '{"name":"MetroNode"}'

# # Create Version
# curl -X POST http://localhost:8080/api/versions \
#   -H "Content-Type: application/json" \
#   -d '{"versionNumber":"8.0Events&Alerts","productId":1}'


# Example Error Code from JSON
curl -X POST http://localhost:8080/api/errorCodes -H "Content-Type: application/json" -d '{
  "errorCodeId": "v8-0x8a6b9001",
  "conditionId": "v8-0x1a0003",
  "component": "bepm/1,bepm/2",
  "idValue": "v8-0x1a0003",
  "severity": "ERROR",
  "callhome": "YES",
  "alertName": "ITDegraded",
  "description": "Marking IT degraded.",
  "rca": "A critical number of ITLs (20 by default,but possibly fewer if fewer than 20 ITLs exist or the threshold was manually changed) on this IT nexus have had multiple IOs experience IO latency of 1 second or greater and all ITLs on that IT have been taken out of service. The IT nexus is",
  "correctiveAction": "Investigate the related switch logs and array performance for the IT nexus to determine the cause for the degraded performance. Once the performance improves the VPLEX will automatically restore the use of ITLs that were taken out of service.",
  "eventSource": "ARRAY",
  "alertType": "Alarm"
}'

# # Error Code 6: RAID configuration error
# curl -X POST http://localhost:8080/api/errorCodes -H "Content-Type: application/json" -d '{
#   "errorCodeId": "1006",
#   "conditionId": "RAID-006",
#   "component": "STORAGE_CONTROLLER",
#   "idValue": "ERR1006",
#   "severity": "CRITICAL",
#   "callhome": "YES",
#   "alertName": "RAID Volume Degraded",
#   "description": "RAID volume is operating in a degraded state due to disk failure. Data protection is compromised until rebuild completes.",
#   "rca": "Disk failure in RAID group caused volume to enter degraded state. Redundancy is reduced or eliminated depending on RAID level.",
#   "correctiveAction": "Replace the failed disk immediately. Monitor rebuild progress. Consider backing up critical data until rebuild completes.",
#   "eventSource": "RAID Controller",
#   "alertType": "Data Protection"
# }'


# Associate Error Code with Version
curl -s -X POST "http://localhost:8080/api/versions/1/errorCodes/v8-0x1a0003"
# curl -s -X POST "http://localhost:8080/api/versions/1/errorCodes/1006"
