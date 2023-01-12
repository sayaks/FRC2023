// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.drive;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.DriveConstants;

public class DriveSubsystem extends SubsystemBase {
    private SwervePod singlePod = new SwervePod(DriveConstants.SINGLE_POD_CONFIG,
            NetworkTableInstance.getDefault().getTable("157/Swerve/SinglePod"));

    public void directSet(final double rollSpeed, final double spinSpeed) {
        singlePod.directSet(rollSpeed, spinSpeed);
    }
}
