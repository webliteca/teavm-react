#!/bin/bash
# Run the teavm-react demo app
# Usage: ./run.sh [port]
#
# Builds the project with Maven and serves the output on localhost.
# Open http://localhost:<port> in your browser (default port: 8080).

set -e

PORT="${1:-8080}"
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
WEBAPP_DIR="$PROJECT_DIR/target/webapp"

echo "=== Building teavm-react ==="
mvn -f "$PROJECT_DIR/pom.xml" clean process-classes -q

if [ ! -f "$WEBAPP_DIR/js/classes.js" ]; then
    echo "ERROR: Build did not produce classes.js"
    exit 1
fi

JS_SIZE=$(wc -c < "$WEBAPP_DIR/js/classes.js")
echo "Build complete. Generated JS: ${JS_SIZE} bytes"

echo ""
echo "=== Serving at http://localhost:$PORT ==="
echo "Press Ctrl+C to stop."
echo ""

python3 -m http.server "$PORT" --directory "$WEBAPP_DIR"
