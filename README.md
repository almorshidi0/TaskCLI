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
