#!/bin/bash
echo "Stopping Orvion ERP services..."

# Kill Angular dev server
if lsof -ti :4200 > /dev/null 2>&1; then
    kill -9 $(lsof -ti :4200) 2>/dev/null && echo "✓ Frontend stopped"
fi

# Kill all Java services
for port in 8087 8086 8085 8084 8083 8082 8081 8080 8888; do
    pid=$(lsof -ti :$port 2>/dev/null)
    if [ -n "$pid" ]; then
        kill -15 $pid 2>/dev/null
        sleep 1
        kill -9 $pid 2>/dev/null
        echo "✓ Service on port $port stopped"
    fi
done

echo ""
echo "All Orvion ERP services stopped."
