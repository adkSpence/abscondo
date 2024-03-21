package irie.models;

import java.util.Random;

public class DiffieHellman {

    PrimeNumbers primeNumbers = new PrimeNumbers();
    Random random = new Random();

    public int generateSharedSecret() {

        int shared_secret;

        /*
        Setting up parameters for Diffie Hellman key exchange
        p: Prime number
        g: Primitive root of p
        secret_a: First secret number
        secret_b: Second secret number
         */

        while (true) {
            int p = primeNumbers.getP();
            int g = primeNumbers.getG();
            int secret_a = random.nextInt(50) + 1;
            int secret_b = random.nextInt(50) + 1;

            int A = (int) (Math.pow(g, secret_a) % p);
            int B = (int) (Math.pow(g, secret_b) % p);

            // Final exchanges here
            int final_exchange_A = (int) (Math.pow(B, secret_a) % p);
            int final_exchange_B = (int) (Math.pow(A, secret_b) % p);

            if(final_exchange_A == final_exchange_B){
                shared_secret = final_exchange_A;
                break;
            }
        }

        return shared_secret;
    }
}