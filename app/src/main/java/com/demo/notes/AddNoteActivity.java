package com.demo.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
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

public class AddNoteActivity extends AppCompatActivity {

    private Spinner spinnerPriority;
    private ArrayList<String> priorityList;

    private EditText editTextTitle;
    private EditText editTextDescription;

    private NotesViewModel viewModel;

    private int positionSpinnerSort;// позиция спиннера SpinnerSort

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        // отключим ночной режим
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        spinnerPriority = findViewById(R.id.spinnerPriorityEdit);// элемент из макета спиннер
        editTextTitle = findViewById(R.id.editTextTitleEdit);
        editTextDescription = findViewById(R.id.editTextDescriptionEdit);

        viewModel = new ViewModelProvider(this).get(NotesViewModel.class);// создали ViewModel и соответственно создали БД database

        // создадим массив и добавим в него значения приоритетов
        priorityList = new ArrayList<>();
        priorityList.add(getString(R.string.priority_1));
        priorityList.add(getString(R.string.priority_2));
        priorityList.add(getString(R.string.priority_3));
        // создадим адаптер для добавления массива priorityList и макета spinner_priority_view в спиннер
        ArrayAdapter<String> adapterSpinnerPriority = new ArrayAdapter<>(this,R.layout.spinner_priority_view,priorityList);
        spinnerPriority.setAdapter(adapterSpinnerPriority);
        // работа с интентом
        Intent intent = getIntent();
        if (intent.hasExtra("positionSpinnerSort")){
            positionSpinnerSort = intent.getIntExtra("positionSpinnerSort", 0);
        }
    }

    public void onClickCreateNote(View view) {
        String title = editTextTitle.getText().toString();// получили введённое пользователем название заметки
        String description = editTextDescription.getText().toString();// получили введённое пользователем описние заметки
        int priority = spinnerPriority.getSelectedItemPosition() + 1;// получили выбранную пользователем позицию в спиннере
        // создадим объект Note из наших значений, если пользователь заполнил все поля; иначе выведется уведомление Toast
        if (!title.isEmpty() && !description.isEmpty()){
            Note note = new Note(title,description,priority);
            viewModel.insertNoteVM(note);// вставим в БД нашу созданную заметку note
            // создаём интент для перехода в главную активность
            Intent intentNotes = new Intent(this, MainActivity.class);
            intentNotes.putExtra("positionSpinnerSort", positionSpinnerSort);
            startActivity(intentNotes);
        } else {
            Toast.makeText(this,getString(R.string.toast_fields),Toast.LENGTH_SHORT).show();
        }
    }

}