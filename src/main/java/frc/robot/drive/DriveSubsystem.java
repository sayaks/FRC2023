// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.drive;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.DriveConstants;

public class DriveSubsystem extends SubsystemBase {

    private final SwerveDriveKinematics kinematics = new SwerveDriveKinematics(
            Constants.DriveConstants.WHEEL_LOCATIONS);

    private final NetworkTable table = NetworkTableInstance.getDefault().getTable("157/Swerve");

    public SwervePod[] swervePods = new SwervePod[] {
            new SwervePod(DriveConstants.POD_CONFIGS[0], table.getSubTable("Pod 1")),
            new SwervePod(DriveConstants.POD_CONFIGS[1], table.getSubTable("Pod 2")),
            new SwervePod(DriveConstants.POD_CONFIGS[2], table.getSubTable("Pod 3")),
            new SwervePod(DriveConstants.POD_CONFIGS[3], table.getSubTable("Pod 4"))
    };

    public void set(final ChassisSpeeds speeds) {
        final var states = kinematics.toSwerveModuleStates(speeds);

        for (var i = 0; i < states.length; i++) {
            swervePods[i].set(states[i]);
        }
    }

    public void stop() {
        for (final var swervePod : swervePods) {
            swervePod.stop();
        }
    }

    public void setSingle(final SwerveModuleState state) {
        swervePods[0].set(state);
    }

    public void directSetSingle(final double rollSpeed, final double spinSpeed) {
        swervePods[0].directSet(rollSpeed, spinSpeed);
    }
}
