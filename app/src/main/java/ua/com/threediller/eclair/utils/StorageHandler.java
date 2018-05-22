package ua.com.threediller.eclair.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ua.com.threediller.eclair.models.CheckList;
import ua.com.threediller.eclair.models.Task;

/**
 * Created by David on 29.03.2018.
 */

public class StorageHandler extends SQLiteOpenHelper {

    // DB Configuration
    private static String DB_NAME = "eclair.db";
    private static Integer DB_VERSION = 1;

    // Table names
    private static String TABLE_TASKS = "tasks";
    private static String TABLE_CHECK_LISTS = "check_lists";
    private static String TABLE_CHECK_LIST_ITEMS = "check_list_items";

    // Other
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);

    public StorageHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_TASKS + " ("
                + "id INTEGER PRIMARY KEY, "
                + "content TEXT, "
                + "check_list TEXT, "
                + "date_create TEXT, "
                + "date_edit TEXT, "
                + "date_from TEXT, "
                + "date_to TEXT"
                + ")");
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_CHECK_LISTS + " ("
                + "id INTEGER PRIMARY KEY, "
                + "caption TEXT, "
                + "items TEXT"
                + ")");
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_CHECK_LIST_ITEMS + " ("
                + "id INTEGER PRIMARY KEY, "
                + "checked TEXT, "
                + "content TEXT"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
//        onCreate(sqLiteDatabase);
    }

    // SAVE METHODS
    public long save(String table, long id, ContentValues values) {
        if (id == 0)
            return getWritableDatabase().insert(table, null, values);
        else
            getReadableDatabase().update(table, values, "id = " + id, null);
        return id;
    }

    public long saveTask(Task task) {
        ContentValues cv = new ContentValues();
        cv.put("content", task.getContent());
        cv.put("check_list", saveCheckList(task.getCheckList()));
        cv.put("date_create", simpleDateFormat.format(task.getDateCreate()));
        cv.put("date_edit", simpleDateFormat.format(task.getDateEdit()));
        cv.put("date_from", simpleDateFormat.format(task.getDateFrom()));
        cv.put("date_to", simpleDateFormat.format(task.getDateTo()));
        return save(TABLE_TASKS, task.getId(), cv);
    }

    public long saveCheckList(CheckList list) {
        if (list == null) return 0;

        ContentValues cv = new ContentValues();
        cv.put("caption", list.getCaption());

        // Save list items
        if (list.getItems() != null) {
            List<Long> itemsIds = new ArrayList<>();
            for (CheckList.CheckListItem item : list.getItems()) {
                itemsIds.add(saveCheckListItem(item));
            }
            cv.put("items", Tools.concat(itemsIds));
        }
        return save(TABLE_CHECK_LISTS, list.getId(), cv);
    }

    public long saveCheckListItem(CheckList.CheckListItem item) {
        if (item == null) return 0;

        ContentValues cv = new ContentValues();
        cv.put("checked", item.getChecked() ? "true" : "false");
        cv.put("content", item.getContent());
        return save(TABLE_CHECK_LIST_ITEMS, item.getId(), cv);
    }

    // GET METHODS
    public ArrayList<Task> getTasks() {
        Cursor c = getReadableDatabase().query(TABLE_TASKS, null, null, null, null, null, "date_create DESC");
        ArrayList<Task> tasks = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                try {
                    tasks.add(
                            new Task()
                                    .setId(c.getLong(c.getColumnIndex("id")))
                                    .setContent(c.getString(c.getColumnIndex("content")))
                                    .setCheckList(getCheckListById(c.getLong(c.getColumnIndex("check_list"))))
                                    .setDateCreate(simpleDateFormat.parse(c.getString(c.getColumnIndex("date_create"))))
                                    .setDateEdit(simpleDateFormat.parse(c.getString(c.getColumnIndex("date_edit"))))
                                    .setDateFrom(simpleDateFormat.parse(c.getString(c.getColumnIndex("date_from"))))
                                    .setDateTo(simpleDateFormat.parse(c.getString(c.getColumnIndex("date_to"))))
                    );
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } while (c.moveToNext());
        }
        return tasks;
    }

    public Task getTaskById(long id) {
        Cursor c = getReadableDatabase().query(TABLE_TASKS, null, "id = " + id, null, null, null, null);
        if (c.moveToFirst()) {
            try {
                return new Task()
                        .setId(c.getLong(c.getColumnIndex("id")))
                        .setContent(c.getString(c.getColumnIndex("content")))
                        .setCheckList(getCheckListById(c.getLong(c.getColumnIndex("check_list"))))
                        .setDateCreate(simpleDateFormat.parse(c.getString(c.getColumnIndex("date_create"))))
                        .setDateEdit(simpleDateFormat.parse(c.getString(c.getColumnIndex("date_edit"))))
                        .setDateFrom(simpleDateFormat.parse(c.getString(c.getColumnIndex("date_from"))))
                        .setDateTo(simpleDateFormat.parse(c.getString(c.getColumnIndex("date_to"))));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new Task();
    }

    public CheckList getCheckListById(long id) {
        Cursor c = getReadableDatabase().query(TABLE_CHECK_LISTS, null, "id = " + id, null, null, null, null);
        CheckList list = new CheckList();
        if (c.moveToFirst()) {
            list
                    .setId(c.getLong(c.getColumnIndex("id")))
                    .setCaption(c.getString(c.getColumnIndex("caption")));

            String itemsIdsString = c.getString(c.getColumnIndex("items"));
            if (itemsIdsString != null) {
                List<Long> itemsIds = Tools.stringArrayToLongList(itemsIdsString.split(","));
                List<CheckList.CheckListItem> items = new ArrayList<>();
                for (Long itemId : itemsIds) {
                    items.add(getCheckListItemById(itemId));
                }
                list.setItems(items);
            }
        }
        return list;
    }

    public CheckList.CheckListItem getCheckListItemById(long id) {
        Cursor c = getReadableDatabase().query(TABLE_CHECK_LIST_ITEMS, null, "id = " + id, null, null, null, null);
        CheckList.CheckListItem item = new CheckList.CheckListItem();
        if (c.moveToFirst()) {
            item
                    .setId(c.getLong(c.getColumnIndex("id")))
                    .setChecked(Boolean.parseBoolean(c.getString(c.getColumnIndex("checked"))))
                    .setContent(c.getString(c.getColumnIndex("content")));
        }
        return item;
    }

    // CHANGE METHODS
    public void setCheckListItemChecked(long id, boolean value) {
        ContentValues cv = new ContentValues();
        cv.put("checked", value ? "true":"false");
        save(TABLE_CHECK_LIST_ITEMS, id, cv);
    }

    // DELETE METHODS
    public void deleteTaskById(long id) {
        getReadableDatabase().delete(TABLE_TASKS, "id = " + id, null);
    }
}