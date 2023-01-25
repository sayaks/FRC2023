package frc.robot.input;

import org.assabet.aztechs157.Range;
import org.assabet.aztechs157.input.inputs.Axis;
import org.assabet.aztechs157.input.layouts.Layout;
import org.assabet.aztechs157.input.layouts.MapLayout;
import org.assabet.aztechs157.input.layouts.SelectableLayout;
import org.assabet.aztechs157.input.models.LogitechExtreme3D;
import org.assabet.aztechs157.input.models.LogitechGamepadF310;

import edu.wpi.first.networktables.NetworkTableInstance;
import frc.robot.Constants.DriveConstants;

public class DriverInputs {
    public static final Axis.Key driveSpeedX = new Axis.Key().label("Drive Speed X");
    public static final Axis.Key driveSpeedY = new Axis.Key().label("Drive Speed Y");
    public static final Axis.Key driveRotation = new Axis.Key().label("Drive Rotation");

    public static Layout getDriverInputs() {

        final var logitechLayout = new MapLayout();
        final var logitech = new LogitechGamepadF310(0);
        logitechLayout.assign(driveSpeedX, manualDeadzone(logitech.leftStickX).scaled(0.5));
        logitechLayout.assign(driveSpeedY, manualDeadzone(logitech.leftStickY).scaled(0.5));
        logitechLayout.assign(driveRotation,
                manualDeadzone(logitech.rightStickX).scaled(DriveConstants.TELEOP_SPIN_SPEED));

        final var stickLayout = new MapLayout();
        final var flightStick = new LogitechExtreme3D(1);

        final var flightStickSpeedModifier = flightStick.slider.inverted()
                .convertRange(new Range(-1, 1), new Range(0, 1));

        stickLayout.assign(driveSpeedX, manualDeadzone(flightStick.stickX).scaled(flightStickSpeedModifier::get));
        stickLayout.assign(driveSpeedY, manualDeadzone(flightStick.stickY).scaled(flightStickSpeedModifier::get));
        stickLayout.assign(driveRotation,
                manualDeadzone(flightStick.stickRotate).scaled(DriveConstants.TELEOP_SPIN_SPEED));

        final var entry = NetworkTableInstance.getDefault().getEntry("157/Drive/StickEnabled");
        entry.setDefaultBoolean(false);

        return new SelectableLayout(
                () -> entry.getBoolean(false)
                        ? stickLayout
                        : logitechLayout);
    }

    public static Axis manualDeadzone(final Axis input) {
        return input.map(value -> {
            if (Math.abs(value) < DriveConstants.TELEOP_AXIS_THRESHOLD) {
                return 0;
            }
            return value;
        });
    }
}
