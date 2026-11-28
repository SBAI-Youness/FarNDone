package com.farndone.android;

import com.farndone.android.databinding.ActivityConnectBinding;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class ConnectActivity extends AppCompatActivity {
    private ActivityConnectBinding binding;
    private boolean isWifiSelected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConnectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupLogo();
        setupButtons();
        setupDemoPcClicks();
        setupBottomNav();
    }

    private void setupLogo() {
        SpannableString title = new SpannableString("FarNDone");
        int blue = ContextCompat.getColor(this, R.color.blue_primary);
        title.setSpan(new ForegroundColorSpan(blue), 3, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.tvAppTitle.setText(title);
    }

    private void setupButtons() {
        binding.btnScanQr.setOnClickListener(v -> startActivity(new Intent(this, ScanQrActivity.class)));
        binding.btnWifi.setOnClickListener(v -> {
            isWifiSelected = true;
            updateTransportButtons();
            Toast.makeText(this, "Switched to Wi-Fi discovery", Toast.LENGTH_SHORT).show();
        });
        binding.btnBluetooth.setOnClickListener(v -> {
            isWifiSelected = false;
            updateTransportButtons();
            Toast.makeText(this, "Switched to Bluetooth discovery", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateTransportButtons() {
        int black = ContextCompat.getColor(this, R.color.black_primary);
        int white = ContextCompat.getColor(this, R.color.white);
        if (isWifiSelected) {
            binding.btnWifi.setBackgroundColor(black);
            binding.btnWifi.setTextColor(white);
            binding.btnWifi.setTypeface(null, Typeface.BOLD);
            binding.btnBluetooth.setBackgroundResource(R.drawable.bg_button_outlined);
            binding.btnBluetooth.setTextColor(black);
            binding.btnBluetooth.setTypeface(null, Typeface.NORMAL);
        } else {
            binding.btnBluetooth.setBackgroundColor(black);
            binding.btnBluetooth.setTextColor(white);
            binding.btnBluetooth.setTypeface(null, Typeface.BOLD);
            binding.btnWifi.setBackgroundResource(R.drawable.bg_button_outlined);
            binding.btnWifi.setTextColor(black);
            binding.btnWifi.setTypeface(null, Typeface.NORMAL);
        }
    }

    private void setupDemoPcClicks() {
        binding.cardPc1.setOnClickListener(v -> connectToDemoPc("Fedora (Youness)", true));
        binding.cardPc2.setOnClickListener(v -> connectToDemoPc("Windows 11 (Taha)", false));
    }

    private void connectToDemoPc(String name, boolean online) {
        if (!online) {
            Toast.makeText(this, name + " is offline", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Connected to " + name, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, TrackpadActivity.class));
        overridePendingTransition(0, 0);
        finish();
    }

    private void setupBottomNav() {
        BottomNavigationInsets.apply(binding.includeBottomNav.getRoot());
        binding.includeBottomNav.bottomNavigation.setSelectedItemId(R.id.nav_connect);
        binding.includeBottomNav.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_connect) return true;
            Intent intent = null;
            if (id == R.id.nav_trackpad) intent = new Intent(this, TrackpadActivity.class);
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
