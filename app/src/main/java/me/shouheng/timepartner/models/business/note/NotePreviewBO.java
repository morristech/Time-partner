package me.shouheng.timepartner.models.business.note;

/**
 * 如果解析的 记录 中不包含指定类型数据，
 * 那么就将指定类型的内容置为"" */
public class NotePreviewBO {

    private long noteId;

    private String day;

    private String month;

    /** 预览的标题和内容字符串 */
    private String titleAndContent;

    private String total;

    private String location;

    private String previewImagePath;

    private boolean audioIncluded;

    private boolean videoIncluded;

    private boolean liked;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getTitleAndContent() {
        return titleAndContent;
    }

    public void setTitleAndContent(String titleAndContent) {
        this.titleAndContent = titleAndContent;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPreviewImagePath() {
        return previewImagePath;
    }

    public void setPreviewImagePath(String previewImagePath) {
        this.previewImagePath = previewImagePath;
    }

    public boolean isAudioIncluded() {
        return audioIncluded;
    }

    public void setAudioIncluded(boolean audioIncluded) {
        this.audioIncluded = audioIncluded;
    }

    public boolean isVideoIncluded() {
        return videoIncluded;
    }

    public void setVideoIncluded(boolean videoIncluded) {
        this.videoIncluded = videoIncluded;
    }

    public long getNoteId() {
        return noteId;
    }

    public void setNoteId(long noteId) {
        this.noteId = noteId;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    @Override
    public String toString() {
        return "NoteId:" + noteId + ", " +
                "Title And Content:" + titleAndContent + ", " +
                "Date:" + month + "-" + day + ", " +
                "Location:" + location + ", " +
                "Preview Image Path:" + previewImagePath + ", " +
                "Audio Added:" + audioIncluded + ", " +
                "Video Added:" + videoIncluded + ", " +
                "Liked:" + liked;
    }
}
