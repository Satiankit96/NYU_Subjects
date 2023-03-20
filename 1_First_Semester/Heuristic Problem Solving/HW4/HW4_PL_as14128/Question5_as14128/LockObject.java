import java.util.*;

/* Contains the object, in this case, a string
   which will work to put a lock on the consumer and producer
 */
public class LockObject {
    public volatile int[] arr;

    public LockObject() {
        // initialized as empty string
        arr = new int[2];
        arr[0] = 0;
        arr[1] = 0;
    }
}
