package com.example.noteapp.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.R;
import com.example.noteapp.model.DatabaseHandler;
import com.example.noteapp.model.Folder;
import com.example.noteapp.model.Note;

import java.util.ArrayList;
import java.util.List;

public class FolderAdapter  extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder>{
    private List<Folder> listFolder;
    private Context context;
    private IClickListenerFolder iClickListenerFolder;
    private Animation fadeAnimation;
    private DatabaseHandler databaseHandler;

    public interface IClickListenerFolder{
        void onClickItemFolder(int position);
    }

    public FolderAdapter(List<Folder> listFolder, Context context, IClickListenerFolder iClickListenerFolder) {
        this.listFolder = listFolder;
        this.context = context;
        this.iClickListenerFolder = iClickListenerFolder;
        databaseHandler = new DatabaseHandler(context, "dbnoteapp", null, 1);
    }


    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_folder, parent, false);
        FolderAdapter.FolderViewHolder viewHolder = new FolderAdapter.FolderViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Folder folder = listFolder.get(position);
        if(folder == null){
            return;
        }
        List<Note> list= new ArrayList<>();
        fadeAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_animation);
        holder.itemFolderName.setText(folder.getNameFolder());


        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(fadeAnimation);
                iClickListenerFolder.onClickItemFolder(position);
            }
        });

        list.addAll(databaseHandler.getNotesInFolder(folder.getIdFolder()));
        holder.tvCount.setText(String.valueOf(list.size()));

    }

    @Override
    public int getItemCount() {
        if(listFolder != null){
            return listFolder.size();
        }
        else return 0;
    }

    public class FolderViewHolder extends RecyclerView.ViewHolder{
        private TextView itemFolderName;
        private RelativeLayout relativeLayout;
        private TextView tvCount;

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            itemFolderName = itemView.findViewById(R.id.tvitemnamefolder);
            relativeLayout = itemView.findViewById(R.id.relativeloutfolderall);
            tvCount = itemView.findViewById(R.id.tvitemcountfolder);
        }
    }
    public Folder GetFolderByPosition(int position) {
        List<Folder> listFolder = this.GetListFolder();
        return listFolder.get(position);
    }

    private List<Folder> GetListFolder() {
        return this.listFolder;
    }



}
