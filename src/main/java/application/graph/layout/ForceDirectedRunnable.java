package application.graph.layout;

import application.graph.GraphPane;
import application.graph.edge.GraphEdge;
import application.graph.shape.GraphShape;
import application.graph.vertex.GraphVertex;
import javafx.geometry.Point2D;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ForceDirectedRunnable implements Runnable{

	private final GraphPane graph;
	private final AtomicLong deltaTime;

	private double repulsionStrength = 300000.0;
	private double attractionStrength = 0.1;
	private double springLength = 500.0;
	private final double damping = 0.1;
	private double timeStep = 100.0;

	private final HashMap<GraphVertex,VertexSpeed> vertices = new HashMap<>();

	private boolean continueRunning = true;

	public ForceDirectedRunnable(GraphPane graph, AtomicLong deltaTime){
		this.graph = graph;
		this.deltaTime = deltaTime;
		graph.getVertices().forEach(vertex -> vertices.put(vertex,new VertexSpeed()));
	}

	@Override public void run(){
		initializeNodes();

		while(continueRunning){
			performIteration();
		}
	}

	private void initializeNodes(){
		double t = 0.0;
		double a = 40;
		for(GraphVertex gv : graph.getVertices()){
			gv.move(a*t*Math.cos(t),a*t*Math.sin(t));
			t += 0.1;
		}
	}

	private void performIteration(){
		double delta = ((double)deltaTime.getAndSet(0) / 1E9) * timeStep;
		if(delta == 0){
			return;
		}

		vertices.keySet().stream().filter(GraphVertex::isVisible).peek(vertexA -> {
			VertexSpeed vertexSpeed = vertices.get(vertexA);

			GraphShape shapeA = vertexA.getShape();

			double resultForceX = 0.0;
			double resultForceY = 0.0;
			final Point2D centerA = shapeA.getCenter();

			//Repel force
			for(GraphVertex vertexB :vertices.keySet()){
				if(!vertexB.isVisible()) continue;

				GraphShape shapeB = vertexB.getShape();
				double dist = Math.max(centerA.distance(shapeB.getCenter()),1.0);
				double force = repulsionStrength / Math.pow(dist,2);

				resultForceX += force * (shapeA.getX() - shapeB.getX()) / dist;
				resultForceY += force * (shapeA.getY() - shapeB.getY()) / dist;
			}

			//Attractive force
			for(GraphEdge edge : vertexA.getAllEdges()){
				if(!edge.isVisible()) continue;

				final GraphShape shapeB = (edge.getVertexA() == vertexA) ? edge.getVertexB().getShape() : edge.getVertexA().getShape();
				final Point2D centerB = shapeB.getCenter();

				double xDist = centerA.getX() - centerB.getX();
				double yDist = centerA.getY() - centerB.getY();

				double dist = Math.max(Math.sqrt(xDist * xDist + yDist * yDist),1.0);
				double force = -attractionStrength * (dist - springLength);

				resultForceX += force * xDist / dist;
				resultForceY += force * yDist / dist;
			}

			//Constant center force
			final double force = repulsionStrength / 100_000_000;
			resultForceX -= force * shapeA.getX();
			resultForceY -= force * shapeA.getY();

			vertexSpeed.add(resultForceX,resultForceY);
		}).forEach(vertex -> {
			VertexSpeed speed = vertices.get(vertex);

			speed.multiply(damping,damping);
			speed.updateVertexPosition(vertex,delta);
			//System.out.println(vertex.getFullLabel()+" - "+vector.toString());
		});
	}

	public void stopRunning(){ this.continueRunning = false; }
	public void setVertexMobility(GraphVertex vertex, boolean canMove){
		vertices.get(vertex).setCanMoveVertex(canMove);
	}
	public double getRepulsionStrength(){
		return repulsionStrength;
	}
	public void setRepulsionStrength(double repulsionStrength){
		this.repulsionStrength = repulsionStrength;
	}
	public double getAttractionStrength(){
		return attractionStrength;
	}
	public void setAttractionStrength(double attractionStrength){
		this.attractionStrength = attractionStrength;
	}
	public double getSpringLength(){
		return springLength;
	}
	public void setSpringLength(double springLength){
		this.springLength = springLength;
	}
	public double getTimeStep(){
		return timeStep;
	}
	public void setTimeStep(double timeStep){
		this.timeStep = timeStep;
	}
}
