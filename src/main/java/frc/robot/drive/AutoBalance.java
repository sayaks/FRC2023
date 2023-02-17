// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.drive;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.AutoConstants;

public class AutoBalance extends CommandBase {
    private final DriveSubsystem drive;

    /** Creates a new AutoBalance. */
    public AutoBalance(final DriveSubsystem drive) {
        this.drive = drive;
        // Use addRequirements() here to declare subsystem dependencies.
        addRequirements(drive);
    }

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {
        if (Math.abs(drive.getRobotPitch().getDegrees()) <= AutoConstants.BALANCE_ACCURACY_DEG) {
            drive.stop(); // TODO: Turn the wheels perpendicular to the platform to lock the robot in
                          // place
        } else if (drive.getRobotPitch().getDegrees() >= AutoConstants.BALANCE_ACCURACY_DEG) {
            drive.set(new ChassisSpeeds(.2, 0, 0));
        } else {
            drive.set(new ChassisSpeeds(-.2, 0, 0));
        }
    }

    // Called once the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) {
        drive.stop();
    }

    // Returns true when the command should end.
    @Override
    public boolean isFinished() {
        return false;
    }
}
