import java.util.*;

/* This is the Producer Class used
   same for Moira and Alice
*/
public class NewProducer extends Thread {
    public static final int MIN_RANGE = 4000;
    public static final int MAX_RANGE = 60000;

    public static final int MIN_SLEEP_TIME = 1;
    public static final int MAX_SLEEP_TIME = 3;

    public static final int MAX_OUTPUT = 6;

    private BoundedBuffer buffer;
    private String name;
    LockObject lockObject;
    int currentCount;
    int index;

    public NewProducer(BoundedBuffer q, String producerName, LockObject l, int idx) {
        buffer = q;
        name = producerName;
        lockObject = l;
        currentCount = 1;
        index  = idx;   
    }

    // 0 - Moira
    // 1 - Alice
    // arr[1] = 1;

    @Override
    public void run() {
        while (currentCount <= MAX_OUTPUT) {

            // print name and sleep time
            int sleepTime = (int) (Math.random() * (MAX_SLEEP_TIME + 1 - MIN_SLEEP_TIME)) + MIN_SLEEP_TIME;
            System.out.println("Producer " + name + " sleeping for " + sleepTime + " seconds");
            try {
                Thread.sleep(sleepTime * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // print the value to be produced
            int data = (int) (Math.random() * (MAX_RANGE - MIN_RANGE)) + MIN_RANGE;

            // print data to be input
            System.out.println("Producer " + name + " produced " + data);

            synchronized (lockObject) {
                while (!(lockObject.arr[index] == 0)) {
                    try {
                        lockObject.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // inside, can push to buffer
                buffer.enter(data);
                currentCount++;
                // set name for consumption by the consumer
                lockObject.arr[index] = 1;
                lockObject.notifyAll();
            }
        }

        // At the end, print EXIT statement
        System.out.println("Producer " + name + " EXIT");
    }
}