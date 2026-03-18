package pro.kosenkov.promtmonster;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pro.kosenkov.promtmonster.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final int COLUMNS = 3;
    private static final int SPACING_DP = 2;

    private ActivityMainBinding binding;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        setupRecyclerView();
        loadPhotos();
    }

    private void setupRecyclerView() {
        int spacingPx = dpToPx(SPACING_DP);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, COLUMNS));
        binding.recyclerView.addItemDecoration(new GridSpacingDecoration(COLUMNS, spacingPx, false));
        binding.recyclerView.setHasFixedSize(true);
    }

    private void loadPhotos() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.recyclerView.setVisibility(View.GONE);
        binding.emptyView.setVisibility(View.GONE);

        executor.execute(() -> {
            List<Photo> photos = new PhotoRepository(this).loadPhotos();
            runOnUiThread(() -> {
                binding.progressBar.setVisibility(View.GONE);
                if (photos.isEmpty()) {
                    binding.emptyView.setVisibility(View.VISIBLE);
                } else {
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    binding.recyclerView.setAdapter(
                            new PhotoAdapter(photos, this::openDetail));
                }
            });
        });
    }

    private void openDetail(Photo photo, int position) {
        Intent intent = new Intent(this, PhotoDetailActivity.class);
        intent.putExtra(PhotoDetailActivity.EXTRA_URI,      photo.getUri());
        intent.putExtra(PhotoDetailActivity.EXTRA_FILENAME, photo.getFilename());
        intent.putExtra(PhotoDetailActivity.EXTRA_PROMPT_EN, photo.getPromptEn());
        // Pass translations as JSON string
        intent.putExtra(PhotoDetailActivity.EXTRA_TRANSLATIONS,
                new com.google.gson.Gson().toJson(photo));
        startActivity(intent);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }
}
