// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.wrist;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class AutoTestArm extends CommandBase {
    private final WristSubsystem armSubsystem;

    /** Creates a new autoTestArm. */
    public AutoTestArm(final WristSubsystem armSubsystem) {
        this.armSubsystem = armSubsystem;
        addRequirements(armSubsystem);
    }

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {
        armSubsystem.rotateWrist(0.1);
    }

    // Called once the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) {
        armSubsystem.rotateWrist(0);
    }

    // Returns true when the command should end.
    @Override
    public boolean isFinished() {
        return false;
    }
}
