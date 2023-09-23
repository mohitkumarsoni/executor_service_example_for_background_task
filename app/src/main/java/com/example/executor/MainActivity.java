package com.example.executor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private Button uiBtn, TStartBtn, TStopBtn;
    private TextView textView;
    private int clickCount = 0;
    private volatile boolean toStop = false;
    private volatile boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();

        uiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uiCheckBtnClicked();
            }
        });

        TStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                threadStartBtnClicked();
            }
        });

        TStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                threadStopBtnClicked();
            }
        });

    }

    private void threadStopBtnClicked() {
        toStop = true;
        isRunning = false;
        textView.setText("inactive");
    }

    private void threadStartBtnClicked() {
        toStop = false;
        if (!toStop && !isRunning) {

            // will be running on background thread
            ExecutorService service = Executors.newSingleThreadExecutor();
            service.execute(new Runnable() {
                @Override
                public void run() {
                    isRunning = true;
                    for (int i=0; i<10000; i++){
                        if (toStop){
                            return;
                        }
                        String status = "running on ui : "+ Integer.toString(i);

                        //will be running on ui thread for updating textView
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText(status);
                            }
                        });

                        //will be running on background thread
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        }
    }

    private void uiCheckBtnClicked() {
        clickCount++;
        uiBtn.setText(clickCount+" times button clicked");
    }

    private void findViews() {
        uiBtn = findViewById(R.id.buttonUiCheck);
        TStartBtn = findViewById(R.id.buttonThreadStart);
        TStopBtn = findViewById(R.id.buttonThreadStop);
        textView = findViewById(R.id.textView);
    }
}