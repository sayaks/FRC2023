// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.statemachines;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.CarriageConstants;
import frc.robot.Constants.CarriageData;
import frc.robot.input.DriverInputs;

public class CarriageSubsystem extends SubsystemBase {
    private final CANSparkMax carriageMotor = new CANSparkMax(CarriageConstants.CARRIAGE_MOTOR_ID,
            MotorType.kBrushless);
    private final AnalogInput carriage10Pot = new AnalogInput(CarriageConstants.CARRIAGE_ANALOG_ID);
    private double carriageSpeed = 0.0;

    /** Creates a new CarriageSubsystem. */
    public CarriageSubsystem() {
        carriageMotor.setIdleMode(IdleMode.kBrake);
    }

    public Command runCarriage(final DriverInputs inputs) {
        return runEnd(() -> {
            final double speed = inputs.axis(DriverInputs.carriage).get();
            runCarriageMotor(speed);
        }, () -> runCarriageMotor(0));
    }

    public void stop() {
        runCarriageMotor(0);
    }

    public double getCarriagePosition() {
        return 4000 - carriage10Pot.getValue(); // is a hack, idk keep it I guess?
    }

    public void runCarriageMotor(final double speed) {
        carriageSpeed = speed;

        final double carriageLimits = CarriageConstants.CARRIAGE_LIMITS.limitMotionWithinRange(speed,
                getCarriagePosition());
        carriageMotor.set(carriageLimits);
    }

    private final NetworkTable table = NetworkTableInstance.getDefault().getTable("157/Carriage");

    @Override
    public void periodic() {
        table.getEntry("Carriage").setNumber(getCarriagePosition());
        table.getEntry("CarriageSpeed").setNumber(carriageSpeed);
    }

    public static class CarriageState extends SafetyLogic<CarriageData> {
        public final double minElbowPos;

        public CarriageState(State state) {
            super(state);
            this.minElbowPos = this.data.minElbowPos;
            this.mainPid = new PIDController(0.01, 0, 0);
        }

        @Override
        public double stateCalculate(double speed, double elbowPosition, double wristPosition, double elevatorPosition,
                double carriagePosition) {
            // Ensures the carriage is safe to move (elbow won't get crushed)
            if (elbowPosition > this.minElbowPos) {
                // if (carriagePosition < this.carriagePosition - 100) {
                // return 0.2;
                // } else if (carriagePosition > this.carriagePosition + 100) {
                // return -0.2;
                //
                return mainPid.calculate(carriagePosition, this.position);
            }
            return 0;
        }

        @Override
        protected SafetyConstants<CarriageData> get_constants() {
            return CarriageConstants.SINGLETON;
        }
    }
}
