package irie.models;

import java.util.Random;

public class PrimeNumbers {

    private int p, g;

    public PrimeNumbers() {
        p = generatePrime();
        g = PrimeRoots.generateRoot(p);
    }

    public int getP() {
        return p;
    }

    public int getG() {
        return g;
    }

    /**
     * Checks to see if the requested value is prime.
     */

    private static int generatePrime() {
        Random rand = new Random(); // generate a random number
        int num = rand.nextInt(1000) + 1;

        while (!PrimeRoots.isPrime(num)) {
            num = rand.nextInt(1000) + 1;
        }

        return num;
    }
}