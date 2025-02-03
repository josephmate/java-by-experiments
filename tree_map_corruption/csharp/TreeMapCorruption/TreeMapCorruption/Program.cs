using System;
using System.Collections.Generic;
using System.Threading;

namespace TreeMapCorruption
{
    class Program
    {
        static void Main(string[] args)
        {
            for (int i = 0; i < args.Length; i++)
                        {
                Console.WriteLine($"Argument {i}: {args[i]}");
            }
            int numThreads = args.Length > 1 ? int.Parse(args[1]) : 5;
            int numUpdates = args.Length > 2 ? int.Parse(args[2]) : 10_000;

            // TreeMap equivalent in C# is SortedDictionary
            var sortedDictionary = new SortedDictionary<int, int>();

            var threads = new List<Thread>();
            for (int i = 0; i < numThreads; i++)
            {
                int myThreadId = i;
                var thread = new Thread(() =>
                {
                    Console.WriteLine($"{myThreadId}: Started");
                    var random = new Random();
                    for (int j = 0; j < numUpdates; j++)
                    {
                        try
                        {
                            sortedDictionary[random.Next(1000)] = random.Next(1000);
                        }
                        catch (Exception ex)
                        {
                            // Let it keep going to reproduce the issue
                            Console.WriteLine("Exception Message: " + ex.Message);
                        }
                    }
                    Console.WriteLine($"{myThreadId}: Finished");
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
