package com.example.daililus;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder>{
    private List<Note> notes = new ArrayList<>();

    public void setNotes(List<Note> newNotes){
        this.notes = newNotes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position){
        Note note = notes.get(position);

        holder.tvTitle.setText(note.getTitle());
        holder.tvDate.setText(note.getDate());

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, NoteEditorActivity.class);
            intent.putExtra("NOTE_ID", note.getId());
            intent.putExtra("NOTE_TITLE", note.getTitle());
            intent.putExtra("NOTE_DATE", note.getDate());
            intent.putExtra("NOTE_CONTENT", note.getContent());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount(){
        return notes.size();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder{
        TextView tvTitle;
        TextView tvDate;
        public NoteViewHolder(@NonNull View itemView){
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNoteTitle);
            tvDate = itemView.findViewById(R.id.tvNoteDate);
        }
    }

}
