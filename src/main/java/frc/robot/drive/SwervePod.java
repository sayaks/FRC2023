package frc.robot.drive;

import com.ctre.phoenix.sensors.CANCoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.networktables.NetworkTable;
import frc.robot.Constants.DriveConstants;

import static org.assabet.aztechs157.ExpectDouble.expect;

public class SwervePod {

    public record Config(int driveMotorId, int angleMotorId, int angleEncoderId, boolean driveMotorInverted) {
    }

    private final CANSparkMax driveMotor;
    private final CANSparkMax angleMotor;
    private final CANCoder angleEncoder;
    private final NetworkTable table;

    public SwervePod(final Config config, final NetworkTable table) {
        this.table = table;
        driveMotor = new CANSparkMax(config.driveMotorId, MotorType.kBrushless);
        angleMotor = new CANSparkMax(config.angleMotorId, MotorType.kBrushless);
        angleEncoder = new CANCoder(config.angleEncoderId);

        table.getEntry("Inverted").setBoolean(config.driveMotorInverted);

        driveMotor.setIdleMode(DriveConstants.ROLL_IDLE_MODE);
        driveMotor.setInverted(false);
        angleMotor.setIdleMode(DriveConstants.SPIN_IDLE_MODE);
        angleMotor.setInverted(false);
        angleEncoder.configSensorDirection(true);
    }

    public void set(final SwerveModuleState state) {
        goToAngle(wrapDegrees(state.angle.getDegrees()));
        drive(state.speedMetersPerSecond);
    }

    public void directSet(final double driveSpeed, final double angleSpeed) {
        getCurrentAngle();

        setAngleSpeed(angleSpeed);
        drive(driveSpeed);
    }

    public void stop() {
        setAngleSpeed(0);
        drive(0);
    }

    private final SlewRateLimiter driveSlewrate = new SlewRateLimiter(DriveConstants.ROLL_SLEW_RATE);
    private boolean reversed = false;

    private void drive(double speed) {
        table.getEntry("Input Speed").setDouble(speed);
        table.getEntry("Roll Reversed?").setBoolean(reversed);

        if (reversed) {
            speed = -speed;
        }

        final var inverted = table.getEntry("Inverted");
        if (inverted.getBoolean(false)) {
            speed = -speed;
        }

        driveMotor.set(driveSlewrate.calculate(speed));
    }

    public double getRawDrivePosition() {
        return driveMotor.getEncoder().getPosition();
    }

    private double wrapDegrees(final double degrees) {
        var wrapped = degrees % 360;

        if (wrapped < 0) {
            wrapped += 360;
        }

        expect(wrapped).greaterOrEqual(0).lessOrEqual(360);
        return wrapped;
    }

    public double getCurrentAngle() {
        return angleEncoder.getAbsolutePosition();
    }

    private void setAngleSpeed(final double speed) {
        angleMotor.set(speed);
    }

    private void goToAngle(final double target) {
        table.getEntry("Input Angle").setDouble(target);

        final var initialDelta = computeInitialDelta(target);
        table.getEntry("Initial Delta").setDouble(initialDelta);

        final var shortestDelta = computeShortestDelta(initialDelta);
        table.getEntry("Shorted Delta").setDouble(shortestDelta);

        final var pidOutput = computeAnglePidOutput(shortestDelta);
        table.getEntry("PID Output").setDouble(pidOutput);
        setAngleSpeed(pidOutput);
    }

    private double computeInitialDelta(final double target) {
        final double initial = getCurrentAngle();

        expect(target).greaterOrEqual(0).lessOrEqual(360);
        expect(initial).greaterOrEqual(0).lessOrEqual(360);

        var initialDelta = target - initial;
        expect(initialDelta).greaterOrEqual(-360).lessOrEqual(360);

        if (initialDelta < 0) {
            initialDelta += 360;
        }
        expect(initialDelta).greaterOrEqual(0).lessOrEqual(360);

        return initialDelta;
    }

    private double computeShortestDelta(final double initialDelta) {
        expect(initialDelta).greaterOrEqual(0).lessOrEqual(360);

        if (initialDelta < 90) {
            reversed = false;
            return initialDelta;

        } else if (initialDelta < 270) {
            reversed = true;
            return initialDelta - 180;

        } else if (initialDelta <= 360) {
            reversed = false;
            return initialDelta - 360;
        }

        throw new RuntimeException("Above expect() should have covered this.");
    }

    private final PIDController anglePid = new PIDController(DriveConstants.SPIN_KP, 0, DriveConstants.SPIN_KD);

    private double computeAnglePidOutput(final double shortestDelta) {
        final var pidOutput = anglePid.calculate(getCurrentAngle() + shortestDelta, getCurrentAngle());
        return pidOutput;
    }
}
