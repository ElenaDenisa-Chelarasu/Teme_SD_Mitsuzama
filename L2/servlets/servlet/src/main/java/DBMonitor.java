import javax.persistence.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DBMonitor {
    private static DBMonitor instance=null;
    private MonitorizeDBThread thread=null;
    private List<String> fieldNames, aliases;
    private List<Integer> a, b;
    private final Lock lock = new ReentrantLock();
    private final ArrayList<String> depasiri = new ArrayList<String>();
    private Thread ownerThread;

    public static synchronized DBMonitor GetInstance()
    {
        if(instance==null)
            instance=new DBMonitor();
        return instance;
    }

    private DBMonitor()
    {
    }

    public void SetParameteres(List<String> fieldNames, List<Integer> a, List<Integer> b, List<String> aliases)
    {
        CheckOwnership();
        this.fieldNames=fieldNames;
        this.a=a;
        this.b=b;
        this.aliases=aliases;
    }

    public void Lock()
    {
        lock.lock();
        ownerThread=Thread.currentThread();
    }

    public void Unlock()
    {
        ownerThread=null;
        lock.unlock();
    }

    public void StartMonitoring() throws IOException {
        CheckOwnership();
        if(a==null || b==null || fieldNames==null || a.size()!=b.size() || a.size()!=fieldNames.size())
            throw new RuntimeException("DBMonitor initializat cu parametri invalizi!");
        thread = new MonitorizeDBThread(fieldNames, a, b, depasiri, aliases);
        thread.start();
    }

    public List<String> GetDepasiri()
    {
        CheckOwnership();
        return depasiri;
    }

    public void StopMonitoring() throws InterruptedException {
        CheckOwnership();
        thread.PutEventInQueue(false);
        thread=null;
        fieldNames = null;
        a = null;
        b = null;
    }

    public void TransactionCommited() throws InterruptedException {
        CheckOwnership();
        thread.PutEventInQueue(true);
    }

    private void CheckOwnership()
    {
        if(ownerThread!=Thread.currentThread())
            throw new RuntimeException("Ownership not acquired!");
    }

    public boolean isMonitorizing()
    {
        CheckOwnership();
        return thread!=null;
    }

    private static class MonitorizeDBThread extends Thread{
        private final List<String> fieldNames, aliases;
        private final List<Integer> a, b;
        private final ArrayBlockingQueue<Boolean> q = new ArrayBlockingQueue<Boolean>(2);
        private final ArrayList<String> depasiri;
        public MonitorizeDBThread(List<String> fieldNames, List<Integer> a, List<Integer> b, ArrayList<String> depasiri, List<String> aliases) throws IOException {
            DebugUtilities.DebugLog("Thread instantiat\n");
            this.fieldNames=fieldNames;
            this.a=a;
            this.b=b;
            this.depasiri=depasiri;
            this.aliases=aliases;
        }

        public void PutEventInQueue(boolean event) throws InterruptedException {
            q.put(event);
        }

        @Override
        public void run() {
            int debugCount=1;
            while(true)
            {
                try {
                    if (!q.take()) break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    DebugUtilities.DebugLog("Nr. rulari while: "+debugCount+"\n fieldNames.size()="+fieldNames.size()+"\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                EntityManagerFactory factory =
                        Persistence.createEntityManagerFactory("bazaDeDateSQLite");
                EntityManager em = factory.createEntityManager();
                List<Integer> results;
                depasiri.clear();
                for (int i = 0; i < fieldNames.size(); ++i)
                {
                    try {
                        DebugUtilities.DebugLog("While: i="+i+"\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    TypedQuery<Integer> query = em.createQuery("SELECT CAST("+fieldNames.get(i)+" as Integer) FROM StudentEntity se", Integer.class);
                    results = query.getResultList();
                    for (int result : results)
                        if (result < a.get(i) || result > b.get(i)) {
                            depasiri.add("Campul monitorizat " + aliases.get(i) + " are valoare " + result + ", neaflandu-se in intervalul [" + a.get(i) + ", " + b.get(i) + "].");
                            break;
                        }
                }
                em.close();
                factory.close();
            }
        }
    }
}
