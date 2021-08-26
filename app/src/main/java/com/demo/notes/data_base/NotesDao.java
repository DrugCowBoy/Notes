package com.demo.notes.data_base;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.demo.notes.item.Note;

import java.util.List;

import io.reactivex.Completable;

// в этом интерфейсе будут методы для работы с БД; запросы будут в формате SQL
@Dao
public interface NotesDao {

    // метод для получения списка с объектами Note из БД; список обернули в LiveData
    // сортировка по приоритету
    @Query("SELECT * FROM notes ORDER BY priority")
    LiveData<List<Note>> getAllNotes();

    // сортировка по дате добавления
    @Query("SELECT * FROM notes ORDER BY id DESC")
    LiveData<List<Note>> getAllNotesSortTime();

    // сортировка по названию
    @Query("SELECT * FROM notes ORDER BY title")
    LiveData<List<Note>> getAllNotesSortTitle();

    // метод для вставки объекта в БД
    @Insert
    Completable insertNote(Note note);

    // метод для удаления объекта в БД
    @Delete
    Completable deleteNote(Note note);

    // метод для удаления всех объектов в БД
    @Query("DELETE FROM notes")
    Completable deleteAllNotes();
}
