
package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class PollenHood {

    private Servo Hood1;
    private Servo Hood2;

    // Servo specifications
    private final double SERVO_RANGE = 1800.0;

    // Servo degrees per hood degree
    private double GearRatio = 4.0;

    // Hood angle limits
    private double MinAngle = 9;
    private double MaxAngle = 50;

    private double TargetAngle = 0;

    public PollenHood(HardwareMap hardwareMap) {

        Hood1 = hardwareMap.get(Servo.class, "Pollen Hood 1");
        Hood2 = hardwareMap.get(Servo.class, "Pollen Hood 2");

        Hood1.setDirection(Servo.Direction.FORWARD);
        Hood2.setDirection(Servo.Direction.REVERSE);
    }

    public void SetTargetAngle(double angle) {

        TargetAngle = Math.max(
                MinAngle,
                Math.min(MaxAngle, angle)
        );

        double ServoAngle = TargetAngle * GearRatio;

        double Position = Math.max(
                0,
                Math.min(1, ServoAngle / SERVO_RANGE)
        );

        Hood1.setPosition(Position);
        Hood2.setPosition(Position);
    }

    public double GetTargetAngle() {
        return TargetAngle;
    }
}
