package frc.robot.drive;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;

public class FullDrive extends CommandBase {
    private final DriveSubsystem drive;
    private final Joystick joystick;

    /** Creates a new FullDrive. */
    public FullDrive(final DriveSubsystem drive, final Joystick joystick) {
        this.drive = drive;
        addRequirements(drive);
        this.joystick = joystick;
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {
        final var speed = (-joystick.getRawAxis(3) + 1) / 2;
        final var x = deadZone(joystick.getRawAxis(1)) * speed;
        final var y = deadZone(joystick.getRawAxis(0)) * speed;
        final var r = deadZone(joystick.getRawAxis(2)) * Constants.DriveConstants.TELEOP_SPIN_SPEED;

        final var speeds = new ChassisSpeeds(x, y, Math.toRadians(r));
        drive.set(speeds);
    }

    public double deadZone(final double input) {
        if (Math.abs(input) < Constants.DriveConstants.TELEOP_AXIS_THRESHOLD) {
            return 0;
        }
        return input;
    }
}
