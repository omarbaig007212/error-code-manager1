#!/bin/bash

echo "Starting data population for Error Code Management System..."

# Create Products
curl -X POST http://localhost:8080/api/products -H "Content-Type: application/json" -d '{"name":"Enterprise Storage Array"}'
curl -X POST http://localhost:8080/api/products -H "Content-Type: application/json" -d '{"name":"Network Storage Gateway"}'
curl -X POST http://localhost:8080/api/products -H "Content-Type: application/json" -d '{"name":"Cloud Backup Appliance"}'

# Create Versions
curl -X POST http://localhost:8080/api/versions -H "Content-Type: application/json" -d '{"versionNumber":"a100","productId":1}'
curl -X POST http://localhost:8080/api/versions -H "Content-Type: application/json" -d '{"versionNumber":"200","productId":1}'
curl -X POST http://localhost:8080/api/versions -H "Content-Type: application/json" -d '{"versionNumber":"c50","productId":2}'
curl -X POST http://localhost:8080/api/versions -H "Content-Type: application/json" -d '{"versionNumber":"z10","productId":3}'

# Error Code 1: Critical drive failure
curl -X POST http://localhost:8080/api/errorCodes -H "Content-Type: application/json" -d '{
  "errorCodeId": "ex100a1",
  "conditionId": "DRIVE-001",
  "component": "STORAGE_SUBSYSTEM",
  "idValue": "ERR1001",
  "severity": "CRITICAL",
  "callhome": "YES",
  "alertName": "Drive Failure Detected",
  "description": "A physical drive in the storage array has failed and needs immediate replacement. Data may be at risk if redundancy has been compromised.",
  "rca": "Hard drive mechanical failure detected. SMART attributes indicate imminent failure with excessive reallocated sectors.",
  "correctiveAction": "Replace the failed drive in slot identified in the error message. Follow hot-swap procedure in maintenance manual section 4.2.",
  "eventSource": "Storage Subsystem Controller",
  "alertType": "Hardware Failure"
}'

# Error Code 2: Network connectivity issue
curl -X POST http://localhost:8080/api/errorCodes -H "Content-Type: application/json" -d '{
  "errorCodeId": "100asd2",
  "conditionId": "NET-002",
  "component": "NETWORK_INTERFACE",
  "idValue": "ERR1002",
  "severity": "WARNING",
  "callhome": "NO",
  "alertName": "Network Port Link Down",
  "description": "Network connectivity lost on one or more physical ports. Redundant connections may be active but bandwidth and availability are reduced.",
  "rca": "Physical network disconnection or switch port failure. Port statistics show increasing CRC errors before link loss.",
  "correctiveAction": "Verify cable connections and inspect for damage. Check network switch status and port configuration. Replace cable if necessary.",
  "eventSource": "Network Controller",
  "alertType": "Connectivity"
}'

# Error Code 3: Power supply warning
curl -X POST http://localhost:8080/api/errorCodes -H "Content-Type: application/json" -d '{
  "errorCodeId": "1001a3",
  "conditionId": "PWR-003",
  "component": "POWER_SUPPLY",
  "idValue": "ERR1003",
  "severity": "WARNING",
  "callhome": "YES",
  "alertName": "Power Supply Degraded",
  "description": "Power supply unit is operating in a degraded state. The system remains operational with redundant power, but fault tolerance is reduced.",
  "rca": "Internal power supply fan failure or voltage regulation issue. Output voltages fluctuating beyond acceptable thresholds.",
  "correctiveAction": "Schedule replacement of the affected power supply unit. System can continue operation until maintenance window.",
  "eventSource": "Environmental Monitoring System",
  "alertType": "Hardware Degradation"
}'

# Error Code 4: Memory module error
curl -X POST http://localhost:8080/api/errorCodes -H "Content-Type: application/json" -d '{
  "errorCodeId": "ex1004",
  "conditionId": "MEM-004",
  "component": "MEMORY",
  "idValue": "ERR1004",
  "severity": "CRITICAL",
  "callhome": "YES",
  "alertName": "Memory Module Failure",
  "description": "Uncorrectable memory error detected in DIMM module. The system has isolated the failing memory but performance may be affected.",
  "rca": "Multiple single-bit errors evolved into multi-bit errors that cannot be corrected by ECC. Memory diagnostic tests confirm DIMM failure in slot.",
  "correctiveAction": "Replace the memory module in the specified slot. Refer to hardware manual for memory replacement procedure.",
  "eventSource": "System Controller",
  "alertType": "Hardware Failure"
}'

# Error Code 5: Fan failure
curl -X POST http://localhost:8080/api/errorCodes -H "Content-Type: application/json" -d '{
  "errorCodeId": "mx1005",
  "conditionId": "FAN-005",
  "component": "COOLING_SYSTEM",
  "idValue": "ERR1005",
  "severity": "WARNING",
  "callhome": "YES",
  "alertName": "Cooling Fan Failure",
  "description": "One or more cooling fans have failed or are operating below required RPM. System temperature may increase if not addressed.",
  "rca": "Fan motor failure or obstruction preventing normal rotation. RPM readings consistently below operational threshold.",
  "correctiveAction": "Replace the failed cooling fan module. Ensure airflow path is free of obstructions. Check for foreign objects in fan blades.",
  "eventSource": "Environmental Controller",
  "alertType": "Hardware Failure"
}'

# Error Code 6: RAID configuration error
curl -X POST http://localhost:8080/api/errorCodes -H "Content-Type: application/json" -d '{
  "errorCodeId": "1006",
  "conditionId": "RAID-006",
  "component": "STORAGE_CONTROLLER",
  "idValue": "ERR1006",
  "severity": "CRITICAL",
  "callhome": "YES",
  "alertName": "RAID Volume Degraded",
  "description": "RAID volume is operating in a degraded state due to disk failure. Data protection is compromised until rebuild completes.",
  "rca": "Disk failure in RAID group caused volume to enter degraded state. Redundancy is reduced or eliminated depending on RAID level.",
  "correctiveAction": "Replace the failed disk immediately. Monitor rebuild progress. Consider backing up critical data until rebuild completes.",
  "eventSource": "RAID Controller",
  "alertType": "Data Protection"
}'

# Error Code 7: Filesystem corruption
curl -X POST http://localhost:8080/api/errorCodes -H "Content-Type: application/json" -d '{
  "errorCodeId": "1007",
  "conditionId": "FS-007",
  "component": "FILE_SYSTEM",
  "idValue": "ERR1007",
  "severity": "CRITICAL",
  "callhome": "YES",
  "alertName": "File System Corruption Detected",
  "description": "File system consistency check has detected metadata corruption. Some files may be inaccessible or corrupted.",
  "rca": "Unexpected system shutdown or power loss during write operations. Journal recovery failed to repair all inconsistencies.",
  "correctiveAction": "Run full file system integrity check. Restore affected files from backup. Consider file system recovery tools if backups unavailable.",
  "eventSource": "File System Manager",
  "alertType": "Data Integrity"
}'

# Error Code 8: Cache battery warning
curl -X POST http://localhost:8080/api/errorCodes -H "Content-Type: application/json" -d '{
  "errorCodeId": "1008",
  "conditionId": "CACHE-008",
  "component": "CACHE_SUBSYSTEM",
  "idValue": "ERR1008",
  "severity": "WARNING",
  "callhome": "NO",
  "alertName": "Cache Battery Backup Low",
  "description": "The cache battery backup unit shows reduced capacity. Write caching may be disabled if power is lost.",
  "rca": "Battery unit nearing end of service life. Charge capacity below 50% of specification after full charging cycle.",
  "correctiveAction": "Schedule replacement of cache battery backup unit. System performance may be degraded until replacement.",
  "eventSource": "Cache Controller",
  "alertType": "Performance Risk"
}'

# Error Code 9: Controller failover event
curl -X POST http://localhost:8080/api/errorCodes -H "Content-Type: application/json" -d '{
  "errorCodeId": "1009",
  "conditionId": "CTRL-009",
  "component": "CONTROLLER",
  "idValue": "ERR1009",
  "severity": "WARNING",
  "callhome": "YES",
  "alertName": "Controller Failover Occurred",
  "description": "Automatic controller failover has been triggered. System continues to operate on the secondary controller.",
  "rca": "Primary controller heartbeat lost or watchdog timeout triggered. Hardware diagnostics indicate potential processor subsystem issue.",
  "correctiveAction": "Investigate primary controller health. Run diagnostic tests. Contact support for assistance with failed controller replacement.",
  "eventSource": "High Availability Manager",
  "alertType": "Availability Event"
}'

# Error Code 10: System temperature alert
curl -X POST http://localhost:8080/api/errorCodes -H "Content-Type: application/json" -d '{
  "errorCodeId": "1010",
  "conditionId": "TEMP-010",
  "component": "THERMAL_SUBSYSTEM",
  "idValue": "ERR1010",
  "severity": "CRITICAL",
  "callhome": "YES",
  "alertName": "Critical Temperature Threshold Exceeded",
  "description": "System components have exceeded critical temperature thresholds. Automatic shutdown may be triggered to prevent hardware damage.",
  "rca": "Ambient data center temperature too high or multiple cooling fan failures. Temperature sensors reporting values above operating specifications.",
  "correctiveAction": "Check data center cooling systems. Ensure all system fans are operational. Clear any dust or obstructions from air intakes.",
  "eventSource": "Environmental Monitoring",
  "alertType": "Environmental"
}'

# Associate error codes with versions
curl -X POST http://localhost:8080/api/versions/1/errorCodes/ex100a1
curl -X POST http://localhost:8080/api/versions/1/errorCodes/100asd2
curl -X POST http://localhost:8080/api/versions/1/errorCodes/1001a3
curl -X POST http://localhost:8080/api/versions/1/errorCodes/ex1004
curl -X POST http://localhost:8080/api/versions/1/errorCodes/mx1005

curl -X POST http://localhost:8080/api/versions/2/errorCodes/ex100a1
curl -X POST http://localhost:8080/api/versions/2/errorCodes/100asd2
curl -X POST http://localhost:8080/api/versions/2/errorCodes/1001a3
curl -X POST http://localhost:8080/api/versions/2/errorCodes/ex1004
curl -X POST http://localhost:8080/api/versions/2/errorCodes/mx1005
curl -X POST http://localhost:8080/api/versions/2/errorCodes/1006
curl -X POST http://localhost:8080/api/versions/2/errorCodes/1007
curl -X POST http://localhost:8080/api/versions/2/errorCodes/1008

curl -X POST http://localhost:8080/api/versions/3/errorCodes/100asd2
curl -X POST http://localhost:8080/api/versions/3/errorCodes/mx1005
curl -X POST http://localhost:8080/api/versions/3/errorCodes/1009
curl -X POST http://localhost:8080/api/versions/3/errorCodes/1010

curl -X POST http://localhost:8080/api/versions/4/errorCodes/1007
curl -X POST http://localhost:8080/api/versions/4/errorCodes/1008
curl -X POST http://localhost:8080/api/versions/4/errorCodes/1009
