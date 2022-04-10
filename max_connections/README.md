What's the max num of connections a server and client can concurrently keep open?



<details>
<summary>Click to reveal the short summary.</summary>

The theoretical limit is 1 quadrillion because:

1. [number of IP addresses]x[num of ports]
2. because the server multiplexes the connections from the client using that.
3. 32 bits for the address and 16 bits for the port
4. In total is 2^48.
5. Which is about a billion connects (log(2^48)/log(10)=14.449)!

## Mac

But the practical limit is much lower on my Mac.
```
2.5 GHz Quad-Core Intel Core i7
16 GB 1600 MHz DDR3
```

To get this to work, you need to jump through a lot of hoops.

First, setup sysctl so that we can have as many file descriptors as we want.
```
sudo sysctl kern.maxfiles=2000000 kern.maxfilesperproc=1000000
kern.maxfiles: 49152 -> 2000000
kern.maxfilesperproc: 24576 -> 1000000
sysctl -a | grep maxfiles
kern.maxfiles: 2000000
kern.maxfilesperproc: 1000000
```

Setup loop back addresses on 10.0.0.X to overcome the src tcp port limit:
```
for i in `seq 0 200`; do sudo ifconfig lo0 alias 10.0.0.$i/8 up  ; done 
```

For some reason we need to set the soft limit too even though the hard limit is
unlimited. I cannot explain why this step was necessary. Without it, it wouldn't
work.
```
ulimit -Sn 1000000
```

Run java with `-XX:-MaxFDLimit` to prevent the JVM from limiting file
descriptors:
```
java -XX:-MaxFDLimit  Main 80000
...
SERVER: 79998 from /10.0.0.15:64069 to /127.0.0.1:9999 msg 79998
SERVER: 79999 from /10.0.0.15:64070 to /127.0.0.1:9999 msg 79999
Shutting down sockets from client
Shutting down sockets to server
```
What's strange is that after running this, even though the above program
completed and released all resources, my Mac becomes less responsive and then
eventually crashes. As a result, I declare this the pratical limit for my Mac.

At this point I lack the skills to keep digging to figure out why my Mac is
crashing; however, I can try to see if it works better on my Linux machine.


</details>

<details>
<summary>Click to reveal the journey on Mac.</summary>

At about 5k my Mac runs out of file descriptors.
```
SERVER: 5115 from /127.0.0.1:51940 to /127.0.0.1:9999
Exception in thread "main" java.lang.ExceptionInInitializerError
  at java.base/sun.nio.ch.SocketDispatcher.close(SocketDispatcher.java:70)
  at java.base/sun.nio.ch.NioSocketImpl.lambda$closerFor$0(NioSocketImpl.java:1203)
  at java.base/jdk.internal.ref.CleanerImpl$PhantomCleanableRef.performCleanup(CleanerImpl.java:178)
  at java.base/jdk.internal.ref.PhantomCleanable.clean(PhantomCleanable.java:133)
  at java.base/sun.nio.ch.NioSocketImpl.tryClose(NioSocketImpl.java:854)
  at java.base/sun.nio.ch.NioSocketImpl.close(NioSocketImpl.java:906)
  at java.base/java.net.SocksSocketImpl.close(SocksSocketImpl.java:562)
  at java.base/java.net.Socket.close(Socket.java:1585)
  at Main.main(Main.java:123)
Caused by: java.io.IOException: Too many open files
  at java.base/sun.nio.ch.FileDispatcherImpl.init(Native Method)
  at java.base/sun.nio.ch.FileDispatcherImpl.<clinit>(FileDispatcherImpl.java:38)
  ... 9 more
```

Some sources say I need to adjust `ulimit -n`:
```
ulimit -n
256
```

I updated it:
```
ulimit -S -n 1000000
ulimit -n
1000000
```
and still get the problem.


Others say `launchctl limit maxfiles`, but that is also already `unlimited`:
```
launchctl limit maxfiles
        maxfiles    256            unlimited
```

This could be it:
```
sysctl -a | grep maxfiles
kern.maxfiles: 49152
kern.maxfilesperproc: 24576
```
Makes sense because I would use about 4 file descriptions per process:
1. server, incoming
2. server, outgoing
3. client, incoming
4. client, outgoing

You can set it according to this [stackoverflow answer](https://superuser.com/a/1644788)
```
sudo sysctl kern.maxfiles=2000000 kern.maxfilesperproc=1000000
kern.maxfiles: 49152 -> 2000000
kern.maxfilesperproc: 24576 -> 1000000
sysctl -a | grep maxfiles
kern.maxfiles: 2000000
kern.maxfilesperproc: 1000000
```

But that didn't help, I still get the error. I think I might have mis-used the
utlimit command because with -n I get:
```
TODO
```


https://superuser.com/a/1171028
```
sudo vim /Library/LaunchDaemons/limit.maxfiles.plist
```
with contents:
```
<?xml version="1.0" encoding="UTF-8"?>  
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN"  
        "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">  
  <dict>
    <key>Label</key>
    <string>limit.maxfiles</string>
    <key>ProgramArguments</key>
    <array>
      <string>launchctl</string>
      <string>limit</string>
      <string>maxfiles</string>
      <string>64000</string>
      <string>524288</string>
    </array>
    <key>RunAtLoad</key>
    <true/>
    <key>ServiceIPC</key>
    <false/>
  </dict>
</plist> 
```

Load the configs (this will load on restarts too):
```
sudo chown root:wheel /Library/LaunchDaemons/limit.maxfiles.plist
sudo launchctl load -w /Library/LaunchDaemons/limit.maxfiles.plist
launchctl limit maxfiles
```

NOPE! Still getting stuck at 5000...
/Library/LaunchDaemons/com.startup.sysctl.plist
```
sudo vim /Library/LaunchDaemons/com.startup.sysctl.plist
```
with contents:
```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN"
"http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>Label</key>
    <string>com.startup.sysctl</string>
    <key>LaunchOnlyOnce</key>
    <true/>
    <key>ProgramArguments</key>
    <array>
        <string>/usr/sbin/sysctl</string>
        <string>kern.maxfiles=40480</string>
        <string>kern.maxfilesperproc=28000</string>
    </array>
    <key>RunAtLoad</key>
    <true/>
</dict>
</plist>
```
install:
```
chown root:wheel /Library/LaunchDaemons/com.startup.sysctl.plist
launchctl load /Library/LaunchDaemons/com.startup.sysctl.plist
```

AHHHHH STILL 5000. Let me restart and see.

Fixed with: https://superuser.com/a/1398360
https://docs.oracle.com/en/java/javase/16/docs/specs/man/java.html

> `-XX:-MaxFDLimit` :     Disables the attempt to set the soft limit for the
> number of open file descriptors to the hard limit. By default, this option is
> enabled on all platforms, but is ignored on Windows. The only time that you
> may need to disable this is on Mac OS, where its use imposes a maximum of
> 10240, which is lower than the actual system maximum. 

```
java -XX:-MaxFDLimit -cp out/production/max_connections Main 6000
```

Looks like I am running out of client ports:
```
Exception in thread "main" java.net.BindException: Can't assign requested address
        at java.base/sun.nio.ch.Net.bind0(Native Method)
        at java.base/sun.nio.ch.Net.bind(Net.java:555)
        at java.base/sun.nio.ch.Net.bind(Net.java:544)
        at java.base/sun.nio.ch.NioSocketImpl.bind(NioSocketImpl.java:643)
        at java.base/java.net.DelegatingSocketImpl.bind(DelegatingSocketImpl.java:94)
        at java.base/java.net.Socket.bind(Socket.java:682)
        at java.base/java.net.Socket.<init>(Socket.java:506)
        at java.base/java.net.Socket.<init>(Socket.java:403)
        at Main.main(Main.java:137)
```

https://phoenixframework.org/blog/the-road-to-2-million-websocket-connections
encountered a similar issue.

I can workaround this by creating
fake addresses for each client and those addresses to /etc/hosts.

Before my /etc/hosts was:
```
##
# Host Database
#
# localhost is used to configure the loopback interface
# when the system is booting.  Do not change this entry.
##
127.0.0.1 localhost
255.255.255.255 broadcasthost
::1             localhost
# Added by Docker Desktop
# To allow the same kube context to work on the host and the container:
127.0.0.1 kubernetes.docker.internal
# End of section
```
Using private address range `10.*`, I can add 256^3 addresses which is ~16
million which should be more than enough. I actually only need about 1000000/5000=200.

```
for i in `seq 0 200`; do echo "10.0.0.$i localhost"  ; done  > out.txt
```
then paste out onto /etc/hosts

then
```
sudo killall -HUP mDNSResponder 
```
to restart DNS


https://serverfault.com/questions/402744/assigning-multiple-ip-addresses-to-localhost-os-x-10-6
```
sudo ifconfig lo0 alias 10.0.0.0/8 up
ping 10.0.0.0
```

To add:
```
for i in `seq 0 200`; do sudo ifconfig lo0 alias 10.0.0.$i/8 up  ; done 
```

To test:
```
for i in `seq 0 200`; do ping -c 1 10.0.0.$i  ; done 
```

To remove:
```
for i in `seq 0 200`; do sudo ifconfig lo0 alias 10.0.0.$i  ; done 
```

Now re-running with the above mac configs, plus the modified java program to use
10.0.0.X as the src address for the clients:
```Exception in thread "main" java.io.IOException: Too many open files
        at java.base/sun.nio.ch.Net.accept(Native Method)
        at java.base/sun.nio.ch.NioSocketImpl.accept(NioSocketImpl.java:755)
        at java.base/java.net.ServerSocket.implAccept(ServerSocket.java:681)
        at
java.base/java.net.ServerSocket.platformImplAccept(ServerSocket.java:647)
        at java.base/java.net.ServerSocket.implAccept(ServerSocket.java:623)
        at java.base/java.net.ServerSocket.implAccept(ServerSocket.java:580)
        at java.base/java.net.ServerSocket.accept(ServerSocket.java:538)
        at Main.lambda$main$1(Main.java:47)
        at java.base/java.lang.Thread.run(Thread.java:831)
        Suppressed: java.lang.NoClassDefFoundError: Could not initialize class
sun.nio.ch.FileDispatcherImpl
                at
java.base/sun.nio.ch.SocketDispatcher.close(SocketDispatcher.java:70)
                at
java.base/sun.nio.ch.NioSocketImpl.lambda$closerFor$0(NioSocketImpl.java:1203)
                at
java.base/jdk.internal.ref.CleanerImpl$PhantomCleanableRef.performCleanup(CleanerImpl.java:178)
                at
java.base/jdk.internal.ref.PhantomCleanable.clean(PhantomCleanable.java:133)
                at
java.base/sun.nio.ch.NioSocketImpl.tryClose(NioSocketImpl.java:854)
                at
java.base/sun.nio.ch.NioSocketImpl.close(NioSocketImpl.java:906)
                at java.base/java.net.ServerSocket.close(ServerSocket.java:723)
                at Main.lambda$main$1(Main.java:86)
                ... 1 more
java.lang.ExceptionInInitializerError
        at java.base/sun.nio.ch.SocketDispatcher.close(SocketDispatcher.java:70)
        at
java.base/sun.nio.ch.NioSocketImpl.lambda$closerFor$0(NioSocketImpl.java:1203)
        at
java.base/jdk.internal.ref.CleanerImpl$PhantomCleanableRef.performCleanup(CleanerImpl.java:178)
        at
java.base/jdk.internal.ref.PhantomCleanable.clean(PhantomCleanable.java:133)
        at java.base/sun.nio.ch.NioSocketImpl.tryClose(NioSocketImpl.java:854)
        at java.base/sun.nio.ch.NioSocketImpl.close(NioSocketImpl.java:906)
        at java.base/java.net.SocksSocketImpl.close(SocksSocketImpl.java:562)
        at java.base/java.net.Socket.close(Socket.java:1585)
        at Main.main(Main.java:174)
```
out of file descriptors again.

For some reason my settings got reset so I had to rerun:
```
sudo sysctl kern.maxfiles=2000000 kern.maxfilesperproc=1000000
kern.maxfiles: 49152 -> 2000000
kern.maxfilesperproc: 24576 -> 1000000
```

Now it's working again:
```
java -XX:-MaxFDLimit  Main 20000
...
SERVER: 19998 from /10.0.0.3:50637 to /127.0.0.1:9999 msg 19998
SERVER: 19999 from /10.0.0.3:50638 to /127.0.0.1:9999 msg 19999
Shutting down sockets from client
Shutting down sockets to server

java -XX:-MaxFDLimit  Main 40000
SERVER: 39998 from /10.0.0.7:57875 to /127.0.0.1:9999 msg 39998
SERVER: 39999 from /10.0.0.7:57876 to /127.0.0.1:9999 msg 39999
Shutting down sockets from client
Shutting down sockets to server
```

At 80,000 my computer crashed. Let me try again.
```
java -XX:-MaxFDLimit  Main 80000
...
SERVER: 79998 from /10.0.0.15:64069 to /127.0.0.1:9999 msg 79998
SERVER: 79999 from /10.0.0.15:64070 to /127.0.0.1:9999 msg 79999
Shutting down sockets from client
Shutting down sockets to server
```
however, a few moments after it completed the experiment my laptop died again. I
can safely say that that's the limit for my Mac.

</details>

<details>
<summary>Click to reveal the journey on Linux.</summary>

```
AMD FX(tm)-6300 Six-Core Processor
8GiB 1600 MHz

```


https://superuser.com/a/1089140
```
for i in `seq 0 200`; do sudo ip addr add 10.0.0.$i/8 dev lo; done 
```

To remove:
```
for i in `seq 0 200`; do sudo ip addr del 10.0.0.$i/8 dev lo; done 
```

I didn't need to adjust limits because by default it's already above 1,000,000:
```
ulimit -Hn
1048576
```

Nevermind there's something wrong with my limits:
```
java -XX:-MaxFDLimit  Main 80000
.
.
.
SERVER: 472 from /10.0.0.0:47375 to /127.0.0.1:9999
Exception in thread "main" java.net.SocketException: Too many open files (Accept
failed)
        at java.net.PlainSocketImpl.socketAccept(Native Method)
        atjava.net.AbstractPlainSocketImpl.accept(AbstractPlainSocketImpl.java:409)
        at java.net.ServerSocket.implAccept(ServerSocket.java:560)
        at java.net.ServerSocket.accept(ServerSocket.java:528)
        at Main.lambda$main$1(Main.java:47)
        at java.lang.Thread.run(Thread.java:748)
java.net.SocketException: Too many open files
        at java.net.Socket.createImpl(Socket.java:478)
        at java.net.Socket.<init>(Socket.java:449)
        at java.net.Socket.<init>(Socket.java:346)
        at Main.main(Main.java:125)

```

Maybe it's the soft limit?
```
ulimit -Sn
1024
```

```
ulimit -Sn 1000000
ulimit -Sn
1000000
```

```
java -XX:-MaxFDLimit  Main 80000
.
.
.
SERVER: 79998 from /10.0.0.15:57459 to /127.0.0.1:9999 msg 79998
SERVER: 79999 from /10.0.0.15:48259 to /127.0.0.1:9999 msg 79999
Shutting down sockets to server
Shutting down sockets from client
```

```
java -XX:-MaxFDLimit  Main 160000
.
.
.
SERVER: 159998 from /10.0.0.31:41043 to /127.0.0.1:9999 msg 159998
SERVER: 159999 from /10.0.0.31:41513 to /127.0.0.1:9999 msg 159999
Shutting down sockets to server
Shutting down sockets from client
```
# second 160000 indicates to use 160,000 connections per client ip address
java -XX:-MaxFDLimit  Main 160000 160000
If you try to use all the ports from a single client IP you run out after 28,000:
```
SERVER: 28230 from /10.0.0.0:39902 to /127.0.0.1:9999
SERVER: 28231 from /10.0.0.0:39906 to /127.0.0.1:9999
Exception in thread "main" java.net.BindException: Address already in use (Bind failed)
        at java.net.PlainSocketImpl.socketBind(Native Method)
        at java.net.AbstractPlainSocketImpl.bind(AbstractPlainSocketImpl.java:387)
        at java.net.Socket.bind(Socket.java:662)
        at java.net.Socket.<init>(Socket.java:451)
        at java.net.Socket.<init>(Socket.java:346)
        at Main.main(Main.java:125)
.
.
.
```


```
java -XX:-MaxFDLimit  Main 320000
...
SERVER: 319998 from /10.0.0.63:39921 to /127.0.0.1:9999 msg 319998
SERVER: 319999 from /10.0.0.63:36455 to /127.0.0.1:9999 msg 319999
Shutting down sockets to server
Shutting down sockets from client
```

Need increase ulimit
```
java -XX:-MaxFDLimit  Main 640000
...
SERVER: 499988 from /10.0.0.99:45337 to /127.0.0.1:9999
SERVER: 499989 from /10.0.0.99:56891 to /127.0.0.1:9999
java.net.SocketException: Too many open files (Accept failed)
        at java.net.PlainSocketImpl.socketAccept(Native Method)
        at
java.net.AbstractPlainSocketImpl.accept(AbstractPlainSocketImpl.java:409)
        at java.net.ServerSocket.implAccept(ServerSocket.java:560)
        at java.net.ServerSocket.accept(ServerSocket.java:528)
        at Main.lambda$main$1(Main.java:47)
        at java.lang.Thread.run(Thread.java:748)
```

At that point linux complains if I try to increase the soft or hard limit beyond
```
ulimit -Sn 2000000
bash: ulimit: open files: cannot modify limit: Invalid argument
ulimit -Sn 1048576
<no error>


ulimit -Hn 
1048576
joseph@Joseph:~/projects/java-by-experiments/max_connections/src$ ulimit -Hn
1048577
bash: ulimit: open files: cannot modify limit: Operation not permitted

ulimit -Hn 1048576
<no error>
ulimit -Hn 1048575
<no error>
```

https://serverfault.com/a/1029846

```
cat /proc/sys/fs/file-max
9223372036854775807
cat /proc/sys/fs/file-nr
8864    0       9223372036854775807

```

```
sudo su
# 2^25
sysctl -w fs.nr_open=33554432
fs.nr_open = 33554432
ulimit -Hn 33554432
ulimit -Sn 33554432

```

It worked!
```
java -XX:-MaxFDLimit  Main 640000
...
SERVER: 639998 from /10.0.0.127:47321 to /127.0.0.1:9999 msg 639998
SERVER: 639999 from /10.0.0.127:48501 to /127.0.0.1:9999 msg 639999
Shutting down sockets to server
Shutting down sockets from client
```

I tried `1,000,000` but my computer became unresponsive. If I moved my mouse, it
moved a minute later.

</details>
