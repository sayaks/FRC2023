package frc.robot.input;

import org.assabet.aztechs157.input.inputs.Axis;
import org.assabet.aztechs157.input.layouts.Layout;
import org.assabet.aztechs157.input.layouts.MapLayout;
import org.assabet.aztechs157.input.layouts.SelectableLayout;
import org.assabet.aztechs157.input.models.LogitechExtreme3D;
import org.assabet.aztechs157.input.models.LogitechGamepadF310;

import edu.wpi.first.networktables.NetworkTableInstance;

public class DriverInputs {
    public static final Axis.Key driveSpeedX = new Axis.Key().label("Drive Speed X");
    public static final Axis.Key driveSpeedY = new Axis.Key().label("Drive Speed Y");
    public static final Axis.Key driveRotation = new Axis.Key().label("Drive Rotation");
    public static final Axis.Key driveSpeed = new Axis.Key().label("Drive Speed");

    public static Layout getDriverInputs() {

        final var logitechLayout = new MapLayout();
        final var logitech = new LogitechGamepadF310(0);
        logitechLayout.assign(driveSpeedX, logitech.leftStickX);
        logitechLayout.assign(driveSpeedY, logitech.leftStickY);
        logitechLayout.assign(driveRotation, logitech.rightStickX);
        logitechLayout.assign(driveSpeed, Axis.always(.5));

        final var stickLayout = new MapLayout();
        final var stick = new LogitechExtreme3D(1);
        stickLayout.assign(driveSpeedX, stick.stickX);
        stickLayout.assign(driveSpeedY, stick.stickY);
        stickLayout.assign(driveRotation, stick.stickRotate);
        stickLayout.assign(driveSpeed, stick.slider);

        final var entry = NetworkTableInstance.getDefault().getEntry("157/Drive/StickEnabled");
        entry.setDefaultBoolean(false);

        return new SelectableLayout(
                () -> entry.getBoolean(false)
                        ? logitechLayout
                        : stickLayout);
    }
}
