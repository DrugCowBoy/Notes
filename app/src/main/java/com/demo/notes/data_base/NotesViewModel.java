package com.demo.notes.data_base;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.demo.notes.item.Note;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

// класс, в котором методы из интерфейса NotesDao для работы с БД, будут выполняться в другом потоке
// в активностях мы не будем использовать класс NotesDatabase, вместо него для создания БД мы будем использовать NotesViewModel
public class NotesViewModel extends AndroidViewModel {

    private NotesDatabase database;
    private CompositeDisposable compositeDisposable;

    // в конструкторе ViewModel мы должны создать БД
    public NotesViewModel(@NonNull @NotNull Application application) {
        super(application);
        // создадим БД в единичном экземпляре
        database = NotesDatabase.getInstance(getApplication());
        // создадим compositeDisposable для складирования Disposable
        compositeDisposable = new CompositeDisposable();
    }

    // метод для получения всех объектов из БД с сортировкой по приоритету; AsyncTask не нужен, ведь мы используем LiveData, а она работает в другом потоке
    public LiveData<List<Note>> getAllNotesVM() {
        return database.NotesDao().getAllNotes();
    }

    // метод для получения всех объектов из БД с сортировкой по дате добавления
    public LiveData<List<Note>> getAllNotesSortTimeVM(){
        return database.NotesDao().getAllNotesSortTime();
    }

    // метод для получения всех объектов из БД с сортировкой по названию
    public LiveData<List<Note>> getAllNotesSortTitleVM(){
        return database.NotesDao().getAllNotesSortTitle();
    }


    // метод для вставки объекта в БД в другом потоке
    public void insertNoteVM(Note note) {

        Disposable disposableInsert = database.NotesDao().insertNote(note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i("My", "Insert note success");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i("My", "Insert note error");
                    }
                });

        compositeDisposable.add(disposableInsert);

    }


    // метод для удаления объекта в БД в другом потоке
    public void deleteNoteVM(Note note) {

        Disposable disposableDelete = database.NotesDao().deleteNote(note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i("My", "Delete note success");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i("My", "Delete note error");
                    }
                });

        compositeDisposable.add(disposableDelete);

    }


    // метод для удаления всех объектов в БД в другом потоке
    public void deleteAllNotesVM(){

        Disposable disposableDeleteAll = database.NotesDao().deleteAllNotes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i("My", "Delete all notes success");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i("My", "Delete all notes error");
                    }
                });

        compositeDisposable.add(disposableDeleteAll);

    }

    // Когда ViewModel будет уничтожен (ViewModel не уничтожается при перевороте экрана, но уничтожается при закрытии активности),
    // то вызовется метод onCleared(). В этот момент мы очистим наш compositeDisposable.
    @Override
    protected void onCleared() {
        if (compositeDisposable != null){
            compositeDisposable.dispose();
        }
        super.onCleared();
    }

}
