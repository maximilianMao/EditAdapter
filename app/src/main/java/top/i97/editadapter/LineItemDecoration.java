package top.i97.editadapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * RecyclerView分割线
 *
 * @author Plain
 * @date 2019/12/5 10:11 上午
 */
public class LineItemDecoration extends RecyclerView.ItemDecoration {

    //水平方向
    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
    //垂直方向
    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

    //绘制的Drawable
    private Drawable mDivider;
    //指定的方向
    private int mOrientation;
    //分割线缩进值
    private int inset;
    //画笔
    private Paint paint;

    public LineItemDecoration(Context context, int orientation) {
        this(context, orientation, R.drawable.item_divider, 0);
    }

    public LineItemDecoration(Context context, int orientation, int inset) {
        this(context, orientation, R.drawable.item_divider, inset);
    }

    public LineItemDecoration(Context context, int orientation, int drawable, int inset) {
        init(context, orientation, drawable, inset);
    }

    /**
     * 初始化分割线
     *
     * @param context     Context
     * @param orientation 分割线方向
     * @param drawable    分割线Drawable
     * @param inset       分割线缩进值(px)
     */
    private void init(Context context, int orientation, int drawable, int inset) {
        mDivider = ContextCompat.getDrawable(context, drawable);
        this.inset = inset;
        paint = new Paint();
        paint.setColor(Color.parseColor("#ffffff"));
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        setOrientation(orientation);
    }

    /**
     * 设置分割线方向
     *
     * @param orientation {@link LineItemDecoration#HORIZONTAL_LIST} or {@link LineItemDecoration#VERTICAL_LIST}
     */
    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (mOrientation == VERTICAL_LIST) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    /**
     * 绘制垂直方向分割线
     *
     * @param c      Canvas
     * @param parent RecyclerView
     */
    private void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        //最后一个item不画分割线
        for (int i = 0; i < childCount - 1; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            if (inset > 0) {
                c.drawRect(left, top, right, bottom, paint);
                mDivider.setBounds(left + inset, top, right - inset, bottom);
            } else {
                mDivider.setBounds(left, top, right, bottom);
            }
            mDivider.draw(c);
        }
    }

    /**
     * 绘制水平方向分割线
     *
     * @param c      Canvas
     * @param parent RecyclerView
     */
    private void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        //由于Divider也有宽高，每一个Item需要向下或者向右偏移
        if (mOrientation == VERTICAL_LIST) {
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
        } else {
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
        }
    }
}
