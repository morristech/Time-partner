package me.shouheng.timepartner.models.entities.note;

public class Note extends BaseNote {

    public static class Columns extends BaseNote.Columns{
        public static final String NOTE_COLOR = "note_color";
    }

    private String noteColor;

    public String getNoteColor() {
        return noteColor;
    }

    public void setNoteColor(String noteColor) {
        this.noteColor = noteColor;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
