
# Results

Was able to run the program 1,000 times without reproducing the problem.
No explanation for why it does not reproduce the issue.
I was expecting it to also reproduce in Ruby.
```
for i in `seq 1 1000`; do echo $i; ruby simple_repo.rb; done
...
993
Done
994
Done
995
Done
996
Done
997
Done
998
Done
999
Done
1000
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

