
package org.firstinspires.ftc.teamcode.Teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Subsystems.PollenLauncher;
import org.firstinspires.ftc.teamcode.Subsystems.PollenHood;

@TeleOp(name = "Pollen Master", group = "TeleOp")
public class PollenMaster extends LinearOpMode {

    @Override
    public void runOpMode() {

        PollenLauncher Launcher = new PollenLauncher(hardwareMap);
        PollenHood Hood = new PollenHood(hardwareMap);

        // Starting settings
        double TargetRPM = 2500;
        double TargetAngle = 30;

        boolean LauncherEnabled = false;

        // Previous button states
        boolean PreviousUp = false;
        boolean PreviousDown = false;
        boolean PreviousLeft = false;
        boolean PreviousRight = false;
        boolean PreviousA = false;

        telemetry.addLine("Pollen Master Ready");
        telemetry.addLine("D-Pad Up/Down: RPM +/- 50");
        telemetry.addLine("D-Pad Left/Right: Hood +/- 1 degree");
        telemetry.addLine("A: Toggle Launcher");
        telemetry.addLine("B: Stop Launcher");
        telemetry.addLine("X: Hood Minimum (9 deg)");
        telemetry.addLine("Y: Hood Maximum (50 deg)");
        telemetry.update();

        waitForStart();

        if (isStopRequested()) return;

        Hood.SetTargetAngle(TargetAngle);

        try {
            while (opModeIsActive()) {

                // Read buttons
                boolean Up = gamepad1.dpad_up;
                boolean Down = gamepad1.dpad_down;
                boolean Left = gamepad1.dpad_left;
                boolean Right = gamepad1.dpad_right;
                boolean A = gamepad1.a;

                // =========================
                // LAUNCHER RPM ADJUSTMENT
                // =========================

                if (Up && !PreviousUp) {
                    TargetRPM += 50;
                }

                if (Down && !PreviousDown) {
                    TargetRPM -= 50;
                }

                TargetRPM = Math.max(0,
                        Math.min(3000, TargetRPM));

                // =========================
                // HOOD ANGLE ADJUSTMENT
                // =========================

                if (Right && !PreviousRight) {
                    TargetAngle += 1;
                }

                if (Left && !PreviousLeft) {
                    TargetAngle -= 1;
                }

                // Hood preset positions
                if (gamepad1.x) {
                    TargetAngle = 9;
                }

                if (gamepad1.y) {
                    TargetAngle = 50;
                }

                // Hood subsystem handles its own limits
                Hood.SetTargetAngle(TargetAngle);
                TargetAngle = Hood.GetTargetAngle();

                // =========================
                // LAUNCHER ON / OFF
                // =========================

                if (A && !PreviousA) {
                    LauncherEnabled = !LauncherEnabled;
                }

                if (gamepad1.b) {
                    LauncherEnabled = false;
                }

                if (LauncherEnabled) {
                    Launcher.SetTargetRPM(TargetRPM);
                } else {
                    Launcher.SetTargetRPM(0);
                }

                // Update launcher PIDF
                Launcher.Update();

                // =========================
                // SAVE BUTTON STATES
                // =========================

                PreviousUp = Up;
                PreviousDown = Down;
                PreviousLeft = Left;
                PreviousRight = Right;
                PreviousA = A;

                // =========================
                // TELEMETRY
                // =========================

                telemetry.addLine("=== POLLEN MASTER ===");

                telemetry.addLine();
                telemetry.addData("Launcher",
                        LauncherEnabled ? "ON" : "OFF");

                telemetry.addData("Selected RPM",
                        "%.0f", TargetRPM);

                telemetry.addData("Target RPM",
                        "%.0f", Launcher.GetTargetRPM());

                telemetry.addData("Current RPM",
                        "%.0f", Launcher.GetCurrentRPM());

                telemetry.addData("RPM Error",
                        "%.0f", Launcher.GetError());

                telemetry.addLine();
                telemetry.addData("Hood Target Angle",
                        "%.1f deg", Hood.GetTargetAngle());

                telemetry.addLine();
                telemetry.addLine("D-Pad UP/DOWN: RPM +/- 50");
                telemetry.addLine("D-Pad LEFT/RIGHT: Hood +/- 1");
                telemetry.addLine("A: Toggle Launcher");
                telemetry.addLine("B: Stop Launcher");
                telemetry.addLine("X: Hood Minimum (9 deg)");
                telemetry.addLine("Y: Hood Maximum (50 deg)");

                telemetry.update();
            }
        }
        finally {
            Launcher.Stop();
        }
    }
}
