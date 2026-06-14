# A Practical Guide to MVVM in JavaFX — Demo

Simple example which demonstrates the MVVM architecture pattern JavaFX

## Run

```bash
cd javafx-mvvm-demo
mvn javafx:run
```

Requires JDK 21 on your `PATH` (or set `JAVA_HOME`). On macOS with Homebrew:

```bash
export JAVA_HOME="$(brew --prefix openjdk@21)/libexec/openjdk.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"
```

## Build only

```bash
mvn clean compile
```

## Requirements

- JDK 21
- Maven 3.9+