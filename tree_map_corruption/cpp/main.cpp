#include <iostream>
#include <vector>
#include <thread>
#include <map>
#include <random>

int main(int argc, char* argv[]) {
    int numThreads = (argc >= 2) ? std::stoi(argv[1]) : 5;
    int numUpdates = (argc >= 3) ? std::stoi(argv[2]) : 1000;

    // Equivalent of TreeMap in C++ is std::map (sorted by keys)
    std::map<int, int> sortedMap;

    std::vector<std::thread> threads;

    for (int i = 0; i < numThreads; i++) {
        threads.emplace_back([&]() {
            std::random_device rd;
            std::mt19937 gen(rd());
            std::uniform_int_distribution<> dis(0, 999);

            for (int j = 0; j < numUpdates; j++) {
                try {
                    int key = dis(gen);
                    int value = dis(gen);

                    sortedMap[key] = value;
                } catch (const std::exception& e) {
                    // Let it keep going to reproduce the issue
                }
            }
        });
    }

    // Start all threads
    for (auto& thread : threads) {
					thread.join();
    }

    return 0;
}

