// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.lift;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ElevatorConstants;
import frc.robot.input.DriverInputs;
import frc.robot.lib.NumberUtil;

public class ElevatorSubsystem extends SubsystemBase {
    private final CANSparkMax elevatorMotor = new CANSparkMax(ElevatorConstants.ELEVATOR_MOTOR_ID,
            MotorType.kBrushless);
    private final AnalogInput elevator10Pot = new AnalogInput(0);
    private double elevatorSpeed = 0.0;

    /** Creates a new ElevatorSubsystem. */
    public ElevatorSubsystem() {

    }

    public Command runElevator(final DriverInputs inputs) {
        return runEnd(() -> {
            final double speed = inputs.axis(DriverInputs.elevator).get();
            runElevatorMotor(speed);
        }, () -> runElevatorMotor(0));
    }

    public double getElevatorPosition() {
        return NumberUtil.ticksToDegs(elevator10Pot.getVoltage());
    }

    public void runElevatorMotor(final double speed) {
        elevatorSpeed = speed;

        final double elevatorLimits = ElevatorConstants.ELEVATOR_LIMITS.limitMotionWithinRange(speed,
                getElevatorPosition());
        elevatorMotor.set(elevatorLimits);
    }

    @Override
    public void periodic() {
        // This method will be called once per scheduler run
    }
}
