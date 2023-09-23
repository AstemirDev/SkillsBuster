package ru.astemir.skillsbuster.client.misc;

public enum InterpolationType {
    LINEAR((a,b,t)-> (1 - t) * a + t * b),
    QUADRATIC((a, b, t) -> a + (b - a) * t * t),
    CUBIC((a, b, t) -> a + (b - a) * t * t * (3 - 2 * t)),
    SMOOTH((a,b,t)->0.5 * (2.0 * a + (b - a) * t + (2.0 * a - 5.0 * a + 4.0 * b - b) * t * t + (3.0 * a - a - 3.0 * b + b) * t * t * t)),
    STEP((a,b,t)->a + (b-a) * (t < 0.5 ? 0.0 : 1.0));

    private InterpolationFunc function;

    InterpolationType(InterpolationFunc function) {
        this.function = function;
    }
    
    public double interpolate(double a,double b,double t){
        return function.interpolate(a,b,t);
    }

    public double interpolateRot(double a,double b,double t){
        double f1 = a % 360.0;
        double f2 = b % 360.0;
        if (Math.abs(f2 - f1) > 180.0) {
            if (f2 > f1) {
                f1 += 360.0;
            } else {
                f2 += 360.0;
            }
        }
        return function.interpolate(f1,f2,t);
    }

    interface InterpolationFunc{

        double interpolate(double startValue,double endValue,double t);
    }
}
