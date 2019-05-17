package raz.sleeptimer;

import android.graphics.Point;

public class CircleFunctions
{
    public static Point getPointOnCircle(int xCenter, int yCenter, float angle, float radius)
    {
        double rads = Math.toRadians(angle - 90);

        int xPos = Math.round((float) (xCenter + Math.cos(rads) * radius));
        int yPos = Math.round((float) (yCenter + Math.sin(rads) * radius));

        return new Point(xPos, yPos);
    }

    public static Point angleToPoints(int xCenter, int yCenter, int angle, int radius)
    {
        double rads = Math.toRadians(angle);

        int x = Math.round((float) (xCenter + Math.cos(rads) * radius));

        int y = Math.round((float) (yCenter + Math.sin(rads) * radius));

       return new Point(x, y);
    }

    public static int pointsToAngle(int xCenter, int yCenter, int xTouch, int yTouch)
    {
        int angle =  Math.round((float) Math.toDegrees(Math.atan2(
                ((yTouch - yCenter) / Math.sqrt(Math.pow(xTouch - xCenter, 2) + (Math.pow(yTouch - yCenter, 2)))),
                ((xTouch - xCenter) / Math.sqrt(Math.pow(xTouch - xCenter, 2) + (Math.pow(yTouch - yCenter, 2)))))));

        return angle;
    }

    public static int angleToClock(int angle)
    {
        if (angle > -90) return (angle / 6 + 15);
        return (angle / 6 + 75);
    }

    public static int clockToAngle(int clock)
    {
        if (clock < 45) return (clock -15) * 6;
        return (clock - 75) * 6;
    }
}
