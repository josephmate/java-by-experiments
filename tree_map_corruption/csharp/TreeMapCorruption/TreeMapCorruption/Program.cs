using System;
using System.Collections.Generic;
using System.Threading;

namespace TreeMapCorruption
{
    class Program
    {
        static void Main(string[] args)
        {
            int numThreads = args.Length >= 1 ? int.Parse(args[0]) : 5;
            int numUpdates = args.Length >= 2 ? int.Parse(args[1]) : 10_000;

            // TreeMap equivalent in C# is SortedDictionary
            var sortedDictionary = new SortedDictionary<int, int>();

            var threads = new List<Thread>();
            for (int i = 0; i < numThreads; i++)
            {
                var thread = new Thread(() =>
                {
                    var random = new Random();
                    for (int j = 0; j < numUpdates; j++)
                    {
                        try
                        {
                            lock (sortedDictionary)
                            {
                                sortedDictionary[random.Next(1000)] = random.Next(1000);
                            }
                        }
                        catch (Exception)
                        {
                            // Let it keep going to reproduce the issue
                        }
                    }
                });
                threads.Add(thread);
            }

            foreach (var thread in threads)
            {
                thread.Start();
            }

            foreach (var thread in threads)
            {
                thread.Join();
            }
        }
    }
}
