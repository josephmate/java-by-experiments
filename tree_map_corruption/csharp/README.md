
The problem reproduced in C#.
Running the program sometimes results in it hanging and 300% CPU util.

```
dotnet run TreeMapCorruption.csproj
TreeMapCorruption.csproj
0: Started
1: Started
2: Started
3: Started
4: Started
Exception Message: Object reference not set to an instance of an object.
Exception Message: Object reference not set to an instance of an object.
Exception Message: Object reference not set to an instance of an object.
<hangs here>
```

```
ps aux | rg 'USER|[T]reeMap'
USER               PID  %CPU %MEM      VSZ    RSS   TT  STAT STARTED      TIME
COMMAND
joseph           83617 364.9  0.3  6608084  14388 s005  R+   11:26pm   9:59.40
/Users/joseph/projects/java-by-experiments-private/tree_map_corruption/csharp/TreeMapCorruption/TreeMapCorruption/bin/Debug/netcoreapp3.0/TreeMapCorruption
TreeMapCorruption.csproj
joseph           83666   0.0  0.0  4265240     12 s006  S+   11:29pm   0:00.00
rg USER|[T]reeMap
joseph           83614   0.0  2.0  6933468  82860 s005  S+   11:26pm   0:06.95
dotnet run TreeMapCorruption.csproj
```


