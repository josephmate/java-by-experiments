Can a thread promote their `ReentrantReadWriteLock.readLock()` to a `ReentrantReadWriteLock.writeLock()`?

1. Think about the answer.
2. Try to write some experimental code to determine the answer.
3. Read through resources online.

<details>
<summary>Click to reveal the solution by experiment.</summary>
You cannot promote an acquired read lock to a write lock.
You must release the read lock then acquire the write lock.

```
./run.sh RRW
Program starting
Acquiring the read lock
Got the read lock
Acquiring the read lock
Got the read lock
Acquiring the write lock
```

However, you can acquire the write lock, then the read lock:
```
./run.sh WR
Program starting
Acquiring the write lock
Got the write lock
Acquiring the read lock
Got the read lock
```
</details>

<details>
<summary>Click to reveal the solution by reading sources online.</summary>
Read through the [javadoc](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/locks/ReentrantReadWriteLock.html).

> Additionally, a writer can acquire the read lock, but not vice-versa. Among other applications, reentrancy can be useful when write locks are held during calls or callbacks to methods that perform reads under read locks. If a reader tries to acquire the write lock it will never succeed. 

> Reentrancy also allows downgrading from the write lock to a read lock, by acquiring the write lock, then the read lock and then releasing the write lock. However, upgrading from a read lock to the write lock is not possible. 

You cannot acquire the read lock then the write lock.
</details>

