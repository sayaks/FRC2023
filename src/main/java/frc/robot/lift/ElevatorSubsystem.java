// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.lift;

import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.controller.PIDController;
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

    private final NetworkTable table = NetworkTableInstance.getDefault().getTable("157/Elevator");

    @Override
    public void periodic() {
        table.getEntry("Elevator").setNumber(getElevatorPosition());
        table.getEntry("ElevatorSpeed").setNumber(elevatorSpeed);
    }

    public static final PIDController lowPid = new PIDController(0, 0, 0);
    public static final PIDController upToMidPid = new PIDController(0, 0, 0);
    public static final PIDController downToMidPid = new PIDController(0, 0, 0);
    public static final PIDController highPid = new PIDController(0, 0, 0);

    public static class ElevatorState implements SafetyLogic {
        public final double elevatorPosition;
        public final PIDController elevatorUpPid;
        public final PIDController elevatorDownPid;
        private double minWristPos;
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

        public static final ElevatorState start = new ElevatorState(0, lowPid, lowPid, ElevatorStates.start);
        public static final ElevatorState low = new ElevatorState(0, lowPid, lowPid, ElevatorStates.low);
        // the min wrist position theoretically will work at 157, however, may not be
        // safe, so will likely need some testing and logic for a safer min pos.
        public static final ElevatorState mid = new ElevatorState(10, upToMidPid, downToMidPid, ElevatorStates.mid);
        public static final ElevatorState loading = new ElevatorState(10, upToMidPid, downToMidPid,
                ElevatorStates.loading);
        public static final ElevatorState high = new ElevatorState(20, highPid, downToMidPid, ElevatorStates.high);

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
                    if (armPosition > 300 && wristPosition > 230 && elevatorPosition > this.elevatorPosition + 100) {
                        return -0.2;
                    }
                    break;

                case low:
                    if (armPosition > 165 && wristPosition > 235 && carriagePosition > 2000
                            && elevatorPosition > this.elevatorPosition + 100) {
                        return -0.2;
                    }
                    break;

                case mid:
                case loading:
                case high:
                    if (elevatorPosition > this.elevatorPosition + 100) {
                        return -0.2;
                    } else if (elevatorPosition < this.elevatorPosition - 100) {
                        return 0.2;
                    }
                    break;
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
