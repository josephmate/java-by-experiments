package main

import (
	"fmt"
	"math/rand"
	"os"
	"strconv"
	"sync"
	"time"

	"github.com/emirpasic/gods/trees/redblacktree"
)

func main() {
	// Parse command-line arguments
	numThreads := 5
	numUpdates := 1000

	if len(os.Args) >= 2 {
		numThreads, _ = strconv.Atoi(os.Args[1])
	}
	if len(os.Args) >= 3 {
		numUpdates, _ = strconv.Atoi(os.Args[2])
	}

	tree := redblacktree.NewWithIntComparator()

	var wg sync.WaitGroup

	for i := 0; i < numThreads; i++ {
    myThreadId := i
		wg.Add(1)
		go func() {
			defer wg.Done()
      fmt.Printf("%d: started\n", myThreadId)
			r := rand.New(rand.NewSource(time.Now().UnixNano()))
			for j := 0; j < numUpdates; j++ {
				key := r.Intn(1000)
				value := r.Intn(1000)
        // not locking on purpose
				tree.Put(key, value)
			}
      fmt.Printf("%d: finished\n", myThreadId)
		}()
	}

	wg.Wait()
}
