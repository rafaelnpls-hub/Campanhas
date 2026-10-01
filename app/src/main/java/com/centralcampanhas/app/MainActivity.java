package com.centralcampanhas.app;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;

public class MainActivity extends Activity {

    private static final String URL_CONTA_CHAVE = "https://campanhacc.netlify.app/";
    private static final String URL_VAREJO = "https://campanhavj.netlify.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button contaChave = findViewById(R.id.btnContaChave);
        Button varejo = findViewById(R.id.btnVarejo);

        contaChave.setOnClickListener(v -> abrirCentral(URL_CONTA_CHAVE));
        varejo.setOnClickListener(v -> abrirCentral(URL_VAREJO));
    }

    private void abrirCentral(String url) {
        CustomTabColorSchemeParams colors = new CustomTabColorSchemeParams.Builder()
                .setToolbarColor(Color.rgb(17, 24, 39))
                .setNavigationBarColor(Color.rgb(17, 24, 39))
                .build();

        CustomTabsIntent intent = new CustomTabsIntent.Builder()
                .setDefaultColorSchemeParams(colors)
                .setShowTitle(false)
                .setUrlBarHidingEnabled(true)
                .build();

        intent.launchUrl(this, Uri.parse(url));
    }
}
