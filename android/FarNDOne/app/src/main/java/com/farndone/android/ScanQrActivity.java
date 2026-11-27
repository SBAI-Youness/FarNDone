package com.farndone.android;

import com.farndone.android.databinding.ActivityScanQrBinding;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class ScanQrActivity extends AppCompatActivity {

    private ActivityScanQrBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScanQrBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Tap the viewfinder box to simulate scanning a QR code during UI testing
        binding.cameraPreviewContainer.setOnClickListener(v -> {
            Toast.makeText(this, "Simulated QR Scan: Paired with Fedora!", Toast.LENGTH_SHORT).show();
            finish();
        });

        binding.btnCancelScan.setOnClickListener(v -> finish());
    }
}