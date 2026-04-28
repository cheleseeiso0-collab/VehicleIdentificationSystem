#!/usr/bin/env bash
set -euo pipefail

ZULU_HOME=/opt/zulu-25
export JAVA_HOME="$ZULU_HOME"
export PATH="$JAVA_HOME/bin:$PATH"

# JavaFX is bundled in the Zulu -fx- JDK
JAVAFX_MODS="$ZULU_HOME/lib"

CP="postgresql.jar:src/resources"

echo "==> Finding source files..."
find src -name "*.java" > sources.txt

echo "==> Compiling..."
javac \
  --module-path "$JAVAFX_MODS" \
  --add-modules javafx.controls,javafx.fxml \
  -cp "$CP" \
  @sources.txt \
  -d bin

echo "==> Copying resources..."
cp src/resources/db.properties bin/

echo "==> Build successful!"
