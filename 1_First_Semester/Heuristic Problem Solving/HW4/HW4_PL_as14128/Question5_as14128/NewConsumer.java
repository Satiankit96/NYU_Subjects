import java.util.*;

/*This is the consumer class used by the code */
public class NewConsumer extends Thread {

    public static int MAX_CONSUME_AMOUNT = 12;
    public static String NAME_OF_BUFFER_1 = "Moira";
    public static String NAME_OF_BUFFER_2 = "Alice";

    private BoundedBuffer buffer1, buffer2;
    private String name;
    private int currentConsumed;
    LockObject lockObject;

    public NewConsumer(BoundedBuffer b1, BoundedBuffer b2, String n, LockObject l) {
        buffer1 = b1;
        buffer2 = b2;
        name = n;
        currentConsumed = 1;
        lockObject = l;
    }

    @Override
    public void run() {
        while (currentConsumed <= MAX_CONSUME_AMOUNT) {
            synchronized (lockObject) {
                while (lockObject.arr[0] == 0 && lockObject.arr[1] == 0) {
                    try {
                        lockObject.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // it is not empty, decide which one
                if (lockObject.arr[0] == 1) {
                    consumeFromBuffer1();
                    lockObject.arr[0] = 0;
                } else {
                    consumeFromBuffer2();
                    lockObject.arr[1] = 0;
                }
                currentConsumed++;
                lockObject.notifyAll();
            }
        }
        System.out.println("Consumer " + name + " EXIT");
    }

    private void consumeFromBuffer1() {
        int data = (int) buffer1.remove();
        System.out.println("Producer " + name + " consumed " + data + " produced by producer Moira");
    }

    private void consumeFromBuffer2() {
        int data = (int) buffer2.remove();
        System.out.println("Producer " + name + " consumed " + data + " produced by producer Alice");
    }
}