// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.drive;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.DriveConstants;

public class DriveSubsystem extends SubsystemBase {
    public SwervePod singlePod = new SwervePod(DriveConstants.POD_CONFIGS[0],
            NetworkTableInstance.getDefault().getTable("157/Swerve/SinglePod"));

    public void set(final SwerveModuleState state) {
        singlePod.set(state);
    }

    public void stop() {
        singlePod.stop();
    }

    public void directSet(final double rollSpeed, final double spinSpeed) {
        singlePod.directSet(rollSpeed, spinSpeed);
    }

    public Command goToAngle(final double angle) {
        final var state = new SwerveModuleState(0.0, Rotation2d.fromDegrees(angle));

        return run(() -> singlePod.set(state)).until(() -> singlePod.isSpinCloseTo(angle));
    }
}
