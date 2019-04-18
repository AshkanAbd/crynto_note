package ir.ashkanabd.cryptonote.note;

import java.io.File;
import java.io.IOException;

public class NoteHandler {
    private File path;

    public NoteHandler(File path) {
        this.path = path;
        if (!path.exists())
            path.mkdirs();
    }

    File createNewNote(String name) throws IOException {
        File newNote = new File(path, name);
        if (newNote.exists()){
            throw new IOException("File exists");
        }
        if (!newNote.createNewFile())
            throw new IOException("Can't create file");
        return newNote;
    }

    public String[] getNotesAsString(){
        return path.list();
    }

    public File[] getNotesAsFile(){
        return path.listFiles();
    }
}
