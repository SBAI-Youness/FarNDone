package ma.emsi.farndone;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ma.emsi.farndone.databinding.ActivityTrackpadBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TrackpadActivity extends AppCompatActivity {

    private ActivityTrackpadBinding binding;
    private float lastX, lastY, lastScrollY;
    private int selectedSpeedIndex = 1; // Medium

    public static class PcAppItem {
        public final String badge;
        public final String name;
        public final String category;
        public final String commandId;

        public PcAppItem(String badge, String name, String category, String commandId) {
            this.badge = badge;
            this.name = name;
            this.category = category;
            this.commandId = commandId;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrackpadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Main Trackpad Drag & Touch
        binding.touchpadArea.setOnTouchListener((v, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = event.getX();
                    lastY = event.getY();
                    binding.layoutGestureHints.animate().alpha(0.25f).setDuration(120).start();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    int dx = (int) (event.getX() - lastX);
                    int dy = (int) (event.getY() - lastY);
                    lastX = event.getX();
                    lastY = event.getY();
                    binding.tvConnectionStatus.setText("● Move: " + dx + ", " + dy);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    binding.layoutGestureHints.animate().alpha(1.0f).setDuration(200).start();
                    binding.tvConnectionStatus.setText("● Fedora · 12 ms");
                    return true;
            }
            return false;
        });

        // Right Scroll Strip
        binding.scrollArea.setOnTouchListener((v, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    lastScrollY = event.getY();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    int scrollDy = (int) (event.getY() - lastScrollY);
                    lastScrollY = event.getY();
                    binding.tvConnectionStatus.setText("● Scroll: " + scrollDy);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    binding.tvConnectionStatus.setText("● Fedora · 12 ms");
                    return true;
            }
            return false;
        });

        // Circular Apps Menu button on bottom edge of trackpad
        binding.btnPcApps.setOnClickListener(v -> showPcAppsBottomSheet());

        // Bottom dock buttons: < (Left click), ◇ (Settings), > (Right click)
        binding.btnLeftClick.setOnClickListener(v ->
                Toast.makeText(this, "Left click (<)", Toast.LENGTH_SHORT).show());


        binding.btnRightClick.setOnClickListener(v ->
                Toast.makeText(this, "Right click (>)", Toast.LENGTH_SHORT).show());

        setupBottomNav();
    }

    private void showQuickSettingsDialog() {
        String[] speeds = {"Cursor Speed: Slow", "Cursor Speed: Medium", "Cursor Speed: Fast"};
        new AlertDialog.Builder(this)
                .setTitle("Trackpad Settings")
                .setSingleChoiceItems(speeds, selectedSpeedIndex, (dialog, which) -> {
                    selectedSpeedIndex = which;
                    Toast.makeText(this, speeds[which], Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .setNegativeButton("Close", null)
                .show();
    }

    private void showPcAppsBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_pc_apps, null, false);
        dialog.setContentView(sheetView);

        RecyclerView appsList = sheetView.findViewById(R.id.rvPcApps);
        EditText searchApps = sheetView.findViewById(R.id.etSearchApps);
        List<PcAppItem> allApps = getMockPcApps();
        PcAppsAdapter adapter = new PcAppsAdapter(allApps, app -> {
            Toast.makeText(this, "Opening " + app.name + " on PC...", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        appsList.setLayoutManager(new LinearLayoutManager(this));
        appsList.setAdapter(adapter);

        searchApps.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().toLowerCase(Locale.US).trim();
                List<PcAppItem> filtered = new ArrayList<>();
                for (PcAppItem item : allApps) {
                    if (item.name.toLowerCase(Locale.US).contains(query) ||
                            item.category.toLowerCase(Locale.US).contains(query)) {
                        filtered.add(item);
                    }
                }
                adapter.updateList(filtered);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        dialog.show();
    }

    private List<PcAppItem> getMockPcApps() {
        List<PcAppItem> apps = new ArrayList<>();
        apps.add(new PcAppItem("FF", "Firefox", "Web Browser", "firefox"));
        apps.add(new PcAppItem("VS", "Visual Studio Code", "Code Editor", "code"));
        apps.add(new PcAppItem(">_", "Terminal", "System Shell", "terminal"));
        apps.add(new PcAppItem("LO", "LibreOffice Impress", "Presentation", "libreoffice --impress"));
        apps.add(new PcAppItem("FM", "Files", "File Manager", "nautilus"));
        apps.add(new PcAppItem("SP", "Spotify", "Music & Media", "spotify"));
        apps.add(new PcAppItem("VL", "VLC Media Player", "Video Player", "vlc"));
        apps.add(new PcAppItem("OB", "Obsidian", "Notes & Knowledge", "obsidian"));
        return apps;
    }

    private interface OnAppClickListener {
        void onAppClick(PcAppItem app);
    }

    private static class PcAppsAdapter extends RecyclerView.Adapter<PcAppsAdapter.AppViewHolder> {
        private List<PcAppItem> items;
        private final OnAppClickListener listener;

        PcAppsAdapter(List<PcAppItem> items, OnAppClickListener listener) {
            this.items = items;
            this.listener = listener;
        }

        void updateList(List<PcAppItem> newList) {
            this.items = newList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_pc_app, parent, false);
            return new AppViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
            PcAppItem app = items.get(position);
            holder.appBadge.setText(app.badge);
            holder.appName.setText(app.name);
            holder.appCategory.setText(app.category);
            holder.itemView.setOnClickListener(v -> listener.onAppClick(app));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class AppViewHolder extends RecyclerView.ViewHolder {
            final TextView appBadge;
            final TextView appName;
            final TextView appCategory;

            AppViewHolder(View itemView) {
                super(itemView);
                appBadge = itemView.findViewById(R.id.tvAppBadge);
                appName = itemView.findViewById(R.id.tvAppName);
                appCategory = itemView.findViewById(R.id.tvAppCategory);
            }
        }
    }

    private void setupBottomNav() {
        BottomNavigationInsets.apply(binding.includeBottomNav.getRoot());
        binding.includeBottomNav.bottomNavigation.setSelectedItemId(R.id.nav_trackpad);
        binding.includeBottomNav.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_trackpad) return true;
            Intent intent = null;
            if (id == R.id.nav_connect) intent = new Intent(this, ConnectActivity.class);
            else if (id == R.id.nav_slides) intent = new Intent(this, PresentationActivity.class);
            else if (id == R.id.nav_media) intent = new Intent(this, MediaActivity.class);

            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
            return true;
        });
    }
}