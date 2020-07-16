package application.graph.edge;

import application.graph.ResizableCanvas;
import application.graph.vertex.GraphVertex;
import application.util.Utils;
import application.util.Vars;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

public class UnlabeledEdge extends GraphEdge{

	public UnlabeledEdge(GraphVertex nodeA,GraphVertex nodeB){
		super(nodeA,nodeB);
	}

	@Override public EdgeGroup getEdgeGroup(){
		return vertexA.getEdgesTo(vertexB);
	}

	@Override public Point2D[] getPath(){
		if(vertexA == vertexB){
			return getSameVertexPath();
		}
		else{
			return getPathToVertex();
		}
	}
	protected Point2D[] getSameVertexPath(){
		final EdgeGroup group = vertexA.getEdgesTo(vertexB);

		Point2D start = group.getStartPoint(this);
		Point2D end = group.getEndPoint(this);

		// No intersection is found
		if(start == null || end == null){
			start = vertexA.getShape().getCenter();
			end = vertexB.getShape().getCenter();
		}

		final Point2D middlePoint = group.getEdgeMidpoint(this);

		Point2D normal = Utils.getNormal(Utils.getVector(start,middlePoint));
		Point2D middle = start.midpoint(middlePoint);

		Point2D middleA = middle.subtract(normal.multiply(Vars.SELF_EDGE_LABEL_SPACING));
		Point2D middleB = middle.add(normal.multiply(Vars.SELF_EDGE_LABEL_SPACING));

		return new Point2D[]{start,middleA,middlePoint,middleB,end};
	}

	protected Point2D[] getPathToVertex(){
		final EdgeGroup group = vertexA.getEdgesTo(vertexB);
		Point2D start = group.getStartPoint(this);
		Point2D end = group.getEndPoint(this);

		// No intersection is found
		if(start == null || end == null){
			start = vertexA.getShape().getCenter();
			end = vertexB.getShape().getCenter();
		}

		if(group.isMultipleEdgeGroup()){
			return new Point2D[]{start,group.getEdgeMidpoint(this),end};
		}
		else{
			return new Point2D[]{start,end};
		}
	}

	public void drawLine(GraphicsContext gc, Point2D[] path){
		double[] xs = new double[path.length];
		double[] ys = new double[path.length];
		for(int i = 0; i<path.length; i++){
			Point2D position = path[i];
			xs[i] = position.getX();
			ys[i] = position.getY();
		}

		gc.setStroke(color);
		gc.setFill(color);
		gc.strokePolyline(xs,ys,path.length);
	}

	public void drawArrow(GraphicsContext gc, Point2D[] path){
		Point2D intersection = vertexB.getShape().getPointIntersection(path[path.length-2]);

		if(intersection != null){
			Point2D direction = Utils.getVector(path[path.length-2],intersection);

			gc.setStroke(color);
			gc.setFill(color);
			Utils.drawTriangle(gc,Math.PI/6,10,intersection,direction);
		}
	}

	@Override public void draw(ResizableCanvas canvas){
		final Point2D[] path = getPath();
		final GraphicsContext gc = canvas.getGraphicsContext2D();
		drawLine(gc,path);
		drawArrow(gc,path);
	}
}
