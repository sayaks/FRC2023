// NOT USED ANYMORE, ONLY USE FOR DEBUGGING SINGULAR SWERVE PODS

package frc.robot.drive;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.input.DriverInputs;

public class DirectDrive extends CommandBase {
    private final DriveSubsystem drive;
    private final DriverInputs driverInputs;

    /** Creates a new DirectDrive. */
    public DirectDrive(final DriveSubsystem drive, final DriverInputs driverInputs) {
        this.drive = drive;
        addRequirements(drive);
        this.driverInputs = driverInputs;
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {
        final var rollSpeed = driverInputs.axis(DriverInputs.driveSpeedY).get();
        final var spinSpeed = driverInputs.axis(DriverInputs.driveRotation).get();

        drive.directSetSingle(rollSpeed, spinSpeed);
    }
}
