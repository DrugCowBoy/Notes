package com.demo.notes.data_base;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.demo.notes.item.Note;

// класс, в котором будет метод для создания БД в единичном экземпляре
@Database(entities = {Note.class},version = 1, exportSchema = false)
public abstract class NotesDatabase extends RoomDatabase {
    private static NotesDatabase database;
    private static final String DB_NAME = "note.db";// имя БД
    private static final String LOCK = new String();// объект для синхронизации

    public static NotesDatabase getInstance(Context context){
        synchronized (LOCK){
            if (database == null){
                database = Room.databaseBuilder(context,NotesDatabase.class,DB_NAME)
                        .fallbackToDestructiveMigration()
                        .build();
            }
            return database;
        }
    }

    // метод для создания объекта NotesDao, нужен чтобы использовать методы этого интерфейса
    public abstract NotesDao NotesDao();
}
