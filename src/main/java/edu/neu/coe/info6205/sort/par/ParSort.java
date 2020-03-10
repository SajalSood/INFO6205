package edu.neu.coe.info6205.sort.par;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

/**
 * This code has been fleshed out by Ziyao Qiao. Thanks very much.
 * TODO tidy it up a bit.
 */
class ParSort {

    public static int cutoff = 1000;
    public static int threads = 0;

    public static void sort(int[] array, int from, int to) {
        if (to - from < cutoff) Arrays.sort(array, from, to);
        else {
            CompletableFuture<int[]> parsort1 = parsort(array, from, from + (to - from) / 2); // TO IMPLEMENT
            CompletableFuture<int[]> parsort2 = parsort(array, from + (to - from) / 2, to); // TO IMPLEMENT
            CompletableFuture<int[]> parsort = parsort1.thenCombine(parsort2, (xs1, xs2) -> {
                        int[] result = new int[xs1.length + xs2.length];
                        // TO BE IMPLEMENTED ...
                        int i = 0, j = 0, k = 0;
                        int lengthOfXS1 = xs1.length;
                        int lengthOfXS2 = xs2.length;
                        while (i < lengthOfXS1 && j < lengthOfXS2) {
                            if (xs1[i] <= xs2[j]) {
                                result[k] = xs1[i];
                                i++;
                            } else if (xs1[i] > xs2[j]) {
                                result[k] = xs2[j];
                                j++;
                            }
                            k++;
                        }
                        while (i < lengthOfXS1) {
                            result[k] = xs1[i];
                            k++; i++;
                        }
                        while (j < lengthOfXS2) {
                            result[k] = xs2[j];
                            k++; j++;
                        }
                        for (int p = 0; p < result.length; p++) {
                            array[p + from] = result[p];
                        }
                        // ... END IMPLEMENTATION
                        return result;
                    });

            parsort.whenComplete((result, throwable) -> System.arraycopy(result, 0, array, from, result.length));
            //System.out.println("# threads: "+ ForkJoinPool.commonPool().getRunningThreadCount());
            parsort.join();
        }
    }

    private static CompletableFuture<int[]> parsort(int[] array, int from, int to) {
        threads = threads + 1;
        return CompletableFuture.supplyAsync(
                () -> {
                    int[] result = new int[to - from];
                    // TO BE IMPLEMENTED ...
                    sort(array, from, to);
                    for (int i = from; i < to; i++) {
                        result[i - from] = array[i];
                    }
                    // ... END IMPLEMENTATION
                    return result;
                }
        );
    }
}