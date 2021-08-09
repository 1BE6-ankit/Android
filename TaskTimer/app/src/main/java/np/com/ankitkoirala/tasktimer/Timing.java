package np.com.ankitkoirala.tasktimer;

import java.io.Serializable;
import java.util.Date;

class Timing implements Serializable {

    private Task task;
    private int id;
    private long duration;
    private long startTime;

    public Timing(Task task) {
        this.task = task;
        startTime = ((new Date()).getTime()) / 1000;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getDuration() {
        return ((new Date()).getTime()) / 1000 - startTime;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
