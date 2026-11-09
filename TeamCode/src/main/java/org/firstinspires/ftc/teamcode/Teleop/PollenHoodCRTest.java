
package org.firstinspires.ftc.teamcode.Teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "Pollen Hood CR Test", group = "Test")
public class PollenHoodCRTest extends LinearOpMode {

    @Override
    public void runOpMode() {

        CRServo Hood1 = hardwareMap.get(
                CRServo.class, "Pollen Hood 1");

        CRServo Hood2 = hardwareMap.get(
                CRServo.class, "Pollen Hood 2");

        // Servos face opposite directions
        Hood1.setDirection(DcMotorSimple.Direction.FORWARD);
        Hood2.setDirection(DcMotorSimple.Direction.REVERSE);

        double Power = 0;

        // Adjustable settings
        double HoldingPower = 0.05;
        double Deadzone = 0.05;

        double Hood1Power = 0;
        double Hood2Power = 0;

        telemetry.addLine("Pollen Hood CR Test Ready");
        telemetry.addLine("Left Stick Y: Hood Movement");
        telemetry.addLine("Centered: Opposing Holding Power");
        telemetry.update();

        waitForStart();

        try {
            while (opModeIsActive()) {

                Power = -gamepad1.left_stick_y;

                if (Math.abs(Power) > Deadzone) {

                    // Both servos move the hood together
                    Hood1Power = Power;
                    Hood2Power = Power;

                } else {

                    // Opposing torque to maintain tension
                    Hood1Power = HoldingPower;
                    Hood2Power = -HoldingPower;

                }

                Hood1.setPower(Hood1Power);
                Hood2.setPower(Hood2Power);

                // Telemetry
                telemetry.addLine("=== POLLEN HOOD CR TEST ===");

                telemetry.addData("Joystick Power", "%.2f", Power);
                telemetry.addData("Servo 1 Power", "%.2f", Hood1Power);
                telemetry.addData("Servo 2 Power", "%.2f", Hood2Power);
                telemetry.addData("Holding Power", "%.2f", HoldingPower);

                telemetry.addData("Mode",
                        Math.abs(Power) > Deadzone
                                ? "MOVING"
                                : "HOLDING");

                telemetry.update();
            }
        }
        finally {
            Hood1.setPower(0);
            Hood2.setPower(0);
        }
    }
}
