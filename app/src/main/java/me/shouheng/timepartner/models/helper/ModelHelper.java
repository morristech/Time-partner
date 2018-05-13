package me.shouheng.timepartner.models.helper;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import me.shouheng.timepartner.activities.note.NoteEdit;
import me.shouheng.timepartner.database.dao.bo.NoteBoDAO;
import me.shouheng.timepartner.models.business.note.NoteBO;
import me.shouheng.timepartner.models.business.note.NotePreviewBO;
import me.shouheng.timepartner.models.entities.collection.CollectionEntity;
import me.shouheng.timepartner.models.entities.location.Location;
import me.shouheng.timepartner.models.entities.note.Note;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.models.entities.picture.Picture;
import me.shouheng.timepartner.utils.TpFile;
import me.shouheng.timepartner.utils.TpTime;

/**
 * Created by wangshouheng on 2017/1/12. */
public class ModelHelper {

    public static NotePreviewBO convertToNotePreview(Context context, Note note){
        long noteId = note.getNoteId();
        NoteBoDAO noteBoDAO = NoteBoDAO.getInstance(context);
        NoteBO noteBO = noteBoDAO.get(noteId);
        noteBoDAO.close();
        return convertToNotePreview(context, noteBO);
    }

    public static NotePreviewBO convertToNotePreview(Context context, NoteBO noteBO) {
        Note note = noteBO.getNote();
        CollectionEntity collectionEntity = noteBO.getCollection();
        Location location = noteBO.getLocation();
        List<Picture> pictureList = noteBO.getPictures();

        NotePreviewBO previewEntity = new NotePreviewBO();
        previewEntity.setNoteId(note.getNoteId());
        previewEntity.setAudioIncluded(false);
        previewEntity.setVideoIncluded(false);

        previewEntity.setLiked(note.getLiked() == 1);

        String audioPath = note.getRecordPath();
        if (!TextUtils.isEmpty(audioPath) && TpFile.isFileExist(audioPath)) {
            previewEntity.setAudioIncluded(true);
        }

        for (Picture picture : pictureList){
            String picPath = picture.getPicturePath();
            if (TpFile.isFileExist(picPath)){
                previewEntity.setPreviewImagePath(picPath);
                previewEntity.setTotal(pictureList.size() + context.getString(R.string.n_list_total_pictures));
            }
        }

        previewEntity.setTitleAndContent(note.getNoteTitle() + "\n" + note.getNoteContent());

        if (location != null) {
            previewEntity.setLocation(location.getCity() + " " + location.getDistrict());
        }

        long addedDate = note.getAddedDate();
        String strDate = TpTime.getDate(addedDate, TpTime.DATE_TYPE_4);
        String[] arr = strDate.split("-");
        previewEntity.setMonth(arr[0]);
        previewEntity.setDay(arr[1]);

        return previewEntity;
    }

    public static List<NotePreviewBO> convertToNotePreview(Context context, List<NoteBO> noteBOs) {
        List<NotePreviewBO> notePreviewEntities = new ArrayList<>();
        for (NoteBO noteBO : noteBOs) {
            NotePreviewBO previewEntity = convertToNotePreview(context, noteBO);
            notePreviewEntities.add(previewEntity);
        }
        return notePreviewEntities;
    }
}
