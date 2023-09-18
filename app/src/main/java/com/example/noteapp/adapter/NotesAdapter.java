package com.example.noteapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.R;
import com.example.noteapp.model.Folder;
import com.example.noteapp.model.Note;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder>{
    private List<Note> listNotes;
    private Context context;
    private Folder folder = null;

    private NotesAdapter.IClickListenerNote iClickListenerNote;
    private Animation fadeAnimation;

    public interface IClickListenerNote{
        void onClickItemNote(int position);
    }
    public NotesAdapter(List<Note> listNotes, Context context , IClickListenerNote iClickListenerNote){
        this.listNotes = listNotes;
        this.context = context;
        this.iClickListenerNote = iClickListenerNote;
    }
    public NotesAdapter(List<Note> listNotes, Context context, Folder folder, IClickListenerNote iClickListenerNote){
        this.listNotes = listNotes;
        this.context = context;
        this.folder = folder;
        this.iClickListenerNote = iClickListenerNote;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        NotesAdapter.NotesViewHolder viewHolder = new NotesAdapter.NotesViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Note note = listNotes.get(position);
        if(note == null){
            return;
        }

        Document document = (Document) Jsoup.parse("<p>"+note.getContext()+"</p>");
        String plainText = document.text();
        holder.tvContextNote.setText(plainText);

        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z", Locale.US);
        try {
            Date date = inputFormat.parse(note.getDate().toString());

            // Định dạng lại thời gian theo định dạng mong muốn
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM HH:mm", Locale.US);
            String formattedTime = outputFormat.format(date);

            holder.tvTimeNote.setText(formattedTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String namenote = note.getName();
        if(namenote.equals("Không có tiêu đề")){
            holder.tvNameNote.setTextColor(Color.parseColor("#A5A5A5"));
            holder.tvNameNote.setText(note.getName());
        }else{
            holder.tvNameNote.setText(note.getName());
            holder.tvNameNote.setTextColor(Color.BLACK);
        }

        if(position == listNotes.size() -1){
            holder.view.setVisibility(View.GONE);
        }


        if (folder != null){
            holder.tvNameFolderNote.setText(folder.getNameFolder());
        }

        if(note.isLock()){
            holder.imgLockNote.setVisibility(View.VISIBLE);
        }else{
            holder.imgLockNote.setVisibility(View.INVISIBLE);
        }

        holder.lnItemNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickListenerNote.onClickItemNote(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(listNotes != null){
            return listNotes.size();
        }
        return 0;
    }

    public class NotesViewHolder extends RecyclerView.ViewHolder{
        private TextView tvNameNote, tvTimeNote, tvContextNote, tvNameFolderNote;
        private ImageView imgLockNote;
        private LinearLayout lnItemNote;
        private View view;
        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.viewitemnote);
            tvNameNote  = itemView.findViewById(R.id.tvnameitemnote);
            tvContextNote= itemView.findViewById(R.id.tvcontextitemnote);
            tvTimeNote = itemView.findViewById(R.id.tvtimeitemnote);
            tvNameFolderNote = itemView.findViewById(R.id.tvnamefolderitemnote);
            imgLockNote = itemView.findViewById(R.id.imglockitemnote);
            lnItemNote = itemView.findViewById(R.id.lnitemnote);
        }
    }
    public void updateData(List<Note> newData) {
        listNotes.clear();
        listNotes.addAll(newData);
        notifyDataSetChanged();
    }

    public Note GetNoteByPosition(int position) {
        List<Note> listNote = this.GetListNote();
        return listNote.get(position);
    }

    private List<Note> GetListNote() {
        return this.listNotes;
    }

}
