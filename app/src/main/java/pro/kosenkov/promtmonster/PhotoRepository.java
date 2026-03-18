package pro.kosenkov.promtmonster;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhotoRepository {

    private static final String[] IMAGE_EXTS = {".jpg", ".jpeg", ".png", ".webp", ".gif"};
    private static final String ASSETS_IMAGES = "images";
    private static final String PROMPTS_JSON  = "prompts.json";

    private final Context context;
    private final Gson gson = new Gson();

    public PhotoRepository(Context context) {
        this.context = context.getApplicationContext();
    }

    /** Loads all photos from assets/images and the app's external files directory. */
    public List<Photo> loadPhotos() {
        Map<String, PhotoMeta> metaMap = loadMetadata();
        List<Photo> result = new ArrayList<>();

        // 1. Bundled photos from assets/images/
        try {
            String[] assetFiles = context.getAssets().list(ASSETS_IMAGES);
            if (assetFiles != null) {
                Arrays.sort(assetFiles);
                for (String filename : assetFiles) {
                    if (isImage(filename)) {
                        String uri = "file:///android_asset/" + ASSETS_IMAGES + "/" + filename;
                        PhotoMeta meta = metaMap.get(filename);
                        result.add(new Photo(filename, uri,
                                meta != null ? meta.promptEn : "",
                                meta != null ? meta.translations : Collections.emptyMap()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. User-added photos from external files directory
        // Path: Android/data/pro.kosenkov.promtmonster/files/
        File extDir = context.getExternalFilesDir(null);
        if (extDir != null && extDir.exists()) {
            File[] files = extDir.listFiles();
            if (files != null) {
                Arrays.sort(files, (a, b) -> a.getName().compareTo(b.getName()));
                for (File f : files) {
                    if (f.isFile() && isImage(f.getName())) {
                        PhotoMeta meta = metaMap.get(f.getName());
                        result.add(new Photo(f.getName(), f.toURI().toString(),
                                meta != null ? meta.promptEn : "",
                                meta != null ? meta.translations : Collections.emptyMap()));
                    }
                }
            }
        }

        return result;
    }

    // --- Metadata parsing ---

    private Map<String, PhotoMeta> loadMetadata() {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(PROMPTS_JSON)));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();

            Type listType = new TypeToken<List<PhotoMetaJson>>() {}.getType();
            List<PhotoMetaJson> list = gson.fromJson(sb.toString(), listType);

            Map<String, PhotoMeta> map = new HashMap<>();
            if (list != null) {
                for (PhotoMetaJson item : list) {
                    map.put(item.filename, new PhotoMeta(
                            item.promptEn != null ? item.promptEn : "",
                            item.translations != null ? item.translations : Collections.emptyMap()));
                }
            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    private boolean isImage(String name) {
        String lower = name.toLowerCase();
        for (String ext : IMAGE_EXTS) {
            if (lower.endsWith(ext)) return true;
        }
        return false;
    }

    // --- Inner data classes ---

    private static class PhotoMeta {
        final String promptEn;
        final Map<String, String> translations;
        PhotoMeta(String promptEn, Map<String, String> translations) {
            this.promptEn = promptEn;
            this.translations = translations;
        }
    }

    private static class PhotoMetaJson {
        String filename;
        @SerializedName("prompt_en") String promptEn;
        Map<String, String> translations;
    }
}
