package ru.astemir.skillsbuster.client.misc;

import org.astemir.api.math.MathUtils;

import java.util.function.Function;

public enum EasingType {
    NONE((t)->t),
    IN((t)->t*t),
    OUT((t)->t * (2 - t)),
    IN_OUT((t)->{
        if (t < 0.5) {
            return 2 * t * t;
        } else {
            return -1 + (4 - 2 * t) * t;
        }
    }),
    BACK_IN((t) -> t * t * ((1.70158 + 1) * t - 1.70158)),
    BACK_OUT((t) -> {
        double c1 = 1.70158;
        double c3 = c1 + 1;
        return 1 + c3 * Math.pow(t - 1, 3) + c1 * Math.pow(t - 1, 2);
    }),
    BACK_IN_OUT((t) -> {
        double c1 = 1.70158;
        double c2 = c1 * 1.525;
        return t < 0.5 ? (Math.pow(2 * t, 2) * ((c2 + 1) * 2 * t - c2)) / 2 : (Math.pow(2 * t - 2, 2) * ((c2 + 1) * (t * 2 - 2) + c2) + 2) / 2;
    }),
    CIRC_IN((t) -> 1 - Math.sqrt(1 - Math.pow(t, 2))),
    CIRC_OUT((t) -> Math.sqrt(1 - Math.pow(t - 1, 2))),
    CIRC_IN_OUT((t) -> t < 0.5 ? (1 - Math.sqrt(1 - Math.pow(2 * t, 2))) / 2 : (Math.sqrt(1 - Math.pow(-2 * t + 2, 2)) + 1) / 2),
    ELASTIC_IN((t) -> {
        double c4 = (2 * Math.PI) / 3;
        return t == 0 ? 0 : t == 1 ? 1 : -Math.pow(2, 10 * t - 10) * Math.sin((t * 10 - 10.75) * c4);
    }),
    ELASTIC_OUT((t) -> {
        double c4 = (2 * Math.PI) / 3;
        return t == 0 ? 0 : t == 1 ? 1 : Math.pow(2, -10 * t) * Math.sin((t * 10 - 0.75) * c4) + 1;
    }),
    ELASTIC_IN_OUT((t) -> {
        double c5 = (2 * Math.PI) / 4.5;
        return t == 0 ? 0 : t == 1 ? 1 : t < 0.5 ? -(Math.pow(2, 20 * t - 10) * Math.sin((20 * t - 11.125) * c5)) / 2 : (Math.pow(2, -20 * t + 10) * Math.sin((20 * t - 11.125) * c5)) / 2 + 1;
    }),
    SIN_IN((t) -> 1 - Math.cos((t * Math.PI) / 2)),
    SIN_OUT((t) -> Math.sin((t * Math.PI) / 2)),
    SIN_IN_OUT((t) -> (-0.5 * (Math.cos(Math.PI * t) - 1))),
    EXP_IN((t) -> (t == 0) ? 0 : Math.pow(2, 10 * (t - 1))),
    EXP_OUT((t) -> (t == 1) ? 1 : 1 - Math.pow(2, -10 * t)),
    EXP_IN_OUT((t) -> {
        if (t == 0) return 0.0;
        if (t == 1) return 1.0;
        if (t < 0.5) return 0.5 * Math.pow(2, (20 * t) - 10);
        return -0.5 * Math.pow(2, (-20 * t) + 10) + 1;
    }),
    COS_IN((t) -> 1 - Math.cos((t * Math.PI) / 2)),
    COS_OUT((t) -> Math.sin((t * Math.PI) / 2)),
    COS_IN_OUT((t) -> (-0.5 * (Math.cos(Math.PI * t) - 1))),
    BOUNCE_OUT((t) -> {
        if (t < 4 / 11.0) {
            return (121 * t * t) / 16.0;
        } else if (t < 8 / 11.0) {
            return (363 / 40.0 * t * t) - (99 / 10.0 * t) + 17 / 5.0;
        } else if (t < 9 / 10.0) {
            return (4356 / 361.0 * t * t) - (35442 / 1805.0 * t) + 16061 / 1805.0;
        } else {
            return (54 / 5.0 * t * t) - (513 / 25.0 * t) + 268 / 25.0;
        }
    }),
    BOUNCE_IN((t) -> 1 - BOUNCE_OUT.ease(1 - t)),
    BOUNCE_IN_OUT((t) -> t < 0.5 ? 0.5 * BOUNCE_IN.ease(t * 2) : 0.5 * BOUNCE_OUT.ease(t * 2 - 1) + 0.5),
    QUAD_IN((t) -> t * t),
    QUAD_OUT((t) -> -t * (t - 2)),
    QUAD_IN_OUT((t) -> t < 0.5 ? 2 * t * t : -2 * t * t + 4 * t - 1),
    QUART_IN((t) -> t * t * t * t),
    QUART_OUT((t) -> 1 - Math.pow(1 - t, 4)),
    QUART_IN_OUT((t) -> t < 0.5 ? 8 * t * t * t * t : 1 - Math.pow(-2 * t + 2, 4) / 2),
    SINE_IN((t) -> 1 - Math.cos((t * Math.PI) / 2)),
    SINE_OUT((t) -> Math.sin((t * Math.PI) / 2)),
    SINE_IN_OUT((t) -> -0.5 * (Math.cos(Math.PI * t) - 1)),
    CUBIC_IN((t) -> t * t * t),
    CUBIC_OUT((t) -> 1 - Math.pow(1 - t, 3)),
    CUBIC_IN_OUT((t) -> t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2)

    ;

    private Function<Double,Double> function;

    EasingType(Function<Double,Double> function) {
        this.function = function;
    }

    public double ease(double value){
        return function.apply((double) MathUtils.clamp((float) value,-1,1));
    }
}
