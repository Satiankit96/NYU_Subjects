
/**
 * BoundedBuffer.java
 *
 * This program implements the bounded buffer using shared memory.
 * Note that this solution is thread-safe - notice the usage of
 * AtomicInteger (read about it here - 
 * https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/atomic/AtomicInteger.html)
 * 
 * @author Abhishek Verma
 */

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BoundedBuffer {
    public BoundedBuffer() {
        // buffer is initially empty
        count = new AtomicInteger(); // count = 0
        in = 0;
        out = 0;
        buffer = new Object[BUFFER_SIZE];
    }

    // producer calls this method
    public void enter(Object item) {
        while (count.get() == BUFFER_SIZE)
            ; // do nothing


        // add an item to the buffer
        count.incrementAndGet();
        buffer[in] = item;
        in = (in + 1) % BUFFER_SIZE;
    }

    // consumer calls this method
    public Object remove() {
        Object item;

        while (count.get() == 0)
            ; // do nothing

        // remove an item from the buffer
        count.decrementAndGet();
        item = buffer[out];
        out = (out + 1) % BUFFER_SIZE;

        return item;
    }

    public static final int NAP_TIME = 5;
    private static final int BUFFER_SIZE = 1;

    private volatile AtomicInteger count; // thread safe variable for keeping the count of number of items in the buffer
    private int in; // points to the next free position in the buffer
    private int out; // points to the next full position in the buffer
    private Object[] buffer;
}
