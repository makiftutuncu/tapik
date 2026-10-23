#!/usr/bin/env bash

set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
WORK_ROOT="$ROOT/target/gradle-verification"
MAVEN_REPOSITORY="$WORK_ROOT/repository"
MAVEN_HOME="$WORK_ROOT/maven-home"
GRADLE_HOME="$WORK_ROOT/gradle-home"
LOG="$WORK_ROOT/external-consumer.log"
GRADLE_COMMAND="${GRADLE:-$ROOT/plugin-gradle/gradlew}"
VERSION="$(grep -m 1 '<version>' "$ROOT/pom.xml" | sed -E 's/.*<version>([^<]+)<\/version>.*/\1/')"

case "$WORK_ROOT" in
    "$ROOT"/target/gradle-verification) ;;
    *) echo "Refusing to clear unexpected verification directory: $WORK_ROOT" >&2; exit 1 ;;
esac

rm -rf "$WORK_ROOT"
STAGING_ROOT="$(mktemp -d "${TMPDIR:-/tmp}/tapik-gradle-verification.XXXXXX")"
STAGING_REPOSITORY="$STAGING_ROOT/repository"
STAGING_MAVEN_HOME="$STAGING_ROOT/maven-home"
mkdir -p "$STAGING_REPOSITORY" "$STAGING_MAVEN_HOME"

echo "Installing tapik $VERSION into $MAVEN_REPOSITORY"
MAVEN_USER_HOME="$STAGING_MAVEN_HOME" "$ROOT/mvnw" \
    --batch-mode \
    --no-transfer-progress \
    -Dmaven.repo.local="$STAGING_REPOSITORY" \
    -DskipTests \
    clean \
    install

mkdir -p "$WORK_ROOT"
mv "$STAGING_REPOSITORY" "$MAVEN_REPOSITORY"
mv "$STAGING_MAVEN_HOME" "$MAVEN_HOME"
rmdir "$STAGING_ROOT"
mkdir -p "$GRADLE_HOME"

echo "Checking and publishing the Gradle plugin with $GRADLE_COMMAND"
GRADLE_USER_HOME="$GRADLE_HOME" "$GRADLE_COMMAND" \
    --no-daemon \
    -p "$ROOT/plugin-gradle" \
    clean \
    check \
    publishAllPublicationsToTapikVerificationRepository \
    -PtapikRepository="$MAVEN_REPOSITORY" \
    -PtapikPublishRepository="$MAVEN_REPOSITORY" \
    -PtapikVersion="$VERSION"

IMPLEMENTATION="$MAVEN_REPOSITORY/dev/akif/tapik-plugin-gradle/$VERSION"
MARKER="$MAVEN_REPOSITORY/dev/akif/tapik/plugin/gradle/dev.akif.tapik.plugin.gradle.gradle.plugin/$VERSION"
for artifact in \
    "$IMPLEMENTATION/tapik-plugin-gradle-$VERSION.jar" \
    "$IMPLEMENTATION/tapik-plugin-gradle-$VERSION-sources.jar" \
    "$IMPLEMENTATION/tapik-plugin-gradle-$VERSION-javadoc.jar" \
    "$IMPLEMENTATION/tapik-plugin-gradle-$VERSION.pom" \
    "$MARKER/dev.akif.tapik.plugin.gradle.gradle.plugin-$VERSION.pom"
do
    test -f "$artifact" || { echo "Missing Gradle publication artifact: $artifact" >&2; exit 1; }
done
for pom in \
    "$IMPLEMENTATION/tapik-plugin-gradle-$VERSION.pom" \
    "$MARKER/dev.akif.tapik.plugin.gradle.gradle.plugin-$VERSION.pom"
do
    for metadata in \
        '<name>tapik Gradle plugin</name>' \
        '<url>https://tapik.akif.dev</url>' \
        '<name>MIT License</name>' \
        '<id>makiftutuncu</id>' \
        '<url>https://github.com/makiftutuncu/tapik</url>' \
        '<url>https://github.com/makiftutuncu/tapik/issues</url>'
    do
        grep -Fq "$metadata" "$pom" || {
            echo "Missing publication metadata '$metadata' in $pom" >&2
            exit 1
        }
    done
done
jar tf "$IMPLEMENTATION/tapik-plugin-gradle-$VERSION-sources.jar" > "$WORK_ROOT/plugin-sources.txt"
grep -q '[.]kt$' "$WORK_ROOT/plugin-sources.txt" || {
    echo "Gradle sources archive contains no Kotlin source" >&2
    exit 1
}
jar tf "$IMPLEMENTATION/tapik-plugin-gradle-$VERSION-javadoc.jar" > "$WORK_ROOT/plugin-documentation.txt"
grep -q '^index[.]html$' "$WORK_ROOT/plugin-documentation.txt" || {
    echo "Gradle API documentation archive contains no Dokka HTML index" >&2
    exit 1
}

EXTERNAL_WORKSPACE="$(mktemp -d "${TMPDIR:-/tmp}/tapik-gradle-consumer.XXXXXX")"
CONSUMER="$EXTERNAL_WORKSPACE/consumer"
printf '%s\n' "$EXTERNAL_WORKSPACE" > "$WORK_ROOT/external-consumer.txt"
mkdir -p "$CONSUMER/example-gradle" "$CONSUMER/example-contract" "$CONSUMER/example-application"
cp "$ROOT/example-gradle/settings.gradle.kts" "$ROOT/example-gradle/build.gradle.kts" \
    "$ROOT/example-gradle/gradle.properties" "$CONSUMER/example-gradle/"
cp "$ROOT/example-contract/build.gradle.kts" "$CONSUMER/example-contract/"
cp "$ROOT/example-application/build.gradle.kts" "$CONSUMER/example-application/"
cp -R "$ROOT/example-contract/src" "$CONSUMER/example-contract/src"
mkdir -p "$CONSUMER/example-application/src"
cp -R "$ROOT/example-application/src/main" "$CONSUMER/example-application/src/main"
mkdir -p "$CONSUMER/example-gradle/application/src/test/kotlin/com/example/library/application"
cp "$ROOT/example-gradle/application/src/test/kotlin/com/example/library/application/GradleLibraryExampleSpec.kt" \
    "$CONSUMER/example-gradle/application/src/test/kotlin/com/example/library/application/"
echo "Building an external Gradle consumer from the published marker"
GRADLE_USER_HOME="$GRADLE_HOME" "$GRADLE_COMMAND" \
    --no-daemon \
    -p "$CONSUMER/example-gradle" \
    clean \
    check \
    -PtapikRepository="$MAVEN_REPOSITORY" \
    -PtapikVersion="$VERSION" \
    2>&1 | tee "$LOG"

echo "Gradle verification passed; external consumer log: $LOG"
