package com.example.noteapp.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.noteapp.R;
import com.example.noteapp.activity.CreateNoteActivity;
import com.example.noteapp.activity.NotesActivity;
import com.example.noteapp.model.DatabaseHandler;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class FragmentBtSheetMoreCreateNote extends BottomSheetDialogFragment {
    private  View view;
    private LinearLayout lnPin, lnDelete, lnLock;
    private ImageView lnPinImg, lnDeleteImg, lnLockImg;
    private TextView lnPinTv, lnDeleteTv, lnLockTv;
    private DatabaseHandler databaseHandler;
    private int idNote;
    private String password;
    private Boolean isCheckNote, isPin, isLock;
    private Animation animation;
    private Dialog dialog ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_btshmorecreatenote, container, false);
        initUI();

        setOnClickDelete();
        setOnClickPin();
        setOnClickLock();


        return view;
    }

    private void setOnClickLock() {
        lnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animation);
                if(isLock){
                    isLock = !isLock;
                    setDialogEnterPassword(isLock);
                }else {
                    isLock = !isLock;
                    setDialogCreatePassword(isLock);
                }
                Log.i("database", "pin"+ String.valueOf(isPin));

            }
        });
    }

    private void setDialogCreatePassword(boolean islock) {
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_create_password);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttribute = window.getAttributes();
        windowAttribute.gravity = Gravity.CENTER;
        window.setAttributes(windowAttribute);

        TextView saveCreateLock = dialog.findViewById(R.id.tv_savecreatepassword);
        EditText editText = dialog.findViewById(R.id.edtcreatepassword);
        TextView destroy = dialog.findViewById(R.id.tv_destroycreatepassword);
        destroy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    int color = Color.parseColor("#81A34D");
                    saveCreateLock.setEnabled(true);
                    saveCreateLock.setTextColor(color);
                } else {
                    int color = Color.parseColor("#A5A5A5");
                    saveCreateLock.setEnabled(false);
                    saveCreateLock.setTextColor(color);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        saveCreateLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lnLockImg.setImageResource(R.drawable.unlocked);
                lnLockTv.setTextColor(Color.parseColor("#071F9C"));
                lnLockTv.setText("Bỏ khóa");
                databaseHandler.updateIsLockNote(idNote, islock, String.valueOf(editText.getText()));
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .remove(FragmentBtSheetMoreCreateNote.this).commit();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void setDialogEnterPassword(boolean islock) {
        dialog = new Dialog(getActivity());
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
                if(String.valueOf(editText.getText()).equals(password)){
                    lnLockImg.setImageResource(R.drawable.lock);
                    lnLockTv.setTextColor(Color.parseColor("#071F9C"));
                    lnLockTv.setText("Khóa");
                    databaseHandler.updateUnIsLockNote(idNote);
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .remove(FragmentBtSheetMoreCreateNote.this).commit();
                    dialog.dismiss();
                }else {
                    textView.setText("Mật khẩu không đúng");
                    textView.setTextColor(Color.parseColor("#F30404"));
                    editText.setText("");
                }

            }
        });
        dialog.show();
    }

    private void setOnClickPin() {
        lnPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animation);
                if(isPin){
                    isPin = !isPin;
                    lnPinImg.setImageResource(R.drawable.pin);
                    lnPinTv.setTextColor(Color.parseColor("#ECAA18"));
                    lnPinTv.setText("Ghim");
                    databaseHandler.updateIsPinNote(idNote, isPin);
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .remove(FragmentBtSheetMoreCreateNote.this).commit();
                }else {
                    isPin = !isPin;
                    lnPinImg.setImageResource(R.drawable.unpin);
                    lnPinTv.setTextColor(Color.parseColor("#ECAA18"));
                    lnPinTv.setText("Bỏ ghim");
                    databaseHandler.updateIsPinNote(idNote, isPin);
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .remove(FragmentBtSheetMoreCreateNote.this).commit();

                }
                Log.i("database", "pin"+ String.valueOf(isPin));

            }
        });

    }

    private void setOnClickDelete() {
        lnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseHandler.deleteNote(idNote);
                if (getActivity() != null) {
                    getActivity().finish();
                }

            }
        });

    }

    private void initUI() {
        lnDelete = view.findViewById(R.id.lndeletebtsheet);
        lnPin = view.findViewById(R.id.lnpinbtsheet);
        lnLock = view.findViewById(R.id.lnlockbtsheet);
        lnDeleteImg = view.findViewById(R.id.imglndeletebtsheet);
        lnPinImg = view.findViewById(R.id.imglnpinbtsheet);
        lnLockImg = view.findViewById(R.id.imglnlockbtsheet);
        lnPinTv = view.findViewById(R.id.tvlnpinbtsheet);
        lnLockTv = view.findViewById(R.id.tvlnlockbtsheet);
        lnDeleteTv = view.findViewById(R.id.tvlndeletebtsheet);

        animation = new AlphaAnimation(1f, 0.5f);
        animation.setDuration(200);

        databaseHandler = new DatabaseHandler(getActivity(), "dbnoteapp", null, 1);

        Bundle args = getArguments();
        if (args != null) {
            idNote = args.getInt("idnote", 0);
            isCheckNote = args.getBoolean("ischecknote", false);
            isPin = args.getBoolean("ispin", false);
            isLock = args.getBoolean("islock", false);
            password = args.getString("password", null);
        }
        if (idNote == 0){
            int color = Color.parseColor("#A5A5A5");
            lnDelete.setEnabled(false);
            lnPin.setEnabled(false);
            lnLock.setEnabled(false);

            lnDeleteImg.setImageResource(R.drawable.trash_can_enable);
            lnDeleteTv.setTextColor(color);

            lnLockImg.setImageResource(R.drawable.lock_enable);
            lnLockTv.setTextColor(color);

            lnPinImg.setImageResource(R.drawable.pin_enable);
            lnPinTv.setTextColor(color);


        }else{
            if(isCheckNote){
                lnDelete.setEnabled(true);
                lnPin.setEnabled(true);
                lnLock.setEnabled(true);

                lnDeleteImg.setImageResource(R.drawable.trash_can);
                lnDeleteTv.setTextColor(Color.parseColor("#D1051D"));

                if(isLock){
                    lnLockImg.setImageResource(R.drawable.unlocked);
                    lnLockTv.setTextColor(Color.parseColor("#071F9C"));
                    lnLockTv.setText("Bỏ khóa");
                }else {
                    lnLockImg.setImageResource(R.drawable.lock);
                    lnLockTv.setTextColor(Color.parseColor("#071F9C"));
                    lnLockTv.setText("Khóa");
                }

                if(isPin){
                    lnPinImg.setImageResource(R.drawable.unpin);
                    lnPinTv.setTextColor(Color.parseColor("#ECAA18"));
                    lnPinTv.setText("Bỏ ghim");
                }else {
                    lnPinImg.setImageResource(R.drawable.pin);
                    lnPinTv.setTextColor(Color.parseColor("#ECAA18"));
                    lnPinTv.setText("Ghim");
                }

            }else {
                int color = Color.parseColor("#A5A5A5");
                lnDelete.setEnabled(false);
                lnPin.setEnabled(false);
                lnLock.setEnabled(false);

                lnDeleteImg.setImageResource(R.drawable.trash_can_enable);
                lnDeleteTv.setTextColor(color);

                lnLockImg.setImageResource(R.drawable.lock_enable);
                lnLockTv.setTextColor(color);

                lnPinImg.setImageResource(R.drawable.pin_enable);
                lnPinTv.setTextColor(color);
            }
        }
    }
}
