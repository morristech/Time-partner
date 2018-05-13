package me.shouheng.timepartner.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import me.shouheng.timepartner.models.business.note.NoteBO;
import me.shouheng.timepartner.models.helper.ModelHelper;
import me.shouheng.timepartner.models.business.note.NotePreviewBO;
import me.shouheng.timepartner.models.entities.note.Note;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.utils.TpFile;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.ViewHolder> {

    private Context mContext;

    private List<NoteBO> noteBOs = new ArrayList<>();

    private List<NotePreviewBO> notePreviewEntities = new ArrayList<>();

    private static final String TAG = "NoteListAdapter__";

    public NoteListAdapter(Context mContext){
        this.mContext = mContext;
    }

    public void setNoteBOs(List<NoteBO> noteBOs){
        this.noteBOs = noteBOs;
        notePreviewEntities = ModelHelper.convertToNotePreview(mContext, noteBOs);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.note_list_preview_list_item, parent, false);
        // 必须要mContext初始化之后才能调用该方法——在onActivityCreated方法之后
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: ");

        NotePreviewBO entity = notePreviewEntities.get(position);
        holder.mTvTC.setText(entity.getTitleAndContent());
        holder.mTvMonth.setText(entity.getMonth());
        holder.mTvDay.setText(entity.getDay());
        holder.mTvLocate.setText(entity.getLocation());

        String imPath = entity.getPreviewImagePath();
        if (TpFile.isFileExist(imPath)){
            holder.mPicPrev.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(imPath).centerCrop().into(holder.mPicPrev);
            holder.mTvTotal.setText(entity.getTotal());
        } else {
            holder.mPicPrev.setVisibility(View.INVISIBLE);
            holder.mTvTotal.setText("");
        }

        if (entity.isLiked()){
            holder.mLikePrew.setVisibility(View.VISIBLE);
        } else {
            holder.mLikePrew.setVisibility(View.INVISIBLE);
        }

        if (entity.isAudioIncluded()){
            holder.mMediaPrev.setVisibility(View.VISIBLE);
            holder.mMediaPrev.setImageResource(R.drawable.ic_audio);
        } else {
            holder.mMediaPrev.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (notePreviewEntities != null){
            return notePreviewEntities.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView mMediaPrev;
        ImageView mPicPrev;
        ImageView mLikePrew;
        TextView mTvMonth;
        TextView mTvDay;
        TextView mTvTotal;
        TextView mTvLocate;
        TextView mTvTC;

        ViewHolder(View itemView) {
            super(itemView);
            mMediaPrev = (ImageView) itemView.findViewById(R.id.media_image);
            mPicPrev = (ImageView) itemView.findViewById(R.id.preview_image);
            mLikePrew = (ImageView) itemView.findViewById(R.id.like_state);
            mTvMonth = (TextView) itemView.findViewById(R.id.month);
            mTvDay = (TextView) itemView.findViewById(R.id.day);
            mTvTotal = (TextView) itemView.findViewById(R.id.total_images);
            mTvLocate = (TextView) itemView.findViewById(R.id.location);
            mTvTC = (TextView) itemView.findViewById(R.id.title_content);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null){
                listener.onSelected(v, noteBOs.get(getAdapterPosition()).getNote());
            }
        }
    }

    private OnListItemSelectedListener listener;

    public interface OnListItemSelectedListener{
        void onSelected(View v, Note noteEntity);
    }

    public void setOnListItemSelectedListener(OnListItemSelectedListener listener){
        this.listener = listener;
    }
}
