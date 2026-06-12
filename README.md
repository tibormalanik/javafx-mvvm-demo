# A Practical Guide to MVVM in JavaFX — Demo

Companion code for the 20-minute conference talk. The same feature (a "Person
Editor" with live validation and a computed full name) is implemented twice so
the audience can compare:

- **MVVM tab** — View binds to a `PersonViewModel` that exposes JavaFX
  `Property` objects. Derived state (full name, save-enabled) is declared once
  with `Bindings`. The ViewModel imports **no** `javafx.scene.*` — so it's
  unit-testable headless.
- **MVC tab** — Same UX, wired imperatively: per-field listeners that re-run a
  `recompute()` method. Works, but logic is entangled with widgets.

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

## Structure

```
src/main/java/
├── module-info.java
└── com/example/mvvm/
    ├── App.java                       entry point — TabPane with both tabs
    ├── model/
    │   ├── Person.java                immutable domain record
    │   └── PersonService.java         stand-in repository/backend
    ├── mvvm/
    │   ├── PersonViewModel.java        ★ the star — Properties + Bindings + commands
    │   └── PersonView.java             thin View, binds only
    └── mvc/
        └── PersonControllerView.java   contrast: imperative controller
```

## Demo script (for the live segment)

1. MVVM tab: type in *First name* → *Full name* updates live; clear it → Save
   greys out and status reads "First name is required". No handler does this —
   it's a binding.
2. MVC tab: identical behavior. Then show `recompute()` and the per-field
   listeners — that's where the logic hides.
3. Land it: *same UX, but one scales and tests cleanly; the other accumulates
   handlers.*

## Talk

Companion to the conference talk *"A Practical Guide to MVVM in JavaFX"*
by Tibor Malanik (~20 min).

## Requirements

- JDK 21 (tested with Homebrew `openjdk@21`)
- Maven 3.9+
- JavaFX 21.0.4 (pulled by Maven; no manual SDK install needed)
