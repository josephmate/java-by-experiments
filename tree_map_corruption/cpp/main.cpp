#include <iostream>
#include <vector>
#include <thread>
#include <map>
#include <random>

int main(int argc, char* argv[]) {
    int numThreads = (argc >= 2) ? std::stoi(argv[1]) : 5;
    int numUpdates = (argc >= 3) ? std::stoi(argv[2]) : 100;
		int progressRatio = 10;

    // Equivalent of TreeMap in C++ is std::map (sorted by keys)
    std::map<int, int> sortedMap;

    std::vector<std::thread> threads;
		std::atomic<int> threadIdGenerator(0);

    for (int i = 0; i < numThreads; i++) {
				// copy so that we don't use the latest value of i (5)
        threads.emplace_back([&]() {
						int threadId = threadIdGenerator.fetch_add(1);
            std::random_device rd;
            std::mt19937 gen(rd());
            std::uniform_int_distribution<> dis(0, 999);
						
						int progressThreshold = numUpdates / progressRatio;
						std::cout << "Thread " << threadId << " started.";
            for (int j = 0; j < numUpdates; j++) {
                try {
                    int key = dis(gen);
                    int value = dis(gen);

                    sortedMap[key] = value;
                } catch (const std::exception& e) {
										std::cerr << "Caught exception on thread " << threadId << ":" << e.what() << std::endl;
                }

								if ((j + 1) % progressThreshold == 0) {
										std::cout << "Thread " << threadId << ": " 
												<< ((j + 1) * 100 / numUpdates) << "% complete (" 
												<< (j + 1) << "/" << numUpdates << " updates)\n";
								}
            }
						std::cout << "Thread " << threadId << " completed.";
        });
    }

    // Start all threads
    for (auto& thread : threads) {
					thread.join();
    }

    return 0;
}

