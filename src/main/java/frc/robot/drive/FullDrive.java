package frc.robot.drive;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.input.DriverInputs;

public class FullDrive extends CommandBase {
    private final DriveSubsystem drive;
    private final DriverInputs driverInputs;
    private final SlewRateLimiter xSlewRateLimiter = new SlewRateLimiter(0.1, -0.1, 0);

    private final SlewRateLimiter ySlewRateLimiter = new SlewRateLimiter(0.1, -0.1, 0);
    private final SlewRateLimiter rotSlewRateLimiter = new SlewRateLimiter(10, -10, 0);

    /** Creates a new FullDrive. */
    public FullDrive(final DriveSubsystem drive, final DriverInputs driverInputs) {
        this.drive = drive;
        addRequirements(drive);
        this.driverInputs = driverInputs;
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {
        // X and Y are swapped here due to trigonometry
        // Having them match will make the robot think forward is to it's right
        // Having them swapped will make it think forward is properly forward
        final var x = driverInputs.axis(DriverInputs.driveSpeedY).get();
        final var y = driverInputs.axis(DriverInputs.driveSpeedX).get();
        final var r = driverInputs.axis(DriverInputs.driveRotation).get();
        // TODO TEST THIS PLS
        // final var x =
        // xSlewRateLimiter.calculate(driverInputs.axis(DriverInputs.driveSpeedY).get());
        // final var y =
        // ySlewRateLimiter.calculate(driverInputs.axis(DriverInputs.driveSpeedX).get());
        // final var r =
        // rotSlewRateLimiter.calculate(driverInputs.axis(DriverInputs.driveRotation).get());

        final var speeds = new ChassisSpeeds(x, y, Math.toRadians(r));
        drive.set(speeds);
    }
}
