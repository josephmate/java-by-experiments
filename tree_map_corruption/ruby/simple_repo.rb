require 'algorithms'
require 'thread'

include Containers

num_threads = (ARGV[0] || 5).to_i
num_updates = (ARGV[1] || 1000).to_i

tree_map = RedBlackTreeMap.new

threads = []

num_threads.times do
  threads << Thread.new do
    random = Random.new
    num_updates.times do
      begin
        tree_map[random.rand(1000)] = random.rand(1000)
      rescue => e
        puts "Exception: #{e.message}"
      end
    end
  end
end

threads.each(&:join)
