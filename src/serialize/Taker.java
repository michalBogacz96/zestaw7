package serialize;
import java.io.Serializable;

public class Taker extends Thread {
    private Circular<Serializable> circularBuffer;
    public <T> void get() throws InterruptedException {
        T val = (T) circularBuffer.take();
        System.out.println("Add:" + val); }
    public Taker(Circular circular) {
        circularBuffer = circular;}
    public void run() {
        while(true) {
            try {
                get();
                Thread.sleep(700);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
