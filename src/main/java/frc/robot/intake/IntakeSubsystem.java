// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.intake;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.IntakeConstants;

public class IntakeSubsystem extends SubsystemBase {
    private final CANSparkMax motor = new CANSparkMax(IntakeConstants.MOTOR_ID, MotorType.kBrushless);
    private final DoubleSolenoid solenoid = new DoubleSolenoid(
            IntakeConstants.PNEUMATICS_HUB_ID,
            PneumaticsModuleType.REVPH,
            IntakeConstants.SOLENOID_FORWARD_ID,
            IntakeConstants.SOLENOID_BACKWARD_ID);
    private final DigitalInput intakeSensor = new DigitalInput(IntakeConstants.INTAKE_SENSOR_ID);
    private final Compressor airCompressor = new Compressor(IntakeConstants.PNEUMATICS_HUB_ID,
            PneumaticsModuleType.REVPH);
    private boolean isOpen = false;

    public IntakeSubsystem() {
        airCompressor.enableDigital();
    }

    public Command runMotor(final double speed) {
        return runEnd(() -> motor.set(speed), () -> motor.set(0));

    }

    public Command intake(final double speed) {
        return runMotor(isOpen ? speed : speed).until(this::getSensor); // TODO: CHANGE EVENTUALLY MAYBE SPEED
                                                                        // MULTIPLIER
    }

    public Command setSolenoid(final DoubleSolenoid.Value value) {
        return runOnce(() -> {
            solenoid.set(value);
            isOpen = value == DoubleSolenoid.Value.kForward;
        });
    }

    private boolean getSensor() {
        return !intakeSensor.get();
    }

    private final NetworkTableEntry sensorEntry = NetworkTableInstance.getDefault().getEntry("157/Intake/Sensor");

    @Override
    public void periodic() {
        // sensorEntry.setBoolean(getSensor());
        // System.out.println(getSensor());
    }

    public Command ejectCargo() {
        return runMotor(-1).withTimeout(2);
    }
}
