//TODO: name comments better

// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.Constants.AutoConstants;
import frc.robot.drive.AutoBalance;
import frc.robot.drive.DriveSubsystem;
import frc.robot.drive.FullDrive;
import frc.robot.elbow.ElbowSubsystem;
import frc.robot.input.DriverInputs;
import frc.robot.intake.IntakeSubsystem;
import frc.robot.wrist.WristSubsystem;
import frc.robot.lift.CarriageSubsystem;
import frc.robot.lift.ElevatorSubsystem;
import frc.robot.statemachines.SubsystemGroup;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.Trigger;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in
 * the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of
 * the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
    // The robot's subsystems and commands are defined here...
    private final DriveSubsystem driveSubsystem = new DriveSubsystem();
    private final IntakeSubsystem intakeSubsystem = new IntakeSubsystem();
    private final WristSubsystem wristSubsystem = new WristSubsystem();
    private final ElbowSubsystem elbowSubsystem = new ElbowSubsystem();
    private final ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem();
    private final CarriageSubsystem carriageSubsystem = new CarriageSubsystem();
    private final SubsystemGroup group = new SubsystemGroup(elevatorSubsystem, carriageSubsystem, elbowSubsystem,
            wristSubsystem);

    private final DriverInputs driverInputs = new DriverInputs();

    /**
     * The container for the robot. Contains subsystems, OI devices, and commands.
     */
    public RobotContainer() {
        // Configure the trigger bindings
        configureBindings();
        CameraServer.startAutomaticCapture();

        driveSubsystem.setDefaultCommand(new FullDrive(driveSubsystem, driverInputs));

        wristSubsystem.setDefaultCommand(wristSubsystem.runWrist(driverInputs));
        elbowSubsystem.setDefaultCommand(elbowSubsystem.runElbow(driverInputs));

        elevatorSubsystem.setDefaultCommand(elevatorSubsystem.runElevator(driverInputs));
        carriageSubsystem.setDefaultCommand(carriageSubsystem.runCarriage(driverInputs));

        intakeSubsystem.setDefaultCommand(intakeSubsystem.runIntake(driverInputs));
    }

    /**
     * Use this method to define your trigger->command mappings. Triggers can be
     * created via the {@link Trigger#Trigger(java.util.function.BooleanSupplier)}
     * constructor with an arbitrary predicate, or via the named factories in {@link
     * edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for
     * {@link CommandXboxControllerXbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4ControllerPS4}
     * controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick}
     * Flight joysticks.
     */
    private void configureBindings() {
        driverInputs.button(DriverInputs.lowPosition).whileHeld(new AutoBalance(driveSubsystem));
        driverInputs.button(DriverInputs.midPosition).whileHeld(group.midPosCommand(1));
        driverInputs.button(DriverInputs.highPosition).whileHeld(group.highPosCommand(1));
        driverInputs.button(DriverInputs.startPosition).whileHeld(group.startingPosCommand(1));
        driverInputs.button(DriverInputs.setIntakeSolenoidForward)
                .whenPressed(intakeSubsystem.setSolenoid(DoubleSolenoid.Value.kForward));
        driverInputs.button(DriverInputs.setIntakeSolenoidBackward)
                .whenPressed(intakeSubsystem.setSolenoid(DoubleSolenoid.Value.kReverse));
    }

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *
     * @return the command to run in autonomous
     */
    public Command getAutonomousCommand() {
        return WristDownThenEjectThenPoorlyDock();
    }

    // TODO: NAMING, PLEASE

    public Command runDistance() {
        return driveSubsystem.resetDrivePositionCommand()
                .andThen(driveSubsystem.driveRawDistanceCommand(
                        AutoConstants.AUTO_SPEEDS,
                        AutoConstants.AUTO_DISTANCE));
    }

    public Command runDistanceWithSpeeds(double x, double y, double dist) {
        return driveSubsystem.resetDrivePositionCommand()
                .andThen(driveSubsystem.driveRawDistanceCommand(new ChassisSpeeds(x, y, 0), dist));
    }

    public Command WristDownThenEjectThenRunDistance() {
        return driveSubsystem.addGyroOffset(180.0f).andThen(wristSubsystem.turnDownToPos(180))
                .andThen(intakeSubsystem.ejectCargo().withTimeout(0.5))
                .andThen(runDistanceWithSpeeds(-0.3, 0.0, 3000.0).withTimeout(4.2));
    }

    public Command ejectThenRunDistance() {
        return intakeSubsystem.ejectCargo().withTimeout(1)
                .andThen(runDistance().withTimeout(4.2));
    }

    public Command WristDownThenEjectThenPoorlyDock() { // TODO: Don't change this name though, kinda cool.
        return driveSubsystem.addGyroOffset(180.0f).andThen(wristSubsystem.turnDownToPos(180))
                .andThen(intakeSubsystem.ejectCargo().withTimeout(0.5))
                .andThen(runDistanceWithSpeeds(-0.5, 0.0, 3000.0).withTimeout(1.75) /**
                                                                                     * change dis, no more time, only
                                                                                     * space
                                                                                     */
                )
                .andThen(driveSubsystem.driveRawDistanceCommand(
                        new ChassisSpeeds(0, 0, 0.001),
                        100000)/** TODO: Dat to */
                );
    }
    /*
     * min arm with low carriage: 227
     * max arm overall: 151
     * min arm overall: 255
     *
     *
     * max wrist: 258
     * min wrist(?): 165
     *
     * max carriage: 2348
     * min carriage: 952
     *
     * min elevator: 2034
     * max elevator: 550
     *
     */
}
