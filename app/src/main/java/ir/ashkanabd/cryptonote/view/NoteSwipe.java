package ir.ashkanabd.cryptonote.view;

import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import co.dift.ui.SwipeToAction;
import ir.ashkanabd.cryptonote.R;
import ir.ashkanabd.cryptonote.StartActivity;
import ir.ashkanabd.cryptonote.note.Note;

public class NoteSwipe implements SwipeToAction.SwipeListener<Note> {
    private StartActivity activity;
    private MaterialDialog deleteDialog;
    private TextView deleteText;
    private TextView deleteButton;
    private TextView cancelButtonDeleteDialog;
    private Note currentItem;

    public NoteSwipe(StartActivity activity) {
        this.activity = activity;
        setupDeleteDialog();
    }

    private void setupDeleteDialog() {
        deleteDialog = new MaterialDialog(activity);
        deleteDialog.setCancelable(false);
        deleteDialog.setContentView(R.layout.note_delete_layout);
        deleteText = deleteDialog.findViewById(R.id.delete_dialog_text);
        deleteButton = deleteDialog.findViewById(R.id.delete_dialog_delete_btn);
        cancelButtonDeleteDialog = deleteDialog.findViewById(R.id.delete_dialog_cancel_btn);
        deleteButton.setOnClickListener((v) -> deleteNote());
        cancelButtonDeleteDialog.setOnClickListener((v) -> deleteDialog.dismiss());
    }

    private void deleteNote() {
        activity.getNoteHandler().deleteNote(currentItem);
        activity.refreshNotes();
        deleteDialog.dismiss();
    }


    @Override
    public boolean swipeLeft(Note itemData) {
        currentItem = itemData;
        deleteText.setText("Delete note " + itemData.getTitle() + "?");
        deleteDialog.show();
        return true;
    }

    @Override
    public boolean swipeRight(Note itemData) {
        return true;
    }

    @Override
    public void onClick(Note itemData) {

    }

    @Override
    public void onLongClick(Note itemData) {

    }
}
