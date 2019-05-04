package ir.ashkanabd.cryptonote.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ir.ashkanabd.cryptonote.R;
import ir.ashkanabd.cryptonote.note.Note;

public class NoteAdapter extends RecyclerView.Adapter<NoteViewHolder> {
    private List<File> notes;
    private Context context;

    public NoteAdapter(List<File> notes, Context context) {
        this.notes = notes;
        this.context = context;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item_layout, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        try {
            Note note = Note.readNote(notes.get(position), context);
            holder.getTitle().setText(note.getTitle());
            holder.getDescription().setText(note.getDescription());
            if (note.isEncrypted()) {
                holder.getEncryption().setBackground(context.getResources().getDrawable(R.drawable.encrypt_icon));
            } else {
                holder.getEncryption().setBackground(context.getResources().getDrawable(R.drawable.decrypted_icon));
            }
            holder.data = note;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }
}
