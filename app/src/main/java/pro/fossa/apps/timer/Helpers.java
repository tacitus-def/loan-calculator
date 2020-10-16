package pro.fossa.apps.timer;

public class Helpers {
	public static String getTimeText (long time) {
	      int milli = (int) (time % 1000);
	      int seconds = (int) (Math.floor(time / 1000));
	      int sec = seconds % 60;
	      int min = (seconds - sec) % 3600;
	      int hrs = ((seconds - sec - min) / 3600);

	      String ret = String.format("%02d:%02d:%02d.%03d", hrs, (int) (min / 60), sec, milli);
	      
	      return ret;
	}
}
