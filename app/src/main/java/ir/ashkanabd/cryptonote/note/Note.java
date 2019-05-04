package ir.ashkanabd.cryptonote.note;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Scanner;

import ir.ashkanabd.cryptonote.Encryption;

public class Note implements Serializable {
    private String title;
    private String description;
    private String password;
    private String text;
    private boolean encrypted;
    private File path;

    public static Note readNote(File noteFile, Context context) throws IOException, JSONException {
        Note note = new Note(noteFile);
        JSONObject jsonObj = new JSONObject(readFile(noteFile));
        note.encrypted = jsonObj.getBoolean("enc");
        note.title = jsonObj.getString("title");
        note.description = jsonObj.getString("desc");
        note.text = jsonObj.getString("txt");
        if (note.encrypted) {
            note.password = Encryption.defaultDecrypt(jsonObj.getString("pass"), context).trim();
            note.text = Encryption.decrypt(note.text, note.password, context);
        }
        return note;
    }

    public static Note createNote(NoteHandler noteHandler, String noteName, Context context) throws IOException, JSONException {
        File noteFile = noteHandler.createNewNote(noteName);
        Note note = new Note(noteFile);
        note.save(context);
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

    public void save(Context context) throws JSONException, IOException {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("enc", encrypted);
        jsonObj.put("title", title);
        jsonObj.put("desc", description);
        if (encrypted) {
            normalizePassword();
            jsonObj.put("pass", Encryption.defaultEncrypt(password, context));
            jsonObj.put("txt", Encryption.encrypt(text, password, context));
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

    private Note(File path) {
        this.path = path;
        this.text = "";
        this.encrypted = false;
        this.description = "";
        this.title = "";
        this.password = "";
    }
}
