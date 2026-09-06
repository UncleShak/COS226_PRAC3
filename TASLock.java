import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong; 
public class TASLock 
{

    private final AtomicBoolean locked = new AtomicBoolean(false);
     private final AtomicLong testAndSetCount = new AtomicLong(0);  

    /* Do not modify this method */
    private boolean testAndSet() 
    {
        return locked.getAndSet(true);
    }

    /* Task 1 - Original TAS implementation.
     * Spin on the atomic testAndSet(). It returns the previous value of
     * 'locked', so a return of false means this thread flipped it from
     * false to true and therefore owns the lock. Any other thread doing
     * the same testAndSet() concurrently sees true and keeps spinning,
     * which gives mutual exclusion. Every iteration of this loop performs
     * an atomic operation, even while the lock is held by someone else -
     * that is the overhead Task 2 addresses. */
    public void lock()
    {
        while(testAndSet())
        {
             testAndSetCount.incrementAndGet();
            /* spin: the lock was already held, try again */
        }
         testAndSetCount.incrementAndGet(); 
    }

    /* Release the lock with a plain atomic write. The AtomicBoolean write
     * also publishes everything the owner did in the critical section to
     * the next thread that acquires the lock. */
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
