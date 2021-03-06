package Analyzer;

import Model.Bucket;
import Model.Buckets;
import Model.Stacktrace;

import java.util.concurrent.CountDownLatch;

/**
 * Created by Junior on 02-11-16.
 */
public class SimilarityAnalyzer extends Analyzer {

    public SimilarityAnalyzer(Buckets buckets) {
        super(buckets);
    }

    public SimilarityAnalyzer() {
        super();
    }

    private Bucket bucketToReturn;

    @Override
    public Bucket searchBucket(Stacktrace stackTrace) {
        bucketToReturn = new Bucket("0");
        CountDownLatch latch = new CountDownLatch(buckets.size());

        final int[] stacktraceAnalyzed = {0};
        final double[] result = {0};

        for (Bucket bucket : this.buckets) {
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    int stacktraceNumber = bucket.size();
                    double globalSimilarity = 0;

                    for (Stacktrace stacktrace : bucket) {
                        double simila = similarity(stackTrace.getFullString(), stacktrace.getFullString());
                        //System.out.print(simila);
                        globalSimilarity += simila;
                        stacktraceAnalyzed[0]++;
                        System.out.print(".");
                    }

                    if (result[0] < (globalSimilarity / stacktraceNumber)) {
                        //System.out.print(result[0] + "  Replaced by  " + (globalSimilarity / stacktraceNumber) );
                        result[0] = (globalSimilarity / stacktraceNumber);
                        bucketToReturn = bucket;
                    }

                    //System.out.print(" Global : " + globalSimilarity / stacktraceNumber);
                    //System.out.println("Stacktrace " + stackTrace.getStackTraceNumber() + "(Bucket "+ bucket.getBucketNumber() +") : " + ((stacktraceAnalyzed[0] /buckets.getTotalStackTrace())*100));
                    latch.countDown();
                }
            });
            thread.start();
        }

        try{
            latch.await();
        }catch(InterruptedException ie) {
            ie.printStackTrace();
        }

        return bucketToReturn;
    }


    /**
     * Calculates the similarity (a number within 0 and 1) between two strings.
     */
    public static double similarity(String s1, String s2) {
        if(s1.length() < 1 || s2.length() < 1)
            return 0;

        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) { // longer should always have greater length
            longer = s2;
            shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) {
            return 1.0; /* both strings are zero length */
        }
    /* // If you have StringUtils, you can use it to calculate the edit distance:
    return (longerLength - StringUtils.getLevenshteinDistance(longer, shorter)) /
                               (double) longerLength; */
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;

    }

    // Example implementation of the Levenshtein Edit Distance
    // See http://rosettacode.org/wiki/Levenshtein_distance#Java
    public static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }



}
