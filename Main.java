package com.example.myapplication;

import static com.example.myapplication.R.layout.activity_main;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class Main extends AppCompatActivity {

    // Peta antara ID CheckBox dan nama gejala di JSON
    private HashMap<Integer, String> gejalaMap = new HashMap<>();
    private TextView textHasil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textHasil = findViewById(R.id.textHasil);
        Button buttonDiagnosa = findViewById(R.id.buttonDiagnosa);

        // Hubungkan semua checkbox ke nama gejalanya (sesuai JSON)
        gejalaMap.put(R.id.checkDemam, "demam");
        gejalaMap.put(R.id.checkHilangNafsu, "hilang_nafsu_makan");
        gejalaMap.put(R.id.checkMuntah, "sering_muntah");
        gejalaMap.put(R.id.checkDiareDarah, "diare_darah");
        gejalaMap.put(R.id.checkBuluKasar, "bulu_kasar");
        gejalaMap.put(R.id.checkBauMulut, "bau_mulut");
        gejalaMap.put(R.id.checkBersin, "bersin");
        gejalaMap.put(R.id.checkPeradanganMataHidung, "peradangan_mata_hidung");
        gejalaMap.put(R.id.checkAirHidung, "air_hidung");
        gejalaMap.put(R.id.checkAirMata, "air_mata");
        gejalaMap.put(R.id.checkLukaMulut, "luka_mulut");
        gejalaMap.put(R.id.checkBuluPitak, "bulu_pitak");
        gejalaMap.put(R.id.checkKulitKerak, "kulit_kerak");
        gejalaMap.put(R.id.checkLukaKoreng, "luka_koreng");
        gejalaMap.put(R.id.checkMataBengkak, "mata_bengkak");
        gejalaMap.put(R.id.checkCairanHidungHijau, "cairan_hidung");
        gejalaMap.put(R.id.checkPerutBuncit, "perut_buncit");
        gejalaMap.put(R.id.checkBeratTurun, "berat_turun");
        gejalaMap.put(R.id.checkMuntahCacing, "muntah_cacing");
        gejalaMap.put(R.id.checkDiareCacing, "diare_cacing");
        gejalaMap.put(R.id.checkKulitJamur, "kulit_jamur");
        gejalaMap.put(R.id.checkBuluKusam, "bulu_kusam");
        gejalaMap.put(R.id.checkKulitLesi, "kulit_lesi");
        gejalaMap.put(R.id.checkKulitBersisik, "kulit_bersisik");
        gejalaMap.put(R.id.checkKepalaMenggelepar, "kepala_menggelepar");
        gejalaMap.put(R.id.checkCairanTelinga, "cairan_telinga");
        gejalaMap.put(R.id.checkBauTelinga, "bau_telinga");
        gejalaMap.put(R.id.checkTelingaMerah, "telinga_merah");
        gejalaMap.put(R.id.checkKotoranTelinga, "kotoran_telinga");
        gejalaMap.put(R.id.checkTelingaBengkak, "telinga_bengkak");

        // Tombol Diagnosa ditekan
        buttonDiagnosa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tampilkanDiagnosa();
            }
        });
    }

    // Fungsi menampilkan hasil dari JSON
    private void tampilkanDiagnosa() {
        ArrayList<String> selectedSymptoms = new ArrayList<>();

        // Ambil semua checkbox dari gejalaMap
        for (Integer id : gejalaMap.keySet()) {
            CheckBox cb = findViewById(id);
            if (cb != null && cb.isChecked()) {
                selectedSymptoms.add(gejalaMap.get(id));
            }
        }

        // Jika belum pilih apapun
        if (selectedSymptoms.isEmpty()) {
            textHasil.setText("⚠️ Silakan pilih minimal satu gejala terlebih dahulu.");
            return;
        }

        // Baca file JSON
        String jsonString = loadJSONFromAsset();
        if (jsonString == null) {
            textHasil.setText("❌ Gagal memuat data penyakit.");
            return;
        }

        try {
            JSONArray rules = new JSONArray(jsonString);
            String hasil = "Tidak ditemukan penyakit yang cocok.";

            for (int i = 0; i < rules.length(); i++) {
                JSONObject rule = rules.getJSONObject(i);
                JSONArray conditions = rule.getJSONArray("if");
                int matchCount = 0;

                for (int j = 0; j < conditions.length(); j++) {
                    String cond = conditions.getString(j);
                    if (selectedSymptoms.contains(cond)) {
                        matchCount++;
                    }
                }

                // Jika semua gejala di rule cocok dengan gejala user
                if (matchCount == conditions.length()) {
                    hasil = "🐾 Kemungkinan penyakit: " + rule.getString("then") +
                            "\nTingkat keyakinan (CF): " + rule.getDouble("cf");
                    break;
                }
            }

            textHasil.setText(hasil);
        } catch (JSONException e) {
            e.printStackTrace();
            textHasil.setText("Terjadi kesalahan saat membaca JSON.");
        }
    }

    // Fungsi baca file JSON dari assets
    private String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = getAssets().open("Diagnosa_Penyakit_Kucing.JSON");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
