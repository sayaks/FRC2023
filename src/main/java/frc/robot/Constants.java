// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import org.assabet.aztechs157.numbers.Range;

import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.util.Units;
import frc.robot.drive.SwervePod;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide
 * numerical or boolean constants. This class should not be used for any other
 * purpose. All constants
 * should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>
 * It is advised to statically import this class (or one of its inner classes)
 * wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {

    public static class DriveConstants {
        public static final IdleMode DRIVE_IDLE_MODE = IdleMode.kBrake;
        public static final IdleMode ANGLE_IDLE_MODE = IdleMode.kCoast;

        public static final double DRIVE_SLEW_RATE = 1;
        public static final double ANGLE_KP = 0.01;
        public static final double ANGLE_KD = 0.00009;

        public static final SwervePod.Config[] POD_CONFIGS = new SwervePod.Config[] {
                new SwervePod.Config(1, 2, 3, false),
                new SwervePod.Config(4, 5, 6, false),
                new SwervePod.Config(7, 8, 9, false),
                new SwervePod.Config(10, 11, 12, false),
        };

        private static final double CENTER_TO_POD_METER = Units.inchesToMeters(9.25);

        public static final Translation2d[] WHEEL_LOCATIONS = new Translation2d[] {
                new Translation2d(-CENTER_TO_POD_METER, CENTER_TO_POD_METER),
                new Translation2d(CENTER_TO_POD_METER, CENTER_TO_POD_METER),
                new Translation2d(CENTER_TO_POD_METER, -CENTER_TO_POD_METER),
                new Translation2d(-CENTER_TO_POD_METER, -CENTER_TO_POD_METER),
        };

        public static final double slewRateVal = 1;
        public static final double slewRotateVal = 100;
    }

    public static class AutoConstants {
        public static final ChassisSpeeds AUTO_SPEEDS = new ChassisSpeeds(-0.3, 0, 0);
        public static final double AUTO_DISTANCE = 3000000; // TODO: DEFINITELY THAT (NOT)
        public static final double TURN_ACCURACY_DEG = 20;
        public static final double DRIVE_ACCURACY = 20;
        public static final double BALANCE_ACCURACY_DEG = 2.5;
    }

    public static class IntakeConstants {
        public static final int MOTOR_ID = 13;
        public static final int SOLENOID_FORWARD_ID = 0;
        public static final int SOLENOID_BACKWARD_ID = 1;
        public static final int PNEUMATICS_HUB_ID = 51;
        public static final int INTAKE_SENSOR_ID = 3;
    }

    public static class WristConstants {
        public static final int ABS_ENCODER_ROTATION_ID = 2;
        public static final int MOTOR_ID = 14;
        public static final Range ROTATE_LIMITS = new Range(150, 250); // 280 is full max

        public static final double startPos = 244;
        public static final double lowPos = 250;
        public static final double midPos = 190;
        public static final double loadingPos = 190;
        public static final double highPos = 190;

        public static final double startPosMinArm = 165;
        public static final double lowPosMinArm = 165;
        public static final double midPosMinArm = 215;
        public static final double loadingPosMinArm = 215;
        public static final double highPosMinArm = 215;
    }

    public static class ElbowConstants {
        public static final int ABS_ENCODER_ROTATION_ID = 1;
        public static final int MOTOR_ID = 15;
        public static final Range ROTATE_LIMITS = new Range(165, 335); // 162 fully down, 336 fully up

        public static final double startPos = 340;
        public static final double lowPos = 165;
        public static final double midPos = 327;
        public static final double loadingPos = 340;
        public static final double highPos = 300;

        public static final double startPosMinCarriage = 800;
        public static final double lowPosMinCarriage = 1900;
        public static final double midPosMinCarriage = 800;
        public static final double loadingPosMinCarriage = 800;
        public static final double highPosMinCarriage = 800;

        public static final double startPosMinWrist = 157;
        public static final double lowPosMinWrist = 235;
        public static final double midPosMinWrist = 157;
        public static final double loadingPosMinWrist = 157;
        public static final double highPosMinWrist = 157;
    }

    public static class ElevatorConstants {
        public static final int ELEVATOR_MOTOR_ID = 16;
        public static final Range ELEVATOR_LIMITS = new Range(440, 1900); // end is the bottom most and start is
                                                                          // the top most
        public static final int ELEVATOR_ANALOG_ID = 3;

        public static final double startPos = 1900;
        public static final double lowPos = 1900;
        public static final double midPos = 1750;
        public static final double loadingPos = 1600;
        public static final double highPos = 600;

        public static final double slewPositiveVal = 0.5;
        public static final double slewNegativeVal = -0.5;
    }

    public static class CarriageConstants {
        public static final int CARRIAGE_MOTOR_ID = 3;
        public static final Range CARRIAGE_LIMITS = new Range(1000, 2200);
        public static final int CARRIAGE_ANALOG_ID = 0;

        public static final double startPos = 1000;
        public static final double lowPos = 2150;
        public static final double midPos = 2150;
        public static final double loadingPos = 2150;
        public static final double highPos = 2150;

        public static final double startPosMinElbow = 244;
        public static final double lowPosMinElbow = 155;
        public static final double midPosMinElbow = 215;
        public static final double loadingPosMinElbow = 215;
        public static final double highPosMinElbow = 215;
    }

}
