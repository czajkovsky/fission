package put.ai.snort.myplayer;

public class MyPlayerTimer {

  private static long end;

  public static void initTimer(long allowed) {
    end = System.currentTimeMillis() + allowed;
  }

  public static long timeLeft() {
    return end - System.currentTimeMillis();
  }

}
