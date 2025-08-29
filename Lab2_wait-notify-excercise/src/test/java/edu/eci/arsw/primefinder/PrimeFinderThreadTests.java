package edu.eci.arsw.primefinder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class PrimeFinderThreadTests {

    Object lock = new Object();
    Control dummyControl = Control.newControl();

    @Test
    void testPrimeFinderThreadFindsPrimes() throws InterruptedException {
        PrimeFinderThread pft = new PrimeFinderThread(1, 50, lock, dummyControl);
        pft.start();
        pft.join();

        assertTrue(pft.getPrimes().contains(2));
        assertTrue(pft.getPrimes().contains(47));
        assertFalse(pft.getPrimes().contains(1));
    }

    @Test
    void testControlStartsAndPauses() throws InterruptedException {
        Control control = Control.newControl();
        control.start();

        // Esperar un poco a que llegue a la primera pausa
        Thread.sleep(6000);

        assertTrue(control.isPaused(), "El control debería estar pausado después de 5s");
    }

    @Test
    void testControlResumesAfterPause() throws InterruptedException {
        Control control = Control.newControl();
        control.start();

        // Esperar hasta que pause
        Thread.sleep(6000);
        assertTrue(control.isPaused());

        // Reanudar
        control.resumeThreads();
        Thread.sleep(1000);

        assertFalse(control.isPaused(), "El control debería reanudarse después de llamar resumeThreads()");
    }

    @Test
    void testMultipleThreadsDivideWorkCorrectly() throws InterruptedException {
        PrimeFinderThread pft1 = new PrimeFinderThread(1, 1000, lock, dummyControl);
        PrimeFinderThread pft2 = new PrimeFinderThread(1001, 2000, lock, dummyControl);

        pft1.start();
        pft2.start();

        pft1.join();
        pft2.join();

        assertFalse(pft1.getPrimes().isEmpty());
        assertFalse(pft2.getPrimes().isEmpty());
        assertTrue(pft1.getPrimes().stream().allMatch(p -> p <= 1000));
        assertTrue(pft2.getPrimes().stream().allMatch(p -> p > 1000));
    }
}
