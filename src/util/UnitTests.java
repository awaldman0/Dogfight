package util;
public class UnitTests {
	public static void CheckFrameRate(long TargetTime,long DeliveredTime,int TargetFPS) {
		int TimeBetweenFrames = 1000 / TargetFPS;
		if((TargetTime - DeliveredTime) > TimeBetweenFrames) {
		   System.out.println("FPS failure by 10 m");
		   System.out.println("Frame was late by  "+ (TargetTime - DeliveredTime) + " ms");
		   //Write out to log file 
		}
	}
}
