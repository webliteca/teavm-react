#!/bin/bash
# Live-reload development server for teavm-react
# Usage: ./dev.sh [port]
#
# Performs an initial build, then starts a dev server that watches for
# source changes, recompiles automatically, and live-reloads the browser.
# Edit files in your favourite editor — the browser updates on save.

set -e

PORT="${1:-8080}"
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
WEBAPP_DIR="$PROJECT_DIR/teavm-react-demo/target/webapp"

echo "=== teavm-react dev server ==="
echo ""

# ── Initial build ──────────────────────────────────────────────────────
echo "Running initial build..."
mvn -f "$PROJECT_DIR/pom.xml" install -N -q 2>/dev/null
mvn -f "$PROJECT_DIR/pom.xml" install -pl teavm-react-core,teavm-react-kotlin -q -DskipTests 2>/dev/null
mvn -f "$PROJECT_DIR/teavm-react-demo/pom.xml" process-classes -q

if [ ! -f "$WEBAPP_DIR/js/classes.js" ]; then
    echo "ERROR: Initial build did not produce classes.js"
    exit 1
fi

JS_SIZE=$(wc -c < "$WEBAPP_DIR/js/classes.js")
echo "Initial build complete. Generated JS: ${JS_SIZE} bytes"
echo ""

# ── Launch dev server ──────────────────────────────────────────────────
echo "Starting dev server on http://localhost:$PORT"
echo "Live-reload is active — edit source files and save to see changes."
echo "Press Ctrl+C to stop."
echo ""

exec java --enable-preview --source 21 "$PROJECT_DIR/DevServer.java" "$PORT"
