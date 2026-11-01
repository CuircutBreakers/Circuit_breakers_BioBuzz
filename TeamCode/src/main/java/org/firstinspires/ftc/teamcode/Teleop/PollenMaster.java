package org.firstinspires.ftc.teamcode.Teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Subsystems.PollenTurret;
import org.firstinspires.ftc.teamcode.Subsystems.PollenLauncher;

@TeleOp(name = "Pollen Master", group = "TeleOp")
public class PollenMaster extends OpMode {

    private PollenTurret turret;
    private PollenLauncher launcher;

    private double TargetRPM = 3000;
    private double TargetAngle = 0;

    private boolean LastUp = false;
    private boolean LastDown = false;

    private static final double JOYSTICK_DEADZONE = 0.15;

    @Override
    public void init() {

        turret = new PollenTurret(hardwareMap);
        launcher = new PollenLauncher(hardwareMap);

        // Start turret at its current position
        TargetAngle = turret.GetCurrentAngle();
        turret.SetTargetAngle(TargetAngle);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void start() {

        launcher.SetTargetRPM(TargetRPM);
    }

    @Override
    public void loop() {

        // -------------------------
        // Flywheel RPM controls
        // -------------------------

        if (gamepad1.dpad_up && !LastUp) {
            TargetRPM += 50;
        }

        if (gamepad1.dpad_down && !LastDown) {
            TargetRPM = Math.max(0, TargetRPM - 50);
        }

        LastUp = gamepad1.dpad_up;
        LastDown = gamepad1.dpad_down;

        // -------------------------
        // Turret joystick controls
        // -------------------------


        double x = gamepad1.left_stick_x;
        double y = -gamepad1.left_stick_y;

        double magnitude = Math.hypot(x, y);

        if (magnitude > JOYSTICK_DEADZONE) {

            TargetAngle = Math.toDegrees(Math.atan2(y, x));

            TargetAngle = (TargetAngle + 360) % 360;

            turret.SetTargetAngle(TargetAngle);
        }

        // -------------------------
        // Set launcher target
        // -------------------------

        launcher.SetTargetRPM(TargetRPM);

        // -------------------------
        // Update subsystems
        // -------------------------

        turret.Update();
        launcher.Update();

        // -------------------------
        // Telemetry
        // -------------------------

        telemetry.addData("Target RPM", launcher.GetTargetRPM());
        telemetry.addData("Current RPM", launcher.GetCurrentRPM());
        telemetry.addData("RPM Error", launcher.GetError());

        telemetry.addData("Target Angle", turret.GetTargetAngle());
        telemetry.addData("Current Angle", turret.GetCurrentAngle());
        telemetry.addData("Angle Error", turret.GetError());

        telemetry.update();
    }

    @Override
    public void stop() {

        launcher.Stop();
        turret.Stop();
    }
}