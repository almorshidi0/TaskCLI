import java.io.FileWriter;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Scanner;

/**
 * This class represents a task in a task list application.
 * A task is created with a description and an ID that is assigned
 * automatically. The status of the task can be TODO, IN_PROGRESS,
 * or DONE. The created at and updated at dates are set automatically
 * when the task is created and updated. The description of the task
 * can be updated. The status of the task can be updated.
 * The ID of the task is immutable.
 * The created at and updated at dates are immutable.
 * 
 * @author Muhammad Almorshidi
 */
public class Task {
    /**
     * The status of a task can be TODO, IN_PROGRESS, or DONE.
     */
    public enum Status {
        TODO,
        IN_PROGRESS,
        DONE,
        UNKNOWN
    }

    /**
     * The next ID to be assigned to a new task.
     * This is incremented automatically each time a new task is created.
     * It's initialized by reading from the config file.
     */
    private static int nextId;
    static {
        try {
            nextId = readNextIdFromConfig();
        } catch (Exception e) {
            e.printStackTrace();
            nextId = 1; // default value if file read fails
        }
    }

    /**
     * The ID of the task. It is immutable.
     */
    private final int id;
    
    /**
     * The status of the task. It can be TODO, IN_PROGRESS, or DONE.
     * It is mutable.
     */
    private Status status;
    
    /**
     * The date and time when the task was created.
     * It is immutable.
     */
    private final LocalDate createdAt;
    
    /**
     * The date and time when the task was last updated.
     * It is mutable.
     */
    private LocalDate updatedAt;
    
    /**
     * The description of the task.
     * It is mutable.
     */
    private String description;

    /**
     * Creates a new task with the given description.
     * @param description the description of the task
     * @throws Exception if there is an error writing to the config file
     */
    public Task(String description) throws Exception {
        this.id = advanceId();
        this.status = Status.TODO;
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
        this.description = description;
    }
    
    /**
     * Creates an object of an existing task with the given parameters.
     * @param id the ID of the task
     * @param status the status of the task
     * @param createdAt the date and time when the task was created
     * @param updatedAt the date and time when the task was last updated
     * @param description the description of the task
     */
    public Task(int id, Status status, LocalDate createdAt, LocalDate updatedAt, String description) {
        this.id = id;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.description = description;
    }
    
    /**
     * Advance the next ID and write it to the config file.
     * @return the ID before it was advanced
     * @throws Exception if there is an error writing to the config file
     */
    private static int advanceId() throws Exception {
        int id = nextId++;
        writeNextIdToConfig(nextId);
        return id;
    }
    
    /**
     * Returns the next ID without advancing it.
     * @return the next ID
     * @throws Exception if there is an error reading from the config file
     */
    public static int getNextId() throws Exception {
        return readNextIdFromConfig();
    }
    
    /**
     * Reads the next ID from the config file.
     * @return the next ID
     * @throws Exception if there is an error reading from the config file
     */
    private static int readNextIdFromConfig() throws Exception {
        Scanner fileScanner = new Scanner(Path.of("config.txt"));
        int i = fileScanner.nextInt();
        fileScanner.close();
        return i;
    }

    /**
     * Writes the next ID to the config file.
     * @param nextId the next ID to write
     * @throws Exception if there is an error writing to the config file
     */
    private static void writeNextIdToConfig(int nextId) throws Exception {
        Scanner fileScanner = new Scanner(Path.of("config.txt"));
        fileScanner.nextLine();
        String fileName = fileScanner.nextLine();
        fileScanner.close();
        FileWriter writer = new FileWriter("config.txt");
        writer.write(Integer.toString(nextId) + "\n" + fileName);
        writer.close();
    }
    
    /**
     * Converts a JSON string representation of a task into a Task object.
     * @param json the JSON string representing a task
     * @return a Task object initialized with values parsed from the JSON string
     */
    public static Task fromJson(String json) {
        int id = 0;
        Status status = Status.UNKNOWN;
        LocalDate createdAt = LocalDate.now();
        LocalDate updatedAt = LocalDate.now();
        String description = "";

        String[] pairs = json.trim().substring(1, json.length() - 1).split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            switch (keyValue[0].trim()) {
                case "\"ID\"" -> id = Integer.parseInt(keyValue[1].trim());
                case "\"Status\"" -> status = switch (keyValue[1].trim().replace("\"", "")) {
                    case "TODO" -> Status.TODO;
                    case "IN_PROGRESS" -> Status.IN_PROGRESS;
                    case "DONE" -> Status.DONE;
                    default -> Status.UNKNOWN;
                };
                case "\"CreatedAt\"" -> createdAt = LocalDate.parse(keyValue[1].trim().replace("\"", ""));
                case "\"UpdatedAt\"" -> updatedAt = LocalDate.parse(keyValue[1].trim().replace("\"", ""));
                case "\"Description\"" -> description = keyValue[1].trim().replace("\"", "");
            }
        }
        return new Task(id, status, createdAt, updatedAt, description);
    }
    
    /**
     * Returns the value of a specified field from a JSON string representation of a task.
     * @param jsonString the JSON string representing a task
     * @param key the name of the field to be retrieved
     * @return the value of the field with the given key, or null if not present
     */
    public static Object getField(String jsonString, String key) {
        String[] pairs = jsonString.trim().substring(1, jsonString.length() - 1).split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            if (keyValue[0].trim().equals("\"" + key + "\"")) {
                String value = keyValue[1].trim();
                switch (key) {
                    case "ID":
                        return Integer.parseInt(value);
                    case "Status":
                        return switch (value.replace("\"", "")) {
                            case "TODO" -> Status.TODO;
                            case "IN_PROGRESS" -> Status.IN_PROGRESS;
                            case "DONE" -> Status.DONE;
                            default -> Status.UNKNOWN;
                        };
                    case "CreatedAt":
                    case "UpdatedAt":
                        return LocalDate.parse(value.replace("\"", ""));
                    case "Description":
                        return value.replace("\"", "");
                    default:
                        return null;
                }
            }
        }
        return null;
    }
    
    /**
     * Returns the ID of the task.
     * @return the ID of the task
     */
    public int getId() {
        return this.id;
    }
    
    /**
     * Returns the status of the task.
     * @return the status of the task
     */
    public Status getStatus() {
        return this.status;
    }
    
    /**
     * Updates the status of the task.
     * @param newStatus the new status of the task
     */
    public void updateStatus(Status newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDate.now();
    }
    
    /**
     * Returns the date when the task was created.
     * @return the date when the task was created
     */
    public LocalDate getCreatedAt() {
        return this.createdAt;
    }
    
    /**
     * Returns the date when the task was last updated.
     * @return the date when the task was last updated
     */
    public LocalDate getUpdatedAt() {
        return this.updatedAt;
    }
    
    /**
     * Returns the description of the task.
     * @return the description of the task
     */
    public String getDescription() {
        return this.description;
    }
    
    /**
     * Updates the description of the task.
     * @param newDescription the new description of the task
     */
    public void updateDescription(String newDescription) {
        this.description = newDescription;
        this.updatedAt = LocalDate.now();
    }

    /**
     * Returns a JSON string representation of the task.
     * @return a JSON string representation of the task
     */
    public String toString() {
        return String.format("{\"ID\":%d,\"Status\":\"%s\",\"CreatedAt\":\"%s\",\"UpdatedAt\":\"%s\",\"Description\":\"%s\"}",
                            this.id,
                            this.status,
                            this.createdAt,
                            this.updatedAt,
                            this.description.replace("\"", "\\\"")
                            );
    }

    /**
     * Test program for the Task class.
     * @param args not used
     * @throws Exception if there is an error
     */
    public static void main(String[] args) throws Exception {
        Task task = new Task("Initial Task");
        System.out.println("Initial Task: " + task);

        task.updateDescription("Updated Task Description");
        System.out.println("After Description Update: " + task);

        task.updateStatus(Status.IN_PROGRESS);
        System.out.println("After Status Update: " + task);

        System.out.println("Task ID: " + task.getId());
        System.out.println("Task Status: " + task.getStatus());
        System.out.println("Task Created At: " + task.getCreatedAt());
        System.out.println("Task Updated At: " + task.getUpdatedAt());
        System.out.println("Task Description: " + task.getDescription());
    }
}
