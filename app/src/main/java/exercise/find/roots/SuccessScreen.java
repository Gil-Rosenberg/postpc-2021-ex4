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
        long originalNum = intentCreatedMe.getLongExtra("original_number", -1);
        long root1 = intentCreatedMe.getLongExtra("root1", -1);
        long root2 = intentCreatedMe.getLongExtra("root2", -1);
        textView.setText(originalNum + " = " + root1 + " * " + root2);

    }

}
