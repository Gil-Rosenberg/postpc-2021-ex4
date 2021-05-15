package exercise.find.roots;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class CalculateRootsService extends IntentService {

  private final int maxTime = 20000;

  private void timeExpired(long numberToCalculateRootsFor, long timeStartMs){
    Intent broadcastIntent = new Intent("stopped_calculations");
    broadcastIntent.putExtra("original_number", numberToCalculateRootsFor);
    long timeInSeconds = (System.currentTimeMillis() - timeStartMs) / 1000;
    broadcastIntent.putExtra("time_until_give_up_seconds", timeInSeconds);

    sendBroadcast(broadcastIntent);
  }

  private void sendRoots(long numberToCalculateRootsFor, long root1, long root2, long timeStartMs){
    Intent broadcastIntent = new Intent("found_roots");
    broadcastIntent.putExtra("original_number", numberToCalculateRootsFor);
    broadcastIntent.putExtra("root1", root1);
    broadcastIntent.putExtra("root2", root2);

    long timeInSeconds = (System.currentTimeMillis() - timeStartMs) / 1000;

    broadcastIntent.putExtra("calculation_time_in_seconds", timeInSeconds);
    sendBroadcast(broadcastIntent);
  }

  private void calculateRoots(long numberToCalculateRootsFor, long timeStartMs){
    boolean isPrime = true;
    int i;
    for (i = 2; i <= numberToCalculateRootsFor / 2; ++i) {

      // upon failure:
      if ((System.currentTimeMillis() - timeStartMs) > maxTime){
        timeExpired(numberToCalculateRootsFor, timeStartMs);
        return;
      }

      // condition for non-prime number
      if (numberToCalculateRootsFor % i == 0) {
        isPrime = false;
        break;
      }
    }

    // upon success:
    if (isPrime){
      sendRoots(numberToCalculateRootsFor, numberToCalculateRootsFor, 1, timeStartMs);
    }

    else {
      sendRoots(numberToCalculateRootsFor, i, numberToCalculateRootsFor / i, timeStartMs);
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
  }
}