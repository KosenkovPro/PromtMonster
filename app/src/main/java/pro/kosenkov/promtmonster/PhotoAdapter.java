package pro.kosenkov.promtmonster;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    public interface OnPhotoClickListener {
        void onPhotoClick(Photo photo, int position);
    }

    private final List<Photo> photos;
    private final OnPhotoClickListener listener;

    public PhotoAdapter(List<Photo> photos, OnPhotoClickListener listener) {
        this.photos   = photos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Photo photo = photos.get(position);

        Glide.with(holder.imageView.getContext())
                .load(photo.getUri())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.placeholder_photo)
                .into(holder.imageView);

        holder.imageView.setOnClickListener(v -> listener.onPhotoClick(photo, position));
        holder.imageView.setContentDescription(photo.getFilename());
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;

        PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            // Root element in item_photo.xml IS the SquareImageView
            imageView = (ImageView) itemView;
        }
    }
}
