package me.shouheng.timepartner.adapters;

import android.animation.Animator;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.shouheng.timepartner.models.entities.collection.CollectionEntity;
import me.shouheng.timepartner.R;
import me.shouheng.timepartner.utils.TpPrefer;
import me.shouheng.timepartner.utils.TpTime;
import me.shouheng.timepartner.widget.reveal.RevealColorView;

public class NoteClnAdapter extends RecyclerView.Adapter<NoteClnAdapter.ViewHolder>{

    private static final String TAG = "NoteClnAdapter__";

    private List<CollectionEntity> clnEntityList;

    public void setClnEntityList(List<CollectionEntity> clnEntityList){
        this.clnEntityList = clnEntityList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (TpPrefer.getInstance(parent.getContext()).isClnGrid()){
            view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.note_cln_cln_grid_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.note_cln_cln_list_item, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final CollectionEntity clnEntity = clnEntityList.get(position);

        holder.tvTitle.setText(clnEntity.getClnTitle());
        holder.tvTotal.setText(String.valueOf(clnEntity.getCount()));
        holder.tvAddTime.setText(TpTime.getTime(clnEntity.getAddedTime()));
        holder.tvAddDate.setText(TpTime.getDate(clnEntity.getAddedDate(), TpTime.DATE_TYPE_3));

        String strColor = clnEntity.getClnColor();
        int pColor = Color.parseColor("#395373");
        try {
            pColor = Color.parseColor(strColor);
            holder.cvBG.setCardBackgroundColor(pColor);
        } catch (Exception e){
            Log.d(TAG, "onBindViewHolder: " + e.getMessage());
        }

        holder.view.setTag(clnEntity);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final int color = Color.parseColor("#20000000");
                final Point p = getLocationInView(holder.revealColorView, v);
                holder.revealColorView.reveal(p.x, p.y, color, v.getHeight() / 4, 450, new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {}

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        holder.revealColorView.hide(p.x, p.y, Color.TRANSPARENT, 0, 0, null);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {}

                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                });
                if (onItemClickListener != null){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onItemClickListener.onItemClick(v);
                        }
                    }, 500);
                }
            }
        });

        holder.ivFooter.setTag(clnEntity);
        holder.ivFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onFooterClickListener != null){
                    onFooterClickListener.onFooterClick(v);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return clnEntityList.size();
    }

    private Point getLocationInView(View src, View target) {
        final int[] l0 = new int[2];
        src.getLocationOnScreen(l0);

        final int[] l1 = new int[2];
        target.getLocationOnScreen(l1);

        l1[0] = l1[0] - l0[0] + target.getWidth() / 2;
        l1[1] = l1[1] - l0[1] + target.getHeight() / 2;

        return new Point(l1[0], l1[1]);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        TextView tvTotal;
        TextView tvTitle;
        TextView tvAddDate;
        TextView tvAddTime;
        CardView cvBG;
        ImageView ivFooter;
        RevealColorView revealColorView;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tvTotal = (TextView) itemView.findViewById(R.id.total);
            tvTitle = (TextView) itemView.findViewById(R.id.title);
            tvAddDate = (TextView) itemView.findViewById(R.id.add_date);
            tvAddTime = (TextView) itemView.findViewById(R.id.add_time);
            cvBG = (CardView) itemView.findViewById(R.id.bg);
            revealColorView = (RevealColorView) itemView.findViewById(R.id.reveal);
            ivFooter = (ImageView) itemView.findViewById(R.id.footer);
        }
    }

    private OnItemClickListener onItemClickListener;

    private OnFooterClickListener onFooterClickListener;

    public interface OnItemClickListener{
        void onItemClick(View view);
    }

    public interface OnFooterClickListener {
        void onFooterClick(View view);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public void setOnFooterClickListener(OnFooterClickListener listener){
        onFooterClickListener = listener;
    }

    public void add(CollectionEntity collectionEntity){
        clnEntityList.add(collectionEntity);
        notifyDataSetChanged();
    }

    public void remove(CollectionEntity collectionEntity){
        if (clnEntityList.indexOf(collectionEntity) != -1){
            clnEntityList.remove(collectionEntity);
            notifyDataSetChanged();
        }
    }

    public void update(long clnId, String strTitle, String strColor){
        for (CollectionEntity clnEntity : clnEntityList){
            if (clnEntity.getClnId() == clnId){
                clnEntity.setClnTitle(strTitle);
                clnEntity.setClnColor(strColor);
                notifyDataSetChanged();
                break;
            }
        }
    }
}
