package me.shouheng.timepartner.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;

import me.shouheng.timepartner.database.dao.base.RatingDAO;
import me.shouheng.timepartner.models.entities.rating.Rating;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.utils.TpDisp;
import me.shouheng.timepartner.utils.TpTime;

public class RateDialog {

    private Context mContext;

    private RatingDAO ratingDAO;

    public RateDialog(Context context) {
        mContext = context;
        ratingDAO = RatingDAO.getInstance(context);
    }

    public RateDialog rate(){
        boolean isRated = ratingDAO.getToday() != null;

        if (!isRated){
            View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_rate, null);
            String str = mContext.getString(R.string.mrate_title) + TpTime.getCurrentDate();
            final RatingBar rate = (RatingBar) view.findViewById(R.id.rate);
            final EditText comment = (EditText) view.findViewById(R.id.comment);
            Dialog dlg = new AlertDialog.Builder(mContext)
                    .setTitle(str)
                    .setView(view)
                    .setNegativeButton(R.string.mrate_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            release();
                        }
                    })
                    .setPositiveButton(R.string.mrate_complete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Rating ratingEntity = new Rating();
                            ratingEntity.setRating(rate.getRating());
                            ratingEntity.setRateDate(TpTime.millisOfCurrentDate());
                            ratingEntity.setRateComments(comment.getText().toString());
                            ratingEntity.setAddedDate(TpTime.millisOfCurrentDate());
                            ratingEntity.setAddedTime(TpTime.millisOfCurrentTime());
                            ratingEntity.setLastModifyDate(TpTime.millisOfCurrentDate());
                            ratingEntity.setLastModifyTime(TpTime.millisOfCurrentTime());

                            ratingDAO.insert(ratingEntity);
                            TpDisp.showToast(mContext, R.string.mrate_success_toast);
                            release();
                        }
                    })
                    .create();
            dlg.show();
        } else {
            TpDisp.showToast(mContext, R.string.mrate_rated_toast);
        }
        return this;
    }

    private void release(){
        if (ratingDAO != null){
            ratingDAO.close();
        }
    }
}
