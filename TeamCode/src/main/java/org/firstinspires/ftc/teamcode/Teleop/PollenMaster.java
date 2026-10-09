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

    // Previous D-pad states
    private boolean LastUp = false;
    private boolean LastDown = false;
    private boolean LastLeft = false;
    private boolean LastRight = false;

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

        // Set initial flywheel speed when Play is pressed
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

        // -------------------------
        // Turret angle controls
        // -------------------------

        if (gamepad1.dpad_right && !LastRight) {
            TargetAngle += 5;
        }

        if (gamepad1.dpad_left && !LastLeft) {
            TargetAngle -= 5;
        }

        // -------------------------
        // Save previous D-pad states
        // -------------------------

        LastUp = gamepad1.dpad_up;
        LastDown = gamepad1.dpad_down;
        LastRight = gamepad1.dpad_right;
        LastLeft = gamepad1.dpad_left;

        // -------------------------
        // Set subsystem targets
        // -------------------------

        launcher.SetTargetRPM(TargetRPM);
        turret.SetTargetAngle(TargetAngle);

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