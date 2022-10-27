package com.example.modul7;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class MainActivity extends AppCompatActivity {
    ImageView slot1, slot2, slot3;
    int img1, img2, img3;
    TextView status;
    Button tombolPlay;
    boolean mulai = true;
    ArrayList<String> url = new ArrayList<>();
    Random acak = new Random();
    ExecutorService exe1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        slot1 = findViewById(R.id.slot1);
        slot2 = findViewById(R.id.slot2);
        slot3 = findViewById(R.id.slot3);
        status = findViewById(R.id.status);
        tombolPlay = findViewById(R.id.tombol);

        exe1 = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        tombolPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == tombolPlay.getId()) {
                    tombolPlay.setText("Stop");
                    status.setText("Sedang berjalan");
                    if (!mulai) {
                        mulai = !mulai;
                        exe1.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    final String link =
                                            loadStringFromNetwork("https://mocki.io/v1/821f1b13-fa9a-43aa-ba9a-9e328df8270e");
                                    try {
                                        JSONArray jsonArray = new JSONArray(link);
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            url.add(jsonObject.getString("url"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    while (mulai) {
                                        img1 = acak.nextInt(3);
                                        img2 = acak.nextInt(3);
                                        img3 = acak.nextInt(3);
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Glide.with(MainActivity.this).load(url.get(img1)).into(slot1);
                                                Glide.with(MainActivity.this).load(url.get(img2)).into(slot2);
                                                Glide.with(MainActivity.this).load(url.get(img3)).into(slot3);
                                            }
                                        });
                                        try {
                                            Thread.sleep(100);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        mulai = false;
                        if ((img1 == img2) && (img2 == img3)) {
                            Toast.makeText(MainActivity.this, "Selamat anda menang", Toast.LENGTH_SHORT).show();
                            tombolPlay.setText("Play");
                            status.setText("Terhenti");
                        } else {
                            Toast.makeText(MainActivity.this, "Maaf anda kurang beruntung, silakan coba lagi", Toast.LENGTH_SHORT).show();
                            tombolPlay.setText("Play");
                            status.setText("Terhenti");
                        }
                    }
                }
            }
        });
    }

    private String loadStringFromNetwork(String jpg) throws IOException {
        final URL link = new URL(jpg);
        final InputStream went = link.openStream();
        final StringBuilder out = new StringBuilder();
        final byte[] buffer = new byte[1024];
        try {
            for (int ctr; (ctr = went.read(buffer)) != -1; ) {
                out.append(new String(buffer, 0, ctr));
            }
        } catch (IOException e) {
            throw new RuntimeException("Gagal", e);
        }
        final String file = out.toString();
        return file;
    }
}