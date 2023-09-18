package com.example.noteapp.activity;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;

import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.noteapp.R;
import com.example.noteapp.fragment.FragmentBtSheetMoreCreateNote;
import com.example.noteapp.model.DatabaseHandler;
import com.example.noteapp.model.Folder;
import com.example.noteapp.model.Note;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.wasabeef.richeditor.RichEditor;

public class CreateNoteActivity extends AppCompatActivity {

    private View contentView;
    private RichEditor mEditor;
    private Animation animation;
    private TextView mPreview, tvFinshedCreateNote, tvOpenLockNote;
    private ImageView imgMoreCreateNote, imgPlusCreateNote, imgLockNote;
    private ImageButton imgBold, imgItalic, imgBullets, imgUnderline, imgListNumber, imgUndo, imgRedo;
    private AutoCompleteTextView autoCompleteHeading;
    private Toolbar toolbarCreateNote;
    //        private boolean isImageOne = true;
    private boolean isToolbarVisible = false, isCheckedNewNote = false;
    private LinearLayout lnExitCreateNote, lnLockCreateNote;
    private DatabaseHandler databaseHandler;
    private String[] headings = {"Heading1", "Heading2", "Heading3"};
    private boolean isCheckNote;
    private List<Note> listNote;
    private Note note = null;
    private boolean isClickLock;
    private Folder folder = null;
    private String nameNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((R.layout.activity_createnote));
        initUI();
        mEditor.setEditorFontSize(22);
        mEditor.setEditorFontColor(Color.BLACK);
        mEditor.setEditorHeight(600);
        mEditor.setPadding(10,10,10,10);
        //mEditor.setInputEnabled(false);

//        mPreview = (TextView) findViewById(R.id.preview);
        if (note != null) {
            mEditor.setHtml(note.getContext());
        }
//        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
//            @Override
//            public void onTextChange(String text) {
//                mPreview.setText(mEditor.getHtml());
//            }
//        });

        setClickImgLockNote();
        setOnClickImgBold();
        setOnClickImgUndo();
        setOnClickImgRedo();
        setOnClickImgItalic();
        setOnClickImgUnderline();
        setOnClickImgBullets();
        setOnClickImgListNumber();
        setOnClickImgHeading();
        setOnCLickImgMoreCreateNote();
        setOnclickImgPlusCreateNote();
        setOnClickTvFinshedCreateNote();
        setOnClickLnExit();
        setEventKeyPad();
        setOnClickTvOpenLockNote();


    }


    private void setOnClickTvOpenLockNote() {
        tvOpenLockNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOpenLockNote();
            }
        });
    }

    private void setOpenLockNote() {
        isClickLock = !isClickLock;
        Dialog dialog = new Dialog(CreateNoteActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_enter_password);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttribute = window.getAttributes();
        windowAttribute.gravity = Gravity.CENTER;
        window.setAttributes(windowAttribute);

        TextView continueCreateLock = dialog.findViewById(R.id.tv_enterokpassword);
        TextView destroy = dialog.findViewById(R.id.tv_destroyenterpassword);
        EditText editText = dialog.findViewById(R.id.edtenterpassword);
        TextView textView = dialog.findViewById(R.id.tventerpassword);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    int color = Color.parseColor("#81A34D");
                    continueCreateLock.setEnabled(true);
                    continueCreateLock.setTextColor(color);
                } else {
                    int color = Color.parseColor("#A5A5A5");
                    continueCreateLock.setEnabled(false);
                    continueCreateLock.setTextColor(color);
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
        continueCreateLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (String.valueOf(editText.getText()).equals(note.getPassword())) {
                    lnLockCreateNote.setVisibility(View.GONE);
                    imgLockNote.setImageResource(R.drawable.unlocknote);
                    imgMoreCreateNote.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                } else {
                    textView.setText("Mật khẩu không đúng");
                    textView.setTextColor(Color.parseColor("#F30404"));
                    editText.setText("");
                }

            }
        });
        dialog.show();
    }

    private void setClickImgLockNote() {
        if(isClickLock){
            imgMoreCreateNote.setVisibility(View.GONE);
        }
        imgLockNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClickLock) {
                    setOpenLockNote();
                } else {
                    isClickLock = !isClickLock;
                    imgLockNote.setImageResource(R.drawable.locknote);
                    lnLockCreateNote.setVisibility(View.VISIBLE);
                    imgMoreCreateNote.setVisibility(View.GONE);
                }
            }
        });


    }

    private void setOnClickLnExit() {
        lnExitCreateNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animation);
                finish();
            }
        });
    }

    //xét sự kiện bàn phím hiện lên
    private void setEventKeyPad() {
        contentView = findViewById(android.R.id.content);
        ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                contentView.getWindowVisibleDisplayFrame(r);
                int screenHeight = contentView.getHeight();
                int keypadHeight = screenHeight - contentView.getRootView().getHeight();
                Log.d("sizekeyboard", String.valueOf(keypadHeight));
                if (keypadHeight < -500) {
                    //Nếu bàn phím hiện và có chứ thì sẽ chuyển ischeckenter để lưu note còn
                    // không sẽ không lưu hoặc xóa note hiện tại
                    tvFinshedCreateNote.setVisibility(View.VISIBLE);

                } else {
                    tvFinshedCreateNote.setVisibility(View.GONE);
                    mEditor.clearFocus();
                    Log.d("databaserich", String.valueOf(mEditor.getHtml() == null));
                    if (mEditor.getHtml() == null) {
                        return;
                    }
                    if (mEditor.getHtml().length() != 0) {
                        if (note == null) {
                            Date date = new Date();
                            date.getTime();
                            isCheckedNewNote = true;
                            if (folder != null) {
                                note = new Note(0, folder.getIdFolder(), nameNote, mEditor.getHtml()
                                        , false, false, false, null, String.valueOf(date));
                            } else {
                                note = new Note(0, 1, nameNote, mEditor.getHtml()
                                        , false, false, false, null, String.valueOf(date));
                            }
                            Log.d("database", " ispin" + String.valueOf(note.isPin())
                                    + " islock" + String.valueOf(note.isLock())
                            );
                            int idNote = (int) databaseHandler.addNote(note);
                            note.setIdNote(idNote);
                            getListNote();
                        } else {
                            Date date = new Date();
                            date.getTime();
                            databaseHandler.updateNote(note.getIdNote(), mEditor.getHtml(), String.valueOf(date));
//                            note = databaseHandler.getNote(note.getIdNote());
                            isCheckNote = true;
                            getListNote();
                        }

                    } else {
                        if (note != null) {
                            databaseHandler.deleteNote(note.getIdNote());
                            isCheckNote = false;
                            getListNote();
                        }
                    }

                }
            }
        };
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
    }


    private void setOnClickTvFinshedCreateNote() {
        tvFinshedCreateNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Thu bàn phím
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
    }

    private void setOnclickImgPlusCreateNote() {
        imgPlusCreateNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isToolbarVisible) {
                    imgPlusCreateNote.setImageResource(R.drawable.cancel);
                    showToolbar();
                } else {
                    imgPlusCreateNote.setImageResource(R.drawable.plus);
                    hideToolbar();
                }
            }
        });
    }

    private void showToolbar() {
        ViewPropertyAnimator animator = toolbarCreateNote.animate()
                .translationX(0)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        toolbarCreateNote.setVisibility(View.VISIBLE);
                    }
                });
        animator.start();
        isToolbarVisible = true;
    }

    private void hideToolbar() {
        ViewPropertyAnimator animator = toolbarCreateNote.animate()
                .translationX(toolbarCreateNote.getWidth())
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        toolbarCreateNote.setVisibility(View.GONE);
                    }
                });
        animator.start();
        isToolbarVisible = false;
    }

    private void setOnCLickImgMoreCreateNote() {
        imgMoreCreateNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (note != null) {
                    note = databaseHandler.getNote(note.getIdNote());
                }

                FragmentBtSheetMoreCreateNote bottomSheetFragment = new FragmentBtSheetMoreCreateNote();
                Bundle args = new Bundle();
                if (note != null) {
                    args.putInt("idnote", note.getIdNote());
                    args.putBoolean("ischecknote", isCheckNote);
                    args.putBoolean("ispin", note.isPin());
                    args.putBoolean("islock", note.isLock());
                    args.putString("password", note.getPassword());
                }
                bottomSheetFragment.setArguments(args);
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });
    }


    private void setOnClickImgHeading() {
        autoCompleteHeading.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mEditor.setHeading(position + 1);
            }
        });
    }

    private void setOnClickImgListNumber() {
        imgListNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animation);
                mEditor.setNumbers();

            }
        });

    }

    private void setOnClickImgBullets() {
        imgBullets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animation);
                mEditor.setBullets();

            }
        });

    }

    private void setOnClickImgUnderline() {
        imgUnderline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animation);
                mEditor.setUnderline();

            }
        });

    }

    private void setOnClickImgItalic() {
        imgItalic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animation);
                mEditor.setItalic();
            }
        });

    }

    private void setOnClickImgBold() {
        imgBold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animation);
                mEditor.setBold();
            }
        });

    }

    private void setOnClickImgRedo() {
        imgRedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animation);
                mEditor.redo();
            }
        });
    }

    private void setOnClickImgUndo() {
        imgUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animation);
                mEditor.undo();
            }
        });

    }

    private void initUI() {
        mEditor = findViewById(R.id.editor);
        imgBold = findViewById(R.id.action_bold);
        imgItalic = findViewById(R.id.action_italic);
        imgBullets = findViewById(R.id.action_insert_bullets);
        imgUnderline = findViewById(R.id.action_underline);
        imgListNumber = findViewById(R.id.action_insert_numbers);
        imgMoreCreateNote = findViewById(R.id.imgmorecreatenote);
        imgPlusCreateNote = findViewById(R.id.imgpluscreatenote);
        toolbarCreateNote = findViewById(R.id.toolbarcratenote);
        tvFinshedCreateNote = findViewById(R.id.tvfinshedcreatenote);
        lnExitCreateNote = findViewById(R.id.lnexitcreatenote);
        autoCompleteHeading = findViewById(R.id.autohompleteheading);
        imgLockNote = findViewById(R.id.imglockcreatenote);
        lnLockCreateNote = findViewById(R.id.lnlockcreatenote);
        tvOpenLockNote = findViewById(R.id.tvopenlocknote);
        imgUndo = findViewById(R.id.action_undo);
        imgRedo = findViewById(R.id.action_redo);
        databaseHandler = new DatabaseHandler(this, "dbnoteapp", null, 1);

        animation = new AlphaAnimation(1f, 0.5f);
        animation.setDuration(200);

        Bundle receivedBundle = getIntent().getExtras();
        if (receivedBundle != null) {
            int idFolder = receivedBundle.getInt("idfolder", 0);
            String nameFolder = receivedBundle.getString("namefolder", null);
            nameNote = receivedBundle.getString("namenote", null);
            if(idFolder != 0){
                folder = new Folder(idFolder, nameFolder);
            }


            int idNote = receivedBundle.getInt("idnote", 0);
            int idNoteFolder = receivedBundle.getInt("idnotefolder", 0);
            String nameNotes = receivedBundle.getString("namenotes", null);
            String noteContext = receivedBundle.getString("notecontext", null);
            String notePassword = receivedBundle.getString("notepassword", null);
            String noteDate = receivedBundle.getString("notedate", null);
            boolean noteIslock = receivedBundle.getBoolean("noteislock", false);
            boolean noteIspin = receivedBundle.getBoolean("noteispin", false);
            isClickLock = noteIslock;

            if (idNote != 0) {
                note = new Note(idNote, idNoteFolder, nameNotes, noteContext, noteIspin
                        , noteIslock, false, notePassword, noteDate);
                if (note.isLock()) {
                    imgLockNote.setVisibility(View.VISIBLE);
                    lnLockCreateNote.setVisibility(View.VISIBLE);
                } else {
                    imgLockNote.setVisibility(View.INVISIBLE);
                    lnLockCreateNote.setVisibility(View.GONE);
                }
            }

            Log.d("databasegetfolder", String.valueOf(idFolder) + " " + nameFolder + " " + nameNote);
        }

        if (nameNote == null || nameNote == "" || nameNote.isEmpty()) {
            nameNote = "Không có tiêu đề";
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.dropdown_heading, headings);
        autoCompleteHeading.setAdapter(adapter);

        listNote = new ArrayList<>();
    }

    private void getListNote() {
        listNote.clear();
        listNote.addAll(databaseHandler.getAllNote());
        for (int i = 0; i < listNote.size(); i++) {
            Log.d("databaselistnote", String.valueOf(listNote.size())
                    + " " + String.valueOf(listNote.get(i).getIdNote())
                    + " idfolder " + String.valueOf(listNote.get(i).getIdFolder())
                    + " " + listNote.get(i).getContext()
                    + " ispin/" + String.valueOf(listNote.get(i).isPin())
                    + " islock/" + String.valueOf(listNote.get(i).isLock())
                    + " pass" + String.valueOf(listNote.get(i).getPassword())
                    + " isChecked/" + String.valueOf(listNote.get(i).isCheckedNewNote())
                    + " " + listNote.get(i).getDate() + "-" + mEditor.getHtml()
                    + " " + String.valueOf(mEditor.getHtml().length()));
        }
    }

}
