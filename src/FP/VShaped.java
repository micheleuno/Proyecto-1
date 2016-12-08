package FP;



import org.apache.commons.math3.special.Erf;

/**
 * Created by Salazar on 5/18/16.
 */
public final class VShaped
{
    // V1
    public static double V1(float x)
    {
        return Math.abs(Erf.erf( (Math.sqrt(Math.PI)/2) * x));
    }

    public static double V1(double x)
    {
        return Math.abs(Erf.erf( (Math.sqrt(Math.PI)/2) * x));
    }

    // V2
    public static double V2(float x)
    {
        return Math.abs( Math.tanh(x) );
    }

    public static double V2(double x)
    {
        return Math.abs( Math.tanh(x) );
    }

    // V3
    public static double V3(float x)
    {
        return Math.abs( x / Math.sqrt(1 + Math.pow(x, 2)));
    }

    public static double V3(double x)
    {
        return Math.abs( x / Math.sqrt(1 + Math.pow(x, 2)));
    }

    // V4
    public static double V4(float x)
    {
        return Math.abs( (2/Math.PI) * Math.atan( (Math.PI/2) * x));
    }

    public static double V4(double x)
    {
        return Math.abs( (2/Math.PI) * Math.atan( (Math.PI/2) * x));
    }

    public static void main(String[] args)
    {
        System.out.println("V1(-1): " + V1(-1));
        System.out.println("V1(0): " + V1(0));
        System.out.println("V1(1): " + V1(1));
        System.out.println("V1(-0.999999): " + V1(-0.999999));
        System.out.println("V1(0.999999): " + V1(0.999999));
        System.out.println("V1(-0.111111): " + V1(-0.111111));
        System.out.println("V1(0.111111): " + V1(0.111111));
        System.out.println("V1(-1.6939411510425213): " + V1(-1.6939411510425213));
        System.out.println("V1(1.6939411510425213): " + V1(1.6939411510425213));
        System.out.println("V1(30): " + V1(30));
        System.out.println("V1(300): " + V1(300));

        System.out.println("---------------");

        System.out.println("V4(-1): " + V4(-1));
        System.out.println("V4(0): " + V4(0));
        System.out.println("V4(1): " + V4(1));
        System.out.println("V4(-0.999999): " + V4(-0.999999));
        System.out.println("V4(0.999999): " + V4(0.999999));
        System.out.println("V4(-0.111111): " + V4(-0.111111));
        System.out.println("V4(0.111111): " + V4(0.111111));
        System.out.println("V4(-1.6939411510425213): " + V4(-1.6939411510425213));
        System.out.println("V4(1.6939411510425213): " + V4(1.6939411510425213));

    }

}
