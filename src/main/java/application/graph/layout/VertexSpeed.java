package application.graph.layout;

import application.graph.vertex.GraphVertex;
import application.util.Utils;

public class VertexSpeed{
	private double vx;
	private double vy;
	private boolean canMoveVertex = true;
	private static final double maxSpeed = 5;

	public VertexSpeed(){
		this.vx = 0.0;
		this.vy = 0.0;
	}
	public VertexSpeed(double vx, double vy){
		this.vx = vx;
		this.vy = vy;
	}

	public void add(double vx, double vy){
		this.vx += vx;
		this.vy += vy;
	}
	public void multiply(double valueX, double valueY){
		this.vx *= valueX;
		this.vy *= valueY;
	}
	public void setCanMoveVertex(boolean value){
		this.canMoveVertex = value;
	}

	public void updateVertexPosition(GraphVertex vertex,double timePassed){
		if(!canMoveVertex) return;
		vertex.move(
				Utils.clamp(vx,-maxSpeed,maxSpeed)*timePassed,
				Utils.clamp(vy,-maxSpeed,maxSpeed)*timePassed
		);
	}
}
