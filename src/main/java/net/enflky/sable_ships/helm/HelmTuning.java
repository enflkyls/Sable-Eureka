package net.enflky.sable_ships.helm;

import net.enflky.sable_ships.SableShips;
import net.minecraft.nbt.CompoundTag;

//Speed tuning
public final class HelmTuning {

    public static final double DEFAULT_KP = 300.0;
    public static final double DEFAULT_KD = 12.0;
    public static final double DEFAULT_KI = 3.5;
    public static final double DEFAULT_THRUST = 10.0;
    public static final double DEFAULT_TURN = 30.0;

    public double kp = DEFAULT_KP;
    public double kd = DEFAULT_KD;
    public double ki = DEFAULT_KI;
    public double thrustForce = DEFAULT_THRUST;
    public double turnForce = DEFAULT_TURN;
    public boolean debug = SableShips.DEBUG;

    public void save(CompoundTag tag) {
        tag.putDouble("kp", kp);
        tag.putDouble("kd", kd);
        tag.putDouble("ki", ki);
        tag.putDouble("thrustForce", thrustForce);
        tag.putDouble("turnForce", turnForce);
    }

    public void load(CompoundTag tag) {
        if (tag.contains("kp")) kp = tag.getDouble("kp");
        if (tag.contains("kd")) kd = tag.getDouble("kd");
        if (tag.contains("ki")) ki = tag.getDouble("ki");
        if (tag.contains("thrustForce")) thrustForce = tag.getDouble("thrustForce");
        if (tag.contains("turnForce")) turnForce = tag.getDouble("turnForce");
    }


     /*Clamps client provided values to bug out server-side limits. Its cookeed right now aaaand i should get out the compound based speeds anytime
    *public void applyFromClient(double thrust, double turn, double newKp, double newKd, double newKi) {
     *   this.thrustForce = clamp(thrust, 0, 500);
    *    this.turnForce = clamp(turn, 0, 500);
   *     this.kp = clamp(newKp, 0, 2000);
  *      this.kd = clamp(newKd, 0, 200);
 *       this.ki = clamp(newKi, 0, 50);
   } //Nice shittiy commenting fuck that make it starry
   */


    public int getThrustForceScaled() {
        return (int) (thrustForce * 10);
    }

    public void setThrustForceScaled(int value) {
        thrustForce = value / 10.0;
    }

    public int getTurnForceScaled() {
        return (int) (turnForce * 10);
    }

    public void setTurnForceScaled(int value) {
        turnForce = value / 10.0;
    }

    public int getKpScaled() {
        return (int) (kp * 10);
    }

    public void setKpScaled(int value) {
        kp = value / 10.0;
    }

    public int getKdScaled() {
        return (int) (kd * 10);
    }

    public void setKdScaled(int value) {
        kd = value / 10.0;
    }

    public int getKiScaled() {
        return (int) (ki * 100);
    }

    public void setKiScaled(int value) {
        ki = value / 100.0;
    }
}
