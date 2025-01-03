import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class JsonUtil {
    public static void initJsonFile(String jsonFileName) throws Exception {
        File file = new File(jsonFileName);
        if (!file.exists()) {
            FileWriter fileWriter = new FileWriter(jsonFileName);
            fileWriter.write("[\n]");
            fileWriter.close();
        }
    }
    
    public static String[] readJsonFileAsObjects(String jsonFileName) throws Exception {
        File file = new File(jsonFileName);
        Scanner fileScanner = new Scanner(file);
        StringBuilder content = new StringBuilder();
        while (fileScanner.hasNextLine()) {
            content.append(fileScanner.nextLine().trim());
        }
        fileScanner.close();
        String[] objects = content.toString().substring(1, content.length() - 1).trim().split("(?<=\\}),(?=\\{)");
        if(objects.length == 1 && objects[0].trim().equals("")) {
            return new String[0];
        }
        for (int i = 0; i < objects.length; i++)
            objects[i] = objects[i].replace("},", "}");
        return objects;
    }

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
    
    public static String accessJsonObject(String jsonFileName, int id) throws Exception {
        String[] objects = readJsonFileAsObjects(jsonFileName);

        for (String obj : objects) {
            if ((int) Task.getField(obj, "ID") == id) {
                return obj;
            }
        }
        throw new Exception("Not Found");
    }

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
        }
        else {
            endIndex++;
        }
        content.delete(startIndex, endIndex);
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(content.toString());
        fileWriter.close();
    }
    
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
