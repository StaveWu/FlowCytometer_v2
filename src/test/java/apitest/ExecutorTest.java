package apitest;

import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

public class ExecutorTest {

    public static void main(String[] args) throws InterruptedException {
        BlockingDeque<Integer> queue = new LinkedBlockingDeque<>();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            while (true) {
                try {
                    Integer num = queue.take();
                    System.out.println(num);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        for (int i = 0; i < 10; i++) {
            queue.put(i);
        }
//        Thread.sleep(10);
    }
}
