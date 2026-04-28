#!/usr/bin/env bash
set -euo pipefail

ZULU_HOME=/opt/zulu-25
export JAVA_HOME="$ZULU_HOME"
export PATH="$JAVA_HOME/bin:$PATH"

JAVAFX_MODS=$(find "$ZULU_HOME/lib" -maxdepth 1 -name "javafx*.jar" | tr '\n' ':' | sed 's/:$//')

java \
  --module-path "$JAVAFX_MODS" \
  --add-modules javafx.controls,javafx.fxml \
  -cp "bin:postgresql.jar" \
  app.MainApp
