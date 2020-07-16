package application.graph.shape;

import application.graph.ResizableCanvas;
import application.util.Utils;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

public class RectangleShape extends GraphShape{

	private double width;
	private double height;

	public RectangleShape(double width, double height){
		this.width = width;
		this.height = height;
	}
	public RectangleShape(int x, int y, double width, double height){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	@Override
	public double getLabelWidthLimit() {
		return width;
	}

	@Override
	public Point2D getCenter() {
		return new Point2D(x + width / 2.0,y + height / 2.0);
	}

	@Override
	public boolean containsPoint(double x, double y) {
		return (x >= this.x && x <= this.x + width && y >= this.y && y <= this.y + height);
	}

	@Override
	public boolean intersects(Rectangle2D rectangle) {
		return (x <= rectangle.getMaxX() && x+width >= rectangle.getMinX() && y <= rectangle.getMaxY() && y+height >= rectangle.getMinY());
	}

	@Override
	public Point2D getPointIntersection(Point2D from){
		Point2D to = getCenter();

		Point2D corner1 = new Point2D(x+width,y);
		Point2D corner2 = new Point2D(x,y);
		Point2D corner3 = new Point2D(x,y+height);
		Point2D corner4 = new Point2D(x+width,y+height);

		double lineAngle = Utils.getAngle(Utils.getVector(to,from));
		double corner1Angle = Utils.getAngle(Utils.getVector(to,corner1));
		double corner2Angle = Utils.getAngle(Utils.getVector(to,corner2));
		double corner3Angle = Utils.getAngle(Utils.getVector(to,corner3));
		double corner4Angle = Utils.getAngle(Utils.getVector(to,corner4));

		if(lineAngle >= corner1Angle && lineAngle <= corner2Angle){
			return Utils.getLineIntersection(from,to,corner1,corner2);
		}
		else if(lineAngle >= corner2Angle && lineAngle <= corner3Angle){
			return Utils.getLineIntersection(from,to,corner2,corner3);
		}
		else if(lineAngle >= corner3Angle && lineAngle <= corner4Angle){
			return Utils.getLineIntersection(from,to,corner3,corner4);
		}
		else{
			return Utils.getLineIntersection(from,to,corner4,corner1);
		}
	}

	@Override
	public void draw(ResizableCanvas canvas) {
		GraphicsContext gc = canvas.getGraphicsContext2D();

		if(strokeColor != null){
			gc.setStroke(strokeColor);
			gc.strokeRect(x,y,width,height);
		}
		if(fillColor != null){
			gc.setFill(fillColor);
			gc.fillRect(x,y,width,height);
		}
	}
}
