// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.Constants.AutoConstants;
import frc.robot.drive.DriveSubsystem;
import frc.robot.drive.FullDrive;
import frc.robot.elbow.ElbowSubsystem;
import frc.robot.input.DriverInputs;
import frc.robot.intake.IntakeSubsystem;
import frc.robot.wrist.WristSubsystem;
import frc.robot.lift.CarriageSubsystem;
import frc.robot.lift.ElevatorSubsystem;
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

    private final DriverInputs driverInputs = new DriverInputs();

    /**
     * The container for the robot. Contains subsystems, OI devices, and commands.
     */
    public RobotContainer() {
        // Configure the trigger bindings
        configureBindings();

        driveSubsystem.setDefaultCommand(new FullDrive(driveSubsystem, driverInputs));

        wristSubsystem.setDefaultCommand(wristSubsystem.runWrist(driverInputs));
        elbowSubsystem.setDefaultCommand(elbowSubsystem.runElbow(driverInputs));

        elevatorSubsystem.setDefaultCommand(elevatorSubsystem.runElevator(driverInputs));
        carriageSubsystem.setDefaultCommand(carriageSubsystem.runCarriage(driverInputs));
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
        driverInputs.button(DriverInputs.TestingButton).whenPressed(wristSubsystem.turnDownToPos(180.0));
        driverInputs.button(DriverInputs.runIntakeMotorIn).whileHeld(intakeSubsystem.intake(1));
        driverInputs.button(DriverInputs.runIntakeMotorOut).whileHeld(intakeSubsystem.runMotor(-1));
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

    public Command WristDownThenEjectThenPoorlyDock() {
        return driveSubsystem.addGyroOffset(180.0f).andThen(wristSubsystem.turnDownToPos(180))
                .andThen(intakeSubsystem.ejectCargo().withTimeout(0.5))
                .andThen(runDistanceWithSpeeds(-0.5, 0.0, 3000.0).withTimeout(1.85))
                .andThen(driveSubsystem.driveRawDistanceCommand(
                        new ChassisSpeeds(0, 0, 0.001),
                        100000));
    }

}
