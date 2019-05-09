package ir.ashkanabd.cryptonote;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import es.dmoral.toasty.Toasty;
import ir.ashkanabd.cryptonote.note.Note;
import ir.ashkanabd.cryptonote.view.NotePasswordWatcher;
import ir.ashkanabd.cryptonote.view.NoteTitleWatcher;

import android.os.Bundle;
import android.view.View;

import com.rey.material.widget.Button;
import com.rey.material.widget.EditText;
import com.rey.material.widget.RelativeLayout;
import com.rey.material.widget.TextView;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class EditActivity extends AppCompatActivity {

    private static boolean EDIT_MODE = true;
    private static boolean SAVE_MODE = false;

    private Note currentNote;
    private ArrayList<File> allNotes;
    private EditText titleEdit;
    private EditText descriptionEdit;
    private EditText passwordEdit;
    private EditText textEdit;
    private Button noteActionButton;
    private TextView titleTextView;
    private boolean mode = SAVE_MODE;
    private NoteTitleWatcher titleWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        findViews();
        getNote();
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(getActionbarView());
    }

    private void findViews() {
        titleEdit = findViewById(R.id.edit_note_title);
        titleWatcher = new NoteTitleWatcher(titleEdit, allNotes);
        descriptionEdit = findViewById(R.id.edit_note_description);
        passwordEdit = findViewById(R.id.edit_note_password);
        passwordEdit.addTextChangedListener(new NotePasswordWatcher());
        textEdit = findViewById(R.id.edit_note_text);
    }

    private void getNote() {
        if (getIntent().getExtras() == null) {
            Toasty.error(this, "Something went wrong", Toasty.LENGTH_SHORT).show();
            finish();
        }
        currentNote = (Note) getIntent().getExtras().get("note");
        allNotes = (ArrayList<File>) getIntent().getExtras().get("allNotes");
        allNotes.remove(currentNote.getPath());
        titleWatcher.setNoteList(allNotes);
        if (currentNote == null) {
            Toasty.error(this, "Something went wrong", Toasty.LENGTH_SHORT).show();
            finish();
        }
        if (!currentNote.isEncrypted()) {
            passwordEdit.setVisibility(View.GONE);
        }
        titleEdit.setText(currentNote.getTitle());
        descriptionEdit.setText(currentNote.getDescription());
        passwordEdit.setText(currentNote.getPassword());
        textEdit.setText(currentNote.getText());
    }

    private RelativeLayout getActionbarView() {
        RelativeLayout layout = (RelativeLayout) getLayoutInflater().inflate(R.layout.edit_toolbar, null);
        titleTextView = layout.findViewById(R.id.title_text_view);
        titleTextView.setText("Edit note: " + currentNote.getTitle());
        noteActionButton = layout.findViewById(R.id.note_action_btn);
        noteActionButton.setOnClickListener(_1 -> noteAction());
        return layout;
    }

    private void noteAction() {
        mode = !mode;
        if (mode == SAVE_MODE) {
            if (passwordEdit.getVisibility() != View.GONE && passwordEdit.getText().length() > 16) {
                Toasty.error(this, "Password too long.", Toasty.LENGTH_SHORT).show();
            } else if (passwordEdit.getVisibility() != View.GONE && passwordEdit.getText().length() < 1) {
                Toasty.error(this, "Password too short.", Toasty.LENGTH_SHORT).show();
            } else if (titleEdit.getError() != null) {
                Toasty.error(this, "Invalid note title", Toasty.LENGTH_SHORT).show();
            } else {
                noteActionButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.edit_icon));
                titleEdit.setEnabled(false);
                descriptionEdit.setEnabled(false);
                passwordEdit.setEnabled(false);
                textEdit.setEnabled(false);
                save();
            }
        }
        if (mode == EDIT_MODE) {
            noteActionButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.save_icon));
            titleEdit.setEnabled(true);
            descriptionEdit.setEnabled(true);
            passwordEdit.setEnabled(true);
            textEdit.setEnabled(true);
        }
    }

    private void save() {
        File preFile = currentNote.getPath();
        preFile.delete();
        String title = titleEdit.getText().toString();
        currentNote.setPath(new File(preFile.getParent(), title));
        currentNote.setTitle(titleEdit.getText().toString());
        currentNote.setDescription(descriptionEdit.getText().toString());
        currentNote.setText(textEdit.getText().toString());
        if (currentNote.isEncrypted()) {
            currentNote.setPassword(passwordEdit.getText().toString());
        }
        try {
            currentNote.save(this);
            titleTextView.setText("Edit note: " + currentNote.getTitle());
            Toasty.success(this, "Note saved successfully", Toasty.LENGTH_SHORT).show();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            // TODO: 5/5/19 catch storage exception
        }
    }

    @Override
    public void onBackPressed() {
        if (mode == SAVE_MODE) {
            setResult(StartActivity.EDIT_RESULT);
            super.onBackPressed();
        }
        if (mode == EDIT_MODE) {
            Toasty.warning(this, "Please save note", Toasty.LENGTH_SHORT).show();
        }
    }
}
