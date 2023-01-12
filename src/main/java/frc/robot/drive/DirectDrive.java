package frc.robot.drive;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.DriveConstants;

public class DirectDrive extends CommandBase {
    private final DriveSubsystem drive;
    private final Joystick joystick;

    /** Creates a new DirectDrive. */
    public DirectDrive(final DriveSubsystem drive, final Joystick joystick) {
        this.drive = drive;
        addRequirements(drive);
        this.joystick = joystick;
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {
        final var rollSpeed = -joystick.getRawAxis(1) * DriveConstants.TELEOP_ROLL_SPEED;
        final var spinSpeed = joystick.getRawAxis(4) * DriveConstants.TELEOP_SPIN_SPEED;

        drive.directSet(rollSpeed, spinSpeed);
    }
}
