package com.thegalaxysoftware.musica.Timely.Models;

import com.thegalaxysoftware.musica.Timely.Models.Numbers.Eight;
import com.thegalaxysoftware.musica.Timely.Models.Numbers.Five;
import com.thegalaxysoftware.musica.Timely.Models.Numbers.Four;
import com.thegalaxysoftware.musica.Timely.Models.Numbers.Nine;
import com.thegalaxysoftware.musica.Timely.Models.Numbers.Null;
import com.thegalaxysoftware.musica.Timely.Models.Numbers.One;
import com.thegalaxysoftware.musica.Timely.Models.Numbers.Seven;
import com.thegalaxysoftware.musica.Timely.Models.Numbers.Six;
import com.thegalaxysoftware.musica.Timely.Models.Numbers.Three;
import com.thegalaxysoftware.musica.Timely.Models.Numbers.Two;
import com.thegalaxysoftware.musica.Timely.Models.Numbers.Zero;

import java.security.InvalidParameterException;

public class NumberUtils {

    public static float[][] getControlPointsFor(int start) {
        switch (start) {
            case (-1):
                return Null.getInstance().getControlPoints();
            case 0:
                return Zero.getInstance().getControlPoints();
            case 1:
                return One.getInstance().getControlPoints();
            case 2:
                return Two.getInstance().getControlPoints();
            case 3:
                return Three.getInstance().getControlPoints();
            case 4:
                return Four.getInstance().getControlPoints();
            case 5:
                return Five.getInstance().getControlPoints();
            case 6:
                return Six.getInstance().getControlPoints();
            case 7:
                return Seven.getInstance().getControlPoints();
            case 8:
                return Eight.getInstance().getControlPoints();
            case 9:
                return Nine.getInstance().getControlPoints();
            default:
                throw new InvalidParameterException("Unsupported number requested");
        }
    }
}
