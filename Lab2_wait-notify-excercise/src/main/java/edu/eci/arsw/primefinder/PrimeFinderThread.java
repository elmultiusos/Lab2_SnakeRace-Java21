package edu.eci.arsw.primefinder;

import java.util.LinkedList;
import java.util.List;

public class PrimeFinderThread extends Thread {

    private final int a, b;
    private final List<Integer> primes;

    private final Object lock;
    private final Control control;

    public PrimeFinderThread(int a, int b, Object lock, Control control) {
        this.a = a;
        this.b = b;
        this.lock = lock;
        this.control = control;
        this.primes = new LinkedList<>();
    }

    @Override
    public void run() {
        for (int i = a; i < b; i++) {
            synchronized (lock) {
                while (control.isPaused()) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (isPrime(i)) {
                primes.add(i);
            }
        }
    }

    private boolean isPrime(int n) {
        if (n < 2) {
            return false;
        }
        if (n == 2) {
            return true;
        }
        if (n % 2 == 0) {
            return false;
        }
        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    public List<Integer> getPrimes() {
        return primes;
    }
}
