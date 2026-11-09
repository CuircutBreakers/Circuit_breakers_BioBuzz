
package org.firstinspires.ftc.teamcode.Teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Subsystems.PollenHood;

@TeleOp(name = "Pollen Hood Test", group = "Test")
public class PollenHoodTest extends LinearOpMode {

    @Override
    public void runOpMode() {

        PollenHood Hood = new PollenHood(hardwareMap);

        double TargetAngle = 0;

        boolean PreviousUp = false;
        boolean PreviousDown = false;

        telemetry.addLine("Pollen Hood Test Ready");
        telemetry.addLine("D-Pad Up: +1 degree");
        telemetry.addLine("D-Pad Down: -1 degree");
        telemetry.update();

        waitForStart();

        if (isStopRequested()) return;

        // Set initial hood angle
        Hood.SetTargetAngle(TargetAngle);

        while (opModeIsActive()) {

            boolean Up = gamepad1.dpad_up;
            boolean Down = gamepad1.dpad_down;

            // Increase angle once per press
            if (Up && !PreviousUp) {
                TargetAngle += 1;
            }

            // Decrease angle once per press
            if (Down && !PreviousDown) {
                TargetAngle -= 1;
            }

            PreviousUp = Up;
            PreviousDown = Down;

            // Command the hood
            Hood.SetTargetAngle(TargetAngle);

            // Read the clamped target from the subsystem
            TargetAngle = Hood.GetTargetAngle();

            // Telemetry
            telemetry.addLine("=== POLLEN HOOD TEST ===");
            telemetry.addData("Target Hood Angle", "%.1f deg", TargetAngle);
            telemetry.addData("Servo Rotation", "%.1f deg",
                    TargetAngle * 1.0);
            telemetry.addData("D-Pad Up", Up);
            telemetry.addData("D-Pad Down", Down);

            telemetry.addLine();
            telemetry.addLine("UP   : +1 degree");
            telemetry.addLine("DOWN : -1 degree");

            telemetry.update();
        }
    }
}
