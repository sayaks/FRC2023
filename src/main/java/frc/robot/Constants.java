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
        public static final Range ROTATE_LIMITS = new Range(25, 140); // 280 is full max

        public static final double startPos = 140;
        public static final double lowPos = 140;
        public static final double midPosCone = 72;
        public static final double midPosCube = 70;
        public static final double loadingPos = 75;
        public static final double highPos = 100;

        public static final double startPosMinArm = 157;
        public static final double lowPosMinArm = 157;
        public static final double midPosConeMinArm = 207;
        public static final double midPosCubeMinArm = 157;
        public static final double loadingPosMinArm = 207;
        public static final double highPosMinArm = 207;
    }

    public static class ElbowConstants {
        public static final int ABS_ENCODER_ROTATION_ID = 1;
        public static final int MOTOR_ID = 15;
        public static final Range ROTATE_LIMITS = new Range(157, 332); // 162 fully down, 336 fully up

        public static final double startPos = 332;
        public static final double lowPos = 157;
        public static final double midPosCone = 320;
        public static final double midPosCube = 332;
        public static final double loadingPos = 290;
        public static final double highPos = 290;

        public static final double startPosMinCarriage = 1638;
        public static final double lowPosMinCarriage = 2003;
        public static final double midPosConeMinCarriage = 1638;
        public static final double midPosCubeMinCarriage = 1638;
        public static final double loadingPosMinCarriage = 1638;
        public static final double highPosMinCarriage = 1638;

        public static final double startPosMinWrist = 36;
        public static final double lowPosMinWrist = 121;
        public static final double midPosConeMinWrist = 36;
        public static final double midPosCubeMinWrist = 36;
        public static final double loadingPosMinWrist = 36;
        public static final double highPosMinWrist = 36;
    }

    public static class ElevatorConstants {
        public static final int ELEVATOR_MOTOR_ID = 16;
        public static final Range ELEVATOR_LIMITS = new Range(1340, 1800); // end is the bottom most and start is
                                                                           // the top most
        public static final int ELEVATOR_ANALOG_ID = 3;

        public static final double startPos = 1795;
        public static final double lowPos = 1795;
        public static final double midPosCone = 1718;
        public static final double midPosCube = 1795;
        public static final double loadingPos = 1540;
        public static final double highPos = 1350;

        public static final double slewPositiveVal = 10;
        public static final double slewNegativeVal = -1;
    }

    public static class CarriageConstants {
        public static final int CARRIAGE_MOTOR_ID = 17;
        public static final Range CARRIAGE_LIMITS = new Range(1860, 3180);
        public static final int CARRIAGE_ANALOG_ID = 0;

        public static final double startPos = 1860;
        public static final double lowPos = 3180;
        public static final double midPosCone = 3180;
        public static final double midPosCube = 1860;
        public static final double loadingPos = 3170;
        public static final double highPos = 3180;

        public static final double startPosMinElbow = 236;
        public static final double lowPosMinElbow = 146;
        public static final double midPosConeMinElbow = 207;
        public static final double midPosCubeMinElbow = 236;
        public static final double loadingPosMinElbow = 207;
        public static final double highPosMinElbow = 207;
    }

}
