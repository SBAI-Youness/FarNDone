package com.farndone.android;

import com.farndone.android.databinding.ActivityPresentationBinding;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import java.util.Locale;

public class PresentationActivity extends AppCompatActivity {

    private ActivityPresentationBinding binding;
    private int currentSlide = 3;
    private final int totalSlides = 12;
    private boolean isBlackScreen = false;

    // Live timer starting at 12:40 (760 seconds)
    private int elapsedSeconds = 12 * 60 + 40;
    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            elapsedSeconds++;
            int mins = elapsedSeconds / 60;
            int secs = elapsedSeconds % 60;
            binding.tvPresentationTimer.setText(String.format(Locale.US, "%02d:%02d", mins, secs));
            timerHandler.postDelayed(this, 1000);
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPresentationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        updateSlideUi();
        timerHandler.postDelayed(timerRunnable, 1000);

        binding.btnPrevSlide.setOnClickListener(v -> {
            if (currentSlide > 1) {
                currentSlide--;
                updateSlideUi();
            }
        });

        binding.btnNextSlide.setOnClickListener(v -> {
            if (currentSlide < totalSlides) {
                currentSlide++;
                updateSlideUi();
            }
        });

        binding.btnStartPres.setOnClickListener(v -> {
            currentSlide = 1;
            elapsedSeconds = 0;
            updateSlideUi();
            Toast.makeText(this, "Presentation started (F5)", Toast.LENGTH_SHORT).show();
        });

        binding.btnBlackScreen.setOnClickListener(v -> {
            isBlackScreen = !isBlackScreen;
            int black = ContextCompat.getColor(this, R.color.black_primary);
            int white = ContextCompat.getColor(this, R.color.white);
            if (isBlackScreen) {
                binding.btnBlackScreen.setBackgroundColor(black);
                binding.btnBlackScreen.setTextColor(white);
            } else {
                binding.btnBlackScreen.setBackgroundResource(R.drawable.bg_button_outlined);
                binding.btnBlackScreen.setTextColor(black);
            }
        });

        binding.btnEndPres.setOnClickListener(v ->
                Toast.makeText(this, "Presentation ended (Esc)", Toast.LENGTH_SHORT).show());

        // Press-and-hold laser pointer feedback
        binding.btnLaserPointer.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                binding.btnLaserPointer.setBackgroundColor(
                        ContextCompat.getColor(this, R.color.blue_primary));
                binding.btnLaserPointer.setText("Laser active — move phone to point");
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                binding.btnLaserPointer.setBackgroundColor(
                        ContextCompat.getColor(this, R.color.black_primary));
                binding.btnLaserPointer.setText("Hold to use laser pointer (gyro)");
                return true;
            }
            return false;
        });

        setupBottomNav();
    }

    private void updateSlideUi() {
        binding.tvSlideCounter.setText("Slide " + currentSlide + " of " + totalSlides + " · Notes");
        binding.tvSpeakerNotes.setText("Speaker notes for slide " + currentSlide + " appear here.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(timerRunnable);
    }

    private void setupBottomNav() {
        BottomNavigationInsets.apply(binding.includeBottomNav.getRoot());
        binding.includeBottomNav.bottomNavigation.setSelectedItemId(R.id.nav_slides);
        binding.includeBottomNav.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_slides) return true;
            Intent intent = null;
            if (id == R.id.nav_connect) intent = new Intent(this, ConnectActivity.class);
            else if (id == R.id.nav_trackpad) intent = new Intent(this, TrackpadActivity.class);
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