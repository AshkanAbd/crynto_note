package ir.ashkanabd.cryptonote;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import co.dift.ui.SwipeToAction;
import ir.ashkanabd.cryptonote.note.Note;
import ir.ashkanabd.cryptonote.note.NoteHandler;
import ir.ashkanabd.cryptonote.view.NoteAdapter;
import ir.ashkanabd.cryptonote.view.NoteSwipe;

public class StartActivity extends AppCompatActivity {

    private NoteHandler noteHandler;
    private Encryption encryption;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private NoteAdapter noteAdapter;

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
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        new SwipeToAction(recyclerView, new NoteSwipe(this));
        swipeRefresh.setOnRefreshListener(this::refreshNotes);
        swipeRefresh.setColorSchemeColors(Color.RED, Color.BLUE);
    }

    public void refreshNotes() {
        loadNotes();
        new Handler().postDelayed(() -> {
            swipeRefresh.setRefreshing(false);
        }, 1500);
    }

    private void loadNotes() {
        noteAdapter = new NoteAdapter(noteHandler.getNotesAsFile(), encryption);
        recyclerView.setAdapter(noteAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
    }

    public void newNote(View view) {
        try {
            Note.createNote(noteHandler, "mynote", encryption);
        } catch (IOException | JSONException e) {
            // TODO: 4/19/19 catch storage error
        }
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
