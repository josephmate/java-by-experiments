If you have a question about Java, you can probably Google it and find Stackoverflow question with the answer in a few minutes.
Or you could spend hours writing an experimental program that also gives you the answer!

Learn about java fundementals by writing experimental programs.
Each article will cover as small of a concept as possible.
The article will start with a question, and hide the answer in an expand block.

For each article you should:

1. Try to think about the question and guess it.
2. Try to write a program that will help you explore the answer. How does it
   compare with your guess?
3. Expand the answer block, and compare with your what you learned from your
   experiment.
4. Inspect the code included with the article and compare the approach.


Topics:

1. Performance
    1. [How many threads until your JVM crashes?](native_memory_impact/README.md)
    1. [How does native memory impact heap? OS memory?](create_threads_until_crash/README.md)
1. Exceptions
    1. [What does stack trace of the overrided method look like compared to super class's method?](stack_trace_super_sub_classes/README.md)
1. Date and time
    1. [Does `System.currentTimeMillis()` or `System.nanoTime()` ever decrease? ever repeat?](millis_nanos_repeat_or_decrease/README.md)
2. JDBC
    1. [Where can you use '?' in prepared statements?](prepared_statement_limits/README.md)
3. Concurrency
    1. [Can a thread promote their `ReentrantReadWriteLock.readLock()` to a `ReentrantReadWriteLock.writeLock()`?](read_write_lock_promotion/README.md)

Future topics:

1. 
1. What happens when you get a deadlock with synchronized in java?
1. What happens when you get a deadlock with locks in java? Is it any different
   from synchronized?
1. Does synchronized use fair locking?
2. What happens when you get a deadlock in jdbc?
3. What line throws NPE when you try to autobox a null?
6. What happens to your thread when you gracefully kill the java process?
7. What happens to the runnables in an ExecutorService when you gracefully kill the java process?
8. What happens when an Runnable in an ExecutorService throws an exception?
9. What hapepns when a Callable in an Executor Service throws an exception and
   you call .get() on the future?
10. Can in inner class use the privates of the holding class?
11. How do you chain method calls when Non-null, nullable, primitive, and
    optional?
12. What happens when you print an array? Does toString() call toString on each
    element? What about an ArrayList?
5. Converting bytes to string
