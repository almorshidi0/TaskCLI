import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

/**
 * This class contains utility methods for working with JSON files.
 * The JSON file is assumed to contain an array of JSON objects.
 * The methods in this class can be used to add, read, update, and delete
 * JSON objects from the file.
 * The methods are static and can be used without creating an instance of the class.
 * 
 * @author Muhammad Almorshidi
 */
public class JsonUtil {
    /**
     * Initialize a JSON file to store an array of JSON objects.
     * If the file does not exist, it will be created.
     * If the file already exists, it will not be changed.
     * @param jsonFileName the name of the JSON file
     * @throws Exception if there is an error
     */
    public static void initJsonFile(String jsonFileName) throws Exception {
        File file = new File(jsonFileName);
        if (!file.exists()) {
            FileWriter fileWriter = new FileWriter(jsonFileName);
            fileWriter.write("[\n]");
            fileWriter.close();
        }
    }
    
    /**
     * Reads a JSON file containing an array of JSON objects and
     * returns the objects as an array of strings.
     * If the file does not exist, an exception is thrown.
     * If the file exists but is empty, an empty array is returned.
     * @param jsonFileName the name of the JSON file
     * @return an array of strings containing the JSON objects
     * @throws Exception if there is an error
     */
    public static String[] readJsonFileAsObjects(String jsonFileName) throws Exception {
        File file = new File(jsonFileName);
        Scanner fileScanner = new Scanner(file);
        StringBuilder content = new StringBuilder();
        while (fileScanner.hasNextLine()) {
            content.append(fileScanner.nextLine().trim());
        }
        fileScanner.close();
        String[] objects = content.toString().substring(1, content.length() - 1).trim().split("(?<=\\}),(?=\\{)");
        if (objects.length == 1 && objects[0].trim().equals("")) {
            return new String[0];
        }
        for (int i = 0; i < objects.length; i++)
            objects[i] = objects[i].replace("},", "}");
        return objects;
    }

    /**
     * Adds a JSON object to a JSON file that stores an array of JSON objects.
     * If the file is empty, the JSON object is added directly. If the file already
     * contains JSON objects, the new object is appended with a comma separator.
     * @param jsonFileName the name of the JSON file
     * @param jsonObject the JSON object to add
     * @throws Exception if there is an error accessing or writing to the file
     */
    public static void addJsonObject(String jsonFileName, String jsonObject) throws Exception {
        File file = new File(jsonFileName);
        StringBuilder content = new StringBuilder();
        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                content.append(fileScanner.nextLine() + "\n");
            }
            content.deleteCharAt(content.length() - 1);
        }
        if (content.length() == 3) {
            content.insert(content.length() - 1, "    " + jsonObject + "\n");
        } else {
            content.insert(content.length() - 2, ",\n" + "    " + jsonObject);
        }
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(content.toString());
        }
    }
    
    /**
     * Retrieves a JSON object from a JSON file that stores an array of JSON objects.
     * @param jsonFileName the name of the JSON file
     * @param id the ID of the object to retrieve
     * @return the JSON object with the given ID
     * @throws Exception if the object is not found
     */
    public static String accessJsonObject(String jsonFileName, int id) throws Exception {
        String[] objects = readJsonFileAsObjects(jsonFileName);

        for (String obj : objects) {
            if ((int) Task.getField(obj, "ID") == id) {
                return obj;
            }
        }
        throw new Exception("Not Found");
    }

    /**
     * Deletes a JSON object from a JSON file that stores an array of JSON objects.
     * @param jsonFileName the name of the JSON file
     * @param jsonObject the JSON object to delete
     * @throws Exception if there is an error accessing or writing to the file
     */
    public static void deleteJsonObject(String jsonFileName, String jsonObject) throws Exception {
        File file = new File(jsonFileName);
        StringBuilder content = new StringBuilder();
        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                content.append(fileScanner.nextLine() + "\n");
            }
            content.deleteCharAt(content.length() - 1);
        }
        int startIndex = content.indexOf(jsonObject) - 4;
        int endIndex = startIndex + 4 + jsonObject.length();
        if (content.charAt(startIndex - 2) == ',') {
            startIndex -= 2;
        } else if (content.charAt(endIndex) == ',') {
            endIndex += 2;
        } else {
            endIndex++;
        }
        content.delete(startIndex, endIndex);
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(content.toString());
        fileWriter.close();
    }
    
    /**
     * Updates a JSON object in a JSON file that stores an array of JSON objects.
     * @param jsonFileName the name of the JSON file
     * @param oldJsonObject the JSON object to replace
     * @param newJsonObject the JSON object to replace with
     * @throws Exception if there is an error accessing or writing to the file
     */
    public static void updateJsonObject(String jsonFileName, String oldJsonObject, String newJsonObject) throws Exception {
        File file = new File(jsonFileName);
        StringBuilder content = new StringBuilder();
        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                content.append(fileScanner.nextLine() + "\n");
            }
            content.deleteCharAt(content.length() - 1);
        }
        int startIndex = content.indexOf(oldJsonObject);
        int endIndex = content.indexOf("}", startIndex);
        content.replace(startIndex, endIndex + 1, newJsonObject);
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(content.toString());
        fileWriter.close();
    }
}
