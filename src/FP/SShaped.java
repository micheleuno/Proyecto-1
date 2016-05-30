package FP;

public final class SShaped
{
    // S1
    public static double S1(float x)
    {
        return 1/(1 + Math.pow(Math.E, (-2 * x)));
    }

    public static double S2(float x)
    {
        return 1/(1 + Math.pow(Math.E, (-1 * x)));
    }

    public static double S3(float x)
    {
        return 1/(1 + Math.pow(Math.E, ((-1 * x)/2) ));
    }

    public static double S4(float x)
    {
        return 1/(1 + Math.pow(Math.E, ((-1 * x)/3) ));
    }

}