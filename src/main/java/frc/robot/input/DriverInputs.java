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

public class DriverInputs extends SelectableLayout {
    public static final Axis.Key driveSpeedX = new Axis.Key("Drive Speed X");
    public static final Axis.Key driveSpeedY = new Axis.Key("Drive Speed Y");
    public static final Axis.Key driveRotation = new Axis.Key("Drive Rotation");

    private static final NetworkTableEntry entry = NetworkTableInstance.getDefault().getEntry("157/Drive/StickEnabled");

    public DriverInputs() {
        super(() -> entry.getBoolean(false)
                ? flightLayout()
                : logitechLayout());
    }

    private static final double kMaxRotationDegreesPerSecond = 50;

    private static Layout logitechLayout() {
        final var layout = new MapLayout("Logitech Layout");
        final var input = new LogitechGamepadF310(0);

        final var speedModifier = 0.5;

        layout.assign(driveSpeedX, manualDeadzone(input.leftStickX).scaled(speedModifier));
        layout.assign(driveSpeedY, manualDeadzone(input.leftStickY).scaled(speedModifier));
        layout.assign(driveRotation, manualDeadzone(input.rightStickX).scaled(kMaxRotationDegreesPerSecond));

        return layout;
    }

    private static Layout flightLayout() {
        final var layout = new MapLayout("Flight Layout");
        final var input = new LogitechExtreme3D(1);

        final var speedModifier = input.slider.inverted()
                .convertRange(new Range(-1, 1), new Range(0, 1));

        layout.assign(driveSpeedX, manualDeadzone(input.stickX).scaled(speedModifier::get));
        layout.assign(driveSpeedY, manualDeadzone(input.stickY).scaled(speedModifier::get));
        layout.assign(driveRotation, manualDeadzone(input.stickRotate).scaled(kMaxRotationDegreesPerSecond));

        return layout;
    }

    private static Axis manualDeadzone(final Axis input) {
        final var threshold = 0.2;

        return input.map(value -> {
            if (Math.abs(value) < threshold) {
                return 0;
            }
            return value;
        });
    }
}
