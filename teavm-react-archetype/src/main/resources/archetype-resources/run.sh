#!/bin/bash
PORT=${1:-8080}
mvn clean process-classes -q
echo "Serving at http://localhost:$PORT"
python3 -m http.server $PORT --directory target/webapp
