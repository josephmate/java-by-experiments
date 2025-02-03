

```
env GOTRACEBACK=all go run simple_repro.go
0: started
2: started
1: started
3: started
4: started
```

```
ps aux | rg '[U]SER|[s]imple'
USER               PID  %CPU %MEM      VSZ    RSS   TT  STAT STARTED      TIME COMMAND
joseph           84608 366.4  0.1  5495976   2408 s004  R+   11:57pm   0:38.18 /var/folders/gd/y2bvmd0530568763mxnfnpdw0000gn/T/go-build2633847538/b001/exe/simple_repro
joseph           84597   0.0  0.4  5540252  15992 s004  S+   11:57pm   0:00.32 go run simple_repro.go
```
