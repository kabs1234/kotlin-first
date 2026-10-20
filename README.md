# Library System

> **HW2:** the Android version of this app (Jetpack Compose, Material 3, ViewModel) is in [`android-app/`](android-app/README.md), with features, screenshots and running instructions.

A Kotlin console app that runs a small university library for one simulated month. It loads new books from two publisher feeds, lets students, a professor and a guest borrow and return books, and prints a report with late fees and catalog stats at the end.

Each member type has its own loan limit, loan period and late-fee rule, so the same `borrow` and `returnBook` calls give different results depending on who is borrowing.

## How to run

You need JDK 17 or newer. The Gradle wrapper is included, so Gradle itself doesn't need to be installed.

```bash
./gradlew run
```

On Windows use `gradlew.bat run`. In IntelliJ IDEA, open the folder as a Gradle project and run `main()` in `src/main/kotlin/library/Main.kt`.

## Where the requirements are

All code is in `src/main/kotlin/library/`.

| Requirement | Where |
|---|---|
| Variables, data types, conditions, loops | `Main.kt`: `runSimulation` loops over days 1..30 and uses `continue` on days with no events. `Guest.lateFee` and `Member.describe` use `if`, `Library.borrow` uses `when`. Types used: `Int`, `Double`, `String`, `Boolean`, `Long` |
| List, Set, Map | `Book.genres` is a `Set`, `Library` keeps its catalog and active loans in `MutableMap`s, `SampleData.schedule` is a `Map<Int, List<LibraryEvent>>` |
| map, filter, reduce | `printReport` in `Main.kt`: `reduce` finds the oldest book, `reduceOrNull` adds up fees, `filter` + `map` collect late readers. `Library.loansOf` uses `filter` |
| Functions, higher-order functions, lambdas | `Library.findBooks(predicate: (Book) -> Boolean)` takes a function. `publishedSince(year)` returns one. Lambdas are passed to `associateBy`, `associateWith`, `forEach` |
| Classes and objects | `Library`, `PublisherFeed`, and `SampleData`, which is an `object` |
| Inheritance | `Member.kt`: abstract `Member`, extended by `Student`, `Professor` and `Guest`, each overriding `describe()` |
| Interfaces and polymorphism | `LateFeePolicy` interface. `Receipt.fee` calls `member.lateFee(...)` without knowing the member type: students pay per day, guests pay a flat fee plus a daily fee, professors pay nothing |
| Data class | `Book`, `Loan`, `Receipt` |
| Sealed class | `BorrowResult` (borrowed / already on loan / limit reached / unknown book) and `LibraryEvent` (borrow / return), both in `Loan.kt`. `describe(result)` matches on every case with no `else` branch |
| Suspend function and coroutine | `PublisherFeed.kt`: `fetchNewArrivals()` is a `suspend` function that uses `delay` to imitate a network call. `loadNewArrivals` starts every feed with `async` inside `coroutineScope`, and `main` runs in `runBlocking`. The feeds load at the same time, so it takes about 400 ms instead of 700 |

## Sample output

```text
== Simulation ==
  Day  1: Alice borrowed "Clean Code", due day 15
  Day  1: Alice borrowed "Kotlin in Action", due day 15
  Day  1: Alice hit the limit of 2 books
  Day  2: "Clean Code" is already with Alice
  ...
  Day 13: Visitor returned "A Brief History of Time" 3 days late, fee $5.00

== Report ==
  Books in catalog: 7
  Oldest book:      Pride and Prejudice (1813)
  Fees collected:   $9.50
```
