package com.kon;
/* async-programming
 * @created 10/31/2022
 * @author Konstantin Staykov
 */

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class E_ReadingOneOfSeveralTasks {
    record Weather(String server,String weather) {

    }

    public static void main(String[] args) throws  InterruptedException {
        run();
    }

    static void run() throws InterruptedException {
        Random random = new Random();
        List<Supplier<Weather>> weatherTasks     = buildWeatherTasks(random);
        List<CompletableFuture<Weather>> futures = new ArrayList<>();
        for (Supplier<Weather> task : weatherTasks) {
            CompletableFuture<Weather> future = CompletableFuture.supplyAsync(task);
            futures.add(future);
        }

        CompletableFuture<Object> future = CompletableFuture.anyOf(futures.toArray(CompletableFuture[]::new));
        future.thenAccept(System.out::println).join();

    }

    private static List<Supplier<Weather>> buildWeatherTasks(Random random) {

        Supplier<Weather> fetchQuotationA = () -> {
            try {
                Thread.sleep(random.nextInt(40, 120));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Weather("Server A", "Sunny");

        };
        Supplier<Weather> fetchQuotationB = () -> {
            try {
                Thread.sleep(random.nextInt(80, 120));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Weather("Server B", "Cloudy");

        };
        Supplier<Weather> fetchQuotationC = () -> {
            try {
                Thread.sleep(random.nextInt(80, 120));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return new Weather("Server C", "Rainy");

        };

        return new ArrayList<>(List.of(fetchQuotationA,fetchQuotationB,fetchQuotationC));
    }

}
