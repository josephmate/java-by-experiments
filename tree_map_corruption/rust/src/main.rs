use rbtree::RBTree;
use std::sync::Arc;
use std::thread;
use rand::Rng;

fn main() {
    let args: Vec<String> = std::env::args().collect();

    let num_threads = if args.len() >= 2 {
        args[1].parse::<usize>().unwrap()
    } else {
        5
    };

    let num_updates = if args.len() >= 3 {
        args[2].parse::<usize>().unwrap()
    } else {
        1000
    };

    let tree = Arc::new(RBTree::new());

    let mut handles = vec![];

    for _ in 0..num_threads {
        let tree = Arc::clone(&tree);
        let handle = thread::spawn(move || {
            let mut rng = rand::thread_rng();
            for _ in 0..num_updates {
                let key = rng.gen_range(0..1000);
                let value = rng.gen_range(0..1000);
                tree.insert(key, value);
            }
        });
        handles.push(handle);
    }

    for handle in handles {
        handle.join().unwrap();
    }
}
