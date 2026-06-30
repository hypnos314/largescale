package it.unipi.largescale.DiscoverEurope.event;

/**
 * Enumerate for task types
 */
public class Task {
    public enum TaskType {
        ADD_PURCHASED_RELATION,
        ADD_REVIEWED_RELATION,
        SYNC_NEW_USER,
        DELETE_USER_NEO4J,
    }
}