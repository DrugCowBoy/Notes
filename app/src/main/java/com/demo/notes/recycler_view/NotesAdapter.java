package com.demo.notes.recycler_view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.notes.R;
import com.demo.notes.item.Note;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {

    private List<Note> notes;// адаптер NotesAdapter будет принимать в качестве параметра массив notes

    private onClickListener onClickListener;

    public void setOnClickListener(NotesAdapter.onClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    // конструктор NotesAdapter
    public NotesAdapter(List<Note> notes) {
        this.notes = notes;
    }

    // интерфейс для слушателя событий, у которго будет метод для редактирования заметки
    public interface onClickListener{
        void editNote(int position);
    }

    @NonNull
    @NotNull
    @Override
    // метод, который создаёт ViewHolder
    public NotesViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item,parent,false);// создали View-объект из xml-файла note_item
        return new NotesViewHolder(view);// вернули созданный объект NotesViewHolder с view в параметре
    }

    @Override
    // метод, который устанавливает значения во ViewHolder
    public void onBindViewHolder(@NonNull @NotNull NotesAdapter.NotesViewHolder holder, int position) {
        Note note = notes.get(position);// взяли один элемент из массива, который мы передали адаптеру
        // из этого элемента возьмём все необходимые значения
        holder.title.setText(note.getTitle());
        holder.description.setText(note.getDescription());

        int priority = note.getPriority();
        int color;
        if (priority == 1){
            color = holder.itemView.getResources().getColor(R.color.priority_red);
            holder.title.setBackgroundColor(color);
        } else if (priority == 2){
            color = holder.itemView.getResources().getColor(R.color.priority_orange);
            holder.title.setBackgroundColor(color);
        } else{
            color = holder.itemView.getResources().getColor(R.color.priority_green);
            holder.title.setBackgroundColor(color);
        }
    }

    @Override
    // метод, который возвращает количество элементов списка
    public int getItemCount() {
        return notes.size();
    }

    // класс, который сделает макет note_item элементом списка, у которого 2 поля title и description
    class NotesViewHolder extends RecyclerView.ViewHolder{

        private TextView title;
        private TextView description;

        public NotesViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textViewTitle);
            description = itemView.findViewById(R.id.textViewDescription);

            // установим слушатель событий для элемента списка при создании ViewHolder'а
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickListener != null){
                        int position = getAdapterPosition();// взяли позицию нажатого элемента
                        onClickListener.editNote(position);
                    }
                }
            });
        }
    }
}
