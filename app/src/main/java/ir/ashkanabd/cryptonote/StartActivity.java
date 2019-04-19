package ir.ashkanabd.cryptonote;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.button.MaterialButton;
import com.rey.material.widget.CheckBox;
import com.rey.material.widget.EditText;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import co.dift.ui.SwipeToAction;
import ir.ashkanabd.cryptonote.note.Note;
import ir.ashkanabd.cryptonote.note.NoteHandler;
import ir.ashkanabd.cryptonote.view.NewNotePasswordWatcher;
import ir.ashkanabd.cryptonote.view.NoteAdapter;
import ir.ashkanabd.cryptonote.view.NoteSwipe;

public class StartActivity extends AppCompatActivity {

    private NoteHandler noteHandler;
    private Encryption encryption;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private NoteAdapter noteAdapter;
    private MaterialDialog createNoteDialog;
    private EditText noteTitleEdit, noteDescEdit, notePasswordEdit;
    private MaterialButton noteCreateButton;
    private CheckBox noteEncryptCheck;
    private List<File> noteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        checkPermission();
        findViews();
        setup();
        loadNotes();
    }

    private void checkPermission() {
        if (!checkStoragePermission()) {
            requestStoragePermission();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            while (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ;
        }
    }

    private void findViews() {
        recyclerView = findViewById(R.id.notes_recycle_view);
        swipeRefresh = findViewById(R.id.swipeRefresh);
    }


    private void setup() {
        encryption = new Encryption(this);
        noteHandler = new NoteHandler(new File(Environment.getExternalStorageDirectory(), "/Notes"));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        layoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.addItemDecoration(divider);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        new SwipeToAction(recyclerView, new NoteSwipe(this));
        swipeRefresh.setOnRefreshListener(this::refreshNotes);
        swipeRefresh.setColorSchemeColors(Color.RED, Color.BLUE);
        setupCreateNoteDialog();
    }

    private void setupCreateNoteDialog() {
        createNoteDialog = new MaterialDialog(this);
        createNoteDialog.setContentView(R.layout.new_note_dialog);
        noteTitleEdit = createNoteDialog.findViewById(R.id.new_note_title);
        noteDescEdit = createNoteDialog.findViewById(R.id.new_note_desc);
        notePasswordEdit = createNoteDialog.findViewById(R.id.new_note_password);
        noteCreateButton = createNoteDialog.findViewById(R.id.new_note_create);
        noteEncryptCheck = createNoteDialog.findViewById(R.id.new_note_encrypt);
        createNoteDialog.setCancelable(true);
        noteTitleEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                noteTitleEdit.clearError();
                if (s.toString().trim().isEmpty()) {
                    noteTitleEdit.setError("Note title is empty");
                } else {
                    noteTitleEdit.clearError();
                }
                for (File file : noteList) {
                    if (file.getName().equals(s.toString().trim())){
                        noteTitleEdit.setError("Note exists");
                    }
                }
            }
        });
        notePasswordEdit.addTextChangedListener(new NewNotePasswordWatcher());
        noteEncryptCheck.setOnCheckedChangeListener((_1, checked) -> this.encryptChecKChange(checked));
        noteCreateButton.setOnClickListener(v -> createNewNote());
    }

    private void encryptChecKChange(boolean checked) {
        if (checked) notePasswordEdit.setEnabled(true);
        else notePasswordEdit.setEnabled(false);
    }

    private void createNewNote() {
        String title = noteTitleEdit.getText().toString().trim();
        String desc = noteDescEdit.getText().toString();
        String password = notePasswordEdit.getText().toString();
        boolean encrypted = noteEncryptCheck.isChecked();
        if (title.trim().isEmpty()) {
            return;
        }
        for (File file : noteList) {
            if (file.getName().equals(title.trim())){
                return;
            }
        }
        if (encrypted) {
            if (password.length() > 17 || password.isEmpty()) {
                return;
            }
        }
        try {
            Note newNote = Note.createNote(noteHandler, title, encryption);
            newNote.setDescription(desc);
            newNote.setEncrypted(encrypted);
            newNote.setPassword(password);
            newNote.setTitle(title);
            newNote.save();
            createNoteDialog.dismiss();
            refreshNotes();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            // TODO: 4/19/19 Handle storage errors
        }
    }

    public void refreshNotes() {
        loadNotes();
        new Handler().postDelayed(() -> {
            swipeRefresh.setRefreshing(false);
        }, 1500);
    }

    private void loadNotes() {
        noteList = Arrays.asList(noteHandler.getNotesAsFile());
        noteAdapter = new NoteAdapter(noteList, encryption);
        recyclerView.setAdapter(noteAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void newNote(View view) {
        noteTitleEdit.setText("");
        noteDescEdit.setText("");
        notePasswordEdit.setText("");
        noteTitleEdit.requestFocus();
        noteEncryptCheck.setChecked(false);
        createNoteDialog.show();
        refreshNotes();
    }

    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    public NoteHandler getNoteHandler() {
        return noteHandler;
    }
}
