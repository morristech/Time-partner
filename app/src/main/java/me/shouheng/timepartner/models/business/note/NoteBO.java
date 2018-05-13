package me.shouheng.timepartner.models.business.note;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import me.shouheng.timepartner.models.entities.collection.CollectionEntity;
import me.shouheng.timepartner.models.entities.location.Location;
import me.shouheng.timepartner.models.entities.note.Note;
import me.shouheng.timepartner.models.entities.picture.Picture;

public class NoteBO implements Serializable{

    private Note note;

    private CollectionEntity collection;

    private List<Picture> pictures = Collections.EMPTY_LIST;

    private Location location;

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public CollectionEntity getCollection() {
        return collection;
    }

    public void setCollection(CollectionEntity collection) {
        this.collection = collection;
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Note:" + note + "Collection:" + collection;
    }
}
