package application.graph.shape;

import application.graph.ResizableCanvas;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

public class CircleShape extends GraphShape{

	private double radius;

	public CircleShape(double radius){
		this.radius = radius;
	}
	public CircleShape(int x, int y, double radius){
		this.x = x;
		this.y = y;
		this.radius = radius;
	}

	public double getRadius(){
		return radius;
	}

	@Override
	public double getLabelWidthLimit() {
		return radius * 2;
	}

	@Override
	public Point2D getCenter() {
		return new Point2D(x + radius,y + radius);
	}

	@Override
	public boolean containsPoint(double x, double y) {
		return new Point2D(x,y).distance(getCenter()) <= radius;
	}

	@Override
	public boolean intersects(Rectangle2D rectangle) {
		final Point2D center = getCenter();
		final Point2D corner1 = new Point2D(rectangle.getMinX(),rectangle.getMinY());
		final Point2D corner2 = new Point2D(rectangle.getMaxX(),rectangle.getMinY());
		final Point2D corner3 = new Point2D(rectangle.getMinX(),rectangle.getMaxY());
		final Point2D corner4 = new Point2D(rectangle.getMaxX(),rectangle.getMaxY());

		return (x <= rectangle.getMaxX() &&
				x+radius*2 >= rectangle.getMinX() &&
				y <= rectangle.getMaxY() &&
				y+radius*2 >= rectangle.getMinY()) || (
				center.distance(corner1) <= radius ||
						center.distance(corner2) <= radius ||
						center.distance(corner3) <= radius ||
						center.distance(corner4) <= radius);
	}

	@Override
	public Point2D getPointIntersection(Point2D from) {
		Point2D direction = from.subtract(getCenter()).normalize().multiply(radius);
		return getCenter().add(direction);
	}

	@Override public void draw(ResizableCanvas canvas){
		GraphicsContext gc = canvas.getGraphicsContext2D();

		if(strokeColor != null){
			gc.setStroke(strokeColor);
			gc.strokeOval(x,y,radius * 2,radius * 2);
		}
		if(fillColor != null){
			gc.setFill(fillColor);
			gc.fillOval(x,y,radius * 2,radius * 2);
		}
	}
}
