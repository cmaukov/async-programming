package com.kon;
/* async-programming
 * @created 10/31/2022
 * @author Konstantin Staykov
 */

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class B_RunExecutorTasks {
    record Quotation(String server, int amount) {

    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        run();
    }

     static void run() throws ExecutionException, InterruptedException {
        Random random = new Random();
        Callable<Quotation> fetchQuotationA = () -> {
            Thread.sleep(random.nextInt(40, 120));
            return new Quotation("Server A", random.nextInt(40, 60));

        };
        Callable<Quotation> fetchQuotationB = () -> {
            Thread.sleep(random.nextInt(80, 120));
            return new Quotation("Server B", random.nextInt(30, 70));

        };
        Callable<Quotation> fetchQuotationC = () -> {
            Thread.sleep(random.nextInt(80, 120));
            return new Quotation("Server C", random.nextInt(40, 80));

        };

        var quotationList = List.of(fetchQuotationA, fetchQuotationB, fetchQuotationC);
        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Future<Quotation>> futures = new ArrayList<>();
        Instant begin = Instant.now();

        for (Callable<Quotation> task : quotationList) {
            Future<Quotation> future = executor.submit(task);
            futures.add(future);
        }

        List<Quotation> quotations = new ArrayList<>();
        for (Future<Quotation> future : futures) {
            Quotation quotation = future.get();
            quotations.add(quotation);
        }
        Quotation bestQuotation = quotations.stream()
                .min(Comparator.comparing(Quotation::amount))
                .orElseThrow();

        Instant end = Instant.now();
        Duration duration = Duration.between(begin, end);
        System.out.println("Best quotation [ES] = " + bestQuotation + " (" + duration.toMillis() + "ms)");
        executor.shutdown();
    }

}
