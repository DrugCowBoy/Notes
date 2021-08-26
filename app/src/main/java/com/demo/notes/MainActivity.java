package com.demo.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.demo.notes.R;
import com.demo.notes.data_base.NotesViewModel;
import com.demo.notes.item.Note;
import com.demo.notes.recycler_view.NotesAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerSort;
    private RecyclerView recyclerViewNotes;

    private ArrayList<String> sorts;// массив для спиннера spinnerSort
    private List<Note> notes;// массив notes мы будем устанавливать в recyclerView

    private NotesAdapter notesAdapter;

    private NotesViewModel viewModel;

    private int positionSpinnerSort;// позиция спиннера SpinnerSort

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // отключим ночной режим
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // присвоим переменным ссылки на реальные объекты из макета
        spinnerSort = findViewById(R.id.spinnerSort);
        recyclerViewNotes = findViewById(R.id.recyclerViewNotes);

        notes = new ArrayList<>();

        viewModel = new ViewModelProvider(this).get(NotesViewModel.class);// создали ViewModel и соответственно создали БД database

        // в массив sorts добавим значения сортировки, далее через адаптер добавим этот массив в спиннер spinnerSort
        sorts = new ArrayList<>();
        sorts.add(getString(R.string.sort_priority));
        sorts.add(getString(R.string.sort_data));
        sorts.add(getString(R.string.sort_title));
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,R.layout.spinner_priority_view,sorts);
        spinnerSort.setAdapter(arrayAdapter);

        // если мы перешли в эту активность из интента, то возьмём номер позиции спиннера
        Intent intent = getIntent();
        if (intent.hasExtra("positionSpinnerSort")){
            positionSpinnerSort = intent.getIntExtra("positionSpinnerSort",0);
            spinnerSort.setSelection(positionSpinnerSort);
        }

        // установим слушатель событий для спиннера spinnerSort
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // код ниже нужен, чтобы если слетит позиция в спиннере, то наш спиннер будет ориентироваться на имя сортировки в спиннере
                String nameSpinner = spinnerSort.getSelectedItem().toString();
                if (nameSpinner == getString(R.string.sort_priority)){
                    position = 0;
                } else if (nameSpinner == getString(R.string.sort_data)){
                    position = 1;
                } else {
                    position =2;
                }
                positionSpinnerSort = position;
                getData(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        recyclerViewNotes.setLayoutManager(new LinearLayoutManager(this));// установим внешний вид recyclerView
        notesAdapter = new NotesAdapter(notes);// создаём адаптер и передаём ему массив notes
        recyclerViewNotes.setAdapter(notesAdapter);// устанавливаем адаптер в recyclerView

        // создадим слушатель событий при нажатии на элемент списка
        notesAdapter.setOnClickListener(new NotesAdapter.onClickListener() {
            @Override
            public void editNote(int position) {
                // получили нажатый пользователем элемент
                Note note = notes.get(position);
                // получим имя, описание и приоритет заметки
                String title = note.getTitle();
                String description = note.getDescription();
                int priority = note.getPriority();
                // запустим другую активность EditNoteActivity и положим в неё данные о текущей заметке
                Intent intentEditNote = new Intent(getApplicationContext(),EditNoteActivity.class);
                intentEditNote.putExtra("title", title);
                intentEditNote.putExtra("description", description);
                intentEditNote.putExtra("priority", priority);
                intentEditNote.putExtra("Note", note);
                intentEditNote.putExtra("positionSpinnerSort", positionSpinnerSort);// позиция спиннера SpinnerSort
                startActivity(intentEditNote);
            }
        });

        // создадим itemTouchHelper чтобы можно было удалять элемент списка свайпом влево или враво
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
                Note note = notes.get(viewHolder.getAdapterPosition());// получили объект Note, удаляемый пользователем
                viewModel.deleteNoteVM(note);// удаляем этот note из БД
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerViewNotes);// соединили RecyclerView и itemTouchHelper
    }


    // метод, выполняющийся при нажатии на FAB
    public void onClickFAB(View view) {
        Intent intentCreateNote = new Intent(this, AddNoteActivity.class);
        intentCreateNote.putExtra("positionSpinnerSort", positionSpinnerSort);// позиция спиннера SpinnerSort
        startActivity(intentCreateNote);
    }

    // метод для вытаскивания объектов из БД; вызывается один раз, так как при измении значений в БД, происходит автоматическое изменение массива notes
    private void getData(int positionSpinner){
        LiveData<List<Note>> notesFromDB;
        // получаю из БД массив объектов обёрнутых в LiveData, чтобы можно было за ним наблюдать
        // получаем массив в зависимости от позиции в спиннере spinnerSort - он указывает вид сортировки
        if (positionSpinner == 0){
            notesFromDB = viewModel.getAllNotesVM();
        } else if (positionSpinner == 1) {
            notesFromDB = viewModel.getAllNotesSortTimeVM();
        } else {
            notesFromDB = viewModel.getAllNotesSortTitleVM();
        }
        // ставим наблюдатель на notesFromDB; если происходит изменение в БД, то вызывается метод onChanged
        notesFromDB.observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notesLiveData) {
                notes.clear();
                notes.addAll(notesLiveData);
                notesAdapter.notifyDataSetChanged();
            }
        });
    }

}