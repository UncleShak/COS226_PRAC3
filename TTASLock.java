import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class TTASLock 
{

    private final AtomicBoolean locked = new AtomicBoolean(false);
    private final AtomicLong testAndSetCount = new AtomicLong(0);

    /* Do not modify this method */
    private boolean testAndSet() 
    {
        return locked.getAndSet(true);
    }

    /* Task 2 - Optimised TTAS implementation.
     * Before attempting the atomic testAndSet(), we first spin on a plain
     * read of 'locked' via get(). This read doesn't modify anything, so
     * it can be served from a thread's local cache without generating
     * the cache-coherence traffic that a testAndSet() would. Once the
     * plain read shows the lock looks free, we attempt the real
     * testAndSet(). That call might still fail if another thread grabbed
     * the lock in between our read and our attempt - if so, we drop back
     * to the cheap plain-read spin instead of hammering testAndSet()
     * again immediately. Mutual exclusion is still guaranteed entirely
     * by testAndSet(), since that's the only place the lock is actually
     * acquired - the plain read is just an optimisation to reduce how
     * often we call it. */
    public void lock()
    {
        while (true)
        {
            while (locked.get())
            {
                /* spin cheaply: plain read, no atomic operation, not counted */
            }

            testAndSetCount.incrementAndGet();   // count every actual testAndSet() call
            if (!testAndSet())
            {
                /* testAndSet() returned false -> we just flipped it
                 * from false to true -> we own the lock now */
                break;
            }
            /* someone else grabbed it between our read and our
             * testAndSet() - go back to the cheap spin */
        }
    }

    /* Release the lock - identical to Task 1. A plain write is enough:
     * whoever's plain-read-spinning will see 'locked' go false and move
     * on to attempt testAndSet(). */
    public void unlock()
    {
        locked.set(false);
    }

    public long getCount()
    {
        return testAndSetCount.get();
    }

    public void resetCount()
    {
        testAndSetCount.set(0);
    }

}