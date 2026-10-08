package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class PollenLauncher {

    private DcMotorEx Flywheel;

    public PollenLauncher(HardwareMap hardwareMap) {

        Flywheel = hardwareMap.get(DcMotorEx.class, "Pollen Flywheel");
        Flywheel.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
    }

    private static final double TICKS_PER_REV = 28.0;
    private double GearRatio = 1.0;
    private double TargetRPM = 0;
    private double Error = 0;
    private double kP = 0.0001;
    private double kF = 1.0 / 6000.0;
    private double kI = 0.0;
    private double Integral = 0;
    private long PreviousTime = System.nanoTime();
    private double kD = 0.0;
    private double PreviousRPM = 0;
    private double MotorPower = 0;
    public double GetCurrentRPM() {
        return (Flywheel.getVelocity() / TICKS_PER_REV) * 60.0 / GearRatio;
    }
    public void SetTargetRPM(double rpm) {
        rpm = Math.max(0, rpm);

        if (rpm != TargetRPM) {
            TargetRPM = rpm;
            Integral = 0;
            PreviousRPM = GetCurrentRPM();
            PreviousTime = System.nanoTime();
        }
    }
    public double GetTargetRPM() {
        return TargetRPM;
    }

    public double GetError() {
        Error = TargetRPM - GetCurrentRPM();
        return Error;
    }
    public void Update() {
        long CurrentTime = System.nanoTime();
        double dt = (CurrentTime - PreviousTime) / 1e9;
        PreviousTime = CurrentTime;

        double CurrentRPM = GetCurrentRPM();
        Error = TargetRPM - CurrentRPM;

        if (TargetRPM == 0) {
            MotorPower = 0;
            Integral = 0;
            PreviousRPM = CurrentRPM;
            Flywheel.setPower(0);
            return;
        }

        double Derivative = 0;

        if (dt > 0 && dt < 0.5) {
            Derivative = -(CurrentRPM - PreviousRPM) / dt;
        }

        PreviousRPM = CurrentRPM;
        if (dt > 0 && dt < 0.5) {
            Integral += Error * dt;
            Integral = Math.max(-1000, Math.min(1000, Integral));
        }

        MotorPower = (kP * Error)
                + (kI * Integral)
                + (kD * Derivative)
                + (kF * TargetRPM * GearRatio);

        MotorPower = Math.max(0, Math.min(1, MotorPower));

        Flywheel.setPower(MotorPower);
    }

    public void Stop() {
        TargetRPM = 0;
        Integral = 0;
        MotorPower = 0;
        PreviousRPM = GetCurrentRPM();
        PreviousTime = System.nanoTime();
        Flywheel.setPower(0);
    }

}