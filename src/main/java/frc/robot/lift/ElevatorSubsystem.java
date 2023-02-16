// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.lift;

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
import frc.robot.lib.NumberUtil;

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
        return elevator10Pot.getValue();
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

    public static class ElevatorState {
        public final double liftPosition;
        public final PIDController liftUpPid;
        public final PIDController liftDownPid;
        public final double carriagePosition;
        public final boolean isSafeToMove;

        public ElevatorState(final double liftPosition, final PIDController liftUpPid, final PIDController liftDownPid,
                final double carriagePosition, final boolean isSafeToMove) {
            this.liftPosition = liftPosition;
            this.liftUpPid = liftUpPid;
            this.liftDownPid = liftDownPid;
            this.carriagePosition = carriagePosition;
            this.isSafeToMove = isSafeToMove;
        }

        public static final ElevatorState lowArmSafe = new ElevatorState(0, upToMidPid, lowPid, 0, true);
        public static final ElevatorState movingArm = new ElevatorState(0, upToMidPid, lowPid, 0, false);
        public static final ElevatorState mid = new ElevatorState(10, upToMidPid, downToMidPid, 0, true);
        public static final ElevatorState loading = new ElevatorState(10, upToMidPid, downToMidPid, 5, true);
        public static final ElevatorState high = new ElevatorState(20, highPid, downToMidPid, 0, true);
    }

    private ElevatorState currentState = ElevatorState.lowArmSafe;
    private boolean goingUp = true;

    public void setState(ElevatorState nextState) {
        goingUp = nextState.liftPosition > currentState.liftPosition;
        currentState = nextState;
    }

    public void lowPosition() {
        setState(ElevatorState.movingArm);
    }

    public void setArmIsSafe() {
        setState(ElevatorState.lowArmSafe);
    }

    public void midPosition() {
        setState(ElevatorState.mid);

    }

    public void loadingPosition() {
        setState(ElevatorState.loading);
    }

    public void highPosition() {
        setState(ElevatorState.high);
    }

    public void elevatorRun() {
        if (!currentState.isSafeToMove) {
            return;
        }

        final var pid = goingUp ? currentState.liftUpPid : currentState.liftDownPid;
        runElevatorMotor(pid.calculate(getElevatorPosition(), currentState.liftPosition));
    }

    public void dummyArmThing() {
        if (currentState == ElevatorState.movingArm) {
            // make arm/wrist safe
            if (true /* arm/wrist is safe */) {
                // stop arm/wrist
                setArmIsSafe();
            }
        } else {
            // operator controll
        }
    }
}
