import java.util.*;

public class Server {
    public static void main(String args[]) {
        
        LockObject lock = new LockObject();

        //1st buffer is Moira
        //2nd buffer is Alice

        BoundedBuffer moiraBuffer = new BoundedBuffer();
        NewProducer moira = new NewProducer(moiraBuffer, "Moira", lock, 0);

        BoundedBuffer aliceBuffer = new BoundedBuffer();
        NewProducer alice = new NewProducer(aliceBuffer, "Alice", lock, 1);

        NewConsumer johnny = new NewConsumer(moiraBuffer, aliceBuffer, "Johnny", lock);

        johnny.start();
        moira.start();
        alice.start();
    }
}
