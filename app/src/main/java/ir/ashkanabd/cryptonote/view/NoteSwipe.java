package ir.ashkanabd.cryptonote.view;

import co.dift.ui.SwipeToAction;
import ir.ashkanabd.cryptonote.StartActivity;
import ir.ashkanabd.cryptonote.note.Note;

public class NoteSwipe implements SwipeToAction.SwipeListener<Note> {
    private StartActivity activity;

    public NoteSwipe(StartActivity activity) {
        this.activity = activity;
    }

    @Override
    public boolean swipeLeft(Note itemData) {
        activity.getNoteHandler().deleteNote(itemData);
        activity.refreshNotes();
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
