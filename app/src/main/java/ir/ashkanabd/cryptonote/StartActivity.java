package ir.ashkanabd.cryptonote;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import ir.ashkanabd.cryptonote.note.Note;
import ir.ashkanabd.cryptonote.note.NoteHandler;

public class StartActivity extends AppCompatActivity {

    private NoteHandler noteHandler;
    private ListView listView;
    private ArrayAdapter<File> adapter;
    private Encryption encryption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        if (!checkStoragePermission()) {
            requestStoragePermission();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            while (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ;
        }
        encryption = new Encryption(this);
        noteHandler = new NoteHandler(new File(Environment.getExternalStorageDirectory(), "/Notes"));
        listView = findViewById(R.id.notes_listview);
        loadNotes();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((_1, _2, pos, _3) -> onItemClicked(pos));
    }

    private void onItemClicked(int pos) {
        try {
            Note note = Note.readNote(adapter.getItem(pos), encryption);
            System.out.println(note.getPath());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public void newNote(View view) {
        try {
            Note note = Note.createNote(noteHandler, "mynote", encryption);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadNotes() {
        File[] notes = noteHandler.getNotesAsFile();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notes);
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

}
