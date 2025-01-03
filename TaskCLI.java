import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.Scanner;

public class TaskCLI {
    private static String fileName;

    public static void addTask(String description) throws Exception {
        Task task = new Task(description);
        String jsonObject = task.toString();
        JsonUtil.addJsonObject(fileName, jsonObject);
    }

    public static void updateTask(int id, String description) throws Exception {
        String oldJsonObject = JsonUtil.accessJsonObject(fileName, id);
        Task task = Task.fromJson(oldJsonObject);
        task.updateDescription(description);
        String newJsonObject = task.toString();
        JsonUtil.updateJsonObject(fileName, oldJsonObject, newJsonObject);
    }
    
    public static void deleteTask(int id) throws Exception {
        String oldJsonObject = JsonUtil.accessJsonObject(fileName, id);
        JsonUtil.deleteJsonObject(fileName, oldJsonObject);
    }
    
    public static void updateTaskStatus(int id, Task.Status status) throws Exception {
        String oldJsonObject = JsonUtil.accessJsonObject(fileName, id);
        Task task = Task.fromJson(oldJsonObject);
        task.updateStatus(status);
        String newJsonObject = task.toString();
        JsonUtil.updateJsonObject(fileName, oldJsonObject, newJsonObject);
    }
    
    private static void handleListCommand(String[] args) throws Exception {
        String[] objects = JsonUtil.readJsonFileAsObjects(fileName);
        if(objects.length == 0) {
            System.out.println("No tasks found.");
            return;
        }
        for (String obj : objects) {
            Task task = Task.fromJson(obj);
            if (args.length == 1 || 
                (args[1].equals("done") && task.getStatus() == Task.Status.DONE) || 
                (args[1].equals("todo") && task.getStatus() == Task.Status.TODO) || 
                (args[1].equals("in-progress") && task.getStatus() == Task.Status.IN_PROGRESS)) {
                
                System.out.printf("%15S    %15S    %15S    %15S    %s\n",
                                task.getId(),
                                task.getStatus(),
                                task.getCreatedAt(),
                                task.getUpdatedAt(),
                                task.getDescription());
            }
        }
    }

    private static void initializeFileName(String[] args) throws Exception {
        if(args.length == 2){
            fileName = args[1];
            if (fileName.contains(" ")) {
                System.out.println("Invalid file name. It should not contain spaces.");
                return;
            }
        }
        if (!fileName.endsWith(".json")) {
            fileName += ".json";
        }
        if(new File(fileName).exists()) {
            System.out.println("File already exists.");
            return;
        }
        JsonUtil.initJsonFile(fileName);
        int nextId = Task.getNextId();
        FileWriter writer = new FileWriter("config.txt");
        writer.write(nextId + "\n" + fileName);
        writer.close();
    }

    private static void loadFileNameFromConfig() throws Exception {
        Scanner fileScanner = new Scanner(Path.of("config.txt"));
        fileScanner.nextLine(); // skip first line
        fileName = fileScanner.nextLine().trim();
        fileScanner.close();
        if (!new File(fileName).exists()) {
            System.out.println("File does not exist.\nPlease run \"init\" to create a new file or use \"config\" to load an existing file.");
            return;
        }
    }

    private static void updateConfigFile(String newFileName) throws Exception {
        if (newFileName.contains(" ")) {
            System.out.println("Invalid file name. It should not contain spaces.");
            return;
        }
        if (!newFileName.endsWith(".json")) {
            newFileName += ".json";
        }
        int nextId = Task.getNextId();
        FileWriter writer = new FileWriter("config.txt");
        writer.write(nextId + "\n" + newFileName);
        writer.close();
    }

    public static void main(String[] args) {
        try {
            fileName = "taskList.json";

            if(args.length < 1 || args.length > 3) {
                System.out.println("Invalid number of arguments.");
            }
            else if(args.length > 0 && args[0].equals("init")) {
                initializeFileName(args);
            }
            else {
                loadFileNameFromConfig();
                switch (args[0]) {
                    case "config"           -> updateConfigFile(args[1]);
                    case "add"              -> addTask(args[1]);
                    case "update"           -> updateTask(Integer.parseInt(args[1]), args[2]);
                    case "delete"           -> deleteTask(Integer.parseInt(args[1]));
                    case "mark-in-progress" -> updateTaskStatus(Integer.parseInt(args[1]), Task.Status.IN_PROGRESS);
                    case "mark-done"        -> updateTaskStatus(Integer.parseInt(args[1]), Task.Status.DONE);
                    case "list"             -> handleListCommand(args);
                }
            }
        }
        catch(Exception exc) {
            exc.printStackTrace();
        }
    }
}
