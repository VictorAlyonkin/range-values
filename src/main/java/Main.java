import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        int maxValue = 0;
        String[] texts = new String[25];
        List<Future> futures = new ArrayList<>();
        ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }

        for (String text : texts) {
            Callable<Integer> callable = () -> {
                int maxSize = 0;
                for (int i = 0; i < text.length(); i++) {
                    for (int j = 0; j < text.length(); j++) {
                        if (i >= j) {
                            continue;
                        }
                        boolean bFound = false;
                        for (int k = i; k < j; k++) {
                            if (text.charAt(k) == 'b') {
                                bFound = true;
                                break;
                            }
                        }
                        if (!bFound && maxSize < j - i) {
                            maxSize = j - i;
                        }
                    }
                }
                System.out.println(text.substring(0, 100) + " -> " + maxSize);
                return maxSize;
            };
            Future<Integer> future = threadPool.submit(callable);
            futures.add(future);
        }

        long startTs = System.currentTimeMillis(); // start time
        for (Future future : futures) {
            int futureValue = Integer.parseInt(future.get().toString());
            maxValue = Integer.max(maxValue, futureValue);
        }

        System.out.println("Максимальный интервал значений среди всех строк = " + maxValue + ".");

        threadPool.shutdown();

        long endTs = System.currentTimeMillis(); // end time

        System.out.println("Time: " + (endTs - startTs) + "ms");
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}