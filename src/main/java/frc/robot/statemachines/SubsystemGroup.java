// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.statemachines;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.statemachines.ElevatorSubsystem.ElevatorState;
import frc.robot.statemachines.SafetyLogic.State;
import frc.robot.statemachines.CarriageSubsystem.CarriageState;
import frc.robot.statemachines.WristSubsystem.WristState;
import frc.robot.statemachines.ElbowSubsystem.ElbowState;

public class SubsystemGroup extends SubsystemBase {
    private ElevatorSubsystem elevator;
    private CarriageSubsystem carriage;
    private ElbowSubsystem elbow;
    private WristSubsystem wrist;

    /**
     * A group of {@link SafetyLogic safety logics} in a given state. To perform
     * operations on all the safety states at once.
     */
    private class SafetyLogicGroup {
        public final ElevatorState elevatorState;
        public final CarriageState carriageState;
        public final ElbowState elbowState;
        public final WristState wristState;
        public final State state;

        public SafetyLogicGroup(State state) {
            this.elevatorState = new ElevatorState(state);
            this.carriageState = new CarriageState(state);
            this.elbowState = new ElbowState(state);
            this.wristState = new WristState(state);
            this.state = state;
        }

        /**
         * Move to the position determined by the {@link SafetyLogicGroup#state state}
         * of this group.
         *
         * @param speed What speed to move at.
         */
        public void run(double speed) {
            double elevatorPosition = elevator.getElevatorPosition();
            double carriagePosition = carriage.getCarriagePosition();
            double elbowPosition = elbow.getElbowRotationPosition();
            double wristPosition = wrist.getWristRotationPosition();

            double elevatorSpeed = elevatorState.stateCalculate(speed, elbowPosition, wristPosition,
                    elevatorPosition,
                    carriagePosition) * speed;
            double carriageSpeed = carriageState.stateCalculate(speed, elbowPosition,
                    wristPosition,
                    elevatorPosition,
                    carriagePosition) * speed;
            double elbowSpeed = elbowState.stateCalculate(speed, elbowPosition, wristPosition,
                    elevatorPosition,
                    carriagePosition) * speed;
            double wristSpeed = wristState.stateCalculate(speed, elbowPosition, wristPosition,
                    elevatorPosition,
                    carriagePosition) * speed;

            elevator.runElevatorMotor(elevatorSpeed);
            carriage.runCarriageMotor(carriageSpeed);
            elbow.rotateElbow(elbowSpeed);
            wrist.rotateWrist(wristSpeed);
        }
    }

    private final SafetyLogicGroup lowGroup = new SafetyLogicGroup(State.low);
    private final SafetyLogicGroup midGroup = new SafetyLogicGroup(State.mid);
    private final SafetyLogicGroup loadingGroup = new SafetyLogicGroup(State.loading);
    private final SafetyLogicGroup highGroup = new SafetyLogicGroup(State.high);
    private final SafetyLogicGroup startGroup = new SafetyLogicGroup(State.start);

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

    /**
     * Move towards the low position.
     *
     * @param speed What speed to move at
     */
    public Command lowPosCommand(double speed) {
        var retval = runOnce(() -> reset()).andThen(runEnd(() -> lowGroup.run(speed), () -> stopAll()));
        retval.addRequirements(wrist, elbow, elevator, carriage);
        return retval;
    }

    /**
     * Move towards the mid position.
     *
     * @param speed What speed to move at
     */
    public Command midPosConeCommand(double speed) {
        var retval = runOnce(() -> reset()).andThen(runEnd(() -> midGroup.run(speed), () -> stopAll()));
        retval.addRequirements(wrist, elbow, elevator, carriage);
        return retval;
    }

    /**
     * Move towards the high position.
     *
     * @param speed What speed to move at
     */
    public Command highPosCommand(double speed) {
        var retval = runOnce(() -> reset()).andThen(runEnd(() -> highGroup.run(speed), () -> stopAll()));
        retval.addRequirements(wrist, elbow, elevator, carriage);
        return retval;
    }

    /**
     * Move towards the start position.
     *
     * @param speed What speed to move at
     */
    public Command startingPosCommand(double speed) {
        var retval = runOnce(() -> reset()).andThen(runEnd(() -> startGroup.run(speed), () -> stopAll()));
        retval.addRequirements(wrist, elbow, elevator, carriage);
        return retval;
    }

    /**
     * Move towards the loading position.
     *
     * @param speed What speed to move at
     */
    public Command loadingPosCommand(double speed) {
        var retval = runOnce(() -> reset()).andThen(runEnd(() -> loadingGroup.run(speed), () -> stopAll()));
        retval.addRequirements(wrist, elbow, elevator, carriage);
        return retval;
    }

    @Override
    public void periodic() {
        // This method will be called once per scheduler run
    }
}
