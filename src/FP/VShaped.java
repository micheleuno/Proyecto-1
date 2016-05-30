package FP;
public final class VShaped
{
	// V1
	public static double V1(float x)
	{
	    return Math.abs( (Math.sqrt(Math.PI)/2) * x);
	}

	public static double V1(double x)
	{
	    return Math.abs((Math.sqrt(Math.PI)/2) * x);
	}

    // V2
    public static double V2(float x)
    {
        return Math.abs( Math.tanh(x) );
    }

    // V3
    public static double V3(float x)
    {
        return Math.abs( x / Math.sqrt(1 + Math.pow(x, 2)));
    }

    // V4
    public static double V4(double newValue)
    {
        return Math.abs( (2/Math.PI) * Math.atan( (Math.PI/2) * newValue));
    }

}