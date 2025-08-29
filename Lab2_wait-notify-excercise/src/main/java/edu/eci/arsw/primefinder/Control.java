package edu.eci.arsw.primefinder;

public class Control extends Thread {

    private final static int NTHREADS = 3;
    private final static int MAXVALUE = 300000;
    private final static int TMILISECONDS = 5000;

    private final int NDATA = MAXVALUE / NTHREADS;
    private final PrimeFinderThread[] pft;

    /* Lock para sincronización */
    private final Object lock = new Object();
    /* Variable para controlar el estado de pausa */
    private volatile boolean paused = false;

    private Control() {
        super();
        this.pft = new PrimeFinderThread[NTHREADS];

        int i;
        for (i = 0; i < NTHREADS - 1; i++) {
            PrimeFinderThread elem = new PrimeFinderThread(i * NDATA, (i + 1) * NDATA, lock, this);
            pft[i] = elem;
        }
        pft[i] = new PrimeFinderThread(i * NDATA, MAXVALUE + 1, lock, this);
    }

    public static Control newControl() {
        return new Control();
    }

    /* Método run del hilo controlador */
    @Override
    public void run() {
        for (PrimeFinderThread thread : pft) {
            thread.start();
        }

        try {
            while (true) {
                Thread.sleep(TMILISECONDS);

                synchronized (lock) {
                    paused = true;
                    System.out.println("\n⏸ Todos los hilos pausados.");
                    int total = 0;
                    for (PrimeFinderThread thread : pft) {
                        total += thread.getPrimes().size();
                    }
                    System.out.println("Primos encontrados hasta ahora: " + total);
                    System.out.println("Presiona ENTER para continuar...");
                    lock.wait(); // esperar ENTER
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /* Método para reanudar los hilos */
    public void resumeThreads() {
        synchronized (lock) {
            paused = false;
            lock.notifyAll();
        }
    }

    /* Getter del estado de pausa */
    public boolean isPaused() {
        return paused;
    }
}
