// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.statemachines;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.statemachines.ElevatorSubsystem.ElevatorState;
import frc.robot.statemachines.CarriageSubsystem.CarriageState;
import frc.robot.statemachines.WristSubsystem.WristState;
import frc.robot.statemachines.ElbowSubsystem.ElbowState;
import frc.robot.statemachines.SubsystemGroup.SafetyLogic.State;

public class SubsystemGroup extends SubsystemBase {
    private ElevatorSubsystem elevator;
    private CarriageSubsystem carriage;
    private ElbowSubsystem elbow;
    private WristSubsystem wrist;

    public static abstract class SafetyLogic {
        public static enum State {
            start, low, mid, loading, high
        }

        public static class PositionConstants {
            public final double low;
            public final double mid;
            public final double loading;
            public final double high;
            public final double start;

            public PositionConstants(double start,
                    double low,
                    double mid,
                    double high,
                    double loading) {
                this.low = low;
                this.mid = mid;
                this.loading = loading;
                this.high = high;
                this.start = start;
            }
        }

        protected PIDController mainPid;
        protected final double position;
        protected final State state;

        public SafetyLogic(State state) {
            this.state = state;
            PositionConstants constants = get_constants();
            switch (state) {
                case high:
                    position = constants.high;
                    break;
                case loading:
                    position = constants.loading;
                    break;
                case low:
                    position = constants.low;
                    break;
                case mid:
                    position = constants.mid;
                    break;
                case start:
                default:
                    position = constants.start;
                    break;

            }
        }

        protected abstract PositionConstants get_constants();

        public abstract double stateCalculate(double speed, double elbowPosition, double wristPosition,
                double elevatorPosition, double carriagePosition);

    }

    /** Creates a new SubsystemGroup. */
    public SubsystemGroup(ElevatorSubsystem elevator, CarriageSubsystem carriage, ElbowSubsystem elbow,
            WristSubsystem wrist) {
        this.elevator = elevator;
        this.carriage = carriage;
        this.elbow = elbow;
        this.wrist = wrist;
    }

    public void stopAll() {
        elevator.stop();
        carriage.stop();
        elbow.stop();
        wrist.stop();
    }

    public void reset() {
        elevator.reset();
    }

    public Command lowPosCommand(double speed) {
        var retval = runOnce(() -> reset()).andThen(runEnd(() -> lowPosition(speed), () -> stopAll()));
        retval.addRequirements(wrist, elbow, elevator, carriage);
        return retval;

    }

    public Command midPosConeCommand(double speed) {
        var retval = runOnce(() -> reset()).andThen(runEnd(() -> midPosition(speed), () -> stopAll()));
        retval.addRequirements(wrist, elbow, elevator, carriage);
        return retval;
    }

    public Command highPosCommand(double speed) {
        var retval = runOnce(() -> reset()).andThen(runEnd(() -> highPosition(speed), () -> stopAll()));
        retval.addRequirements(wrist, elbow, elevator, carriage);
        return retval;
    }

    public Command startingPosCommand(double speed) {
        var retval = runOnce(() -> reset()).andThen(runEnd(() -> startPosition(speed), () -> stopAll()));
        retval.addRequirements(wrist, elbow, elevator, carriage);
        return retval;
    }

    public Command loadingPosCommand(double speed) {
        var retval = runOnce(() -> reset()).andThen(runEnd(() -> loadingPosition(speed), () -> stopAll()));
        retval.addRequirements(wrist, elbow, elevator, carriage);
        return retval;
    }

    private void calculatePosition(SafetyLogic.State state, double speed) {
        double elevatorPosition = elevator.getElevatorPosition();
        double carriagePosition = carriage.getCarriagePosition();
        double elbowPosition = elbow.getElbowRotationPosition();
        double wristPosition = wrist.getWristRotationPosition();

        double elevatorSpeed = (new ElevatorState(state)).stateCalculate(speed, elbowPosition, wristPosition,
                elevatorPosition,
                carriagePosition) * speed;
        double carriageSpeed = (new CarriageState(state)).stateCalculate(speed, elbowPosition,
                wristPosition,
                elevatorPosition,
                carriagePosition) * speed;
        double elbowSpeed = (new ElbowState(state)).stateCalculate(speed, elbowPosition, wristPosition,
                elevatorPosition,
                carriagePosition) * speed;
        double wristSpeed = (new WristState(state)).stateCalculate(speed, elbowPosition, wristPosition,
                elevatorPosition,
                carriagePosition) * speed;

        elevator.runElevatorMotor(elevatorSpeed);
        carriage.runCarriageMotor(carriageSpeed);
        elbow.rotateElbow(elbowSpeed);
        wrist.rotateWrist(wristSpeed);
    }

    public void startPosition(double speed) {
        calculatePosition(State.start, speed);
    }

    public void lowPosition(double speed) {
        calculatePosition(State.low, speed);
    }

    public void midPosition(double speed) {
        calculatePosition(State.mid, speed);
    }

    public void loadingPosition(double speed) {
        calculatePosition(State.loading, speed);
    }

    public void highPosition(double speed) {
        calculatePosition(State.high, speed);
    }

    @Override
    public void periodic() {
        // This method will be called once per scheduler run
    }
}
