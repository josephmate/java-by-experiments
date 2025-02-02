require 'algorithms'
require 'concurrent'
require 'thread'

include Containers

num_threads = (ARGV[0] || 10).to_i
num_updates = (ARGV[1] || 10000000).to_i
barrier = Concurrent::CyclicBarrier.new(num_threads)

tree_map = RBTreeMap.new

threads = []

for thread_id in 1..num_threads
  thread = Thread.new(thread_id) do |my_thread_id|
    puts "#{my_thread_id}: waiting for other threads to start"
    $stdout.flush
    barrier.wait
    puts "#{my_thread_id}: started"
    $stdout.flush
    random = Random.new
    for update_num in 1..num_updates do
      begin
        tree_map[random.rand(1000)] = random.rand(1000)
      rescue => e
        puts "Exception: #{e.message}"
				$stdout.flush
      end
    end
    puts "#{my_thread_id}: finished"
    $stdout.flush
  end
  threads.push(thread)
end

for thread in threads do
  thread.join
end

puts "Done"
