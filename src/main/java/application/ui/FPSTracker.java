package application.ui;

import javafx.animation.AnimationTimer;
import javafx.beans.property.StringProperty;

//http://stackoverflow.com/questions/28287398/what-is-the-preferred-way-of-getting-the-frame-rate-of-a-javafx-application
public class FPSTracker{

	private final long[] frameTimes = new long[100];
	private int frameTimeIndex = 0;
	private boolean arrayFilled = false;
	private final AnimationTimer frameRateMeter = new AnimationTimer(){
		@Override public void handle(long now){
			long oldFrameTime = frameTimes[frameTimeIndex];
			frameTimes[frameTimeIndex] = now;
			frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length;
			if(frameTimeIndex == 0){
				arrayFilled = true;
			}
			if(arrayFilled){
				long elapsedNanos = now - oldFrameTime;
				long elapsedNanosPerFrame = elapsedNanos / frameTimes.length;
				double frameRate = 1_000_000_000.0 / elapsedNanosPerFrame;
				fpsString.set(String.format("%.0f",frameRate));
			}
		}
	};

	private final StringProperty fpsString;

	public FPSTracker(StringProperty fpsString){
		this.fpsString = fpsString;
		frameRateMeter.start();
	}
}
