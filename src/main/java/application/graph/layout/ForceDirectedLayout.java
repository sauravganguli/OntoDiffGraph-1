package application.graph.layout;

import application.graph.GraphPane;
import javafx.animation.AnimationTimer;

import java.util.concurrent.atomic.AtomicLong;

public class ForceDirectedLayout implements GraphLayout{

	private final GraphPane graph;
	private final AtomicLong deltaTime = new AtomicLong(0);
	private final AnimationTimer animationTimer = new AnimationTimer(){
		private long previousNow = -1;

		@Override public void handle(long now){
			if(this.previousNow != -1){
				deltaTime.addAndGet(now-this.previousNow);
			}

			this.previousNow = now;
			graph.draw();
		}
	};

	private Thread layoutThread;
	private ForceDirectedRunnable runnable;

	public ForceDirectedLayout(GraphPane graph){
		this.graph = graph;
	}

	@Override public void applyLayout(){
		this.runnable = new ForceDirectedRunnable(graph,deltaTime);
		this.layoutThread = new Thread(runnable);
		layoutThread.setDaemon(true);
		layoutThread.start();

		animationTimer.start();
	}

	@Override public void resetLayout(){
		stopLayout();
		applyLayout();
	}

	@Override public void stopLayout(){
		animationTimer.stop();
		if(runnable != null) runnable.stopRunning();
	}

	public ForceDirectedRunnable getRunnable(){
		return runnable;
	}
}
