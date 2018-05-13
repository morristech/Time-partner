package me.shouheng.timepartner.widget.fastscroller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.shouheng.timepartner.R;

import static android.support.v7.widget.RecyclerView.OnScrollListener;

// 自定义组件 滚动指示条
public class FastScroller extends LinearLayout {
    private static final int BUBBLE_ANIMATION_DURATION = 100;
    private static final int TRACK_SNAP_RANGE = 5;
    private final ScrollListener scrollListener = new ScrollListener();
    private TextView bubble;
    private View handle;
    private RecyclerView recyclerView;
    private int height;

    private ObjectAnimator currentAnimator = null;

    public FastScroller(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialise(context);
    }

    public FastScroller(final Context context) {
        super(context);
        initialise(context);
    }

    public FastScroller(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        initialise(context);
    }

    private void initialise(Context context) {
        setOrientation(HORIZONTAL); // LinearLayout的方向
        setClipChildren(false);
        LayoutInflater inflater = LayoutInflater.from(context);
        // 原来是从布局当中获取的然后在进行设置
        inflater.inflate(R.layout.recyclerview_fastscroller, this, true);
        // bubble是指按下之后滑动的指示器
        bubble = (TextView) findViewById(R.id.fastscroller_bubble); // 泡泡
        handle = findViewById(R.id.fastscroller_handle); // 手柄
        bubble.setVisibility(INVISIBLE); // 泡泡，没有触摸则不可见
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        height = h;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        // 触摸监听事件
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (event.getX() < handle.getX())
                    return false;
                if (currentAnimator != null)
                    currentAnimator.cancel();
                // 如果泡泡处于不可见状态，则显示它
                if (bubble.getVisibility() == INVISIBLE)
                    showBubble();
                handle.setSelected(true);
            case MotionEvent.ACTION_MOVE:
                // 设置 手柄 和 泡泡 的位置
                final float y = event.getY();
                setBubbleAndHandlePosition(y);
                setRecyclerViewPosition(y);
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 设置手柄状态 隐藏泡泡
                handle.setSelected(false);
                hideBubble();
                return true;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 设置与该组件关联的RecyclerView
     * @param recyclerView RecyclerView*/
    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        // 设置RecyclerView的滚动的监听器
        recyclerView.addOnScrollListener(scrollListener);
    }

    /**
     * 设置RecyclerView的位置
     * @param y 纵坐标 */
    private void setRecyclerViewPosition(float y) {
        if (recyclerView != null) {
            int itemCount = recyclerView.getAdapter().getItemCount();
            float proportion;
            if (handle.getY() == 0)
                proportion = 0f;
            else if (handle.getY() + handle.getHeight() >= height - TRACK_SNAP_RANGE)
                proportion = 1f;
            else
                proportion = y / (float) height;
            int targetPos = getValueInRange(0, itemCount - 1, (int) (proportion * (float) itemCount));
            // 滚动到指定的位置，方式->
            ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(targetPos, 0);
            // recyclerView.oPositionWithOffset(targetPos);
            // 获取用于设置给bubble的字符串，与该RecyclerView关联的Adapter应该实现该方法
            // 另外注意从RecyclerView中是可以获取与它关联的Adapter的
            String bubbleText = ((BubbleTextGetter) recyclerView.getAdapter())
                    .getTextToShowInBubble(targetPos);
            bubble.setText(bubbleText);
        }
    }

    private int getValueInRange(int min, int max, int value) {
        int minimum = Math.max(min, value);
        return Math.min(minimum, max);
    }

    /**
     * 设置泡泡到指定的位置
     * @param y 纵坐标 */
    private void setBubbleAndHandlePosition(float y) {
        int bubbleHeight = bubble.getHeight();
        int handleHeight = handle.getHeight();
        handle.setY(getValueInRange(0, height - handleHeight, (int) (y - handleHeight / 2)));
        bubble.setY(getValueInRange(0, height - bubbleHeight - handleHeight / 2, (int) (y - bubbleHeight)));
    }

    /**
     * 显示泡泡 */
    private void showBubble() {
        // 设置泡泡可见
        bubble.setVisibility(VISIBLE);
        // 如果动画不为空则取消动画，然后设置新的动画
        if (currentAnimator != null)
            currentAnimator.cancel();
        // 显示泡泡的动画
        currentAnimator = ObjectAnimator.ofFloat(bubble, "alpha", 0f, 1f)
                .setDuration(BUBBLE_ANIMATION_DURATION);
        currentAnimator.start();
    }

    /**
     * 隐藏 泡泡 */
    private void hideBubble() {
        if (currentAnimator != null)
            currentAnimator.cancel();
        // 属性动画 透明度从1到0
        currentAnimator = ObjectAnimator.ofFloat(bubble, "alpha", 1f, 0f).setDuration(BUBBLE_ANIMATION_DURATION);
        currentAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // 动画结束后设置泡泡不可见
                bubble.setVisibility(INVISIBLE);
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                // 动画取消则设置泡泡不可见
                bubble.setVisibility(INVISIBLE);
                currentAnimator = null;
            }
        });
        currentAnimator.start();
    }

    /**
     * 上面设置 泡泡 和 手柄 的时候是触摸它们的时候执行，
     * 下面的是设置给RecyclerView的监听器，用于当滑动RecyclerView的时候，
     * 移动泡泡和手柄 */
    private class ScrollListener extends OnScrollListener {
        @Override
        public void onScrolled(RecyclerView rv, int dx, int dy) {
            if (handle.isSelected()) {
                return;
            }
            // 第一个可见组件的位置
            View firstVisibleView = recyclerView.getChildAt(0);
            int firstVisiblePosition = recyclerView.getChildLayoutPosition(firstVisibleView);
            // 可见的组件的数量
            int visibleRange = recyclerView.getChildCount();
            // 最后一个可见组件的位置
            int lastVisiblePosition = firstVisiblePosition + visibleRange;
            // 全部组件的数量
            int itemCount = recyclerView.getAdapter().getItemCount();
            int position;
            if (firstVisiblePosition == 0)
                position = 0;
            else if (lastVisiblePosition == itemCount)
                position = itemCount;
            else
                // (first / (count - visibleCount)) * count
                position = (int) (((float) firstVisiblePosition / (((float) itemCount - (float) visibleRange))) * (float) itemCount);
            float proportion = (float) position / (float) itemCount;
            // 设置 泡泡 和 手柄 的位置
            setBubbleAndHandlePosition(height * proportion);
        }
    }
}