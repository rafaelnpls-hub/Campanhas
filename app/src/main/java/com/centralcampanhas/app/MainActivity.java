package com.centralcampanhas.app;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.browser.customtabs.CustomTabsClient;

import java.util.ArrayList;
import java.util.List;

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
        abrirFallback(Uri.parse(url));
    }

    private void abrirFallback(Uri uri) {
        // Consulta uma URL genérica para encontrar navegadores, sem abrir a página.
        Intent probe = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.example.com/"));
        probe.addCategory(Intent.CATEGORY_BROWSABLE);
        List<String> packages = new ArrayList<>();
        for (ResolveInfo info : getPackageManager().queryIntentActivities(probe, 0)) {
            String candidate = info.activityInfo.packageName;
            if (!getPackageName().equals(candidate) && !packages.contains(candidate)) {
                packages.add(candidate);
            }
        }
        // Mantém a preferência pelo navegador padrão quando ele suporta Custom Tabs.
        String browserPackage = CustomTabsClient.getPackageName(this, packages, false);

        if (browserPackage != null && !getPackageName().equals(browserPackage)) {
            CustomTabColorSchemeParams colors = new CustomTabColorSchemeParams.Builder()
                    .setToolbarColor(Color.rgb(17, 24, 39))
                    .setNavigationBarColor(Color.rgb(17, 24, 39))
                    .build();

            CustomTabsIntent customTab = new CustomTabsIntent.Builder()
                    .setDefaultColorSchemeParams(colors)
                    .setShowTitle(false)
                    .setUrlBarHidingEnabled(true)
                    .build();

            // Impede que o Android encaminhe esta URL de volta ao próprio app.
            customTab.intent.setPackage(browserPackage);
            try {
                customTab.launchUrl(this, uri);
                return;
            } catch (ActivityNotFoundException | SecurityException e) {
                Log.w("CentralCampanhas", "Falha ao abrir Custom Tab; tentando navegador", e);
            }
        }

        // O manifesto não registra mais o app como receptor desses links.
        Intent browser = new Intent(Intent.ACTION_VIEW, uri);
        browser.addCategory(Intent.CATEGORY_BROWSABLE);
        try {
            startActivity(browser);
        } catch (ActivityNotFoundException | SecurityException e) {
            Log.e("CentralCampanhas", "Não foi possível abrir a campanha", e);
            Toast.makeText(this,
                    "Não foi possível abrir a campanha. Verifique se há um navegador instalado e habilitado.",
                    Toast.LENGTH_LONG).show();
        }
    }
}
