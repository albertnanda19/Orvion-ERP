#!/bin/bash
# Elasticsearch Setup Script for Orvion ERP
# Creates indexes with proper mappings

set -euo pipefail

ES_URL="${ES_URL:-http://localhost:9200}"

echo "=== Creating Elasticsearch Indexes for Orvion ERP ==="

# 1. Audit Logs Index
curl -s -X PUT "$ES_URL/orvion-audit-logs" -H "Content-Type: application/json" -d '{
  "settings": {
    "number_of_shards": 1,
    "number_of_replicas": 0
  },
  "mappings": {
    "properties": {
      "timestamp":     { "type": "date" },
      "service":       { "type": "keyword" },
      "user":          { "type": "keyword" },
      "action":        { "type": "keyword" },
      "resource":      { "type": "keyword" },
      "resource_id":   { "type": "keyword" },
      "details":       { "type": "text" },
      "ip_address":    { "type": "ip" },
      "status":        { "type": "keyword" }
    }
  }
}' | python3 -c "import sys,json; d=json.load(sys.stdin); print('orvion-audit-logs:', 'acknowledged' if d.get('acknowledged') else d.get('error', d))"

# 2. Transactions Index
curl -s -X PUT "$ES_URL/orvion-transactions" -H "Content-Type: application/json" -d '{
  "settings": {
    "number_of_shards": 1,
    "number_of_replicas": 0
  },
  "mappings": {
    "properties": {
      "timestamp":     { "type": "date" },
      "service":       { "type": "keyword" },
      "transaction_id":{ "type": "keyword" },
      "type":          { "type": "keyword" },
      "amount":        { "type": "double" },
      "currency":      { "type": "keyword" },
      "status":        { "type": "keyword" },
      "description":   { "type": "text" },
      "user":          { "type": "keyword" }
    }
  }
}' | python3 -c "import sys,json; d=json.load(sys.stdin); print('orvion-transactions:', 'acknowledged' if d.get('acknowledged') else d.get('error', d))"

# 3. Products Index
curl -s -X PUT "$ES_URL/orvion-products" -H "Content-Type: application/json" -d '{
  "settings": {
    "number_of_shards": 1,
    "number_of_replicas": 0
  },
  "mappings": {
    "properties": {
      "product_id":    { "type": "keyword" },
      "sku":           { "type": "keyword" },
      "name":          { "type": "text", "fields": { "keyword": { "type": "keyword" } } },
      "description":   { "type": "text" },
      "category":      { "type": "keyword" },
      "price":         { "type": "double" },
      "stock":         { "type": "integer" },
      "active":        { "type": "boolean" },
      "created_at":    { "type": "date" },
      "updated_at":    { "type": "date" }
    }
  }
}' | python3 -c "import sys,json; d=json.load(sys.stdin); print('orvion-products:', 'acknowledged' if d.get('acknowledged') else d.get('error', d))"

# 4. Employees Index
curl -s -X PUT "$ES_URL/orvion-employees" -H "Content-Type: application/json" -d '{
  "settings": {
    "number_of_shards": 1,
    "number_of_replicas": 0
  },
  "mappings": {
    "properties": {
      "employee_id":   { "type": "keyword" },
      "name":          { "type": "text", "fields": { "keyword": { "type": "keyword" } } },
      "email":         { "type": "keyword" },
      "department":    { "type": "keyword" },
      "position":      { "type": "keyword" },
      "skills":        { "type": "text" },
      "hire_date":     { "type": "date" },
      "active":        { "type": "boolean" }
    }
  }
}' | python3 -c "import sys,json; d=json.load(sys.stdin); print('orvion-employees:', 'acknowledged' if d.get('acknowledged') else d.get('error', d))"

echo ""
echo "=== Index Summary ==="
curl -s "$ES_URL/_cat/indices?v" 2>&1
echo ""
echo "=== Elasticsearch Setup Complete ==="
