package pro.kosenkov.promtmonster;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;

import pro.kosenkov.promtmonster.databinding.ActivityPhotoDetailBinding;

public class PhotoDetailActivity extends AppCompatActivity {

    public static final String EXTRA_URI          = "extra_uri";
    public static final String EXTRA_FILENAME     = "extra_filename";
    public static final String EXTRA_PROMPT_EN    = "extra_prompt_en";
    public static final String EXTRA_TRANSLATIONS = "extra_translations";

    private ActivityPhotoDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhotoDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String uri          = getIntent().getStringExtra(EXTRA_URI);
        String filename     = getIntent().getStringExtra(EXTRA_FILENAME);
        String promptEn     = getIntent().getStringExtra(EXTRA_PROMPT_EN);
        String translJson   = getIntent().getStringExtra(EXTRA_TRANSLATIONS);

        // Parse translations from Photo JSON
        Map<String, String> translations = null;
        if (translJson != null) {
            try {
                Type type = new TypeToken<Photo>() {}.getType();
                Photo p = new Gson().fromJson(translJson, type);
                // We only need translations; promptEn is passed separately
            } catch (Exception ignored) {}
        }

        // Re-parse just translations map for simplicity
        if (translJson != null) {
            try {
                // Photo has a "translations" field - extract it
                com.google.gson.JsonObject obj = new Gson().fromJson(translJson,
                        com.google.gson.JsonObject.class);
                if (obj != null && obj.has("translations")) {
                    Type mapType = new TypeToken<Map<String, String>>() {}.getType();
                    translations = new Gson().fromJson(obj.get("translations"), mapType);
                }
            } catch (Exception ignored) {}
        }

        // Build Photo object with what we have
        Photo photo = new Photo(
                filename != null ? filename : "",
                uri != null ? uri : "",
                promptEn != null ? promptEn : "",
                translations != null ? translations : java.util.Collections.emptyMap()
        );

        setupUI(photo);
    }

    private void setupUI(Photo photo) {
        // Title = filename
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(photo.getFilename());
        }

        // Load full-width image
        Glide.with(this)
                .load(photo.getUri())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .into(binding.imageView);

        // English prompt
        String promptEn = photo.getPromptEn();
        if (!promptEn.isEmpty()) {
            binding.layoutEnglish.setVisibility(View.VISIBLE);
            binding.textPromptEn.setText(promptEn);
            binding.btnCopyEn.setOnClickListener(v -> copyToClipboard(promptEn));
        } else {
            binding.layoutEnglish.setVisibility(View.GONE);
        }

        // Localized prompt
        String langCode = Locale.getDefault().getLanguage();
        String promptLocal = photo.getPromptForLocale(langCode);
        boolean isEnglish  = langCode.equals("en") || langCode.isEmpty();
        boolean hasDiff    = !promptLocal.isEmpty() && !promptLocal.equals(promptEn);

        if (!isEnglish && hasDiff) {
            binding.layoutLocal.setVisibility(View.VISIBLE);
            binding.textPromptLocal.setText(promptLocal);
            binding.btnCopyLocal.setOnClickListener(v -> copyToClipboard(promptLocal));
        } else {
            binding.layoutLocal.setVisibility(View.GONE);
        }

        // Empty state
        if (promptEn.isEmpty() && (isEnglish || !hasDiff)) {
            binding.textNoPrompt.setVisibility(View.VISIBLE);
        }
    }

    private void copyToClipboard(String text) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            cm.setPrimaryClip(ClipData.newPlainText("prompt", text));
            Toast.makeText(this, R.string.copied, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
