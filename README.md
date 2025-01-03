# TaskCLI

TaskCLI is a simple command-line interface (CLI) based task tracker that allows you to manage your tasks efficiently. It supports adding, updating, deleting, and listing tasks with various statuses.

## Features

- **Add Tasks**: Create new tasks with a description.
- **Update Tasks**: Modify the description of existing tasks.
- **Delete Tasks**: Remove tasks from the list.
- **Mark Tasks**: Change the status of tasks to in-progress or done.
- **List Tasks**: Display tasks based on their status (all, done, to-do, in-progress).

## Data Format

Data is stored in JSON format for easy and internationalized data storage.

## Task Properties

Each task has the following properties:

- **id**: A unique identifier for the task.
- **status**: The status of the task (to-do, in-progress, done).
- **createdAt**: The date when the task was created.
- **updatedAt**: The date when the task was last updated.
- **description**: A short description of the task.

## Project Structure

- **TaskCLI.java**: The main class that handles CLI commands and interacts with other classes.
- **Task.java**: The class representing a task with properties and methods to manipulate task data.
- **JsonUtil.java**: Utility class for reading and writing JSON data to and from files.
- **config.txt**: Configuration file storing the next task ID and the name of the JSON file used for storing tasks.

## Getting Started

1. **Clone the repository**:

    ```sh
    git clone <repository-url>
    cd TaskCLI
    ```

2. **Compile the Java files**:

    ```sh
    javac *.java
    ```

3. **Initialize the task file**:

    ```sh
    java TaskCLI init [<fileName>]
    ```

    If no file name is provided, `taskList.json` will be used by default.

4. **Run the `TaskCLI` class with the desired command**.

## Example Usage

Here is a practical example that showcases the full functionality of TaskCLI from start to finish:

```sh
# Initialize the task file
java TaskCLI init

# Add a new task
java TaskCLI add "Buy groceries"

# List all tasks
java TaskCLI list

# Update the task description
java TaskCLI update 1 "Buy groceries and cook dinner"

# Mark the task as in progress
java TaskCLI mark-in-progress 1

# List all tasks to see the updated status
java TaskCLI list

# Mark the task as done
java TaskCLI mark-done 1

# List all done tasks
java TaskCLI list done

# Delete the task
java TaskCLI delete 1

# List all tasks to confirm deletion
java TaskCLI list
```

## Commands

- **init [`<fileName>`]**: Initialize a new task file. If no file name is provided, `taskList.json` is used by default.
- **add `<description>`**: Add a new task with the given description.
- **update `<id>` `<description>`**: Update the description of the task with the given ID.
- **delete `<id>`**: Delete the task with the given ID.
- **mark-in-progress `<id>`**: Mark the task with the given ID as in progress.
- **mark-done `<id>`**: Mark the task with the given ID as done.
- **list [done|todo|in-progress]**: List tasks based on their status. If no status is provided, all tasks are listed.

## Conclusion

TaskCLI is a straightforward and efficient tool for managing tasks via the command line. By following the steps and examples provided, you can easily integrate TaskCLI into your workflow to keep track of your tasks and their statuses.

## Credits

I got the idea for this project from [Roadmap.sh](https://roadmap.sh/projects/task-tracker). They provide comprehensive roadmaps for learning various programming skills, including Java. If you are interested in learning Java and other programming technologies, I recommend checking out their website.
