package me.shouheng.timepartner.models.entities.rating;

import me.shouheng.timepartner.models.entities.Entity;
import me.shouheng.timepartner.utils.TpTime;

public class Rating extends Entity {

    public static final String TABLE_NAME = "rating_table";

    public static final class Columns extends Entity.Columns{
        public static final String RATE_DATE = "rate_date";
        public static final String RATING = "rating";
        public static final String RATE_COMMENT = "rate_comment";
    }

    private long rateDate;

    private String rateComments;

    private float rating;

    public long getRateDate() {
        return rateDate;
    }

    public void setRateDate(long rateDate) {
        this.rateDate = rateDate;
    }

    public String getRateComments() {
        return rateComments;
    }

    public void setRateComments(String rateComments) {
        this.rateComments = rateComments;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return Columns.ID + ":" + id + " , " +
                Columns.ACCOUNT + ":" + account + " , " +
                Columns.RATING + ":" + rating + " , " +
                Columns.RATE_DATE + ":" + TpTime.getFormatDate(rateDate) + " , " +
                Columns.RATE_COMMENT + ":" + rateComments + " , " +
                Columns.SYNCED + ":" + synced;
    }
}
