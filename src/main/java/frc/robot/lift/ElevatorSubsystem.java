// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.lift;

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
import frc.robot.statemachines.SubsystemGroup.SafetyLogic;

public class ElevatorSubsystem extends SubsystemBase {
    private final CANSparkMax elevatorMotor = new CANSparkMax(ElevatorConstants.ELEVATOR_MOTOR_ID,
            MotorType.kBrushless);
    private final AnalogInput elevator10Pot = new AnalogInput(ElevatorConstants.ELEVATOR_ANALOG_ID);
    private double elevatorSpeed = 0.0;

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

    public static final PIDController mainPid = new PIDController(0.001, 0, 0);

    public static class ElevatorState implements SafetyLogic {
        public final double elevatorPosition;
        public final PIDController elevatorUpPid;
        public final PIDController elevatorDownPid;
        public static final SlewRateLimiter slew = new SlewRateLimiter(ElevatorConstants.slewPositiveVal,
                ElevatorConstants.slewNegativeVal, 0);
        private ElevatorStates state;

        public enum ElevatorStates {
            start, low, mid, loading, high
        }

        public ElevatorState(final double elevatorPosition, final PIDController elevatorUpPid,
                final PIDController elevatorDownPid, ElevatorStates state) {
            this.elevatorPosition = elevatorPosition;
            this.elevatorUpPid = elevatorUpPid;
            this.elevatorDownPid = elevatorDownPid;
            this.state = state;
        }

        public static final ElevatorState start = new ElevatorState(
                ElevatorConstants.startPos, mainPid, mainPid, ElevatorStates.start);
        public static final ElevatorState low = new ElevatorState(
                ElevatorConstants.lowPos, mainPid, mainPid, ElevatorStates.low);
        // the min wrist position theoretically will work at 157, however, may not be
        // safe, so will likely need some testing and logic for a safer min pos.
        public static final ElevatorState mid = new ElevatorState(
                ElevatorConstants.midPos, mainPid, mainPid, ElevatorStates.mid);
        public static final ElevatorState loading = new ElevatorState(
                ElevatorConstants.loadingPos, mainPid, mainPid, ElevatorStates.loading);
        public static final ElevatorState high = new ElevatorState(
                ElevatorConstants.highPos, mainPid, mainPid, ElevatorStates.high);

        @Override
        public SafetyLogic lowPosition() {
            // TODO Auto-generated method stub
            return low;
        }

        @Override
        public SafetyLogic midPosition() {
            // TODO Auto-generated method stub
            return mid;
        }

        @Override
        public SafetyLogic loadingPosition() {
            // TODO Auto-generated method stub
            return loading;
        }

        @Override
        public SafetyLogic highPosition() {
            // TODO Auto-generated method stub
            return high;
        }

        @Override
        public SafetyLogic defaultPosition() {
            // TODO Auto-generated method stub
            return start;
        }

        @Override
        public double stateCalculate(double speed, double armPosition, double wristPosition, double elevatorPosition,
                double carriagePosition) {
            // TODO Auto-generated method stub
            switch (this.state) {
                case start:
                    if (armPosition > 300 && wristPosition > 230) {
                        return slew.calculate(mainPid.calculate(elevatorPosition, this.elevatorPosition)) * 0.2;
                    }
                    break;

                case low:
                    if (armPosition > 165 && wristPosition > 235 && carriagePosition > 2000) {
                        return slew.calculate(mainPid.calculate(elevatorPosition, this.elevatorPosition)) * 0.2;
                    }
                    break;

                case mid:
                case loading:
                case high:
                    return slew.calculate(mainPid.calculate(elevatorPosition, this.elevatorPosition)) * 0.2;
                default:

                    break;
            }
            return 0;
        }
    }

    // private ElevatorState currentState = ElevatorState.lowArmSafe;
    // private boolean goingUp = true;

    // public void setState(ElevatorState nextState) {
    // goingUp = nextState.elevatorPosition > currentState.elevatorPosition;
    // currentState = nextState;
    // }

    // public void lowPosition() {
    // setState(ElevatorState.movingArm);
    // }

    // public void setArmIsSafe() {
    // setState(ElevatorState.lowArmSafe);
    // }

    // public void midPosition() {
    // setState(ElevatorState.mid);

    // }

    // public void loadingPosition() {
    // setState(ElevatorState.loading);
    // }

    // public void highPosition() {
    // setState(ElevatorState.high);
    // }

    // public void elevatorRun() {
    // if (!currentState.isSafeToMove) {
    // return;
    // }

    // final var pid = goingUp ? currentState.elevatorUpPid :
    // currentState.elevatorDownPid;
    // runElevatorMotor(pid.calculate(getElevatorPosition(),
    // currentState.elevatorPosition));
    // }

    // public void dummyArmThing() {
    // if (currentState == ElevatorState.movingArm) {
    // // make arm/wrist safe
    // if (true /* arm/wrist is safe */) {
    // // stop arm/wrist
    // setArmIsSafe();
    // }
    // } else {
    // // operator controll
    // }
    // }
}
