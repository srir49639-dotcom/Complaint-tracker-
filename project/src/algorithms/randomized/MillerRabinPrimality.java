package algorithms.randomized;

import java.math.BigInteger;
import java.util.Random;

public class MillerRabinPrimality {

    private final Random random = new Random();

    /**
     * Probabilistic Miller-Rabin Primality test.
     * Used for cryptographic security token verification and prime modulus validation.
     * Error probability <= (1/4)^k for k rounds.
     */
    public boolean isPrime(long n, int k) {
        if (n <= 1 || n == 4) return false;
        if (n <= 3) return true;
        if (n % 2 == 0) return false;

        // Find d such that n - 1 = d * 2^r with d odd
        long d = n - 1;
        while (d % 2 == 0) {
            d /= 2;
        }

        for (int i = 0; i < k; i++) {
            if (!millerTest(d, n)) {
                return false;
            }
        }
        return true;
    }

    private boolean millerTest(long d, long n) {
        // Pick random number in [2, n - 2]
        long a = 2 + (long) (random.nextDouble() * (n - 4));
        long x = modularExponentiation(a, d, n);

        if (x == 1 || x == n - 1) return true;

        while (d != n - 1) {
            x = (x * x) % n;
            d *= 2;

            if (x == 1) return false;
            if (x == n - 1) return true;
        }

        return false;
    }

    private long modularExponentiation(long base, long exp, long mod) {
        long result = 1;
        base = base % mod;
        while (exp > 0) {
            if ((exp & 1) == 1) {
                result = (result * base) % mod;
            }
            exp >>= 1;
            base = (base * base) % mod;
        }
        return result;
    }

    public String getName() {
        return "Miller-Rabin Primality Test";
    }

    public String getTimeComplexity() {
        return "O(k * log^3 N)";
    }
}
