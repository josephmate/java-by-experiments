
```
./SimpleRepro
zsh: segmentation fault  ./SimpleRepro
./SimpleRepro
zsh: segmentation fault  ./SimpleRepro
./SimpleRepro
./SimpleRepro
zsh: segmentation fault  ./SimpleRepro
./SimpleRepro
./SimpleRepro
zsh: segmentation fault  ./SimpleRepro
./SimpleRepro
./SimpleRepro
./SimpleRepro
<stuck>
```

```
top
PID    COMMAND      %CPU  TIME     #TH   #WQ  #PORT MEM    PURG   CMPRS  PGRP  PPID  STATE    BOOSTS            %CPU_ME %CPU_OTHRS UID  FAULTS     COW     MSGSENT    MSGRECV
44679  SimpleRepro  107.0 09:54.66 5/4   0    14    376K   0B     328K   44679 43311 running  *0[1]             0.00000 0.00000    501  418        91      40         13
```


TODO: get a stack trace or something that shows it's stuck in an infinite loop
