package tsafe.server.calculation;

import tsafe.common_datastructures.LatLonBounds;
import tsafe.common_datastructures.Point2D;
import tsafe.common_datastructures.PointXY;

/**
 * 
 * 
 * Contains method to compute more sophisticated calculation such as converting
 * to certain coordinates.
 */
public class Calculator {

	/**
	 * Conversion constants
	 */
	private static final double EARTH_RADIUS = 6371000.0;

	private static final double RADIANS_PER_DEGREE = Math.PI / 180.0;

	private static final double METERS_PER_LAT = EARTH_RADIUS
			* RADIANS_PER_DEGREE;

	private static final double METERS_PER_LON_AT_EQUATOR = METERS_PER_LAT;

	/**
	 * Return the meters per degree longitude at the given degree latitude
	 */
	private double metersPerLonAt(double lat) {
		return METERS_PER_LON_AT_EQUATOR * Math.cos(lat * RADIANS_PER_DEGREE);
	}

	/**
	 * Implementation of Calculator method
	 */
	public PointXY toXY(double lat, double lon, LatLonBounds bounds) {
		return new PointXY((lon - bounds.minLon) * metersPerLonAt(lat),
				(lat - bounds.minLat) * METERS_PER_LAT);
	}

	/**
	 * Implementation of Calculator method
	 */
	public Point2D toLL(double x, double y, LatLonBounds bounds) {
		double lat = (y / METERS_PER_LAT) + bounds.minLat;
		double lon = (x / metersPerLonAt(lat)) + bounds.minLon;
		return new Point2D(lat, lon);
	}
	
	public Point2D toLL(double x, double y) {
		return toLL(x, y, new LatLonBounds(0,0,0,0));
	}

	//  NON-ABSTRACT CONVERSION METHODS
	public PointXY toXY(Point2D p, LatLonBounds bounds) {
		return toXY(p.getLatitude(), p.getLongitude(), bounds);
	}
	
	public PointXY toXY(Point2D p) {
		return toXY(p, new LatLonBounds(0,0,0,0));
	}

	public Point2D toLL(PointXY p, LatLonBounds bounds) {
		return toLL(p.getX(), p.getY(), bounds);
	}

	// DISTANCES BETWEEN LAT/LON COORDINATES
	public double distanceLL(double lat1, double lon1, double lat2,
			double lon2, LatLonBounds bounds) {
		return distanceXY(toXY(lat1, lon1, bounds), toXY(lat2, lon2, bounds));
	}
	

	public double distanceLL(double lat1, double lon1, Point2D p2,
			LatLonBounds bounds) {
		return distanceLL(lat1, lon1, p2.getLatitude(), p2.getLongitude(),
				bounds);
	}

	public double distanceLL(Point2D p1, Point2D p2, LatLonBounds bounds) {
		return distanceLL(p1.getLatitude(), p1.getLongitude(),
				p2.getLatitude(), p2.getLongitude(), bounds);
	}
	
	public double distanceLL(Point2D p1, Point2D p2) {
		return distanceLL(p1, p2, new LatLonBounds(0,0,0,0));
	}

	// DISTANCES BETWEEN X,Y COORDINATES
	public double distanceXY(double x1, double y1, double x2, double y2) {
		return java.awt.geom.Point2D.distance(x1, y1, x2, y2);
	}

	public double distanceXY(double x1, double y1, PointXY p2) {
		return distanceXY(x1, y1, p2.getX(), p2.getY());
	}

	public double distanceXY(PointXY p1, PointXY p2) {
		return distanceXY(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}

	// ANGLE BETWEEN LAT/LON COORDINATES
	public double angleLL(double lat1, double lon1, double lat2, double lon2,
			LatLonBounds bounds) {
		return angleXY(toXY(lat1, lon1, bounds), toXY(lat2, lon2, bounds));
	}
	
	public double angleLL(double lat1, double lon1, double lat2, double lon2) {
		return angleLL(lat1, lon1, lat2, lon2, new LatLonBounds(0,0,0,0));
	}

	public double angleLL(double lat1, double lon1, Point2D p2,
			LatLonBounds bounds) {
		return angleLL(lat1, lon1, p2.getLatitude(), p2.getLongitude(), bounds);
	}

	public double angleLL(Point2D p1, Point2D p2, LatLonBounds bounds) {
		return angleLL(p1.getLatitude(), p1.getLongitude(), p2.getLatitude(),
				p2.getLongitude(), bounds);
	}

	// ANGLE BETWEEN X,Y COORDINATES
	/* Returns an angle between 0 and 2PI */
	public double angleXY(double x1, double y1, double x2, double y2) {
		double x = x2 - x1;
		double y = y2 - y1;

		if (x == 0 && y > 0) {
			return Math.PI / 2;
		} else if (x == 0 && y <= 0) {
			return Math.PI / -2;
		} else {
			double angle = Math.atan(y / x);

			if (x < 0) {
				angle += Math.PI;
			} else if (x > 0 && y < 0) {
				angle += 2 * Math.PI;
			}

			return angle;
		}
	}

	public double angleXY(double x1, double y1, PointXY p2) {
		return angleXY(x1, y1, p2.getX(), p2.getY());
	}

	public double angleXY(PointXY p1, PointXY p2) {
		return angleXY(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}

}