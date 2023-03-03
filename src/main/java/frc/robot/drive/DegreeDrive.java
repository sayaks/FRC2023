// NOT USED ANYMORE, ONLY USE FOR DEBUGGING SINGULAR SWERVE PODS

package frc.robot.drive;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.input.DriverInputs;

public class DegreeDrive extends CommandBase {
    private final DriveSubsystem drive;
    private final DriverInputs driverInputs;

    /** Creates a new TeleopDrive. */
    public DegreeDrive(final DriveSubsystem drive, final DriverInputs driverInputs) {
        this.drive = drive;
        addRequirements(drive);
        this.driverInputs = driverInputs;
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {
        final var x = driverInputs.axis(DriverInputs.driveSpeedX).get();
        final var y = driverInputs.axis(DriverInputs.driveSpeedY).get();

        if (x == 0 || y == 0) {
            drive.stop();
            return;
        }

        final var angle = Math.toDegrees(Math.atan2(y, x)) + 180;

        final var length = Math.sqrt((x * x) + (y * y));
        final var speed = Math.min(1, length) * .1;

        final var state = new SwerveModuleState(speed, Rotation2d.fromDegrees(angle));
        drive.setSingle(state);
    }
}
