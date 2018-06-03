package utils.multithreading;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CompletableFutureHelper {
    public static void waitForAllOf(List<CompletableFuture> futuresList) {
        CompletableFuture.allOf(futuresList.toArray(new CompletableFuture[0])).join();
    }
}
