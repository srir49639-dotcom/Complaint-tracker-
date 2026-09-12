package algorithms.randomized;

import java.util.Random;

public class RandomizedPolynomialHash {

    private static final long DEFAULT_MOD = 1000000009L;
    private final long base;
    private final long mod;

    public RandomizedPolynomialHash() {
        Random rand = new Random();
        // Pick random base between 257 and 10000
        this.base = 257 + rand.nextInt(9743);
        this.mod = DEFAULT_MOD;
    }

    public RandomizedPolynomialHash(long base, long mod) {
        this.base = base;
        this.mod = mod;
    }

    public long hash(String text) {
        if (text == null) return 0;
        long h = 0;
        for (int i = 0; i < text.length(); i++) {
            h = (h * base + Character.toLowerCase(text.charAt(i))) % mod;
        }
        return h;
    }

    public long getBase() { return base; }
    public long getMod() { return mod; }

    public String getName() {
        return "Randomized Universal Polynomial Hash (Rabin Fingerprint)";
    }
}
