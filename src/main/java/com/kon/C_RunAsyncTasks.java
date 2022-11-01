package com.kon;
/* async-programming
 * @created 10/31/2022
 * @author Konstantin Staykov
 */

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class C_RunAsyncTasks {
    record Quotation(String server, int amount) {

    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        run();
    }

     static void run() throws ExecutionException, InterruptedException {
        Random random = new Random();
        Supplier<Quotation> fetchQuotationA = () -> {
            try {
                Thread.sleep(random.nextInt(40, 120));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Quotation("Server A", random.nextInt(40, 60));

        };
        Supplier<Quotation> fetchQuotationB = () -> {
            try {
                Thread.sleep(random.nextInt(80, 120));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Quotation("Server B", random.nextInt(30, 70));

        };
        Supplier<Quotation> fetchQuotationC = () -> {
            try {
                Thread.sleep(random.nextInt(80, 120));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Quotation("Server C", random.nextInt(40, 80));

        };

        var quotationList = List.of(fetchQuotationA, fetchQuotationB, fetchQuotationC);

        Instant begin = Instant.now();
        List<CompletableFuture<Quotation>> futures = new ArrayList<>();

        for (Supplier<Quotation> task : quotationList) {
            CompletableFuture<Quotation> future = CompletableFuture.supplyAsync(task);
            futures.add(future);
        }
        List<Quotation> quotations = new ArrayList<>();

        for (CompletableFuture<Quotation> future : futures) {
            Quotation quotation = future.join();
            quotations.add(quotation);
        }
        Quotation bestQuotation = quotations.stream().min(Comparator.comparing(Quotation::amount)).orElseThrow();

        Instant end = Instant.now();
        Duration duration = Duration.between(begin, end);
        System.out.println("Best quotation [ASYNC] = " + bestQuotation + " (" + duration.toMillis() + "ms)");

    }
    
}
