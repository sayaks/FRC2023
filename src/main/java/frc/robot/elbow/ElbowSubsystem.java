// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.elbow;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.Counter.Mode;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ElbowConstants;
import frc.robot.input.DriverInputs;
import frc.robot.lib.NumberUtil;

public class ElbowSubsystem extends SubsystemBase {

    private final CANSparkMax elbowMotor = new CANSparkMax(ElbowConstants.MOTOR_ID, MotorType.kBrushless);
    private final Counter elbowAbsEncoder = new Counter(Mode.kSemiperiod);
    private double elbowSpeed = 0.0;

    public ElbowSubsystem() {
        elbowMotor.setInverted(false);
        final var tab = Shuffleboard.getTab("Encoder Debug");
        tab.addNumber("elbow position", this::getElbowRotationPosition);
        elbowAbsEncoder.setSemiPeriodMode(true);
        elbowAbsEncoder.setUpSource(ElbowConstants.ABS_ENCODER_ROTATION_ID);
        elbowAbsEncoder.reset();
    }

    public Command runElbow(final DriverInputs inputs) {
        return runEnd(() -> {
            final double speed = inputs.axis(DriverInputs.rotateElbow).get();
            rotateElbow(speed);
        }, () -> rotateElbow(0));
    }

    public double getElbowRotationPosition() {
        return NumberUtil.ticksToDegs(elbowAbsEncoder.getPeriod());
    }

    public void rotateElbow(final double speed) {
        elbowSpeed = speed;

        final double limitedSpeed = ElbowConstants.ROTATE_LIMITS.limitMotionWithinRange(
                speed, getElbowRotationPosition());
        elbowMotor.set(limitedSpeed);
    }

    private final NetworkTable table = NetworkTableInstance.getDefault().getTable("157/Elbow");

    @Override
    public void periodic() {
        table.getEntry("Elbow").setNumber(getElbowRotationPosition());
        table.getEntry("ElbowSpeed").setNumber(elbowSpeed);
    }

}
