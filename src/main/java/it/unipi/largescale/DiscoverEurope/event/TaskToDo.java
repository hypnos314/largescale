package it.unipi.largescale.DiscoverEurope.event;

import lombok.Getter;

/**
 * Task manager class representing a specific asynchronous task or event to be executed in the system.
 * Handles different payloads based on the specific event type.
 */
@Getter
public class TaskToDo {

    private final Task.TaskType type;
    private final String userId;
    private final String packageId;

    private final Double rating;
    private final String timestamp;

    private final Object payload;

    /**
     * Constructor for tasks involving a user reviewing a travel package (REVIEWED event).
     * @param type the specific type of task to execute
     * @param userId ID of the user submitting the review
     * @param packageId ID of the reviewed travel package
     * @param rating the numerical score assigned by the user
     * @param timestamp the date and time the review was created
     */
    public TaskToDo(Task.TaskType type, String userId, String packageId, Double rating, String timestamp) {
        this.type = type;
        this.userId = userId;
        this.packageId = packageId;
        this.rating = rating;
        this.timestamp = timestamp;
        this.payload = null;
    }

    /**
     * Constructor for tasks representing a package purchase (PURCHASED event).
     * @param type the specific type of task to execute
     * @param userId ID of the user who made the purchase
     * @param packageId ID of the purchased travel package
     * @param timestamp the date and time the purchase was completed
     */
    public TaskToDo(Task.TaskType type, String userId, String packageId, String timestamp) {
        this.type = type;
        this.userId = userId;
        this.packageId = packageId;
        this.rating = null;
        this.timestamp = timestamp;
        this.payload = null;
    }

    /**
     * Constructor for synchronization tasks involving complete data nodes (User).
     * @param type the specific type of task to execute
     * @param payload the complete object containing the node data to synchronize
     */
    public TaskToDo(Task.TaskType type, Object payload) {
        this.type = type;
        this.userId = null;
        this.packageId = null;
        this.rating = null;
        this.timestamp = null;
        this.payload = payload;
    }

    /**
     * Constructor for quick deletion tasks requiring only a user ID.
     * @param type the specific type of task to execute
     * @param userIdToDelete ID of the user to be permanently removed from the system
     */
    public TaskToDo(Task.TaskType type, String userIdToDelete) {
        this.type = type;
        this.userId = userIdToDelete;
        this.packageId = null;
        this.rating = null;
        this.timestamp = null;
        this.payload = null;
    }
}