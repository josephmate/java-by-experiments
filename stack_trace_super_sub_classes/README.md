What does stack trace of the overrided method look like compared to super class's method?


1. Think about the answer.
2. Try to write up some expected stacktraces. 
3. Try to write some experimental code to determine the answer and compare with
   your stack traces.

<details>
<summary>Click to reveal my expected stack traces.</summary>

If the subclass throws the exception, I would expect it to look something like:

```
RuntimeException
  Sub.method()
  Main.main()
```

If the super class throws the exception, I would expect it to look something
like:
```
RuntimeException
  Super.method()
  Sub.method()
  Main.main()
```
since it needs to call the subclass first, so the subclass can call the super
method.
There's not way to directly call the super method.
</details>

<details>
<summary>Click to reveal stack traces from my experiment.</summary>
```
java.lang.RuntimeException
        at Main$Super.callsSuper(Main.java:16)
        at Main$Sub.callsSuper(Main.java:8)
        at Main.main(Main.java:23)
java.lang.RuntimeException
        at Main$Sub.callsSuper(Main.java:9)
        at Main.main(Main.java:28)
```

In the fist example, the sub class is called first, then the super method throws the exception.
In the second example, the sub class throws the exception, so there's only two
elements on the stack: the sub method and the main method.
</details>

