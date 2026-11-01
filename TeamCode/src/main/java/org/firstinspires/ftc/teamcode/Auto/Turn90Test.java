
package org.firstinspires.ftc.teamcode.Auto;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ftc.Actions;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.TankDrive;

@Autonomous(name = "Turn 90 Test", group = "Test")
public class Turn90Test extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {

        // Initialize Road Runner with starting pose
        TankDrive drive = new TankDrive(
                hardwareMap,
                new Pose2d(0, 0, 0)
        );

        telemetry.addLine("Road Runner 90 Degree Turn Test");
        telemetry.addLine("Ready");
        telemetry.update();

        // Wait for driver to press START
        waitForStart();

        if (isStopRequested()) {
            return;
        }

        // Execute a 90 degree counterclockwise turn
        Actions.runBlocking(
                drive.actionBuilder(new Pose2d(0, 0, 0))
                        .turn(Math.toRadians(90))
                        .build()
        );

        telemetry.addLine("Turn Complete");
        telemetry.update();
    }
}
