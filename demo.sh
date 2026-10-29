#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

# Discover Java 25 on macOS; other systems use their configured JAVA_HOME.
if [[ -x /usr/libexec/java_home ]]; then
  demo_java_home=$(/usr/libexec/java_home -v 25 2>/dev/null) || {
    echo "Java 25 is required. Install JDK 25 and try again." >&2
    exit 1
  }
  export JAVA_HOME="$demo_java_home"
fi
if [[ ! -f backend/.env ]]; then
  echo "Create backend/.env with your MySQL settings (see backend/.env.exemple)." >&2
  exit 1
fi

echo "Starting demo backend: trips are saved in MySQL with a shared test user."
echo "Keep this terminal open. Stop with Ctrl+C."
# The profile is scoped to this script; the parent terminal is unchanged.
export SPRING_PROFILES_ACTIVE=local-demo
exec bash backend/mvnw -f backend/pom.xml org.springframework.boot:spring-boot-maven-plugin:4.1.1:run
