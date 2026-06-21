# BlockingQueue Learning Demo

This project now focuses on a beginner-friendly `BlockingQueue` demo using only `put()` and `take()`.

## What it demonstrates

- `take()` blocks when queue is empty
- `put()` blocks when queue is full
- another thread later unblocks the waiting thread

## Files

- `src/MyBlockingQueue.java`: beginner demo with 2 blocking scenes
- `src/Test.java`: tiny runner that calls `MyBlockingQueue.main(...)`

## Run (PowerShell)

```powershell
Set-Location "A:\mycode\java\2026\myBlockingQueue"
javac src\MyBlockingQueue.java src\Test.java
java -cp src Test
```

You should see logs with thread names and timestamps, including lines like:

- `consumer calls take(), queue is empty -> block`
- `producer puts 100`
- `producer calls put(2), queue full -> block`


