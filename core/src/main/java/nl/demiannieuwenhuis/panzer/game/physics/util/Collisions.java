package nl.demiannieuwenhuis.panzer.game.physics.util;

import nl.demiannieuwenhuis.panzer.game.physics.rigidbody.Body;
import nl.demiannieuwenhuis.panzer.game.physics.rigidbody.Circle;
import nl.demiannieuwenhuis.panzer.game.physics.rigidbody.Rect;

public class Collisions {

    public static void correctPosition(Body body1, Body body2, CollisionData collisionData) {
        Vector2D correction = collisionData.normal.scale(0.4 * Math.max(collisionData.penetration - 0.01, (double)0.0F) / ((double)1.0F / body1.mass + (double)1.0F / body2.mass));
        double ivMass1 = (double)1.0F / body1.mass / ((double)1.0F / body1.mass + (double)1.0F / body2.mass);
        body1.setX(body1.getX() + correction.x * ivMass1);
        body1.setY(body1.getY() + correction.y * ivMass1);
        double ivMass2 = (double)1.0F / body2.mass / ((double)1.0F / body1.mass + (double)1.0F / body2.mass);
        body2.setX(body2.getX() - correction.x * ivMass2);
        body2.setY(body2.getY() - correction.y * ivMass2);
    }

    public static CollisionData circleCircle(Circle c1, Circle c2) {
        if (c1.getCenterOfMass().subtract(c2.getCenterOfMass()).length() >= c1.radius + c2.radius) {
            return new CollisionData(false, new Vector2D(0.0F, 0.0F), 0.0F);
        } else {
            Vector2D diff = c1.getCenterOfMass().subtract(c2.getCenterOfMass());
            Vector2D normal = diff.normalized();
            double penetration = c1.radius + c2.radius - diff.length();
            return new CollisionData(true, normal, penetration);
        }
    }

    public static CollisionData rectCircle(Rect r, Circle c) {
        Vector2D rectCenter = r.getCenterOfMass();
        Vector2D circleCenter = c.getCenterOfMass();
        double angle = Math.toRadians(r.getRotation());

        // Bring the circle's center into the rect's local (unrotated) frame
        Vector2D localCircleCenter = rotatePoint(circleCenter, rectCenter, -angle);

        double hw = r.getWidth() / 2.0;
        double hh = r.getHeight() / 2.0;

        double closestX = Math.clamp(localCircleCenter.x, rectCenter.x - hw, rectCenter.x + hw);
        double closestY = Math.clamp(localCircleCenter.y, rectCenter.y - hh, rectCenter.y + hh);
        Vector2D closestPoint = new Vector2D(closestX, closestY);

        Vector2D diff = localCircleCenter.subtract(closestPoint);
        double distSq = diff.x * diff.x + diff.y * diff.y;

        if (distSq >= c.radius * c.radius) {
            return new CollisionData(false, new Vector2D(0.0, 0.0), 0.0);
        }

        double dist = Math.sqrt(distSq);
        Vector2D localNormal;
        double penetration;

        if (dist == 0.0) {
            // Circle center is inside the rect; push out along the axis of least penetration
            double dx1 = localCircleCenter.x - (rectCenter.x - hw);
            double dx2 = (rectCenter.x + hw) - localCircleCenter.x;
            double dy1 = localCircleCenter.y - (rectCenter.y - hh);
            double dy2 = (rectCenter.y + hh) - localCircleCenter.y;
            double minX = Math.min(dx1, dx2);
            double minY = Math.min(dy1, dy2);
            if (minX < minY) {
                localNormal = new Vector2D(dx1 < dx2 ? -1.0 : 1.0, 0.0);
                penetration = minX + c.radius;
            } else {
                localNormal = new Vector2D(0.0, dy1 < dy2 ? -1.0 : 1.0);
                penetration = minY + c.radius;
            }
        } else {
            localNormal = diff.scale(1.0 / dist);
            penetration = c.radius - dist;
        }

        // Rotate the normal back into world space
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        Vector2D worldNormal = new Vector2D(
            localNormal.x * cos - localNormal.y * sin,
            localNormal.x * sin + localNormal.y * cos
        );

        return new CollisionData(true, worldNormal, penetration);
    }

    public static CollisionData rectRect(Rect r1, Rect r2) {
        Vector2D[] corners1 = getCorners(r1);
        Vector2D[] corners2 = getCorners(r2);
        Vector2D[] axes1 = getAxes(corners1);
        Vector2D[] axes2 = getAxes(corners2);

        Vector2D[] axes = new Vector2D[] { axes1[0], axes1[1], axes2[0], axes2[1] };

        double minOverlap = Double.POSITIVE_INFINITY;
        Vector2D smallestAxis = null;

        for (Vector2D axis : axes) {
            double[] proj1 = project(corners1, axis);
            double[] proj2 = project(corners2, axis);
            double overlap = Math.min(proj1[1], proj2[1]) - Math.max(proj1[0], proj2[0]);

            if (overlap <= 0.0) {
                return new CollisionData(false, new Vector2D(0.0, 0.0), 0.0);
            }
            if (overlap < minOverlap) {
                minOverlap = overlap;
                smallestAxis = axis;
            }
        }

        // Orient the normal to point from r2 toward r1 (matches original convention)
        Vector2D centerDiff = r1.getCenterOfMass().subtract(r2.getCenterOfMass());
        if (Vector2D.dot(centerDiff, smallestAxis)  < 0.0) {
            smallestAxis = smallestAxis.scale(-1.0);
        }

        return new CollisionData(true, smallestAxis, minOverlap);
    }

// ---- helpers ----

    private static Vector2D rotatePoint(Vector2D point, Vector2D center, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double dx = point.x - center.x;
        double dy = point.y - center.y;
        return new Vector2D(center.x + dx * cos - dy * sin, center.y + dx * sin + dy * cos);
    }

    private static Vector2D[] getCorners(Rect r) {
        Vector2D center = r.getCenterOfMass();
        double hw = r.getWidth() / 2.0;
        double hh = r.getHeight() / 2.0;
        double cos = Math.cos(Math.toRadians(r.getRotation()));
        double sin = Math.sin(Math.toRadians(r.getRotation()));

        double[] lx = { -hw, hw, hw, -hw };
        double[] ly = { -hh, -hh, hh, hh };

        Vector2D[] worldCorners = new Vector2D[4];
        for (int i = 0; i < 4; i++) {
            double x = lx[i] * cos - ly[i] * sin;
            double y = lx[i] * sin + ly[i] * cos;
            worldCorners[i] = new Vector2D(center.x + x, center.y + y);
        }
        return worldCorners;
    }

    private static Vector2D[] getAxes(Vector2D[] corners) {
        Vector2D[] axes = new Vector2D[2];
        for (int i = 0; i < 2; i++) {
            Vector2D edge = corners[i + 1].subtract(corners[i]);
            Vector2D normal = new Vector2D(-edge.y, edge.x);
            axes[i] = normal.scale(1.0 / normal.length());
        }
        return axes;
    }

    private static double[] project(Vector2D[] corners, Vector2D axis) {
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        for (Vector2D corner : corners) {
            double proj = Vector2D.dot(corner, axis);
            min = Math.min(min, proj);
            max = Math.max(max, proj);
        }
        return new double[] { min, max };
    }
}
