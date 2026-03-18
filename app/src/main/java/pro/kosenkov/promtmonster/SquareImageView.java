package pro.kosenkov.promtmonster;

import android.content.Context;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * ImageView that is always square (height = width).
 * Used for the photo grid to guarantee 1:1 aspect ratio on all devices.
 */
public class SquareImageView extends AppCompatImageView {

    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Force height = width for a perfect square on every screen size and OEM
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
