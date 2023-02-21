// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.lift;

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
import frc.robot.input.DriverInputs;
import frc.robot.statemachines.SubsystemGroup.SafetyLogic;

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

    public static class CarriageState implements SafetyLogic {
        private static PIDController mainPID = new PIDController(0.01, 0, 0.00);

        private double carriagePosition;
        private PIDController carriageDownPid;
        private PIDController carriageUpPid;
        private double minElbowPos;

        public CarriageState(final double carriagePosition, final PIDController carriageDownPid,
                final PIDController carriageUpPid, final double minElbowPos) {
            this.carriagePosition = carriagePosition;
            this.carriageDownPid = carriageDownPid;
            this.carriageUpPid = carriageUpPid;
            this.minElbowPos = minElbowPos;
        }

        public static final CarriageState start = new CarriageState(1000, mainPID, mainPID, 244);
        public static final CarriageState low = new CarriageState(2150, mainPID, mainPID, 155);
        public static final CarriageState mid = new CarriageState(2150, mainPID, mainPID, 215);
        public static final CarriageState loading = new CarriageState(2150, mainPID, mainPID, 215);
        public static final CarriageState high = new CarriageState(2150, mainPID, mainPID, 215);

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
            if (armPosition > this.minElbowPos) {
                // if (carriagePosition < this.carriagePosition - 100) {
                // return 0.2;
                // } else if (carriagePosition > this.carriagePosition + 100) {
                // return -0.2;
                //
                return this.carriageDownPid.calculate(carriagePosition, this.carriagePosition);
            }
            return 0;
        }

    }
}
