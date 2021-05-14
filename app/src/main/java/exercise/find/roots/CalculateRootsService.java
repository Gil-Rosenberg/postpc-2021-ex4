package exercise.find.roots;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class CalculateRootsService extends IntentService {

  private final int maxTime = 20000;

  private void timeExpired(long numberToCalculateRootsFor){
    Intent broadcastIntent = new Intent("stopped_calculations");
    broadcastIntent.putExtra("original_number", numberToCalculateRootsFor);
    broadcastIntent.putExtra("time_until_give_up_seconds", maxTime);
    sendBroadcast(broadcastIntent);
  }

  private void sendRoots(long numberToCalculateRootsFor, long root1, long root2){
    Intent broadcastIntent = new Intent("found_roots");
    broadcastIntent.putExtra("original_number", numberToCalculateRootsFor);
    broadcastIntent.putExtra("root1", root1);
    broadcastIntent.putExtra("root2", root2);
    sendBroadcast(broadcastIntent);
  }

  private void calculateRoots(long numberToCalculateRootsFor, long timeStartMs){
    boolean isPrime = true;
    int i;
    for (i = 2; i <= numberToCalculateRootsFor / 2; ++i) {

      // upon failure:
      if ((timeStartMs - System.currentTimeMillis()) > maxTime){
        timeExpired(numberToCalculateRootsFor);
      }

      // condition for non-prime number
      if (numberToCalculateRootsFor % i == 0) {
        isPrime = false;
        break;
      }
    }

    // upon success:
    if (isPrime){
      sendRoots(numberToCalculateRootsFor, numberToCalculateRootsFor, 1);
    }

    else {
      sendRoots(numberToCalculateRootsFor, i, numberToCalculateRootsFor / i);
    }
  }

  public CalculateRootsService() {
    super("CalculateRootsService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    if (intent == null) return;
    long timeStartMs = System.currentTimeMillis();
    long numberToCalculateRootsFor = intent.getLongExtra("number_for_service", 0);
    if (numberToCalculateRootsFor <= 0) {
      Log.e("CalculateRootsService", "can't calculate roots for non-positive input" + numberToCalculateRootsFor);
      return;
    }
    calculateRoots(numberToCalculateRootsFor, timeStartMs);

    /*
    TODO:
     calculate the roots.
     check the time (using `System.currentTimeMillis()`) and stop calculations if can't find an answer after 20 seconds
     upon success (found a root, or found that the input number is prime):
      send broadcast with action "found_roots" and with extras:
       - "original_number"(long)
       - "root1"(long)
       - "root2"(long)
     upon failure (giving up after 20 seconds without an answer):
      send broadcast with action "stopped_calculations" and with extras:
       - "original_number"(long)
       - "time_until_give_up_seconds"(long) the time we tried calculating

      examples:
       for input "33", roots are (3, 11)
       for input "30", roots can be (3, 10) or (2, 15) or other options
       for input "17", roots are (17, 1)
       for input "829851628752296034247307144300617649465159", after 20 seconds give up
     */
  }
}