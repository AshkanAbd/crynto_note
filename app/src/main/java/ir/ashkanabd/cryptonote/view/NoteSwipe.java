package ir.ashkanabd.cryptonote.view;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.rey.material.widget.EditText;

import co.dift.ui.SwipeToAction;
import ir.ashkanabd.cryptonote.EditActivity;
import ir.ashkanabd.cryptonote.R;
import ir.ashkanabd.cryptonote.StartActivity;
import ir.ashkanabd.cryptonote.note.Note;

public class NoteSwipe implements SwipeToAction.SwipeListener<Note>, TextWatcher {
    private StartActivity activity;
    private MaterialDialog deleteDialog;
    private MaterialDialog passwordDialog;
    private TextView deleteText;
    private Note currentItem;
    private TextView passwordText;
    private EditText inputPasswordEdit;
    private EditText deleteInputPasswordEdit;


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
        TextView deleteButton = deleteDialog.findViewById(R.id.delete_dialog_delete_btn);
        deleteInputPasswordEdit = deleteDialog.findViewById(R.id.delete_dialog_input);
        deleteInputPasswordEdit.addTextChangedListener(this);
        TextView cancelButtonDeleteDialog = deleteDialog.findViewById(R.id.delete_dialog_cancel_btn);
        deleteButton.setOnClickListener((v) -> deleteNote());
        cancelButtonDeleteDialog.setOnClickListener((v) -> deleteDialog.dismiss());
    }

    private void setupPasswordDialog() {
        passwordDialog = new MaterialDialog(activity);
        passwordDialog.setCancelable(true);
        passwordDialog.setContentView(R.layout.note_password_layout);
        passwordText = passwordDialog.findViewById(R.id.password_dialog_text);
        TextView openButton = passwordDialog.findViewById(R.id.password_dialog_open_btn);
        TextView cancelButtonPasswordDialog = passwordDialog.findViewById(R.id.password_dialog_cancel_btn);
        inputPasswordEdit = passwordDialog.findViewById(R.id.password_dialog_input);
        openButton.setOnClickListener(_1 -> openNote());
        cancelButtonPasswordDialog.setOnClickListener(_1 -> passwordDialog.dismiss());
        inputPasswordEdit.addTextChangedListener(this);
        inputPasswordEdit.clearError();
    }

    private void openNote() {
        if (inputPasswordEdit.getError() == null || !currentItem.isEncrypted()) {
            Intent editIntent = new Intent(activity, EditActivity.class);
            editIntent.putExtra("note", currentItem);
            editIntent.putExtra("allNotes", activity.getNoteList());
            activity.startActivityForResult(editIntent, StartActivity.EDIT_RESULT);
            passwordDialog.dismiss();
        }
    }

    private void deleteNote() {
        if (currentItem.isEncrypted()) {
            if (!deleteInputPasswordEdit.getText().toString().equals(currentItem.getPassword())) {
                return;
            }
        }
        activity.getNoteHandler().deleteNote(currentItem);
        activity.refreshNotes();
        deleteDialog.dismiss();
    }


    @Override
    public boolean swipeLeft(Note itemData) {
        currentItem = itemData;
        deleteText.setText("Delete note " + itemData.getTitle() + "?");
        deleteInputPasswordEdit.setText("");
        if (!currentItem.isEncrypted()) {
            deleteInputPasswordEdit.setVisibility(View.GONE);
        } else {
            deleteInputPasswordEdit.setVisibility(View.VISIBLE);
        }
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
        if (s.toString().equals(currentItem.getPassword())) {
            deleteInputPasswordEdit.clearError();
        } else {
            deleteInputPasswordEdit.setError("Incorrect password");
        }

    }
}
