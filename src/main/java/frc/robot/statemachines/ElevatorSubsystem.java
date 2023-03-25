// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.statemachines;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ElevatorConstants;
import frc.robot.input.DriverInputs;

public class ElevatorSubsystem extends SubsystemBase {
    private final CANSparkMax elevatorMotor = new CANSparkMax(ElevatorConstants.ELEVATOR_MOTOR_ID,
            MotorType.kBrushless);
    private final AnalogInput elevator10Pot = new AnalogInput(ElevatorConstants.ELEVATOR_ANALOG_ID);
    private double elevatorSpeed = 0.0;

    public void reset() {
        ElevatorState.slew.reset(0);
    }

    /** Creates a new ElevatorSubsystem. */
    public ElevatorSubsystem() {
        elevatorMotor.setIdleMode(IdleMode.kBrake);
    }

    public Command runElevator(final DriverInputs inputs) {
        return runEnd(() -> {
            final double speed = inputs.axis(DriverInputs.elevator).get();
            runElevatorMotor(speed);
        }, () -> runElevatorMotor(0));
    }

    public double getElevatorPosition() {
        return 2500 - elevator10Pot.getValue(); // kinda a hack, same dealio as carriage
    }

    public void runElevatorMotor(final double speed) {
        elevatorSpeed = speed;

        final double elevatorLimits = ElevatorConstants.ELEVATOR_LIMITS.limitMotionWithinRange(speed,
                getElevatorPosition());
        elevatorMotor.set(elevatorLimits);
    }

    public void stop() {
        runElevatorMotor(0);
    }

    private final NetworkTable table = NetworkTableInstance.getDefault().getTable("157/Elevator");

    @Override
    public void periodic() {
        table.getEntry("Elevator").setNumber(getElevatorPosition());
        table.getEntry("ElevatorSpeed").setNumber(elevatorSpeed);
    }

    public static class ElevatorState
            extends SafetyLogic<Void> {

        public ElevatorState(State state) {
            super(state);
            this.mainPid = new PIDController(0.01, 0, 0);
        }

        public static final SlewRateLimiter slew = new SlewRateLimiter(ElevatorConstants.SLEW_POSITIVE_VAL,
                ElevatorConstants.SLEW_NEGATIVE_VAL, 0);

        @Override
        public double stateCalculate(double speed, double elbowPosition, double wristPosition, double elevatorPosition,
                double carriagePosition) {
            // Switch case for the elevator to ensure the elevator is safe to move (no
            // crushing of other systems), check comments for each state for further details
            switch (this.state) {
                // Ensure the wrist and elbow won't be crushed when moving the elevator down
                case mid:
                case start:
                    if (elbowPosition > ElevatorConstants.SAFETY_ELBOW_LIMIT_START_MID
                            && wristPosition > ElevatorConstants.SAFETY_WRIST_LIMIT_START_MID) {
                        return mainPid.calculate(elevatorPosition, this.position);
                    }
                    break;
                // Ensures everything is safe to move before elevator goes too low
                case low:
                    if (elbowPosition > ElevatorConstants.SAFETY_ELBOW_LIMIT_LOW
                            && wristPosition > ElevatorConstants.SAFETY_WRIST_LIMIT_LOW
                            && carriagePosition > ElevatorConstants.SAFETY_CARRIAGE_LIMIT_LOW) {
                        return mainPid.calculate(elevatorPosition, this.position);
                    }
                    break;
                // Will run the elbow up if already too low to ensure it won't collide with the
                // pole, check the switch case in elbow for further detail
                case high:
                    if (elbowPosition > ElevatorConstants.SAFETY_ELBOW_LIMIT_HIGH
                            && wristPosition < ElevatorConstants.SAFETY_WRIST_LIMIT_HIGH) {
                        return mainPid.calculate(elevatorPosition, this.position);
                    }
                    break;
                // Runs the elevator up to loading as there are no safety limits we need to keep
                // in mind for loading
                case loading:
                    return mainPid.calculate(elevatorPosition, this.position);

                default:

                    break;
            }
            return 0;
        }

        @Override
        protected SafetyConstants<Void> get_constants() {
            return ElevatorConstants.SINGLETON;
        }
    }
}
