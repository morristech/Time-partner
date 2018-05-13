package me.shouheng.timepartner.models.business.alarm;


public class Alarm {
    private String strTitle;
    private String strSubTitle;
    private String strFooter1;
    private String strFooter2;
    private String strContent;
    private String strColor;
    private long entityId;
    private long entityDate;
    private int entityTime;
    private int type;
    public final static int TYPE_CLASS = 0;
    public final static int TYPE_EXAM = 1;
    public final static int TYPE_TASK = 2;
    public final static int TYPE_ASSIGN = 3;
    public final static int TYPE_NOTE = 4;

    public Alarm(
            int type,
            String strTitle,
            String strSubTitle,
            String strFooter1,
            String strFooter2,
            String strContent,
            String strColor,
            long entityId,
            long entityDate,
            int entityTime){
        this.type = type;
        this.strTitle = strTitle;
        this.strSubTitle = strSubTitle;
        this.strFooter1 = strFooter1;
        this.strFooter2 = strFooter2;
        this.strContent = strContent;
        this.strColor = strColor;
        this.entityId = entityId;
        this.entityDate = entityDate;
        this.entityTime = entityTime;
    }

    public int getType(){
        return type;
    }

    public String getStrTitle(){
        return strTitle;
    }

    public String getStrSubTitle(){
        return strSubTitle;
    }

    public String getStrFooter1(){
        return strFooter1;
    }

    public String getStrFooter2(){
        return strFooter2;
    }

    public String getStrContent(){
        return strContent;
    }

    public String getStrColor(){
        return strColor;
    }

    public long getEntityId(){
        return entityId;
    }

    public long getEntityDate(){
        return entityDate;
    }

    public int getEntityTime(){
        return entityTime;
    }
}
