package ua.com.threediller.eclair.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

import ua.com.threediller.eclair.models.CheckList;
import ua.com.threediller.eclair.models.Task;

/**
 * Created by David on 08.05.2018.
 */

public class SharedData {

    static String lang = "ru";
    public static void setLanguage(String lang) {
        SharedData.lang = lang;
    }
    public static String getLanguage() {
        return lang;
    }

    static String prefFileName = "preferences";
    public static void setPrefFileName(String prefFileName) {
        SharedData.prefFileName = prefFileName;
    }
    public static String getPrefFileName() {
        return prefFileName;
    }


    static Activity activity;
    static StorageHandler storageHandler;

    // Data
    static ArrayList<Task> tasks;


    public static void setActivity(Activity activity) {
        SharedData.activity = activity;
    }
    public static Activity getActivity() {
        return activity;
    }

    public static void setStorageHandler(StorageHandler storageHandler) {
        SharedData.storageHandler = storageHandler;
    }
    public static StorageHandler getStorageHandler() {
        return storageHandler;
    }

    public static void loadTasks() {tasks = storageHandler.getTasks();}
    public static void deleteTaskById(long id) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == id)
                tasks.remove(i);
        }
        storageHandler.deleteTaskById(id);
    }
    public static List<Task> getTasks() {
        return tasks;
    }
    public static Task getTask(int i){
        return tasks.get(i);
    }
    public static void saveTask(Task task){
        if (task.getId() == 0) {
            tasks.add(0, storageHandler.getTaskById(storageHandler.saveTask(task)));
        } else {
            storageHandler.saveTask(task);
            setTaskById(storageHandler.getTaskById(task.getId()));
        }

    }
    public static void setTaskById(Task newTask) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == newTask.getId())
                tasks.set(i, newTask);
        }
    }
    public static Task getTaskById(long id) {
        for (Task task: tasks) {
            if (task.getId() == id)
                return task;
        }
        return null;
    }

    public static void setCheckListItemChecked(long taskId, long id, boolean value) {
        for (CheckList.CheckListItem item: getTaskById (taskId).getCheckList().getItems()) {
            if (item.getId() == id)
                item.setChecked(value);
        }
        storageHandler.setCheckListItemChecked(id, value);
    }
}
