package irie.models;

import java.math.BigInteger;
import java.util.*;

public class PrimeRoots {

    public static int generateRoot(int p) {
        int m = p;
        if (isPrime(p)) {
            m = p - 1;
        }
        int primeRoot = 0;
        Map<Integer, Integer> primeFactor = getPrimeFactor(m);
        for (Map.Entry<Integer, Integer> map : primeFactor.entrySet()) {
            primeFactor.put(map.getKey(), m / map.getKey());
        }
        for (int i = 2; i <= m; i++) {
            boolean notPrimeRoot = false;

            for (Map.Entry<Integer, Integer> map : primeFactor.entrySet()) {
                if (BigInteger.valueOf(i).modPow(BigInteger.valueOf(map.getValue()), BigInteger.valueOf(p)).equals(BigInteger.ONE)) {
                    notPrimeRoot = true;
                    break;
                }
            }
            if (!notPrimeRoot) {
                primeRoot = i;
                break;
            }
        }
        return primeRoot;
    }

    public static boolean isPrime(int p) {
        for (int i = 2; i <= Math.sqrt(p); i++) {
            if (p % i == 0) {
                return false;
            }
        }
        return true;
    }

    private static Map<Integer, Integer> getPrimeFactor(int p) {
        Map<Integer, Integer> map = new HashMap<>();
        while (p % 2 == 0) {
            insertToMap(2, map);
            p /= 2;
        }

        for (int i = 3; i <= Math.sqrt(p); i += 2) {
            while (p % i == 0) {
                insertToMap(i, map);
                p /= i;
            }
        }

        if (p > 2)
            insertToMap(p, map);
        return map;
    }

    private static void insertToMap(int i, Map<Integer, Integer> map) {
        map.merge(i, 1, Integer::sum);
    }
}