package me.shouheng.timepartner.widget.materialtab;

import java.util.Locale;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import me.shouheng.timepartner.R;
import me.shouheng.timepartner.widget.reveal.RevealColorView;


@SuppressLint({ "InflateParams", "ClickableViewAccessibility" })
public class MaterialTab implements View.OnTouchListener {

    private final static int REVEAL_DURATION = 200;
    private final static int HIDE_DURATION = 300;
	
	private View completeView;
	private ImageView icon;
	private TextView text;
	private RevealColorView background;
	private ImageView selector;
	
	private Resources res;
	private MaterialTabListener listener;
	private Drawable iconDrawable;

	private int textColor;
	private int iconColor;
	private int primaryColor;
	private int accentColor;

	private boolean active;
	private int position;
    private boolean hasIcon;
    private float density;
    private Point lastTouchedPoint;

	public MaterialTab(Context ctx,boolean hasIcon) {
        this.hasIcon = hasIcon;
        density = ctx.getResources().getDisplayMetrics().density;
		res = ctx.getResources();

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if(!hasIcon) {
                completeView = LayoutInflater.from(ctx).inflate(R.layout.view_tab, null);
                text = (TextView) completeView.findViewById(R.id.text);
            }
            else {
                completeView = LayoutInflater.from(ctx).inflate(R.layout.view_tab_icon, null);
                icon = (ImageView) completeView.findViewById(R.id.icon);
            }
            selector = (ImageView) completeView.findViewById(R.id.selector);
        }
        else {
            if (!hasIcon) {
                completeView = LayoutInflater.from(ctx).inflate(R.layout.view_material_tab, null);
                text = (TextView) completeView.findViewById(R.id.text);
            } else {
                completeView = LayoutInflater.from(ctx).inflate(R.layout.view_material_tab_icon, null);
                icon = (ImageView) completeView.findViewById(R.id.icon);
            }
            background = (RevealColorView) completeView.findViewById(R.id.reveal);
            selector = (ImageView) completeView.findViewById(R.id.selector);
        }
		//添加一个触摸监听器
		completeView.setOnTouchListener(this);

        //默认的文本颜色和图标颜色
		active = false;
		textColor = Color.WHITE;
		iconColor = Color.WHITE;
	}

    //为组件设置选中时的颜色
	public void setAccentColor(int color) {
		this.accentColor = color;
        this.textColor = color;
        this.iconColor = color;
	}

    //为组件设置主要颜色
	public void setPrimaryColor(int color) {
		this.primaryColor = color;
        if(deviceHaveRippleSupport()) {
            background.setBackgroundColor(color);
        }
        else {
            completeView.setBackgroundColor(color);
        }
	}

    //设置文字颜色
	public void setTextColor(int color) {
		textColor = color;
		if(text != null) {
			text.setTextColor(color);
		}
	}

    //设置图标颜色
	public void setIconColor(int color) {
	    this.iconColor = color;
	    if (this.icon != null)
	      this.icon.setColorFilter(color);
	}

    //为组件设置文字
	public MaterialTab setText(CharSequence text) {
        if(hasIcon)
            throw new RuntimeException("You had setted tabs with icons, uses icons instead text");
		this.text.setText(text.toString().toUpperCase(Locale.US));
        return this;
	}

    //为组件设置图标
	public MaterialTab setIcon(Drawable icon) {
        if(!hasIcon)
            throw new RuntimeException("You had setted tabs without icons, uses text instead icons");
		iconDrawable = icon;
		this.icon.setImageDrawable(icon);
		this.setIconColor(this.iconColor);
		return this;
	}

    //禁用Tab组件
	public void disableTab() {
		//设置文字为60%的透明度
		if(text != null)
			this.text.setTextColor(Color.argb(0x99 ,Color.red(textColor), Color.green(textColor), Color.blue(textColor)));
		//设置图标为60%的透明度
		if(icon != null)
			setIconAlpha(0x99);
		//下面的指示组件的颜色设置为透明
		this.selector.setBackgroundColor(res.getColor(android.R.color.transparent));
		active = false;
		if(listener != null)
			listener.onTabUnselected(this);
	}

    //激活Tab组件
	public void activateTab() {
		//设置色彩
		if(text != null)
			this.text.setTextColor(textColor);
		//设置图标
		if(icon != null)
			setIconAlpha(0xFF);
		//设置选择时的颜色
		this.selector.setBackgroundColor(accentColor);
		active = true;
	}

    //判断组件是否被选中
	public boolean isSelected() {
		return active;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
        lastTouchedPoint = new Point();
		lastTouchedPoint.x = (int) event.getX();
        lastTouchedPoint.y = (int) event.getY();

        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            if(!deviceHaveRippleSupport()) {
                completeView.setBackgroundColor(Color.argb(0x80, Color.red(accentColor), Color.green(accentColor), Color.blue(accentColor)));
            }
            return true;
        }

        if(event.getAction() == MotionEvent.ACTION_CANCEL) {
            if(!deviceHaveRippleSupport()) {
                completeView.setBackgroundColor(primaryColor);
            }
            return true;
        }

        //新的效果
        if(event.getAction() == MotionEvent.ACTION_UP) {
            if(!deviceHaveRippleSupport()) {
                completeView.setBackgroundColor(primaryColor);
            }
            else {
                //设置背景颜色
                this.background.reveal(lastTouchedPoint.x, lastTouchedPoint.y, Color.argb(0x80, Color.red(accentColor), Color.green(accentColor), Color.blue(accentColor)), 0, REVEAL_DURATION, new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {}

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        background.reveal(lastTouchedPoint.x, lastTouchedPoint.y, primaryColor, 0, HIDE_DURATION, null);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {}

                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                });
            }

            // set the click
            if(listener != null) {
                if(active) {
                    // if the tab is active when the user click on it it will be reselect
                    listener.onTabReselected(this);
                }
                else {
                    listener.onTabSelected(this);
                }
            }
            // if the tab is not activated, it will be active
            if(!active)
                this.activateTab();
            return true;
        }
		return false;
	}
	
	public View getView() {
		return completeView;
	}
	
	public MaterialTab setTabListener(MaterialTabListener listener) {
		this.listener = listener;
		return this;
	}

    public MaterialTabListener getTabListener() {
        return listener;
    }


	public int getPosition() {
		return position;
	}


	public void setPosition(int position) {
		this.position = position;
	}
	
	@SuppressLint({"NewApi"})
	private void setIconAlpha(int paramInt)
	{
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
	    {
	      this.icon.setImageAlpha(paramInt);
	      return;
	    }
	    this.icon.setColorFilter(Color.argb(paramInt, Color.red(this.iconColor), Color.green(this.iconColor), Color.blue(this.iconColor)));
	}

   private int getTextLenght() {
       String textString = text.getText().toString();
        Rect bounds = new Rect();
        Paint textPaint = text.getPaint();
        textPaint.getTextBounds(textString,0,textString.length(),bounds);
        return bounds.width();
   }

    private boolean deviceHaveRippleSupport() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return false;
        }
        else {
            return true;
        }

    }

    private int getIconWidth() {
        return (int) (density * 24);
    }

   public int getTabMinWidth() {
        if(hasIcon) {
            return getIconWidth();
        }
       else {
            return getTextLenght();
        }
   }

}
