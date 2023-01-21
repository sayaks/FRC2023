package org.assabet.aztechs157.input.models;

import org.assabet.aztechs157.input.Model;
import org.assabet.aztechs157.input.inputs.Axis;
import org.assabet.aztechs157.input.inputs.Button;
import org.assabet.aztechs157.input.inputs.Pov;

public class LogitechGamepadF310 extends Model {

    public LogitechGamepadF310(final int joystickId) {
        super(joystickId);
    }

    public final Button a = button(1).label("A");
    public final Button b = button(2).label("B");
    public final Button x = button(3).label("X");
    public final Button y = button(4).label("Y");
    public final Button leftBumper = button(5).label("Left Bumper");
    public final Button rightBumper = button(6).label("Right Bumper");
    public final Button back = button(7).label("Back");
    public final Button start = button(8).label("Start");
    public final Button leftStickPress = button(9).label("Left Stick Press");
    public final Button rightStickPress = button(10).label("Right Stick Press");

    public final Axis leftStickX = axis(0).label("Left Stick X");
    public final Axis leftStickY = axis(1).label("Left Stick Y");
    public final Axis rightTriggerHeld = axis(2).label("Right Trigger Held");
    public final Axis leftTriggerHeld = axis(3).label("Left Trigger Held");
    public final Axis rightStickX = axis(4).label("Right Stick X");
    public final Axis rightStickY = axis(5).label("Right Stick Y");

    public final Pov pov = pov(0).label("");
}
