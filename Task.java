import java.io.FileWriter;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Scanner;

public class Task {
    public enum Status {
        TODO,
        IN_PROGRESS,
        DONE,
        UNKNOWN
    }

    private static int nextId;

    static {
        try {
            nextId = readNextIdFromConfig();
        } catch (Exception e) {
            e.printStackTrace();
            nextId = 1; // default value if file read fails
        }
    }

    private int id;
    private Status status;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private String description;

    public Task(String description) throws Exception {
        this.id = advanceId();
        this.status = Status.TODO;
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
        this.description = description;
    }
    
    public Task(int id, Status status, LocalDate createdAt, LocalDate updatedAt, String description) {
        this.id = id;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.description = description;
    }
    
    private static int advanceId() throws Exception {
        int id = nextId++;
        writeNextIdToConfig(nextId);
        return id;
    }
    
    public static int getNextId() throws Exception {
        return readNextIdFromConfig();
    }
    
    private static int readNextIdFromConfig() throws Exception {
        Scanner fileScanner = new Scanner(Path.of("config.txt"));
        int  i = fileScanner.nextInt();
        fileScanner.close();
        return i;
    }

    private static void writeNextIdToConfig(int nextId) throws Exception {
        Scanner fileScanner = new Scanner(Path.of("config.txt"));
        fileScanner.nextLine();
        String fileName = fileScanner.nextLine();
        fileScanner.close();
        FileWriter writer = new FileWriter("config.txt");
        writer.write(Integer.toString(nextId) + "\n" + fileName);
        writer.close();
    }
    
    public static Task fromJson(String json) {
        int id = 0;
        Status status = Status.UNKNOWN;
        LocalDate createdAt = LocalDate.now();
        LocalDate updatedAt = LocalDate.now();
        String description = "";

        String[] pairs = json.trim().substring(1, json.length() - 1).split(",");
        for(String pair: pairs) {
            String[] keyValue = pair.split(":");
            switch(keyValue[0].trim()) {
                case "\"ID\"" -> id = Integer.parseInt(keyValue[1].trim());
                case "\"Status\"" -> status = switch(keyValue[1].trim().replace("\"", "")) {
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
    
    public static Object getField(String jsonString, String key) {
        String[] pairs = jsonString.trim().substring(1, jsonString.length() - 1).split(",");
        for(String pair: pairs) {
            String[] keyValue = pair.split(":");
            if(keyValue[0].trim().equals("\"" + key + "\"")) {
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
    
    public int getId() {
        return this.id;
    }
    
    public Status getStatus() {
        return this.status;
    }
    
    public void updateStatus(Status newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDate.now();
    }
    
    public LocalDate getCreatedAt() {
        return this.createdAt;
    }
    
    public LocalDate getUpdatedAt() {
        return this.updatedAt;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void updateDescription(String newDescription) {
        this.description = newDescription;
        this.updatedAt = LocalDate.now();
    }

    public String toString() {
        return String.format("{\"ID\":%d,\"Status\":\"%s\",\"CreatedAt\":\"%s\",\"UpdatedAt\":\"%s\",\"Description\":\"%s\"}",
                            this.id,
                            this.status,
                            this.createdAt,
                            this.updatedAt,
                            this.description.replace("\"", "\\\"")
                            );
    }

    public static void main(String[] args) throws Exception {
        Task task = new Task("Task 1");
        System.out.println(task);
        task.updateDescription("Task 2");
        System.out.println(task);
        task.updateStatus(Status.IN_PROGRESS);
        System.out.println(task);
    }
}
