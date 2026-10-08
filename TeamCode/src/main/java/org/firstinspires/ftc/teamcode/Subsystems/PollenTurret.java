package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class PollenTurret {

    private CRServo PollenTurret;
    private AnalogInput PollenEncoder;
    public PollenTurret(HardwareMap hardwareMap){

        PollenTurret = hardwareMap.get(CRServo.class, "Pollen Turret");
        PollenEncoder = hardwareMap.get(AnalogInput.class,"Pollen Encoder");

        TargetAngle = GetCurrentAngle();
        previousTime = System.nanoTime();
    }

    private double TargetAngle = 0;
    private double CurrentAngle = 0;
    private double Error = 0;
    private double ServoPower = 0;

    // PIDF tuning constants
    private double kP = 0.015;
    private double kI = 0.0;
    private double kD = 0.0;
    private double kF = 0.0;

    // PID controller state
    private double integral = 0;
    private double previousError = 0;
    private long previousTime = System.nanoTime();

    public double GetCurrentAngle(){
        double voltage = PollenEncoder.getVoltage();
        CurrentAngle = (voltage/3.3)*360;
        return CurrentAngle;
    }
    public void SetTargetAngle(double angle){

        TargetAngle = ((angle % 360)+ 360) % 360;

        integral = 0;
        previousError = GetError();
        previousTime = System.nanoTime();
    }
    public double GetError(){
        CurrentAngle = GetCurrentAngle();

        Error = TargetAngle-CurrentAngle;

        //convert to shortest path
        if(Error > 180){
            Error -= 360;
        }
        if (Error < -180){
            Error += 360;
        }
        return Error;
    }

    public double GetTargetAngle(){
        return TargetAngle;
    }

    public double GetServoPower(){
        return ServoPower;
    }
    public void Update(){

        long currentTime = System.nanoTime();
        double dt = (currentTime - previousTime) / 1e9;
        previousTime = currentTime;


        double absError = Math.abs(GetError());

        if (dt <= 0 || dt > 0.25) {
            previousError = Error;
            ServoPower = 0;
            PollenTurret.setPower(0);
            return;
        }

        if (absError <= 2.0) {
            integral = 0;
            previousError = Error;
            ServoPower = 0;
            PollenTurret.setPower(0);
            return;
        }

        double P = kP * Error;

        if (absError < 20) {
            integral += Error * dt;
            integral = Math.max(-20, Math.min(20, integral));
        } else {
            integral = 0;
        }

        double I = kI * integral;

        double errorChange = Error - previousError;

        if (errorChange > 180) {
            errorChange -= 360;
        }

        if (errorChange < -180) {
            errorChange += 360;
        }

        double derivative = errorChange / dt;
        double D = kD * derivative;


        double F = kF * Math.signum(Error);

        ServoPower = P + I + D + F;

        ServoPower = Math.max(-1.0, Math.min(1.0, ServoPower));

        PollenTurret.setPower(-ServoPower);

        previousError = Error;
    }

    public void Stop() {
        ServoPower = 0;
        PollenTurret.setPower(0);
    }



    }

