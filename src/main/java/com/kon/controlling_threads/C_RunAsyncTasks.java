package com.kon.controlling_threads;
/* async-programming
 * @created 11/01/2022
 * @author Konstantin Staykov
 */

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class C_RunAsyncTasks {
    record Quotation(String server, int amount) {
    }

    public static void main(String[] args) {
        run();
    }

    public static void run() {

        ExecutorService executor = Executors.newFixedThreadPool(4);
        Random random = new Random();

        List<Supplier<Quotation>> quotationTasks = buildQuotationTasks(random);

        List<CompletableFuture<Quotation>> futures = new ArrayList<>();
        for (Supplier<Quotation> quotationTask : quotationTasks) {
            var future =
                    CompletableFuture.supplyAsync(quotationTask);
            futures.add(future);
        }

        List<Quotation> quotations = new ArrayList<>();
        for (CompletableFuture<Quotation> future : futures) {
            Quotation quotation = future.join();
            quotations.add(quotation);
        }

        Quotation bestQuotation =
                quotations.stream()
                        .min(Comparator.comparing(Quotation::amount))
                        .orElseThrow();

        System.out.println("Best quotation = " + bestQuotation);
    }

    private static List<Supplier<Quotation>> buildQuotationTasks(Random random) {
        Supplier<Quotation> fetchQuotationA =
                () -> {
                    try {
                        Thread.sleep(random.nextInt(80, 120));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Quotation A in thread " + Thread.currentThread() );
                    return new Quotation("Server A", random.nextInt(40, 60));
                };
        Supplier<Quotation> fetchQuotationB =
                () -> {
                    try {
                        Thread.sleep(random.nextInt(80, 120));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Quotation B in thread " + Thread.currentThread() );
                    return new Quotation("Server B", random.nextInt(30, 70));
                };
        Supplier<Quotation> fetchQuotationC =
                () -> {
                    try {
                        Thread.sleep(random.nextInt(80, 120));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Quotation C in thread " + Thread.currentThread() );
                    return new Quotation("Server C", random.nextInt(40, 80));
                };

        var quotationTasks =
                List.of(fetchQuotationA, fetchQuotationB, fetchQuotationC);
        return quotationTasks;
    }

    private static Quotation openFuture(Future<Quotation> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
