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

    public record Config(int motorSpinId, int encoderSpinId, int motorRollId, boolean motorRollInverted) {
    }

    private final CANSparkMax motorRoll;
    private final CANSparkMax motorSpin;
    private final CANCoder encoderSpin;
    private final NetworkTable table;

    public SwervePod(final Config config, final NetworkTable table) {
        this.table = table;
        motorRoll = new CANSparkMax(config.motorRollId, MotorType.kBrushless);
        motorSpin = new CANSparkMax(config.motorSpinId, MotorType.kBrushless);
        encoderSpin = new CANCoder(config.encoderSpinId);

        table.getEntry("Inverted").setBoolean(config.motorRollInverted);

        motorRoll.setIdleMode(DriveConstants.ROLL_IDLE_MODE);
        motorRoll.setInverted(false);
        motorSpin.setIdleMode(DriveConstants.SPIN_IDLE_MODE);
        motorSpin.setInverted(false);
        encoderSpin.configSensorDirection(true);
    }

    public void set(final SwerveModuleState state) {
        spin(wrapDegrees(state.angle.getDegrees()));
        roll(state.speedMetersPerSecond);
    }

    public void directSet(final double rollSpeed, final double spinSpeed) {
        getCurrentSpin();

        directSpin(spinSpeed);
        roll(rollSpeed);
    }

    public void stop() {
        directSpin(0);
        roll(0);
    }

    private final SlewRateLimiter rollSlewRate = new SlewRateLimiter(DriveConstants.ROLL_SLEW_RATE);
    private boolean reversed = false;

    private void roll(double speed) {
        table.getEntry("Input Speed").setDouble(speed);
        table.getEntry("Roll Reversed?").setBoolean(reversed);

        if (reversed) {
            speed = -speed;
        }

        final var inverted = table.getEntry("Inverted");
        if (inverted.getBoolean(false)) {
            speed = -speed;
        }

        motorRoll.set(rollSlewRate.calculate(speed));
    }

    public double getRawRoll() {
        return motorRoll.getEncoder().getPosition();
    }

    private double wrapDegrees(final double degrees) {
        var wrapped = degrees % 360;

        if (wrapped < 0) {
            wrapped += 360;
        }

        expect(wrapped).greaterOrEqual(0).lessOrEqual(360);
        return wrapped;
    }

    private double getCurrentSpin() {
        return encoderSpin.getAbsolutePosition();
    }

    private void directSpin(final double speed) {
        motorSpin.set(speed);
    }

    private void spin(final double target) {
        table.getEntry("Input Angle").setDouble(target);

        final var initialDelta = computeInitialDelta(target);
        table.getEntry("Initial Delta").setDouble(initialDelta);

        final var shortestDelta = computeShortestDelta(initialDelta);
        table.getEntry("Shorted Delta").setDouble(shortestDelta);

        final var pidOutput = computeSpinPidOutput(shortestDelta);
        table.getEntry("PID Output").setDouble(pidOutput);
        directSpin(pidOutput);
    }

    private double computeInitialDelta(final double target) {
        final double initial = getCurrentSpin();

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

    private final PIDController spinPid = new PIDController(DriveConstants.SPIN_KP, 0, DriveConstants.SPIN_KD);

    private double computeSpinPidOutput(final double shortestDelta) {
        final var pidOutput = spinPid.calculate(getCurrentSpin() + shortestDelta, getCurrentSpin());
        return pidOutput;
    }
}
