package it.unipi.largescale.DiscoverEurope.service;

import it.unipi.largescale.DiscoverEurope.event.TaskToDo;
import it.unipi.largescale.DiscoverEurope.repository.neo4j.Sync_Neo4jInterface;
import it.unipi.largescale.DiscoverEurope.model.mongodb.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Background task manager responsible for asynchronously synchronizing state changes
 * from the primary MongoDB database to the Neo4j recommendation graph.
 * Ensures the graph database remains eventually consistent without blocking main user threads.
 */
@Service
public class Neo4jSyncManager {

    @Autowired
    private Sync_Neo4jInterface syncRepo;

    private static final Queue<TaskToDo> taskQueue = new ConcurrentLinkedQueue<>();

    /**
     * Appends a new synchronization task to the execution queue.
     * @param task the {@link TaskToDo} event detailing the state change
     */
    public void addTask(TaskToDo task) {
        taskQueue.add(task);
        System.out.println("[SYNC MANAGER] Task successfully added to the queue " + task.getType());
    }

    /**
     * Scheduled job that processes the synchronization queue.
     * Operates securely by verifying system CPU load before executing intensive graph mutations.
     */
    @Scheduled(fixedDelay = 10000)
    public void processQueue() {
        if (taskQueue.isEmpty()) return;

        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        if (osBean.getCpuLoad() >= 0 && osBean.getCpuLoad() < 0.4) {
            System.out.println("[SYNC MANAGER] CPU Load OK (" + String.format("%.2f", osBean.getCpuLoad()));
            int processedTasks = 0;
            while (processedTasks < 10 && !taskQueue.isEmpty()) {
                TaskToDo task = taskQueue.poll();
                executeTask(task);
                processedTasks++;
            }
        } else {
            System.out.println("[SYNC MANAGER] CPU Load too high (" + String.format("%.2f", osBean.getCpuLoad()));
        }
    }

    private void executeTask(TaskToDo task) {
        try {
            switch (task.getType()) {
                case ADD_PURCHASED_RELATION:
                    System.out.println("Relationship PURCHASED created between User: " + task.getUserId() + " and Travel Package: " + task.getPackageId());
                    syncRepo.createPurchasedRelation(
                            task.getUserId(),
                            task.getPackageId(),
                            task.getTimestamp()
                    );
                    break;
                case ADD_REVIEWED_RELATION:
                    System.out.println("Relationship REVIEWED created.");
                    syncRepo.createReviewedRelation(
                            task.getUserId(),
                            task.getPackageId(),
                            task.getTimestamp(),
                            task.getRating()
                    );
                    break;
                case SYNC_NEW_USER:
                    System.out.println("Synchronization new node User on Neo4j");
                    User u = (User) task.getPayload();
                    syncRepo.syncUser(
                            u.getId(),
                            u.getPersonalInfo().getFirstName(),
                            u.getPersonalInfo().getLastName(),
                            u.getPersonalInfo().getDateOfBirth() != null ? u.getPersonalInfo().getDateOfBirth().toString() : null,
                            u.getRole()
                    );
                    break;
                case DELETE_USER_NEO4J:
                    System.out.println("[SYNC] Elimination node User from Neo4j: " + task.getUserId());
                    syncRepo.deleteUser(task.getUserId()
                    );
                    break;
            }
        } catch (Exception e) {
            System.err.println("[SYNC MANAGER] Error Neo4j, failed task. Trying again...");
            e.printStackTrace();
            taskQueue.add(task);
        }
    }
}