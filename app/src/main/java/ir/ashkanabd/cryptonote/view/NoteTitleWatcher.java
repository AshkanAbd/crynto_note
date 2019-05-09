package ir.ashkanabd.cryptonote.view;

import android.text.Editable;
import android.text.TextWatcher;

import com.rey.material.widget.EditText;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NoteTitleWatcher implements TextWatcher {
    private EditText editText;
    private List<File> noteList;

    public NoteTitleWatcher(EditText editText, List<File> noteList) {
        this.editText = editText;
        if (noteList == null){
            this.noteList = new ArrayList<>();
        }else{
            this.noteList = noteList;
        }
        editText.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        editText.clearError();
        if (s.toString().trim().isEmpty()) {
            editText.setError("Note title is empty");
        } else {
            editText.clearError();
        }
        for (File file : noteList) {
            if (file.getName().equals(s.toString().trim())) {
                editText.setError("Note exists");
            }
        }
    }

    public EditText getEditText() {
        return editText;
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    public List<File> getNoteList() {
        return noteList;
    }

    public void setNoteList(List<File> noteList) {
        this.noteList = noteList;
    }
}
