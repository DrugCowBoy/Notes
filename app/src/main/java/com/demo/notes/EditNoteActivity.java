package com.demo.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.demo.notes.data_base.NotesViewModel;
import com.demo.notes.item.Note;

import java.util.ArrayList;

public class EditNoteActivity extends AppCompatActivity {

    private EditText editTextTitleEdit;
    private EditText editTextDescriptionEdit;

    private Spinner spinnerPriorityEdit;
    private ArrayList<String> priorityList;

    private NotesViewModel viewModel;

    private Note noteDelete;// переменная для хранения объекта типа Note, который мы будем удалять из БД

    private int positionSpinnerSort;// позиция спиннера SpinnerSort

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        // отключим ночной режим
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        editTextTitleEdit = findViewById(R.id.editTextTitleEdit);
        editTextDescriptionEdit = findViewById(R.id.editTextDescriptionEdit);
        spinnerPriorityEdit = findViewById(R.id.spinnerPriorityEdit);

        viewModel = new ViewModelProvider(this).get(NotesViewModel.class);// создали ViewModel и соответственно создали БД database

        // создадим массив и добавим в него значения приоритетов
        priorityList = new ArrayList<>();
        priorityList.add(getString(R.string.priority_1));
        priorityList.add(getString(R.string.priority_2));
        priorityList.add(getString(R.string.priority_3));
        // создадим адаптер для добавления массива priorityList и макета spinner_priority_view в спиннер
        ArrayAdapter<String> adapterSpinnerPriority = new ArrayAdapter<>(this,R.layout.spinner_priority_view,priorityList);
        spinnerPriorityEdit.setAdapter(adapterSpinnerPriority);

        // вытащим из интента, который вызвал данную активность название, описание и приоритет заметки
        Intent intent = getIntent();
        if (intent.hasExtra("title") && intent.hasExtra("description") && intent.hasExtra("priority") && intent.hasExtra("positionSpinnerSort")){
            String title = intent.getStringExtra("title");
            String  description = intent.getStringExtra("description");
            int priority = intent.getIntExtra("priority", 0);
            noteDelete = intent.getParcelableExtra("Note");
            positionSpinnerSort = intent.getIntExtra("positionSpinnerSort", 0);
            // установим полученные значения в поля нашего макета
            editTextTitleEdit.setText(title);
            editTextDescriptionEdit.setText(description);
            spinnerPriorityEdit.setSelection(priority-1);// выберем установленную позицию спиннера
        }
    }

    public void onClickEditNote(View view){
        String title = editTextTitleEdit.getText().toString();// получили введённое пользователем название заметки
        String description = editTextDescriptionEdit.getText().toString();// получили введённое пользователем описние заметки
        int priority = spinnerPriorityEdit.getSelectedItemPosition() + 1;// получили выбранную пользователем позицию в спиннере
        // создадим объект Note из наших значений, если пользователь заполнил все поля; иначе выведется уведомление Toast
        if (!title.isEmpty() && !description.isEmpty()){
            viewModel.deleteNoteVM(noteDelete);
            // добавим в БД новую заметку
            Note note = new Note(title,description,priority);
            viewModel.insertNoteVM(note);
            // создаём интент для перехода в главную активность
            Intent intentNotes = new Intent(this,MainActivity.class);
            intentNotes.putExtra("positionSpinnerSort", positionSpinnerSort);
            startActivity(intentNotes);
        } else {
            Toast.makeText(this,getString(R.string.toast_fields),Toast.LENGTH_SHORT).show();
        }
    }
}