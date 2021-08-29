Do `System.currentTimeMillis()` or `System.nanoTime()` ever decrease between
successive calls? Do they every repeat?

This question is important because I have maintained code where the author
expected that `currentTimeMillis()` always created a new value. The code would
fail if there was ever a repeat.

1. Think about the answer.
2. Try to write some experimental code to determine the answer.
3. Read through resources online.

<details>
<summary>Click to reveal the solution by experiment.</summary>
My experiment creates 4 threads that run forever. Each thread compares the
current value to the previous, prints, and stops if a violation is found.

```
long prev = System.currentTimeMillis();
while (true) {
  final long curr = System.currentTimeMillis();
  if (prev == curr) {
    System.out.println("System.currentTimeMillis() repeated prev=" + prev + ", curr=" + curr);
    break;
  } 
  prev = curr;
}
```
Only currentTimeMillis() repeated. nanoTime() did not. currentTimeMillis and
nanoTime never decreased. 

This results make sense since between successive currentTimeMillis() calls could
be less than 1 millisecond. However, it would be more difficult depending on the
accuracy of nanoTime() implemented on your system.

However, in this case experimentation is not
sufficient. However, what happens if you set the system time to something in the
past? 

Using `sudo date -u 0829120021` I was able to do that, and the values returned
never decreased. However, online sources claim otherwise.
</details>

<details>
<summary>Click to reveal the solution by reading sources online.</summary>
Depending on what version of the JDK you use, you might see the time decrease
when the system time changes!

https://stackoverflow.com/questions/2978598/will-system-currenttimemillis-always-return-a-value-previous-calls

https://bugs.java.com/bugdatabase/view_bug.do?bug_id=6458294
</details>

<details>
<summary>Click to reveal the overall lesson of this exercise.</summary>
NEVER use currentTimeMillis() nor nanoTime() to create a unique identifier.
</details>
