package ir.ashkanabd.cryptonote.note;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import ir.ashkanabd.cryptonote.Encryption;

public class Note {
    private String title;
    private String description;
    private String password;
    private String text;
    private boolean encrypted;
    private File path;
    private Encryption encryption;

    public static Note readNote(File noteFile, Encryption encryption) throws IOException, JSONException {
        Note note = new Note(noteFile, encryption);
        JSONObject jsonObj = new JSONObject(readFile(noteFile));
        note.encrypted = jsonObj.getBoolean("enc");
        note.title = jsonObj.getString("title");
        note.description = jsonObj.getString("desc");
        note.text = jsonObj.getString("txt");
        if (note.encrypted) {
            note.password = encryption.defaultDecrypt(jsonObj.getString("pass")).trim();
            note.text = encryption.decrypt(note.text, note.password);
        }
        return note;
    }

    public static Note createNote(NoteHandler noteHandler, String noteName, Encryption encryption) throws IOException, JSONException {
        File noteFile = noteHandler.createNewNote(noteName);
        Note note = new Note(noteFile, encryption);
        note.save();
        return note;
    }

    private static String readFile(File inputFile) throws IOException {
        StringBuilder builder = new StringBuilder();
        Scanner scn = new Scanner(inputFile);
        while (scn.hasNextLine()) {
            builder.append(scn.nextLine());
        }
        scn.close();
        return builder.toString();
    }

    public void save() throws JSONException, IOException {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("enc", encrypted);
        jsonObj.put("title", title);
        jsonObj.put("desc", description);
        if (encrypted) {
            normalizePassword();
            jsonObj.put("pass", encryption.defaultEncrypt(password));
            jsonObj.put("txt", encryption.encrypt(text, password));
        } else {
            jsonObj.put("txt", text);
        }
        writeFile(path, jsonObj.toString());
    }

    private void normalizePassword() {
        StringBuilder builder = new StringBuilder(password);
        while (builder.length() != 16) {
            builder.append(" ");
        }
        password = builder.toString();
    }

    private static void writeFile(File outputFile, String string) throws IOException {
        PrintWriter writer = new PrintWriter(outputFile);
        writer.println(string);
        writer.close();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public File getPath() {
        return path;
    }

    private Note(File path, Encryption encryption) {
        this.path = path;
        this.text = "";
        this.encrypted = false;
        this.description = "";
        this.title = "";
        this.password = "";
        this.encryption = encryption;
    }
}
