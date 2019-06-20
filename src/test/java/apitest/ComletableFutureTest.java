package apitest;

import java.util.concurrent.CompletableFuture;

public class ComletableFutureTest {

    public static void main(String[] args) {
        CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("task done");
        });
        System.out.println("just start a thread before");
    }
}
