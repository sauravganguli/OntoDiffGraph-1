package application.graph;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.transform.Affine;


public class ResizableCanvas extends Canvas{

	public ResizableCanvas(){
		super();
	}

	public void clearCanvas(){
		final Point2D start = screenCoordToCanvas(0.0,0.0);
		final Point2D end = screenDistanceToCanvas(getWidth(),getHeight());
		getGraphicsContext2D().clearRect(start.getX(),start.getY(),end.getX(),end.getY());
	}

	public Point2D screenCoordToCanvas(double x,double y){
		try{
			return getGraphicsContext2D().getTransform().inverseTransform(x,y);
		}
		catch(Exception e){
			return Point2D.ZERO;
		}
	}

	public Point2D screenDistanceToCanvas(double x,double y){
		Affine aff = getGraphicsContext2D().getTransform();
		if(aff.getMxx() == 0 || aff.getMyy() == 0){
			return Point2D.ZERO;
		}
		else{
			return new Point2D(x / aff.getMxx(),y / aff.getMyy());
		}
	}

	public Rectangle2D getVisibleArea(){
		Point2D start = screenCoordToCanvas(0.0,0.0);
		Point2D size = screenDistanceToCanvas(getWidth(),getHeight());
		return new Rectangle2D(start.getX(),start.getY(),size.getX(),size.getY());
	}

	public void centerTo(double x, double y){
		Affine currentAffine = getGraphicsContext2D().getTransform();
		Point2D position = screenDistanceToCanvas(getWidth()/2.0,getHeight()/2.0).subtract(x,y);
		currentAffine.setTx(position.getX()*currentAffine.getMxx());
		currentAffine.setTy(position.getY()*currentAffine.getMyy());
		getGraphicsContext2D().setTransform(currentAffine);
	}

	public Point2D getCenter(){
		return screenCoordToCanvas(getWidth()/2.0,getHeight()/2.0);
	}

	@Override public boolean isResizable(){
		return true;
	}

	@Override public double prefWidth(double height){
		return getWidth();
	}

	@Override public double prefHeight(double width){
		return getHeight();
	}
}
