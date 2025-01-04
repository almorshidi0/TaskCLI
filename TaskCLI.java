import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * This class provides a command-line interface for managing tasks.
 * It supports operations such as initializing a task file, adding,
 * updating, deleting tasks, and listing tasks based on their status.
 * The class interacts with a JSON file to persist task data and uses
 * the Task and JsonUtil classes to perform task operations.
 * 
 * Supported commands:
 * - init: Initialize a new task file.
 * - config: Update the task file used for storing tasks.
 * - add: Add a new task with a description.
 * - update: Update the description of an existing task.
 * - delete: Delete a task by its ID.
 * - mark-in-progress: Mark a task as in-progress.
 * - mark-done: Mark a task as done.
 * - list: List tasks based on their status (all, todo, in-progress, done).
 * 
 * Usage examples:
 * - java TaskCLI init
 * - java TaskCLI add "New Task Description"
 * 
 * @author Muhammad Almorshidi
 */
public class TaskCLI {
    /**
     * The name of the JSON file used for storing tasks.
     * This value is read from the config file on startup and
     * can be changed with the config command.
     */
    private static String fileName;

    /**
     * Adds a new task to the task list.
     * @param description the description of the new task
     * @throws Exception if there is an error accessing or writing to the task file
     */
    public static void addTask(String description) throws Exception {
        Task task = new Task(description);
        String jsonObject = task.toString();
        JsonUtil.addJsonObject(fileName, jsonObject);
        System.out.println("Task added successfully.");
    }

    /**
     * Updates the description of an existing task identified by its ID.
     * @param id the ID of the task to update
     * @param description the new description for the task
     * @throws Exception if there is an error accessing or updating the task file
     */
    public static void updateTask(int id, String description) throws Exception {
        String oldJsonObject = JsonUtil.accessJsonObject(fileName, id);
        Task task = Task.fromJson(oldJsonObject);
        task.updateDescription(description);
        String newJsonObject = task.toString();
        JsonUtil.updateJsonObject(fileName, oldJsonObject, newJsonObject);
        System.out.println("Task updated successfully.");
    }
    
    /**
     * Deletes a task from the task list.
     * @param id the ID of the task to delete
     * @throws Exception if there is an error accessing or writing to the task file
     */
    public static void deleteTask(int id) throws Exception {
        String oldJsonObject = JsonUtil.accessJsonObject(fileName, id);
        JsonUtil.deleteJsonObject(fileName, oldJsonObject);
        System.out.println("Task deleted successfully.");
    }
    
    /**
     * Updates the status of a task identified by its ID.
     * @param id the ID of the task to update
     * @param status the new status for the task
     * @throws Exception if there is an error accessing or updating the task file
     */
    public static void updateTaskStatus(int id, Task.Status status) throws Exception {
        String oldJsonObject = JsonUtil.accessJsonObject(fileName, id);
        Task task = Task.fromJson(oldJsonObject);
        task.updateStatus(status);
        String newJsonObject = task.toString();
        JsonUtil.updateJsonObject(fileName, oldJsonObject, newJsonObject);
        System.out.println("Task status updated successfully.");
    }
    
    /**
     * Lists tasks based on their status from the JSON file.
     * If no status is provided, all tasks are listed.
     * 
     * @param args the command-line arguments, where the second
     *             argument can be "done", "todo", or "in-progress"
     *             to filter tasks by status
     * @throws Exception if there is an error accessing or reading
     *                   from the task file
     */
    private static void handleListCommand(String[] args) throws Exception {
        String[] objects = JsonUtil.readJsonFileAsObjects(fileName);
        if (objects.length == 0) {
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

    /**
     * Initializes the task file name based on the provided command-line argument.
     * If no argument is provided, the default file name is used.
     * If the file already exists, an error message is printed and the method returns.
     * 
     * @param args the command-line arguments, where the second argument is the
     *             file name to use
     * @throws Exception if there is an error accessing or writing to the task file
     */
    private static void initializeFileName(String[] args) throws Exception {
        if (args.length == 2) {
            fileName = args[1];
            if (fileName.contains(" ")) {
                System.out.println("Invalid file name. It should not contain spaces.");
                return;
            }
        }
        if (!fileName.endsWith(".json")) {
            fileName += ".json";
        }
        if (new File(fileName).exists()) {
            System.out.println("File already exists.");
            return;
        }
        JsonUtil.initJsonFile(fileName);
        int nextId = Task.getNextId();
        FileWriter writer = new FileWriter("config.txt");
        writer.write(nextId + "\n" + fileName);
        writer.close();
        System.out.println("Task file initialized successfully.");
    }

    /**
     * Loads the file name from the config file and checks if the file exists.
     * If the file does not exist, an error message is printed and the method returns.
     * @throws Exception if there is an error reading from the config file
     */
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

    /**
     * Updates the config file with a new JSON file name and the next task ID.
     * If the new file name contains spaces, an error message is printed and the
     * operation is aborted. If the file name does not end with ".json", the extension
     * is appended automatically.
     * 
     * @param newFileName the new file name to set in the config file
     * @throws Exception if there is an error writing to the config file
     */
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
        System.out.println("Config file updated successfully.");
    }

    /**
     * The main method of the TaskCLI class is the entry point of the program.
     * It takes command-line arguments to perform different operations on tasks.
     * The supported operations are:
     * <ul>
     * <li>init: Initialize a new task file.</li>
     * <li>config <fileName>: Update the task file used for storing tasks.</li>
     * <li>add <description>: Add a new task with the given description.</li>
     * <li>update <id> <description>: Update the description of the task with the given ID.</li>
     * <li>delete <id>: Delete the task with the given ID.</li>
     * <li>mark-in-progress <id>: Mark the task with the given ID as in progress.</li>
     * <li>mark-done <id>: Mark the task with the given ID as done.</li>
     * <li>list [all|todo|in-progress|done]: List tasks based on their status.</li>
     * </ul>
     * If the number of arguments is invalid, an error message is printed and the
     * program exits. If the command is unknown, an error message is printed and
     * the program exits.
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        try {
            fileName = "taskList.json";

            if (args.length < 1 || args.length > 3) {
                System.out.println("Invalid number of arguments.");
            } else if (args.length > 0 && args[0].equals("init")) {
                initializeFileName(args);
            } else {
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
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
