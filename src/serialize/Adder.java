package serialize;

import java.io.Serializable;
import java.util.Random;

public class Adder extends Thread {

    private Circular<Serializable> circularBuffor;
    private Random random;


    //dodanie elemntu do bufora
    public void put() throws InterruptedException {
        int value = random.nextInt(1500);
        circularBuffor.put(value);
        System.out.println("Put:" + value);
    }


    public Adder(Circular pointer) {
        circularBuffor = pointer;
        random = new Random();
    }

    @Override
    public void run() {
        while(true) {
            try {
                put();
                Thread.sleep(random.nextInt(1000)); }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
