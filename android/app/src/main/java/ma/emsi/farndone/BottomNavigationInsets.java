package ma.emsi.farndone;

import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

final class BottomNavigationInsets {
    private BottomNavigationInsets() {}

    static void apply(View navigationContainer) {
        final int initialBottomPadding = navigationContainer.getPaddingBottom();
        BottomNavigationView bottomNavigation =
                navigationContainer.findViewById(R.id.bottom_navigation);

        navigationContainer.findViewById(R.id.labelConnect).setOnClickListener(
                v -> bottomNavigation.setSelectedItemId(R.id.nav_connect));
        navigationContainer.findViewById(R.id.labelTrackpad).setOnClickListener(
                v -> bottomNavigation.setSelectedItemId(R.id.nav_trackpad));
        navigationContainer.findViewById(R.id.labelSlides).setOnClickListener(
                v -> bottomNavigation.setSelectedItemId(R.id.nav_slides));
        navigationContainer.findViewById(R.id.labelMedia).setOnClickListener(
                v -> bottomNavigation.setSelectedItemId(R.id.nav_media));

        ViewCompat.setOnApplyWindowInsetsListener(navigationContainer, (view, windowInsets) -> {
            Insets systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars());
            view.setPadding(
                    view.getPaddingLeft(),
                    view.getPaddingTop(),
                    view.getPaddingRight(),
                    initialBottomPadding + systemBars.bottom
            );
            return WindowInsetsCompat.CONSUMED;
        });
        ViewCompat.requestApplyInsets(navigationContainer);
    }
}
