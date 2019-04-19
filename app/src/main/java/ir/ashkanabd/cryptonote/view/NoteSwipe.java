package ir.ashkanabd.cryptonote.view;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.rey.material.widget.EditText;

import co.dift.ui.SwipeToAction;
import ir.ashkanabd.cryptonote.R;
import ir.ashkanabd.cryptonote.StartActivity;
import ir.ashkanabd.cryptonote.note.Note;

public class NoteSwipe implements SwipeToAction.SwipeListener<Note>, TextWatcher {
    private StartActivity activity;
    private MaterialDialog deleteDialog;
    private MaterialDialog passwordDialog;
    private TextView deleteText;
    private TextView deleteButton;
    private TextView cancelButtonDeleteDialog;
    private Note currentItem;
    private TextView passwordText;
    private TextView openButton;
    private TextView cancelButtonPasswordDialog;
    private EditText inputPasswordEdit;


    public NoteSwipe(StartActivity activity) {
        this.activity = activity;
        setupDeleteDialog();
        setupPasswordDialog();
    }

    private void setupDeleteDialog() {
        deleteDialog = new MaterialDialog(activity);
        deleteDialog.setCancelable(true);
        deleteDialog.setContentView(R.layout.note_delete_layout);
        deleteText = deleteDialog.findViewById(R.id.delete_dialog_text);
        deleteButton = deleteDialog.findViewById(R.id.delete_dialog_delete_btn);
        cancelButtonDeleteDialog = deleteDialog.findViewById(R.id.delete_dialog_cancel_btn);
        deleteButton.setOnClickListener((v) -> deleteNote());
        cancelButtonDeleteDialog.setOnClickListener((v) -> deleteDialog.dismiss());
    }

    private void setupPasswordDialog() {
        passwordDialog = new MaterialDialog(activity);
        passwordDialog.setCancelable(true);
        passwordDialog.setContentView(R.layout.note_password_layout);
        passwordText = passwordDialog.findViewById(R.id.password_dialog_text);
        openButton = passwordDialog.findViewById(R.id.password_dialog_open_btn);
        cancelButtonPasswordDialog = passwordDialog.findViewById(R.id.password_dialog_cancel_btn);
        inputPasswordEdit = passwordDialog.findViewById(R.id.password_dialog_input);
        openButton.setOnClickListener(_1 -> openNote());
        cancelButtonPasswordDialog.setOnClickListener(_1 -> passwordDialog.dismiss());
        inputPasswordEdit.addTextChangedListener(this);
    }

    private void openNote() {
        if (inputPasswordEdit.getError() == null) {
            Toast.makeText(activity, "Note opened", Toast.LENGTH_SHORT).show();
        }
        //todo send to open note Activity
    }

    private void deleteNote() {
        activity.getNoteHandler().deleteNote(currentItem);
        activity.refreshNotes();
        deleteDialog.dismiss();
    }


    @Override
    public boolean swipeLeft(Note itemData) {
        currentItem = itemData;
        deleteText.setText("Delete note " + itemData.getTitle() + "?");
        deleteDialog.show();
        return true;
    }

    @Override
    public boolean swipeRight(Note itemData) {
        return true;
    }

    @Override
    public void onClick(Note itemData) {
        currentItem = itemData;
        if (itemData.isEncrypted()) {
            passwordText.setText("Enter password for " + itemData.getTitle());
            inputPasswordEdit.setText("");
            passwordDialog.show();
        } else {
            openNote();
        }
    }

    @Override
    public void onLongClick(Note itemData) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().equals(currentItem.getPassword())) {
            inputPasswordEdit.clearError();
        } else {
            inputPasswordEdit.setError("Incorrect password");
        }
    }
}
