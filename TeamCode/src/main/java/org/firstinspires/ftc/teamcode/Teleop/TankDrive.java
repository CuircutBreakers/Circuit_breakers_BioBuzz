
package org.firstinspires.ftc.teamcode.Teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "Tank Drive", group = "TeleOp")
public class TankDrive extends OpMode {

    private DcMotorEx FrontLeft;
    private DcMotorEx FrontRight;
    private DcMotorEx BackLeft;
    private DcMotorEx BackRight;

    // Measured acceleration and braking times
    private static final double ACCEL_TIME = 0.50;
    private static final double BRAKE_TIME = 0.25;

    private static final double ACCEL_RATE = 1.0 / ACCEL_TIME;
    private static final double BRAKE_RATE = 1.0 / BRAKE_TIME;

    // Controller settings
    private static final double DEADZONE = 0.05;
    private static final double SPEED_TOLERANCE = 0.03;
    private static final double BRAKE_TRIGGER = 0.15;

    // Estimated speeds
    private double LeftSpeed = 0;
    private double RightSpeed = 0;

    // Previous joystick targets
    private double LastLeftTarget = 0;
    private double LastRightTarget = 0;

    // Active braking states
    private boolean LeftBraking = false;
    private boolean RightBraking = false;

    private long LastTime = 0;

    @Override
    public void init() {

        FrontLeft = hardwareMap.get(DcMotorEx.class, "FrontLeft");
        FrontRight = hardwareMap.get(DcMotorEx.class, "FrontRight");
        BackLeft = hardwareMap.get(DcMotorEx.class, "BackLeft");
        BackRight = hardwareMap.get(DcMotorEx.class, "BackRight");

        // Your existing motor directions
        FrontLeft.setDirection(DcMotor.Direction.REVERSE);
        FrontRight.setDirection(DcMotor.Direction.FORWARD);
        BackLeft.setDirection(DcMotor.Direction.REVERSE);
        BackRight.setDirection(DcMotor.Direction.FORWARD);

        FrontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FrontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BackLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BackRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    @Override
    public void start() {

        LeftSpeed = 0;
        RightSpeed = 0;

        LastLeftTarget = 0;
        LastRightTarget = 0;

        LeftBraking = false;
        RightBraking = false;

        LastTime = System.nanoTime();
    }

    @Override
    public void loop() {

        long CurrentTime = System.nanoTime();

        double dt = (CurrentTime - LastTime) / 1e9;
        LastTime = CurrentTime;

        dt = Math.max(0, Math.min(dt, 0.05));

        // Joystick controls
        double y = -gamepad1.left_stick_y;
        double rx = gamepad1.right_stick_x;

        if (Math.abs(y) < DEADZONE) y = 0;
        if (Math.abs(rx) < DEADZONE) rx = 0;

        // Arcade drive calculations
        double denominator = Math.max(
                Math.abs(y) + Math.abs(rx),
                1.0
        );

        double LeftTarget = (y + rx) / denominator;
        double RightTarget = (y - rx) / denominator;

        // Detect significant speed reductions
        boolean LeftReduction =
                Math.abs(LeftTarget) <
                        Math.abs(LastLeftTarget) - BRAKE_TRIGGER;

        boolean RightReduction =
                Math.abs(RightTarget) <
                        Math.abs(LastRightTarget) - BRAKE_TRIGGER;

        // Detect requested reversals
        boolean LeftReversal =
                Math.abs(LeftTarget) > DEADZONE
                        && Math.abs(LeftSpeed) > SPEED_TOLERANCE
                        && Math.signum(LeftTarget) != Math.signum(LeftSpeed);

        boolean RightReversal =
                Math.abs(RightTarget) > DEADZONE
                        && Math.abs(RightSpeed) > SPEED_TOLERANCE
                        && Math.signum(RightTarget) != Math.signum(RightSpeed);

        // Start braking when appropriate
        if ((LeftReduction || LeftReversal)
                && Math.abs(LeftSpeed) >
                Math.abs(LeftTarget) + SPEED_TOLERANCE) {

            LeftBraking = true;
        }

        if ((RightReduction || RightReversal)
                && Math.abs(RightSpeed) >
                Math.abs(RightTarget) + SPEED_TOLERANCE) {

            RightBraking = true;
        }

        // Cancel braking when accelerating again
        if (Math.abs(LeftTarget) >
                Math.abs(LastLeftTarget) + BRAKE_TRIGGER) {

            LeftBraking = false;
        }

        if (Math.abs(RightTarget) >
                Math.abs(LastRightTarget) + BRAKE_TRIGGER) {

            RightBraking = false;
        }

        // Stop braking when target speed is reached
        if (Math.abs(LeftSpeed) <=
                Math.abs(LeftTarget) + SPEED_TOLERANCE) {

            LeftBraking = false;
        }

        if (Math.abs(RightSpeed) <=
                Math.abs(RightTarget) + SPEED_TOLERANCE) {

            RightBraking = false;
        }

        // Calculate motor outputs
        double LeftOutput;
        double RightOutput;

        if (LeftBraking) {
            LeftOutput = -Math.signum(LeftSpeed);
        } else {
            LeftOutput = LeftTarget;
        }

        if (RightBraking) {
            RightOutput = -Math.signum(RightSpeed);
        } else {
            RightOutput = RightTarget;
        }

        // Update estimated speeds
        LeftSpeed = UpdateSpeed(
                LeftSpeed, LeftOutput, dt
        );

        RightSpeed = UpdateSpeed(
                RightSpeed, RightOutput, dt
        );

        // Apply motor powers
        // Preserving your existing motor assignments
        FrontLeft.setPower(LeftOutput);
        BackLeft.setPower(LeftOutput);

        FrontRight.setPower(RightOutput);
        BackRight.setPower(RightOutput);

        // Save joystick targets
        LastLeftTarget = LeftTarget;
        LastRightTarget = RightTarget;

        // Telemetry
        telemetry.addData("Forward/Reverse", y);
        telemetry.addData("Turning", rx);

        telemetry.addData("Left Target", LeftTarget);
        telemetry.addData("Right Target", RightTarget);

        telemetry.addData("Left Estimated Speed", LeftSpeed);
        telemetry.addData("Right Estimated Speed", RightSpeed);

        telemetry.addData("Left Output", LeftOutput);
        telemetry.addData("Right Output", RightOutput);

        telemetry.addData("Left Braking", LeftBraking);
        telemetry.addData("Right Braking", RightBraking);

        telemetry.update();
    }

    private double UpdateSpeed(
            double Speed,
            double Power,
            double dt) {

        double Rate;

        // Opposite power means active braking
        boolean OppositePower =
                Math.abs(Speed) > SPEED_TOLERANCE
                        && Math.abs(Power) > 0.01
                        && Math.signum(Speed) != Math.signum(Power);

        if (OppositePower) {
            Rate = BRAKE_RATE;
        } else {
            Rate = ACCEL_RATE;
        }

        double NewSpeed = Speed + Power * Rate * dt;

        // Prevent estimated speed from reversing
        // during the braking calculation
        if (OppositePower
                && Math.signum(NewSpeed) != Math.signum(Speed)) {

            NewSpeed = 0;
        }

        // Estimate passive braking at zero power
        if (Math.abs(Power) < 0.01) {

            double PassiveDecel = BRAKE_RATE * 0.5 * dt;

            if (Math.abs(NewSpeed) <= PassiveDecel) {
                NewSpeed = 0;
            } else {
                NewSpeed -= Math.signum(NewSpeed) * PassiveDecel;
            }
        }

        return Math.max(-1.0, Math.min(1.0, NewSpeed));
    }

    @Override
    public void stop() {

        FrontLeft.setPower(0);
        FrontRight.setPower(0);
        BackLeft.setPower(0);
        BackRight.setPower(0);
    }
}
