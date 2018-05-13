package me.shouheng.timepartner.database.dao.bo;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import me.shouheng.timepartner.database.dao.SortType;
import me.shouheng.timepartner.database.dao.base.CollectionDAO;
import me.shouheng.timepartner.database.dao.base.LocationDAO;
import me.shouheng.timepartner.database.dao.base.NoteDAO;
import me.shouheng.timepartner.database.dao.base.PictureDAO;
import me.shouheng.timepartner.models.business.note.NoteBO;
import me.shouheng.timepartner.models.entities.collection.CollectionEntity;
import me.shouheng.timepartner.models.entities.location.Location;
import me.shouheng.timepartner.models.entities.note.Note;
import me.shouheng.timepartner.models.entities.picture.Picture;

/**
 * Created by wangshouheng on 2017/1/22. */
public class NoteBoDAO extends AbstractBoDAO<NoteBO>{

    private NoteDAO noteDAO;

    private PictureDAO pictureDAO;

    private LocationDAO locationDAO;

    private CollectionDAO collectionDAO;

    public static NoteBoDAO getInstance(Context context){
        return new NoteBoDAO(context);
    }

    private NoteBoDAO(Context context){
        noteDAO = NoteDAO.getInstance(context);
        pictureDAO = PictureDAO.getInstance(context);
        locationDAO = LocationDAO.getInstance(context);
        collectionDAO = CollectionDAO.getInstance(context);
    }

    @Override
    public NoteBO get(long noteId) {
        Note note = noteDAO.get(noteId);
        if (note == null){
            return null;
        }
        long clnId = note.getClnId();
        List<Location> locations = locationDAO.gets(noteId);
        Location location = null;
        if (locations.size() > 0){
            location = locations.get(0);
        }
        List<Picture> pictures = pictureDAO.gets(noteId); // EMPTYABLE
        CollectionEntity collectionEntity = collectionDAO.get(clnId); // NOT NULL
        NoteBO noteBO = new NoteBO();
        noteBO.setCollection(collectionEntity);
        noteBO.setLocation(location);
        noteBO.setNote(note);
        noteBO.setPictures(pictures);
        return noteBO;
    }

    @Override
    public List<NoteBO> getAll() {
        List<Note> notes = noteDAO.getAll();
        List<NoteBO> noteBOs = new ArrayList<>();
        for (Note note : notes){
            NoteBO noteBO = get(note.getNoteId());
            noteBOs.add(noteBO);
        }
        return noteBOs;
    }

    @Override
    public List<NoteBO> getAll(SortType sortType) {
        if (sortType == SortType.DATE_DESC){
            return getAll();
        } else {
            List<Note> notes = noteDAO.getAll(sortType);
            List<NoteBO> noteBOs = new ArrayList<>();
            for (Note note : notes){
                NoteBO noteBO = get(note.getNoteId());
                noteBOs.add(noteBO);
            }
            return noteBOs;
        }
    }

    @Override
    public List<NoteBO> getOfDay(long millisOfDay) {
        List<Note> notes = noteDAO.getOfDay(millisOfDay, SortType.DATE_DESC);
        List<NoteBO> noteBOs = new ArrayList<>();
        for (Note note : notes){
            NoteBO noteBO = get(note.getNoteId());
            noteBOs.add(noteBO);
        }
        return noteBOs;
    }

    @Override
    public List<NoteBO> getOfDay(long millisOfDay, SortType sortType) {
        List<Note> notes = noteDAO.getOfDay(millisOfDay, sortType);
        List<NoteBO> noteBOs = new ArrayList<>();
        for (Note note : notes){
            NoteBO noteBO = get(note.getNoteId());
            noteBOs.add(noteBO);
        }
        return noteBOs;
    }

    @Override
    public List<NoteBO> getScope(long startMillis, long endMillis) {
        List<Note> notes = noteDAO.getScope(startMillis, endMillis, SortType.DATE_DESC);
        List<NoteBO> noteBOs = new ArrayList<>();
        for (Note note : notes){
            NoteBO noteBO = get(note.getNoteId());
            noteBOs.add(noteBO);
        }
        return noteBOs;
    }

    @Override
    public List<NoteBO> getScope(long startMillis, long endMillis, SortType sortType) {
        List<Note> notes = noteDAO.getScope(startMillis, endMillis, sortType);
        List<NoteBO> noteBOs = new ArrayList<>();
        for (Note note : notes){
            NoteBO noteBO = get(note.getNoteId());
            noteBOs.add(noteBO);
        }
        return noteBOs;
    }

    @Override
    public void close() {
        super.close();
    }
}
