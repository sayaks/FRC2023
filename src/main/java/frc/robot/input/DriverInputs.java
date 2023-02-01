package frc.robot.input;

import org.assabet.aztechs157.input.layouts.Layout;
import org.assabet.aztechs157.input.layouts.MapLayout;
import org.assabet.aztechs157.input.layouts.DynamicLayout;
import org.assabet.aztechs157.input.models.LogitechExtreme3D;
import org.assabet.aztechs157.input.models.LogitechGamepadF310;
import org.assabet.aztechs157.input.values.Axis;
import org.assabet.aztechs157.input.values.Button;
import org.assabet.aztechs157.numbers.Deadzone;
import org.assabet.aztechs157.numbers.Range;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class DriverInputs extends DynamicLayout {
    public static final Button.Key runIntakeMotorIn = new Button.Key("Run Intake Motor In");
    public static final Button.Key runIntakeMotorOut = new Button.Key("Run Intake Motor Out");
    public static final Button.Key setIntakeSolenoidForward = new Button.Key("Set Intake Solenoid Forward");
    public static final Button.Key setIntakeSolenoidBackward = new Button.Key("Set Intake Solenoid Backward");

    public static final Axis.Key driveSpeedX = new Axis.Key("Drive Speed X");
    public static final Axis.Key driveSpeedY = new Axis.Key("Drive Speed Y");
    public static final Axis.Key driveRotation = new Axis.Key("Drive Rotation");

    private static final NetworkTableEntry entry = NetworkTableInstance.getDefault().getEntry("157/Drive/StickEnabled");

    public DriverInputs() {
        super(() -> entry.getBoolean(false)
                ? flightLayout()
                : logitechLayout());

        entry.setDefaultBoolean(false);
        entry.setPersistent();
    }

    private static final Deadzone deadzone = Deadzone.forAxis(new Range(-0.2, 0.2));
    private static final Rotation2d maxRotationPerSecond = Rotation2d.fromDegrees(50);

    private static Layout logitechLayout() {
        final var layout = new MapLayout("Logitech Layout");
        final var input = new LogitechGamepadF310(0);

        layout.assign(runIntakeMotorIn, input.a);
        layout.assign(runIntakeMotorOut, input.y);
        layout.assign(setIntakeSolenoidForward, input.b);
        layout.assign(setIntakeSolenoidBackward, input.x);

        final var speedModifier = 0.5;

        layout.assign(driveSpeedX, input.leftStickX.map(deadzone::apply).scaledBy(speedModifier));
        layout.assign(driveSpeedY, input.leftStickY.map(deadzone::apply).scaledBy(speedModifier));
        layout.assign(driveRotation, input.rightStickX.map(deadzone::apply).scaledBy(speedModifier)
                .scaledBy(maxRotationPerSecond.getDegrees()));

        return layout;
    }

    private static Layout flightLayout() {
        final var layout = new MapLayout("Flight Layout");
        final var input = new LogitechExtreme3D(1);

        layout.assign(runIntakeMotorIn, input.button5);
        layout.assign(runIntakeMotorOut, input.button3);
        layout.assign(setIntakeSolenoidForward, input.button9);
        layout.assign(setIntakeSolenoidBackward, input.button11);

        final var axisToSpeedConverter = Axis.kDeviceDefaultRange.convertingTo(new Range(0, 1));
        final var speedModifier = input.slider.inverted().map(axisToSpeedConverter::convert);

        layout.assign(driveSpeedX, input.stickX.map(deadzone::apply).scaledBy(speedModifier::get));
        layout.assign(driveSpeedY, input.stickY.map(deadzone::apply).scaledBy(speedModifier::get));
        layout.assign(driveRotation, input.stickRotate.map(deadzone::apply).scaledBy(speedModifier::get)
                .scaledBy(maxRotationPerSecond.getDegrees()));

        return layout;
    }
}
