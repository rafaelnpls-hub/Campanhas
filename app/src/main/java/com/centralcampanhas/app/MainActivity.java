package com.centralcampanhas.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.browser.customtabs.CustomTabsClient;
import androidx.browser.customtabs.CustomTabsServiceConnection;
import androidx.browser.customtabs.CustomTabsSession;
import androidx.browser.customtabs.CustomTabsService;
import androidx.browser.trusted.TrustedWebActivityIntentBuilder;
import android.content.ComponentName;

public class MainActivity extends Activity {

    private static final String URL_CONTA_CHAVE = "https://campanhacc.netlify.app/";
    private static final String URL_VAREJO = "https://campanhavj.netlify.app/";
    private static final String PREFS = "central_campanhas";
    private static final String PREF_DARK = "modo_escuro";

    private View root;
    private TextView titulo, subtitulo, rodape;
    private Button tema;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        root = findViewById(R.id.root);
        titulo = findViewById(R.id.titulo);
        subtitulo = findViewById(R.id.subtitulo);
        rodape = findViewById(R.id.rodape);
        tema = findViewById(R.id.btnTema);
        Button contaChave = findViewById(R.id.btnContaChave);
        Button varejo = findViewById(R.id.btnVarejo);

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        aplicarTema(prefs.getBoolean(PREF_DARK, false));

        tema.setOnClickListener(v -> {
            boolean novoModo = !prefs.getBoolean(PREF_DARK, false);
            prefs.edit().putBoolean(PREF_DARK, novoModo).apply();
            aplicarTema(novoModo);
        });

        contaChave.setOnClickListener(v -> abrirCentral(URL_CONTA_CHAVE));
        varejo.setOnClickListener(v -> abrirCentral(URL_VAREJO));
    }

    private void aplicarTema(boolean escuro) {
        root.setBackgroundColor(Color.parseColor(escuro ? "#0F172A" : "#F3F6FA"));
        titulo.setTextColor(Color.parseColor(escuro ? "#F8FAFC" : "#1D3557"));
        subtitulo.setTextColor(Color.parseColor(escuro ? "#CBD5E1" : "#6B7280"));
        rodape.setTextColor(Color.parseColor(escuro ? "#94A3B8" : "#9CA3AF"));
        tema.setText(escuro ? "☀️ Tema claro" : "🌙 Tema escuro");
        tema.setTextColor(Color.parseColor(escuro ? "#FFFFFF" : "#1D3557"));
        tema.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                Color.parseColor(escuro ? "#1F2937" : "#FFFFFF")));
    }

    private void abrirCentral(String url) {
        Uri uri = Uri.parse(url);
        String packageName = CustomTabsClient.getPackageName(this, null);

        if (packageName == null) {
            abrirFallback(uri);
            return;
        }

        CustomTabsServiceConnection connection = new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
                client.warmup(0L);
                CustomTabsSession session = client.newSession(null);

                if (session == null) {
                    abrirFallback(uri);
                    return;
                }

                try {
                    TrustedWebActivityIntentBuilder twaBuilder =
                            new TrustedWebActivityIntentBuilder(uri);
                    twaBuilder.build(session).launchTrustedWebActivity(MainActivity.this);
                } catch (Exception e) {
                    abrirFallback(uri);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };

        boolean connected = CustomTabsClient.bindCustomTabsService(
                this, packageName, connection);

        if (!connected) {
            abrirFallback(uri);
        }
    }

    private void abrirFallback(Uri uri) {
        CustomTabColorSchemeParams colors = new CustomTabColorSchemeParams.Builder()
                .setToolbarColor(Color.rgb(17, 24, 39))
                .setNavigationBarColor(Color.rgb(17, 24, 39))
                .build();

        CustomTabsIntent intent = new CustomTabsIntent.Builder()
                .setDefaultColorSchemeParams(colors)
                .setShowTitle(false)
                .setUrlBarHidingEnabled(true)
                .build();

        intent.launchUrl(this, uri);
    }
}
