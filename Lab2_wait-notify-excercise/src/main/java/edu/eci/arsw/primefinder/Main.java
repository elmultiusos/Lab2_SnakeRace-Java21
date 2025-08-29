package edu.eci.arsw.primefinder;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Control control = Control.newControl();
        control.start();

        while (true) {
            System.in.read(); // esperar ENTER
            control.resumeThreads();
        }
    }
}
