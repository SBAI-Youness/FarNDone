package ma.emsi.farndone;

import ma.emsi.farndone.databinding.ActivityMediaBinding;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


public class MediaActivity extends AppCompatActivity {

    private ActivityMediaBinding binding;
    private int volume = 60;
    private boolean isMuted = false;
    private boolean isPlaying = false;

    private final String[] speedOptions = {"Slow", "Medium", "Fast"};
    private int speedIndex = 1; // Medium
    private boolean naturalScrolling = true;
    private boolean keepConnected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMediaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Volume controls
        binding.btnVolDown.setOnClickListener(v -> {
            isMuted = false;
            volume = Math.max(0, volume - 10);
            updateVolumeUi();
        });

        binding.btnVolUp.setOnClickListener(v -> {
            isMuted = false;
            volume = Math.min(100, volume + 10);
            updateVolumeUi();
        });

        binding.btnMute.setOnClickListener(v -> {
            isMuted = !isMuted;
            updateVolumeUi();
        });

        // Media playback controls
        binding.btnMediaPrev.setOnClickListener(v ->
                Toast.makeText(this, "Previous track", Toast.LENGTH_SHORT).show());

        binding.btnMediaPlayPause.setOnClickListener(v -> {
            isPlaying = !isPlaying;
            binding.btnMediaPlayPause.setText(isPlaying ? "Pause" : "Play / Pause");
        });

        binding.btnMediaNext.setOnClickListener(v ->
                Toast.makeText(this, "Next track", Toast.LENGTH_SHORT).show());

        // Settings rows
        binding.rowCursorSpeed.setOnClickListener(v -> {
            speedIndex = (speedIndex + 1) % speedOptions.length;
            binding.tvCursorSpeedValue.setText(speedOptions[speedIndex]);
        });

        binding.rowNaturalScrolling.setOnClickListener(v -> {
            naturalScrolling = !naturalScrolling;
            updateToggleText(binding.tvNaturalScrollingValue, naturalScrolling);
        });

        binding.rowKeepConnected.setOnClickListener(v -> {
            keepConnected = !keepConnected;
            updateToggleText(binding.tvKeepConnectedValue, keepConnected);
        });

        setupBottomNav();
    }

    private void updateVolumeUi() {
        int black = ContextCompat.getColor(this, R.color.black_primary);
        int white = ContextCompat.getColor(this, R.color.white);

        if (isMuted) {
            binding.tvVolumePercent.setText("Muted");
            binding.progressVolume.setProgress(0);
            binding.btnMute.setBackgroundColor(black);
            binding.btnMute.setTextColor(white);
        } else {
            binding.tvVolumePercent.setText(volume + "%");
            binding.progressVolume.setProgress(volume);
            binding.btnMute.setBackgroundResource(R.drawable.bg_button_outlined);
            binding.btnMute.setTextColor(black);
        }
    }

    private void updateToggleText(TextView tv, boolean isOn) {
        if (isOn) {
            tv.setText("On");
            tv.setTextColor(ContextCompat.getColor(this, R.color.blue_primary));
            tv.setTypeface(null, Typeface.BOLD);
        } else {
            tv.setText("Off");
            tv.setTextColor(ContextCompat.getColor(this, R.color.text_muted));
            tv.setTypeface(null, Typeface.NORMAL);
        }
    }

    private void setupBottomNav() {
        BottomNavigationInsets.apply(binding.includeBottomNav.getRoot());
        binding.includeBottomNav.bottomNavigation.setSelectedItemId(R.id.nav_media);
        binding.includeBottomNav.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_media) return true;
            Intent intent = null;
            if (id == R.id.nav_connect) intent = new Intent(this, ConnectActivity.class);
            else if (id == R.id.nav_trackpad) intent = new Intent(this, TrackpadActivity.class);
            else if (id == R.id.nav_slides) intent = new Intent(this, PresentationActivity.class);

            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
            return true;
        });
    }
}