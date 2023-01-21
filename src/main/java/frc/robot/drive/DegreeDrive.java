package frc.robot.drive;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.DriveConstants;

public class DegreeDrive extends CommandBase {
    private final DriveSubsystem drive;
    private final Joystick joystick;

    /** Creates a new TeleopDrive. */
    public DegreeDrive(final DriveSubsystem drive, final Joystick joystick) {
        this.drive = drive;
        addRequirements(drive);
        this.joystick = joystick;
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {
        final var x = joystick.getRawAxis(0);
        final var y = joystick.getRawAxis(1);

        final var aboveThreshold = Math.abs(x) > DriveConstants.TELEOP_AXIS_THRESHOLD
                || Math.abs(y) > DriveConstants.TELEOP_AXIS_THRESHOLD;

        if (!aboveThreshold) {
            drive.stop();
            return;
        }

        final var angle = Math.toDegrees(Math.atan2(y, x)) + 180;

        final var length = Math.sqrt((x * x) + (y * y));
        final var speed = Math.min(1, length) * DriveConstants.TELEOP_ROLL_SPEED;

        final var state = new SwerveModuleState(speed, Rotation2d.fromDegrees(angle));
        drive.setSingle(state);
    }
}
