package exercise.find.roots;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SuccessScreen extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savesInstanceState) {

        super.onCreate(savesInstanceState);
        setContentView(R.layout.activity_success);

        Intent intentCreatedMe = getIntent();
        TextView textView = findViewById(R.id.successText);
        TextView calculationTime = findViewById(R.id.calculation_time);

        String result = intentCreatedMe.getStringExtra("calculation");
        long time = intentCreatedMe.getLongExtra("calculation_time_in_seconds", -1);
        textView.setText(result);
        calculationTime.setText("calculation time in seconds: "+ time);
    }
}
