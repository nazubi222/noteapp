package com.example.noteapp;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.activity.NotesActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;


public abstract class NoteItemTouchHelperCallback extends ItemTouchHelper.SimpleCallback {

    public static final int BUTTON_WIDTH = 200;
    private RecyclerView recyclerView;
    private List<UnderlayButton> buttons, buttonsRight;
    private GestureDetector gestureDetector;//    nhận dạng các sự kiện vuốt và nhấn đơn trên mục.
    private int swipedPos = -1;
    private float swipeThreshold = 0.5f;
    private Map<Integer, List<UnderlayButton>> buttonsBuffer, buttonsBufferRight;
    private Queue<Integer> recoverQueue;
    private static Boolean animate;
    private Context context;

    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d("onSingleTapConfirmed", "onSingleTapConfirmed: ");

            for (UnderlayButton button : buttons) {
                if (button.onClick(e.getX(), e.getY())) {
                    break;
                }
            }
            for (UnderlayButton button : buttonsRight) {
                if (button.onClick(e.getX(), e.getY()))
                    break;
            }

            return true;
        }
    };

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent e) {
            if (swipedPos < 0) return false;
            Point point = new Point((int) e.getRawX(), (int) e.getRawY());

            RecyclerView.ViewHolder swipedViewHolder = recyclerView.findViewHolderForAdapterPosition(swipedPos);
            View swipedItem = swipedViewHolder.itemView;
            Rect rect = new Rect();
            swipedItem.getGlobalVisibleRect(rect);

            if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_MOVE) {
                if (rect.top < point.y && rect.bottom > point.y)
                    gestureDetector.onTouchEvent(e);
                else {
                    //Đưa về ban đầu
                    recoverQueue.add(swipedPos);
                    swipedPos = -1;
                    recoverSwipedItem();
                }
            }
            Log.d("ontouch", "onTouch: " + String.valueOf(point) + "swipedpos " + String.valueOf(swipedPos));
            return false;
        }
    };

    public NoteItemTouchHelperCallback(Context context, RecyclerView recyclerView, Boolean animate, int swipeDir) {
        super(0, swipeDir);
        this.context = context;
        this.animate = animate;
        this.recyclerView = recyclerView;
        this.buttons = new ArrayList<>();
        this.buttonsRight = new ArrayList<>();
        this.gestureDetector = new GestureDetector(context, gestureListener);
        this.recyclerView.setOnTouchListener(onTouchListener);
        buttonsBuffer = new HashMap<>();
        buttonsBufferRight = new HashMap<>();
        recoverQueue = new LinkedList<Integer>() {
            @Override
            public boolean add(Integer o) {
                //Không được thêm vào hàng đợi nếu đã tồn tại
                if (contains(o))
                    return false;
                else
                    return super.add(o);
            }
        };

        attachSwipe();
    }


    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int pos = viewHolder.getAdapterPosition();

        if (swipedPos != pos)
            recoverQueue.add(swipedPos);

        swipedPos = pos;

        if (direction == ItemTouchHelper.LEFT) {
            if (buttonsBuffer.containsKey(swipedPos))
                buttons = buttonsBuffer.get(swipedPos);
            else
                buttons.clear();
            buttonsBuffer.clear();
            swipeThreshold = 0.5f * buttons.size() * BUTTON_WIDTH;
            recoverSwipedItem();
        } else if (direction == ItemTouchHelper.RIGHT) {
            if (buttonsBufferRight.containsKey(swipedPos))
                buttonsRight = buttonsBufferRight.get(swipedPos);
            else
                buttonsRight.clear();
            buttonsBufferRight.clear(); // Xóa nút phải để không gây xung đột
            swipeThreshold = 0.5f * buttonsRight.size() * BUTTON_WIDTH;
            recoverSwipedItem();
        }


    }

    @Override
    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
        return swipeThreshold;
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        return 0.1f * defaultValue;
    }

    @Override
    public float getSwipeVelocityThreshold(float defaultValue) {
        return 5.0f * defaultValue;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        int pos = viewHolder.getAdapterPosition();
        float translationX = dX;
        View itemView = viewHolder.itemView;

        if (pos < 0) {
            swipedPos = pos;
            return;
        }

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            List<UnderlayButton> bufferLeft = new ArrayList<>();
            List<UnderlayButton> bufferRight = new ArrayList<>();

            if (dX < 0) {
                if (!buttonsBuffer.containsKey(pos)) {
                    instantiateUnderlayButton(viewHolder, bufferLeft, bufferRight);
                    buttonsBuffer.put(pos, bufferLeft);
                } else {
                    bufferLeft = buttonsBuffer.get(pos);
                }
                Log.d("onChildDraw", "onChildDraw: " + String.valueOf(pos) + " " + bufferLeft);
                translationX = dX * bufferLeft.size() * BUTTON_WIDTH / itemView.getWidth();
                drawButtons(c, itemView, bufferLeft, pos, translationX);
            } else if (dX > 0) { // Điều này sẽ cho phép kéo sang phải
                if (!buttonsBufferRight.containsKey(pos)) {
                    instantiateUnderlayButton(viewHolder, bufferLeft, bufferRight);
                    buttonsBufferRight.put(pos, bufferRight);
                } else {
                    bufferRight = buttonsBufferRight.get(pos);
                }
                Log.d("onChildDraw", "onChildDraw: " + String.valueOf(pos) + " " + bufferRight);
                translationX = dX * bufferRight.size() * BUTTON_WIDTH / itemView.getWidth();
                drawButtons(c, itemView, bufferRight, pos, translationX);
            }
        }


        super.onChildDraw(c, recyclerView, viewHolder, translationX, dY, actionState, isCurrentlyActive);
    }

    private synchronized void recoverSwipedItem() {
        while (!recoverQueue.isEmpty()) {
            int pos = recoverQueue.poll();
            if (pos > -1) {
                recyclerView.getAdapter().notifyItemChanged(pos);
            }
        }
    }

    private void drawButtons(Canvas c, View itemView, List<UnderlayButton> buffer, int pos, float dX) {
        if (dX < 0) {
            float right = itemView.getRight();
            float dButtonWidth = (-1) * dX / buffer.size();

            for (UnderlayButton button : buffer) {
                float left = right - dButtonWidth;
                button.onDraw(
                        c,
                        new RectF(
                                left,
                                itemView.getTop(),
                                right,
                                itemView.getBottom()
                        ),
                        pos
                );

                right = left;
            }
        } else {
            float left = itemView.getLeft();
            float dButtonWidth = dX / buffer.size();

            for (UnderlayButton button : buffer) {
                float right = left + dButtonWidth;
                button.onDraw(
                        c,
                        new RectF(
                                left,
                                itemView.getTop(),
                                right,
                                itemView.getBottom()
                        ),
                        pos
                );

                left = right;
            }
        }

    }

    public void attachSwipe() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(this);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public abstract void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons, List<UnderlayButton> underlayButtonsRight);

    public static class UnderlayButton {
        private Drawable imageResId;
        private int buttonBackgroundcolor;
        private int pos;
        private RectF clickRegion;
        private UnderlayButtonClickListener clickListener;

        public UnderlayButton(Drawable imageResId, int buttonBackgroundcolor, UnderlayButtonClickListener clickListener) {
            this.imageResId = imageResId;
            this.buttonBackgroundcolor = buttonBackgroundcolor;
            this.clickListener = clickListener;
        }

        public boolean onClick(float x, float y) {
            Log.d("UnderlayButtonf", "Clicked at x: " + x + ", y: " + y);

            if (clickRegion != null && clickRegion.contains(x, y)) {
                Log.d("UnderlayButtonf", "Inside clickRegion");
                clickListener.onClick(pos);
                return true;
            }
            return false;
        }

        private void onDraw(Canvas canvas, RectF rect, int pos) {

            Path buttonPath = new Path();
            buttonPath.addRoundRect(rect, 18, 18, Path.Direction.CW);

            // Draw background của nút và vẽ nội dung bên trong
            Paint buttonPaint = new Paint();
            buttonPaint.setColor(buttonBackgroundcolor);
            canvas.drawPath(buttonPath, buttonPaint);

            if (!animate) {
                int targetSize = 60; // Kích thước mong muốn (32px)
                if (imageResId != null) {
                    // Đặt kích thước mới cho ảnh
                    int newWidth = targetSize;
                    int newHeight = targetSize;

                    // Đặt vị trí sao cho ảnh nằm giữa theo chiều ngang và chiều dọc trong rect
                    int left = (int) (rect.left + (rect.width() - newWidth) / 2f);
                    int top = (int) (rect.top + (rect.height() - newHeight) / 2f);
                    int right = left + newWidth;
                    int bottom = top + newHeight;

                    // Đặt vị trí và kích thước mới cho ảnh
                    imageResId.setBounds(left, top, right, bottom);
                    imageResId.draw(canvas);
                }
            } else {
                if (imageResId != null) {
                    int intrinsicWidth = imageResId.getIntrinsicWidth();
                    int intrinsicHeight = imageResId.getIntrinsicHeight();

                    // Tính toán tỷ lệ thu nhỏ hoặc tỷ lệ kích thước mới
                    float scale = 0.5f; // Đây là một ví dụ, bạn có thể thay đổi tỷ lệ theo ý muốn

                    // Tính toán kích thước mới của hình ảnh dựa trên tỷ lệ
                    float newWidth = intrinsicWidth * scale;
                    float newHeight = intrinsicHeight * scale;

                    // Tính toán vị trí của hình ảnh để nó nằm giữa vùng chứa
                    float left = rect.left + (rect.width() - newWidth) / 2f;
                    float top = rect.top + (rect.height() - newHeight) / 2f;
                    float right = left + newWidth;
                    float bottom = top + newHeight;

                    // Cập nhật vị trí và kích thước của hình ảnh
                    imageResId.setBounds((int) left, (int) top, (int) right, (int) bottom);
                    imageResId.draw(canvas);
                }
            }

            clickRegion = rect;
            Log.d("onclicknote", "onClick:1 "  + clickRegion);
            this.pos = pos;
        }

    }

    public interface UnderlayButtonClickListener {
        void onClick(int pos);
    }
}