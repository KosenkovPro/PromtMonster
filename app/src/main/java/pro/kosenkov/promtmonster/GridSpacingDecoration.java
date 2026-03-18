package pro.kosenkov.promtmonster;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/** Adds equal spacing between grid items. */
public class GridSpacingDecoration extends RecyclerView.ItemDecoration {

    private final int spanCount;
    private final int spacing;
    private final boolean includeEdge;

    public GridSpacingDecoration(int spanCount, int spacing, boolean includeEdge) {
        this.spanCount   = spanCount;
        this.spacing     = spacing;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int column   = position % spanCount;

        if (includeEdge) {
            outRect.left   = spacing - column * spacing / spanCount;
            outRect.right  = (column + 1) * spacing / spanCount;
            outRect.top    = (position < spanCount) ? spacing : 0;
            outRect.bottom = spacing;
        } else {
            outRect.left   = column * spacing / spanCount;
            outRect.right  = spacing - (column + 1) * spacing / spanCount;
            outRect.top    = (position < spanCount) ? 0 : spacing;
            outRect.bottom = 0;
        }
    }
}
