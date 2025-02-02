
# Results

Even after trying all the following, I still wasn't able to reproduce the issue:
1. adding barrier
2. flushing output to stdout
3. increasing the amount of work per thread to 1,000,000
4. increasing the number of threads to 10

I believe it might not be able to reproduce the problem due to the Global Interpreter Lock (GIL),
and how it limits when threads can switch preventing such an interleaving of threads that cause an infinite loop.

```
ruby % ruby simple_repo.rb
1: waiting for other threads to start
2: waiting for other threads to start
3: waiting for other threads to start
4: waiting for other threads to start
5: waiting for other threads to start
6: waiting for other threads to start
7: waiting for other threads to start
8: waiting for other threads to start
9: waiting for other threads to start
10: waiting for other threads to start
10: started
1: started
2: started
3: started
4: started
5: started
6: started
7: started
8: started
9: started
1: finished
10: finished
2: finished
3: finished
4: finished
5: finished
7: finished
6: finished
8: finished
9: finished
Done

```

# Running

## Using macports

```
sudo port install ruby
sudo port install ruby34
sudo port select --set ruby ruby34
ruby --version
ruby 2.6.10p210 (2022-04-12 revision 67958) [universal.x86_64-darwin20]
# reopen terminal
ruby --version
ruby 3.4.1 (2024-12-25 revision 48d4efcb85) +PRISM [x86_64-darwin20]


sudo gem install kanwei-algorithms
sudo gem install concurrent-ruby
```

## Using Mac's ruby doesn't work:
```
sudo gem install kanwei-algorithms


Fetching kanwei-algorithms-0.2.0.gem
Building native extensions. This could take a while...
ERROR:  Error installing kanwei-algorithms:
        ERROR: Failed to build gem native extension.

    current directory:
/Library/Ruby/Gems/2.6.0/gems/kanwei-algorithms-0.2.0/ext/containers/deque
/System/Library/Frameworks/Ruby.framework/Versions/2.6/usr/bin/ruby -I
/System/Library/Frameworks/Ruby.framework/Versions/2.6/usr/lib/ruby/2.6.0 -r
./siteconf20250202-72756-eu9zss.rb extconf.rb
creating Makefile

current directory:
/Library/Ruby/Gems/2.6.0/gems/kanwei-algorithms-0.2.0/ext/containers/deque
make "DESTDIR=" clean

current directory:
/Library/Ruby/Gems/2.6.0/gems/kanwei-algorithms-0.2.0/ext/containers/deque
make "DESTDIR="
make: *** No rule to make target
`/Library/Developer/CommandLineTools/SDKs/MacOSX.sdk/System/Library/Frameworks/Ruby.framework/Versions/2.6/usr/include/ruby-2.6.0/universal-darwin20/ruby/config.h',
needed by `deque.o'.  Stop.

make failed, exit code 2

Gem files will remain installed in
/Library/Ruby/Gems/2.6.0/gems/kanwei-algorithms-0.2.0 for inspection.
Results logged to
/Library/Ruby/Gems/2.6.0/extensions/universal-darwin-20/2.6.0/kanwei-algorithms-0.2.0/gem_make.out

```

> 
https://github.com/github-linguist/linguist/issues/5147

