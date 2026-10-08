package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class PollenLauncher {

    private DcMotorEx Flywheel;

    public PollenLauncher(HardwareMap hardwareMap) {

        Flywheel = hardwareMap.get(DcMotorEx.class, "Pollen Flywheel");

    }
}