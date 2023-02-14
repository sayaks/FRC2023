// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.wrist;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.Counter.Mode;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.WristConstants;
import frc.robot.input.DriverInputs;
import frc.robot.lib.NumberUtil;

public class WristSubsystem extends SubsystemBase {

    private final CANSparkMax wristMotor = new CANSparkMax(WristConstants.MOTOR_ID, MotorType.kBrushless);
    private final Counter wristAbsEncoder = new Counter(Mode.kSemiperiod);
    private double wristSpeed = 0.0;

    public WristSubsystem() {
        wristMotor.setInverted(true);
        wristAbsEncoder.setSemiPeriodMode(true);
        wristAbsEncoder.setUpSource(Constants.WristConstants.ABS_ENCODER_ROTATION_ID);
        wristAbsEncoder.reset();
    }

    public Command runWrist(final DriverInputs inputs) {
        return runEnd(() -> {
            final double speed = inputs.axis(DriverInputs.rotateWrist).get();
            rotateWrist(speed);
        }, () -> rotateWrist(0));
    }

    public double getWristRotationPosition() {
        return NumberUtil.ticksToDegs(wristAbsEncoder.getPeriod());
    }

    public void rotateWrist(final double speed) {
        wristSpeed = speed;

        final double limitedSpeed = WristConstants.ROTATE_LIMITS.limitMotionWithinRange(
                speed, getWristRotationPosition());
        wristMotor.set(limitedSpeed);
    }

    public void test() {
        final var testTable = table.getSubTable("Test1");
        final var output = testTable.getEntry("Output");

        final var speedEntry = testTable.getEntry("Speed Input");
        speedEntry.setDefaultNumber(0);
        final var speed = speedEntry.getNumber(0).doubleValue();

        final var encoderEntry = testTable.getEntry("Encoder Input");
        encoderEntry.setDefaultNumber(0);
        final var position = encoderEntry.getNumber(0).doubleValue();

        final double limitedSpeed = WristConstants.ROTATE_LIMITS.limitMotionWithinRange(speed, position);
        output.setNumber(limitedSpeed);
    }

    private final NetworkTable table = NetworkTableInstance.getDefault().getTable("157/Arm");

    @Override
    public void periodic() {
        table.getEntry("Wrist").setNumber(getWristRotationPosition());
        table.getEntry("WristSpeed").setNumber(wristSpeed);
        test();
    }

}
