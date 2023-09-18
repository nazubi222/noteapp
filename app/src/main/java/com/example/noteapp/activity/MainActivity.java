package com.example.noteapp.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.NoteItemTouchHelperCallback;
import com.example.noteapp.R;
import com.example.noteapp.adapter.FolderAdapter;
import com.example.noteapp.adapter.NotesAdapter;
import com.example.noteapp.model.DatabaseHandler;
import com.example.noteapp.model.Folder;
import com.example.noteapp.model.Note;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ImageView imgCreateFolder, imgCreateNote;
    private List<Folder> listFolder;
    private List<Note> listNoteSearch, listNote;
    private RecyclerView rcvFolder, rcvSearchView;
    private LinearLayout lnMain;
    private SearchView searchView;
    private FolderAdapter folderAdapter;
    private DatabaseHandler databaseHandler;

    private ImageView imgMoreCreateNote;
    private RelativeLayout relativeloutfolderall, relativeloutfolderGhiChu;
    private TextView tvCountNoteFolder, tvCountNoteFolderGhichu;
    private Animation fadeAnimation;
    private NotesAdapter notesAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        fadeAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_animation);

        imgCreateFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCLickCreateFolder();
            }
        });

        imgCreateNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickCreateNote();
            }
        });
        relativeloutfolderall.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                relativeloutfolderall.startAnimation(fadeAnimation);
                setOnClickRelativeLoutFolderAll();
            }
        });

        relativeloutfolderGhiChu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                relativeloutfolderGhiChu.startAnimation(fadeAnimation);
                setOnClickRelativeLoutFolderGhiChu();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query.length()>0){
                    performSearch(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length()==0){
                    rcvSearchView.setVisibility(View.GONE);
                    lnMain.setVisibility(View.VISIBLE);
                }else {
                    performSearch(newText);
                }
                Log.d("searchabc", "onQueryTextChange: " + newText);
                return true;
            }
        });
    }

    private void performSearch(String text){
        listNoteSearch = new ArrayList<>();
        for(int i=0; i<listNote.size(); i++){
            if(listNote.get(i).getName().toUpperCase().contains(text.toUpperCase())){
                listNoteSearch.add(listNote.get(i));
            }
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        rcvSearchView.setLayoutManager(linearLayoutManager);

        notesAdapter = new NotesAdapter(listNote, MainActivity.this, new NotesAdapter.IClickListenerNote() {
            @Override
            public void onClickItemNote(int position) {
                Note note = notesAdapter.GetNoteByPosition(position);
                Bundle bundle = new Bundle();
                bundle.putInt("idnote", note.getIdNote());
                bundle.putInt("idnotefolder", note.getIdFolder());
                bundle.putString("namenotes", note.getName());
                bundle.putString("notecontext", note.getContext());
                bundle.putString("notepassword", note.getPassword());
                bundle.putString("notedate", note.getDate());
                bundle.putBoolean("noteispin", note.isPin());
                bundle.putBoolean("noteislock", note.isLock());

                Intent intent = new Intent(MainActivity.this, CreateNoteActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        rcvSearchView.setAdapter(notesAdapter);
        rcvSearchView.setVisibility(View.VISIBLE);
        lnMain.setVisibility(View.GONE);
    }

    private void setOnClickRelativeLoutFolderGhiChu() {
        Bundle bundle = new Bundle();
        bundle.putInt("idfolder", 1);
        bundle.putString("namefolder", "Ghi chú");

        Intent intent = new Intent(MainActivity.this, NotesActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void setOnClickRelativeLoutFolderAll() {
        Intent intent = new Intent(MainActivity.this, NotesActivity.class);
        startActivity(intent);
    }

    private void onClickCreateNote() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_newnote);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttribute = window.getAttributes();
        windowAttribute.gravity = Gravity.CENTER;
        window.setAttributes(windowAttribute);

        TextView continueCreateNote = dialog.findViewById(R.id.tv_continuecreatenote);
        EditText editText = dialog.findViewById(R.id.edtnamecreatenote);


        continueCreateNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("namenote", editText.getText().toString());
                Intent intent = new Intent(MainActivity.this, CreateNoteActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                Log.d("databasemain", editText.getText().toString());
                dialog.dismiss();

            }
        });
        dialog.show();


    }

    private void onCLickCreateFolder() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_newfolder);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttribute = window.getAttributes();
        windowAttribute.gravity = Gravity.CENTER;
        window.setAttributes(windowAttribute);

        TextView save = dialog.findViewById(R.id.tv_savecreatefolder);
        TextView textView = dialog.findViewById(R.id.tvnamelayoutcreate);
        TextView destroy = dialog.findViewById(R.id.tv_destroycreatefolder);
        EditText editText = dialog.findViewById(R.id.edtnamelayoutcreate);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    int color = Color.parseColor("#81A34D");
                    save.setEnabled(true);
                    save.setTextColor(color);
                } else {
                    int color = Color.parseColor("#A5A5A5");
                    save.setEnabled(false);
                    save.setTextColor(color);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        destroy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean check = false;
                for(int i=0; i<listFolder.size(); i++){
                    if (listFolder.get(i).getNameFolder().equals(editText.getText().toString())){
                        check = true;
                        break;
                    }
                }
                if(check == false){
                    Folder folder = new Folder(0, editText.getText().toString());
                    databaseHandler.addFolder(folder);
                    getListFolder();
                    folderAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }else {
                    textView.setText("Thư mục bị trùng tên");
                    textView.setTextColor(Color.RED);
                }

            }
        });
        dialog.show();

    }

    private void initUI() {
        imgCreateFolder = findViewById(R.id.imgcreatefolder);
        imgCreateNote = findViewById(R.id.imgcreatenote);
        rcvFolder = findViewById(R.id.rcv_folder);
        tvCountNoteFolder = findViewById(R.id.tvcountnotefolder);
        relativeloutfolderall  = findViewById(R.id.relativeloutfolderall);
        relativeloutfolderGhiChu = findViewById(R.id.relativeloutfolderghichu);
        tvCountNoteFolderGhichu = findViewById(R.id.tvcountnotefolderghichu);
        searchView = findViewById(R.id.searchviewmain);
        rcvSearchView = findViewById(R.id.rcvseachviewmain);
        lnMain = findViewById(R.id.lnmainabc);
        databaseHandler = new DatabaseHandler(this, "dbnoteapp", null, 1);

        listNote = new ArrayList<>();
        listNote.addAll(databaseHandler.getAllNote());
        listFolder = new ArrayList<>();
        getListFolder();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvFolder.setLayoutManager(linearLayoutManager);
        folderAdapter = new FolderAdapter(listFolder, this, new FolderAdapter.IClickListenerFolder() {
            @Override
            public void onClickItemFolder(int position) {
                Folder folder = folderAdapter.GetFolderByPosition(position);
                Bundle bundle = new Bundle();
                bundle.putInt("idfolder", folder.getIdFolder());
                bundle.putString("namefolder", folder.getNameFolder());

                Intent intent = new Intent(MainActivity.this, NotesActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });
        ItemTouchHelper.Callback callback = new NoteItemTouchHelperCallback(this, rcvFolder, false, ItemTouchHelper.LEFT) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons, List<UnderlayButton> underlayButtonsRight) {
                underlayButtons.add(new NoteItemTouchHelperCallback.UnderlayButton(
                        AppCompatResources.getDrawable(
                                MainActivity.this,
                                R.drawable.trash_can_enable
                        ),
                        Color.parseColor("#FF0000"),
                        new NoteItemTouchHelperCallback.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                databaseHandler.deleteFolder(listFolder.get(pos).getIdFolder());
                                getListFolder();
                                folderAdapter.notifyDataSetChanged();
                                resetCount();
                            }
                        }
                ));

                underlayButtons.add(new NoteItemTouchHelperCallback.UnderlayButton(
                        AppCompatResources.getDrawable(
                                MainActivity.this,
                                R.drawable.editing
                        ),
                        Color.parseColor("#56BC93"),
                        new NoteItemTouchHelperCallback.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                onCLickEditName(listFolder.get(pos).getIdFolder(), folderAdapter);
                            }
                        }
                ));
            }
        };
        ItemTouchHelper touchHelper2 = new ItemTouchHelper(callback);
        rcvFolder.setAdapter(folderAdapter);
        touchHelper2.attachToRecyclerView(rcvFolder);
    }

    private void onCLickEditName(int idFolder, FolderAdapter adapter) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_editnamenote);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttribute = window.getAttributes();
        windowAttribute.gravity = Gravity.CENTER;
        window.setAttributes(windowAttribute);

        TextView name = dialog.findViewById(R.id.tvlayouteditnamename);
        TextView notifi = dialog.findViewById(R.id.tvlayouteditnamenotifi);
        TextView save = dialog.findViewById(R.id.tv_saveeditname);
        TextView destroy = dialog.findViewById(R.id.tv_destroyeditname);
        EditText editText = dialog.findViewById(R.id.edtnamelayouteditname);

        name.setText("Nhập tên thư mục ghi chú muốn đổi");

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    int color = Color.parseColor("#81A34D");
                    save.setEnabled(true);
                    save.setTextColor(color);
                } else {
                    int color = Color.parseColor("#A5A5A5");
                    save.setEnabled(false);
                    save.setTextColor(color);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        destroy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = false;
                for(int i=0; i<listFolder.size(); i++){
                    if (listFolder.get(i).getNameFolder().equals(editText.getText().toString())){
                        check = true;
                        break;
                    }
                }
                if(check == false){
                    databaseHandler.updateChangeNameFolder(idFolder, editText.getText().toString());
                    getListFolder();
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                }else {
                    notifi.setVisibility(View.VISIBLE);
                    notifi.setText("Không được trùng tên thư mục");
                    notifi.setTextColor(Color.RED);
                }

            }
        });
        dialog.show();

    }
    private void getListFolder() {
        listFolder.clear();
        listFolder.addAll(databaseHandler.getAllFolder());
        listFolder.remove(listFolder.get(0));
        for(int i=0; i<listFolder.size(); i++){
            Log.d("databasemain", String.valueOf(listFolder.get(i).getIdFolder())+ " "
                    + listFolder.get(i).getNameFolder());
        }
    }

    private void resetCount(){
        List<Note> listall = new ArrayList<>();
        List<Note> listghichu = new ArrayList<>();
        listall.addAll(databaseHandler.getAllNote());
        for(int i=0; i<listall.size(); i++){
            if(listall.get(i).getIdFolder() == 1){
                listghichu.add(listall.get(i));
            }
        }

        tvCountNoteFolder.setText(String.valueOf(listall.size()));
        tvCountNoteFolderGhichu.setText(String.valueOf(listghichu.size()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetCount();
        getListFolder();
        folderAdapter.notifyDataSetChanged();
    }
    public void onBackPressed() {
        if(!searchView.isIconified()){
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }
}
