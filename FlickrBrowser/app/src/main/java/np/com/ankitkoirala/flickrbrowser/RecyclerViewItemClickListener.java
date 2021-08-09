package np.com.ankitkoirala.flickrbrowser;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewItemClickListener extends RecyclerView.SimpleOnItemTouchListener {

    private static final String TAG = "RecyclerViewItemClickLi";

    interface OnClickRecyclerListener {
        void onItemClick(View v, int position);
        void onItemLongClick(View v, int position);
    }

    private OnClickRecyclerListener listener;
    private GestureDetectorCompat detector;

    public RecyclerViewItemClickListener(Context context, final RecyclerView recyclerView, OnClickRecyclerListener listener) {
        this.listener = listener;
        this.detector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d(TAG, "onSingleTapUp: Single tap hit");

                View childView = recyclerView.findChildViewUnder(e.getX(),e.getY());
                if(listener != null && childView != null) {
                    int pos = recyclerView.getChildAdapterPosition(childView);
                    listener.onItemClick(childView,pos);
                }

                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.d(TAG, "onLongPress: Long pressed");
                View childView = recyclerView.findChildViewUnder(e.getX(),e.getY());
                if(listener != null && childView != null) {
                    int pos = recyclerView.getChildAdapterPosition(childView);
                    listener.onItemLongClick(childView,pos);
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull  MotionEvent e) {
        Log.d(TAG, "onInterceptTouchEvent: event intercept started");
        if (detector != null) {
            Log.d(TAG, "onInterceptTouchEvent: Using gesture detector");
            boolean result = detector.onTouchEvent(e);
            Log.d(TAG, "onInterceptTouchEvent: gesture detector returned: " + result);
            return result;
        } else {
            Log.d(TAG, "onInterceptTouchEvent: gesture detector not defined");
            return false;
        }
    }
}
