package ngo.friendship.satellite.asynctask.async;

public class TaskNotExecutedException extends Exception {
    public TaskNotExecutedException() {
        super("Task not executed before calling get()");
    }
}
