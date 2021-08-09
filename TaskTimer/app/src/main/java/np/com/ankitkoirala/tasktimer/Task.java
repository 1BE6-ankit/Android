package np.com.ankitkoirala.tasktimer;

import java.io.Serializable;

public class Task implements Serializable {

    private long id;
    private final String name;
    private final String description;
    private final long sortOrder;

    public Task(long id, String name, String description, long sortOrder) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.sortOrder = sortOrder;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public long getSortOrder() {
        return sortOrder;
    }

    public String toString() {
        return "id = " + id +
                " name = " + name;
    }
}
