package raz.sleeptimer;

import android.graphics.Point;

public class CircleFunctions
{
    public static Point getPointOnCircle(int xCenter, int yCenter, float degrees, float radius)
    {
        double rads = Math.toRadians(degrees - 90);

        int xPos = Math.round((float) (xCenter + Math.cos(rads) * radius));
        int yPos = Math.round((float) (yCenter + Math.sin(rads) * radius));

        return new Point(xPos, yPos);
    }

//    public double getAngle(int xCenter, int yCenter)
//    {
//        return Math.toDegrees(Math.atan2(
//                ((slider.getY() - yCenter) / Math.sqrt(Math.pow(slider.getX() - xCenter, 2) + (Math.pow(slider.getY() - yCenter, 2)))),
//                ((slider.getX() - xCenter) / Math.sqrt(Math.pow(slider.getX() - xCenter, 2) + (Math.pow(slider.getY() - yCenter, 2))))));
//
//        return 0;
//    }
}
