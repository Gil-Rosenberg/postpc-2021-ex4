package exercise.find.roots;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

  private BroadcastReceiver broadcastReceiverForSuccess = null;
  private BroadcastReceiver broadcastReceiverForFailure = null;
  private boolean inProgress = false;

  private boolean textIsValid(String newText) {
    long num;
    try {
      num = Long.parseLong(newText);
    }
    catch (Exception e){
      return false;
    }
    return (0 < num) && (num < Long.MAX_VALUE);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // find all views:
    ProgressBar progressBar = findViewById(R.id.progressBar);
    EditText editTextUserInput = findViewById(R.id.editTextInputNumber);
    Button buttonCalculateRoots = findViewById(R.id.buttonCalculateRoots);

    // set initial UI:
    progressBar.setVisibility(View.GONE); // hide progress
    editTextUserInput.setText(""); // cleanup text in edit-text
    editTextUserInput.setEnabled(true); // set edit-text as enabled (user can input text)
    buttonCalculateRoots.setEnabled(false); // set button as disabled (user can't click)

    // set listener on the input written by the keyboard to the edit-text
    editTextUserInput.addTextChangedListener(new TextWatcher() {
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

      public void onTextChanged(CharSequence s, int start, int before, int count) {}

      public void afterTextChanged(Editable s) {
        // text did change
        String newText = editTextUserInput.getText().toString();
        inProgress = false;
        editTextUserInput.setEnabled(true);
        buttonCalculateRoots.setEnabled(textIsValid(newText));
      }
    });

    // set click-listener to the button
    buttonCalculateRoots.setOnClickListener(v -> {
      Intent intentToOpenService = new Intent(MainActivity.this, CalculateRootsService.class);
      String userInputString = editTextUserInput.getText().toString();
      buttonCalculateRoots.setEnabled(textIsValid(userInputString));

      long userInputLong = Long.parseLong(userInputString);
      intentToOpenService.putExtra("number_for_service", userInputLong);
      startService(intentToOpenService);

      // set views states according to the spec (below)
      inProgress = true;
      editTextUserInput.setEnabled(false);
      buttonCalculateRoots.setEnabled(false);
      progressBar.setVisibility(View.VISIBLE);
    });

    // register a broadcast-receiver to handle action "found_roots"
    broadcastReceiverForSuccess = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent incomingIntent) {
        if (incomingIntent == null || !incomingIntent.getAction().equals("found_roots")) return;

        // success finding roots!
        long root1 = incomingIntent.getLongExtra("root1", -1);
        long root2 = incomingIntent.getLongExtra("root2", -1);
        long original_number = incomingIntent.getLongExtra("original_number", -1);
        long calculation_time_in_seconds = incomingIntent.getLongExtra("calculation_time_in_seconds",
                -1);

        // set views states according to the spec:
        inProgress = false;
        editTextUserInput.setEnabled(true);
        buttonCalculateRoots.setEnabled(true);
        progressBar.setVisibility(View.GONE);

        // start new activity for success:
        Intent successIntent = new Intent(MainActivity.this, SuccessScreen.class);
        successIntent.putExtra("calculation",
                root1 + " * " + root2 + " = " + original_number);
        successIntent.putExtra("calculation_time_in_seconds", calculation_time_in_seconds);
        startActivity(successIntent);
      }
    };

    registerReceiver(broadcastReceiverForSuccess, new IntentFilter("found_roots"));

    // register a broadcast-receiver to handle action "stopped_calculations"
    broadcastReceiverForFailure = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent incomingIntent) {
        if (incomingIntent == null || !incomingIntent.getAction().equals("stopped_calculations"))
          return;
        // failure finding roots!

        // set views states according to the spec:
        inProgress = false;
        progressBar.setVisibility(View.GONE);
        editTextUserInput.setEnabled(true);
        buttonCalculateRoots.setEnabled(true);

        // time took for calculation:
        long time = incomingIntent.getLongExtra("time_until_give_up_seconds", -1);

        // send Toast:
        Toast.makeText(MainActivity.this,
                "calculation aborted after " + time + " seconds",
                Toast.LENGTH_SHORT).show();
      }
    };
    registerReceiver(broadcastReceiverForFailure, new IntentFilter("stopped_calculations"));
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    this.unregisterReceiver(broadcastReceiverForSuccess);
    this.unregisterReceiver(broadcastReceiverForFailure);
  }

  @Override
  protected void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);

    // find changeable views:
    EditText editTextInputNumber = findViewById(R.id.editTextInputNumber);

    // insert to bundle:
    outState.putString("editTextInputNumber", editTextInputNumber.getText().toString());
    outState.putBoolean("inProgress", inProgress);
  }

  @Override
  protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);

    // extract data from bundle:
    String prevInput = savedInstanceState.getString("editTextInputNumber");
    inProgress = savedInstanceState.getBoolean("inProgress");

    // find all views:
    ProgressBar progressBar = findViewById(R.id.progressBar);
    EditText editTextUserInput = findViewById(R.id.editTextInputNumber);
    Button buttonCalculateRoots = findViewById(R.id.buttonCalculateRoots);

    // set state:
    editTextUserInput.setText(prevInput);

    if (inProgress){
      editTextUserInput.setEnabled(false);
      buttonCalculateRoots.setEnabled(false);
      progressBar.setVisibility(View.VISIBLE);
    }

    else {
      editTextUserInput.setEnabled(true);
      buttonCalculateRoots.setEnabled(true);
      progressBar.setVisibility(View.GONE);
    }
  }
}
