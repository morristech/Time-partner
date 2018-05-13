package me.shouheng.timepartner.models.business.note;

import java.util.List;

import me.shouheng.timepartner.models.entities.collection.CollectionEntity;
import me.shouheng.timepartner.models.entities.note.Note;

/**
 * 为了节约在获取到信息之后组装信息的时间，下面提供了两个字段，
 * 可以只在必须的情况下加载集合信息 */
public class NoteCollectionBO {

    private CollectionEntity collection;

    /**
     * 该集合中包含的记录的数目 */
    private List<Note> noteEntities;

    /**
     * 该集合中记录的总数 */
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public CollectionEntity getCollection() {
        return collection;
    }

    public void setCollection(CollectionEntity collection) {
        this.collection = collection;
    }

    public List<Note> getNoteEntities() {
        return noteEntities;
    }

    public void setNoteEntities(List<Note> noteEntities) {
        this.noteEntities = noteEntities;
    }

    @Override
    public String toString() {
        return "Collection:" + collection + "\n" +
                "Notes:" + noteEntities + "\n" +
                "Count:" + count;
    }
}
