// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.statemachines;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.elbow.ElbowSubsystem;
import frc.robot.lift.CarriageSubsystem;
import frc.robot.lift.ElevatorSubsystem;
import frc.robot.wrist.WristSubsystem;

public class SubsystemGroup extends SubsystemBase {
    private ElevatorSubsystem elevator;
    private CarriageSubsystem carriage;
    private ElbowSubsystem elbow;
    private WristSubsystem wrist;

    private SafetyLogic elevatorStart = ElevatorSubsystem.ElevatorState.start;
    private SafetyLogic elevatorLow = ElevatorSubsystem.ElevatorState.low;
    private SafetyLogic elevatorMidCone = ElevatorSubsystem.ElevatorState.high;
    private SafetyLogic elevatorLoading = ElevatorSubsystem.ElevatorState.loading;
    private SafetyLogic elevatorHigh = ElevatorSubsystem.ElevatorState.highCone;

    private SafetyLogic carriageStart = CarriageSubsystem.CarriageState.start;
    private SafetyLogic carriageLow = CarriageSubsystem.CarriageState.low;
    private SafetyLogic carriageMidCone = CarriageSubsystem.CarriageState.high;
    private SafetyLogic carriageLoading = CarriageSubsystem.CarriageState.loading;
    private SafetyLogic carriageHigh = CarriageSubsystem.CarriageState.highCube;

    private SafetyLogic elbowStart = ElbowSubsystem.ElbowState.start;
    private SafetyLogic elbowLow = ElbowSubsystem.ElbowState.low;
    private SafetyLogic elbowMidCone = ElbowSubsystem.ElbowState.high;
    private SafetyLogic elbowLoading = ElbowSubsystem.ElbowState.loading;
    private SafetyLogic elbowHigh = ElbowSubsystem.ElbowState.highCone;

    private SafetyLogic wristStart = WristSubsystem.WristState.start;
    private SafetyLogic wristLow = WristSubsystem.WristState.low;
    private SafetyLogic wristMidCone = WristSubsystem.WristState.high;
    private SafetyLogic wristLoading = WristSubsystem.WristState.loading;
    private SafetyLogic wristHigh = WristSubsystem.WristState.highCone;

    public interface SafetyLogic {
        public SafetyLogic lowPosition();

        public SafetyLogic highPosition();

        public SafetyLogic loadingPosition();

        public SafetyLogic highConePosition();

        public SafetyLogic defaultPosition();

        public SafetyLogic midPosition();

        public double stateCalculate(double speed, double armPosition, double wristPosition,
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

    public void startPosition(double speed) {

        double elevatorPosition = elevator.getElevatorPosition();
        double carriagePosition = carriage.getCarriagePosition();
        double elbowPosition = elbow.getElbowRotationPosition();
        double wristPosition = wrist.getWristRotationPosition();

        double elevatorSpeed = elevatorStart.stateCalculate(speed, elbowPosition, wristPosition, elevatorPosition,
                carriagePosition) * speed;
        double carriageSpeed = carriageStart.stateCalculate(speed, elbowPosition, wristPosition, elevatorPosition,
                carriagePosition) * speed;
        double elbowSpeed = elbowStart.stateCalculate(speed, elbowPosition, wristPosition, elevatorPosition,
                carriagePosition) * speed;
        double wristSpeed = wristStart.stateCalculate(speed, elbowPosition, wristPosition, elevatorPosition,
                carriagePosition) * speed;

        elevator.runElevatorMotor(elevatorSpeed);
        carriage.runCarriageMotor(carriageSpeed);
        elbow.rotateElbow(elbowSpeed);
        wrist.rotateWrist(wristSpeed);
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
        var retval = runOnce(() -> reset()).andThen(runEnd(() -> midPositionCone(speed), () -> stopAll()));
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

    public void lowPosition(double speed) {
        double elevatorPosition = elevator.getElevatorPosition();
        double carriagePosition = carriage.getCarriagePosition();
        double elbowPosition = elbow.getElbowRotationPosition();
        double wristPosition = wrist.getWristRotationPosition();

        double elevatorSpeed = elevatorLow.stateCalculate(speed, elbowPosition, wristPosition, elevatorPosition,
                carriagePosition) * speed;
        double carriageSpeed = carriageLow.stateCalculate(speed, elbowPosition, wristPosition, elevatorPosition,
                carriagePosition) * speed;
        double elbowSpeed = elbowLow.stateCalculate(speed, elbowPosition, wristPosition, elevatorPosition,
                carriagePosition) * speed;
        double wristSpeed = wristLow.stateCalculate(speed, elbowPosition, wristPosition, elevatorPosition,
                carriagePosition) * speed;

        elevator.runElevatorMotor(elevatorSpeed);
        carriage.runCarriageMotor(carriageSpeed);
        elbow.rotateElbow(elbowSpeed);
        wrist.rotateWrist(wristSpeed);
    }

    public void midPositionCone(double speed) {
        double elevatorPosition = elevator.getElevatorPosition();
        double carriagePosition = carriage.getCarriagePosition();
        double elbowPosition = elbow.getElbowRotationPosition();
        double wristPosition = wrist.getWristRotationPosition();

        double elevatorSpeed = elevatorMidCone.stateCalculate(speed, elbowPosition, wristPosition, elevatorPosition,
                carriagePosition) * speed;
        double carriageSpeed = carriageMidCone.stateCalculate(speed, elbowPosition, wristPosition, elevatorPosition,
                carriagePosition) * speed;
        double elbowSpeed = elbowMidCone.stateCalculate(speed, elbowPosition, wristPosition, elevatorPosition,
                carriagePosition) * speed;
        double wristSpeed = wristMidCone.stateCalculate(speed, elbowPosition, wristPosition, elevatorPosition,
                carriagePosition) * speed;

        elevator.runElevatorMotor(elevatorSpeed);
        carriage.runCarriageMotor(carriageSpeed);
        elbow.rotateElbow(elbowSpeed);
        wrist.rotateWrist(wristSpeed);
    }

    public void loadingPosition(double speed) {
        double elevatorPosition = elevator.getElevatorPosition();
        double carriagePosition = carriage.getCarriagePosition();
        double elbowPosition = elbow.getElbowRotationPosition();
        double wristPosition = wrist.getWristRotationPosition();

        double elevatorSpeed = elevatorLoading.stateCalculate(speed, elbowPosition, wristPosition, elevatorPosition,
                carriagePosition) * speed;
        double carriageSpeed = carriageLoading.stateCalculate(speed, elbowPosition, wristPosition, elevatorPosition,
                carriagePosition) * speed;
        double elbowSpeed = elbowLoading.stateCalculate(speed, elbowPosition, wristPosition, elevatorPosition,
                carriagePosition) * speed;
        double wristSpeed = wristLoading.stateCalculate(speed, elbowPosition, wristPosition, elevatorPosition,
                carriagePosition) * speed;

        elevator.runElevatorMotor(elevatorSpeed);
        carriage.runCarriageMotor(carriageSpeed);
        elbow.rotateElbow(elbowSpeed);
        wrist.rotateWrist(wristSpeed);
    }

    public void highPosition(double speed) {
        double elevatorPosition = elevator.getElevatorPosition();
        double carriagePosition = carriage.getCarriagePosition();
        double elbowPosition = elbow.getElbowRotationPosition();
        double wristPosition = wrist.getWristRotationPosition();

        double elevatorSpeed = elevatorHigh.stateCalculate(speed, elbowPosition, wristPosition, elevatorPosition,
                carriagePosition) * speed;
        double carriageSpeed = carriageHigh.stateCalculate(speed, elbowPosition, wristPosition, elevatorPosition,
                carriagePosition) * speed;
        double elbowSpeed = elbowHigh.stateCalculate(speed, elbowPosition, wristPosition, elevatorPosition,
                carriagePosition) * speed;
        double wristSpeed = wristHigh.stateCalculate(speed, elbowPosition, wristPosition, elevatorPosition,
                carriagePosition) * speed;

        elevator.runElevatorMotor(elevatorSpeed);
        carriage.runCarriageMotor(carriageSpeed);
        elbow.rotateElbow(elbowSpeed);
        wrist.rotateWrist(wristSpeed);
    }

    @Override
    public void periodic() {
        // This method will be called once per scheduler run
    }
}
