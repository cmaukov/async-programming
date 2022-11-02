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

public class F_ReadingSeveralTasks {
    record Quotation(String server, int amount) {

    }

    record Weather(String server, String weather) {

    }

    record TravelPage(Quotation server, Weather weather) {

    }

    public static void main(String[] args) throws InterruptedException {
        run();
    }

    static void run() throws InterruptedException {
        Random random = new Random();
        List<Supplier<Weather>> weatherTasks = buildWeatherTasks(random);
        List<Supplier<Quotation>> quotationTasks = buildQuotationTasks(random);

        List<CompletableFuture<Weather>> weatherCFs = new ArrayList<>();
        for (Supplier<Weather> task : weatherTasks) {
            CompletableFuture<Weather> future = CompletableFuture.supplyAsync(task);
            weatherCFs.add(future);
        }
        CompletableFuture<Weather> anyWeather =
                CompletableFuture.anyOf(weatherCFs.toArray(CompletableFuture[]::new))
                        .thenApply(o -> (Weather) o);


        List<CompletableFuture<Quotation>> quotationCFs = new ArrayList<>();
        for (Supplier<Quotation> task : quotationTasks) {
            CompletableFuture<Quotation> future = CompletableFuture.supplyAsync(task);
            quotationCFs.add(future);
        }

        CompletableFuture<Void> allOfQuotations = CompletableFuture.allOf(quotationCFs.toArray(CompletableFuture[]::new));
        CompletableFuture<Quotation> bestQuotation = allOfQuotations.thenApply(v -> quotationCFs.stream()
                .map(CompletableFuture::join)
                .min(Comparator.comparing(Quotation::amount))
                .orElseThrow());
// Two patterns we can use
        // Pattern one
        CompletableFuture<TravelPage> pageCompletableFuture = bestQuotation.thenCombine(
                CompletableFuture.anyOf(weatherCFs.toArray(CompletableFuture[]::new))
                        .thenApply(o -> (Weather) o), TravelPage::new);
        pageCompletableFuture.thenAccept(System.out::println).join();
// or pattern two
        CompletableFuture<TravelPage> pageCompletableFuture2 = bestQuotation.thenCompose(
                quotation -> anyWeather
                        .thenApply(o -> (Weather) o)
                        .thenApply(weather -> new TravelPage(quotation, weather))
        );
        pageCompletableFuture2.thenAccept(System.out::println).join();
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

        return new ArrayList<>(List.of(fetchQuotationA, fetchQuotationB, fetchQuotationC));
    }

    private static List<Supplier<Quotation>> buildQuotationTasks(Random random) {

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
        return new ArrayList<>(List.of(fetchQuotationA, fetchQuotationB, fetchQuotationC));
    }

}
