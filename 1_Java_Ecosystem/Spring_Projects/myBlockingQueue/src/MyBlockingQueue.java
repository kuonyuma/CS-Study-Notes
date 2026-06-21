import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class MyBlockingQueue {
	private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

	public static void main(String[] args) throws InterruptedException {
		log("===== 演示A：队列为空时，take() 会阻塞 =====");
		demoTakeBlocking();

		log("\n===== 演示B：队列已满时，put() 会阻塞 =====");
		demoPutBlocking();

		log("\n演示结束。");
	}

	private static void demoTakeBlocking() throws InterruptedException {
		BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(1);

		// 先启动消费者，让它在空队列上调用 take() 并进入阻塞。
		Thread consumer = new Thread(() -> {
			try {
				log("消费者调用 take()，队列为空 -> 进入阻塞");
				Integer value = queue.take();
				log("消费者恢复执行，拿到数据：" + value);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}, "consumer");

		// 生产者延迟 1 秒后 put，解除消费者的阻塞。
		Thread producer = new Thread(() -> {
			try {
				Thread.sleep(1000);
				log("生产者放入 100");
				queue.put(100);
				log("生产者 put 完成");
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}, "producer");

		consumer.start();
		producer.start();
		consumer.join();
		producer.join();
	}

	private static void demoPutBlocking() throws InterruptedException {
		BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(1);
		queue.put(1);
		log("主线程先放入 1（队列已满）");

		// 队列已满时，put() 会阻塞，直到有消费者 take()。
		Thread producer = new Thread(() -> {
			try {
				log("生产者调用 put(2)，队列已满 -> 进入阻塞");
				queue.put(2);
				log("生产者恢复执行，put(2) 完成");
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}, "producer");

		Thread consumer = new Thread(() -> {
			try {
				Thread.sleep(1000);
				log("消费者取走一个元素");
				Integer value = queue.take();
				log("消费者拿到数据：" + value);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}, "consumer");

		producer.start();
		consumer.start();
		producer.join();
		consumer.join();

		log("主线程最后再取一个元素：" + queue.take());
	}

	private static void log(String msg) {
		System.out.println("[" + LocalTime.now().format(TIME_FMT) + "] [" + Thread.currentThread().getName() + "] " + msg);
	}
}

