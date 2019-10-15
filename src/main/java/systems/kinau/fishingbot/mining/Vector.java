/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/11
 */

package systems.kinau.fishingbot.mining;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class Vector {

    @Getter @Setter private double xV;
    @Getter @Setter private double yV;
    @Getter @Setter private double zV;

    public void addX(double xAdd) {
        xV += xAdd;
    }

    public void addY(double yAdd) {
        yV += yAdd;
    }

    public void addZ(double zAdd) {
        zV += zAdd;
    }

    @Override
    public String toString() {
        return "x:" + xV + ", y:" + yV + ", z:" + zV;
    }
}
