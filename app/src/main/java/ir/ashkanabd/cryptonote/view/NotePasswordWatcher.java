package ir.ashkanabd.cryptonote.view;

import android.text.Editable;
import android.text.TextWatcher;


public class NotePasswordWatcher implements TextWatcher {
    private boolean replaceFlag = false;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().contains(" "))
            replaceFlag = true;
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (replaceFlag) {
            replaceFlag = false;
            String before = s.toString().replace(" ","");
            s.clear();
            s.append(before);
        }
    }
}
