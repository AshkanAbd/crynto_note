package ir.ashkanabd.cryptonote;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import co.dift.ui.SwipeToAction;
import es.dmoral.toasty.Toasty;
import ir.ashkanabd.cryptonote.note.Note;
import ir.ashkanabd.cryptonote.note.NoteHandler;
import ir.ashkanabd.cryptonote.view.NotePasswordWatcher;
import ir.ashkanabd.cryptonote.view.NoteAdapter;
import ir.ashkanabd.cryptonote.view.NoteSwipe;
import ir.ashkanabd.cryptonote.view.NoteTitleWatcher;

public class StartActivity extends AppCompatActivity {

    public static int EDIT_RESULT = 10101;

    public static Context appContext;
    private NoteHandler noteHandler;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private MaterialDialog createNoteDialog;
    private EditText noteTitleEdit, noteDescEdit, notePasswordEdit;
    private CheckBox noteEncryptCheck;
    private ArrayList<File> noteList;
    private boolean backPress = false;
    private NoteTitleWatcher titleWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        appContext = this;
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
        MaterialButton noteCreateButton = createNoteDialog.findViewById(R.id.new_note_create);
        noteEncryptCheck = createNoteDialog.findViewById(R.id.new_note_encrypt);
        createNoteDialog.setCancelable(true);
        titleWatcher = new NoteTitleWatcher(noteTitleEdit, noteList);
        notePasswordEdit.addTextChangedListener(new NotePasswordWatcher());
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
            if (file.getName().equals(title.trim())) {
                return;
            }
        }
        if (encrypted) {
            if (password.length() > 17 || password.isEmpty()) {
                return;
            }
        }
        try {
            Note newNote = Note.createNote(noteHandler, title, this);
            newNote.setDescription(desc);
            newNote.setEncrypted(encrypted);
            newNote.setPassword(password);
            newNote.setTitle(title);
            newNote.save(this);
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
        }, 1000);
    }

    private void loadNotes() {
        noteList = new ArrayList<>(Arrays.asList(noteHandler.getNotesAsFile()));
        titleWatcher.setNoteList(noteList);
        NoteAdapter noteAdapter = new NoteAdapter(noteList, this);
        recyclerView.setAdapter(noteAdapter);
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

    public ArrayList<File> getNoteList() {
        return noteList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == EDIT_RESULT) {
            loadNotes();
        }
    }

    @Override
    public void onBackPressed() {
        if (backPress) {
            super.onBackPressed();
        } else {
            Toasty.warning(this, "Press back agian", Toasty.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> backPress = true, 2000);
        }
    }
}
