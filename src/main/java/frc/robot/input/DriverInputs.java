package frc.robot.input;

import org.assabet.aztechs157.Range;
import org.assabet.aztechs157.input.inputs.Axis;
import org.assabet.aztechs157.input.layouts.Layout;
import org.assabet.aztechs157.input.layouts.MapLayout;
import org.assabet.aztechs157.input.layouts.SelectableLayout;
import org.assabet.aztechs157.input.models.LogitechExtreme3D;
import org.assabet.aztechs157.input.models.LogitechGamepadF310;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.robot.Constants.DriveConstants;

public class DriverInputs extends SelectableLayout {
    public static final Axis.Key driveSpeedX = new Axis.Key().label("Drive Speed X");
    public static final Axis.Key driveSpeedY = new Axis.Key().label("Drive Speed Y");
    public static final Axis.Key driveRotation = new Axis.Key().label("Drive Rotation");

    private static final NetworkTableEntry entry = NetworkTableInstance.getDefault().getEntry("157/Drive/StickEnabled");

    public DriverInputs() {
        super(() -> entry.getBoolean(false)
                ? flightLayout()
                : logitechLayout());
    }

    private static final double kMaxRotationDegreesPerSecond = 50;

    private static Layout logitechLayout() {
        final var layout = new MapLayout();
        final var input = new LogitechGamepadF310(0);

        final var speedModifier = 0.5;

        layout.assign(driveSpeedX, manualDeadzone(input.leftStickX).scaled(speedModifier));
        layout.assign(driveSpeedY, manualDeadzone(input.leftStickY).scaled(speedModifier));
        layout.assign(driveRotation, manualDeadzone(input.rightStickX).scaled(kMaxRotationDegreesPerSecond));

        return layout;
    }

    private static Layout flightLayout() {
        final var layout = new MapLayout();
        final var input = new LogitechExtreme3D(1);

        final var speedModifier = input.slider.inverted()
                .convertRange(new Range(-1, 1), new Range(0, 1));

        layout.assign(driveSpeedX, manualDeadzone(input.stickX).scaled(speedModifier::get));
        layout.assign(driveSpeedY, manualDeadzone(input.stickY).scaled(speedModifier::get));
        layout.assign(driveRotation, manualDeadzone(input.stickRotate).scaled(kMaxRotationDegreesPerSecond));

        return layout;
    }

    private static Axis manualDeadzone(final Axis input) {
        return input.map(value -> {
            if (Math.abs(value) < DriveConstants.TELEOP_AXIS_THRESHOLD) {
                return 0;
            }
            return value;
        });
    }
}
