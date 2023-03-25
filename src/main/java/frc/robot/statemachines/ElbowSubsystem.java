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
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Counter.Mode;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.ElbowConstants;
import frc.robot.input.DriverInputs;
import frc.robot.lib.NumberUtil;
import frc.robot.statemachines.SubsystemGroup.SafetyLogic;

public class ElbowSubsystem extends SubsystemBase {

    private final CANSparkMax elbowMotor = new CANSparkMax(ElbowConstants.MOTOR_ID, MotorType.kBrushless);
    private final Counter elbowAbsEncoder = new Counter(Mode.kSemiperiod);
    private double elbowSpeed = 0.0;

    public ElbowSubsystem() {
        elbowMotor.setInverted(true);
        elbowMotor.setIdleMode(IdleMode.kBrake);
        final var tab = Shuffleboard.getTab("Encoder Debug");
        tab.addNumber("elbow position", this::getElbowRotationPosition);
        elbowAbsEncoder.setSemiPeriodMode(true);
        elbowAbsEncoder.setUpSource(ElbowConstants.ABS_ENCODER_ROTATION_ID);
        elbowAbsEncoder.reset();
    }

    public Command runElbow(final DriverInputs inputs) {
        return runEnd(() -> {
            final double speed = inputs.axis(DriverInputs.rotateElbow).get();
            rotateElbow(speed);
        }, () -> rotateElbow(0));
    }

    public void stop() {
        rotateElbow(0);
    }

    public double getElbowRotationPosition() {
        return 360 - NumberUtil.ticksToDegs(elbowAbsEncoder.getPeriod());
    }

    public void rotateElbow(final double speed) {
        elbowSpeed = speed;

        final double limitedSpeed = ElbowConstants.ROTATE_LIMITS.limitMotionWithinRange(
                speed, getElbowRotationPosition());
        elbowMotor.set(limitedSpeed);
    }

    private final NetworkTable table = NetworkTableInstance.getDefault().getTable("157/Elbow");

    public static class ElbowState extends SafetyLogic {
        private double minCarriagePos;
        private double minWristPos;

        public ElbowState(State state) {
            super(state);
            switch (state) {
                case high:
                    this.minCarriagePos = ElbowConstants.HIGH_POS_MIN_CARRIAGE;
                    this.minWristPos = ElbowConstants.HIGH_POS_MIN_WRIST;
                    break;
                case loading:
                    this.minCarriagePos = ElbowConstants.LOADING_POS_MIN_CARRIAGE;
                    this.minWristPos = ElbowConstants.LOADING_POS_MIN_WRIST;
                    break;
                case low:
                    this.minCarriagePos = ElbowConstants.LOW_POS_MIN_CARRIAGE;
                    this.minWristPos = ElbowConstants.LOW_POS_MIN_WRIST;
                    break;
                case mid:
                    this.minCarriagePos = ElbowConstants.MID_POS_MIN_CARRIAGE;
                    this.minWristPos = ElbowConstants.MID_POS_MIN_WRIST;
                    break;
                case start:
                default:
                    this.minCarriagePos = ElbowConstants.START_POS_MIN_CARRIAGE;
                    this.minWristPos = ElbowConstants.START_POS_MIN_WRIST;
                    break;
            }
            mainPid = new PIDController(0.03, 0, 0);
        }

        @Override
        public double stateCalculate(double speed, double elbowPosition, double wristPosition, double elevatorPosition,
                double carriagePosition) {
            // This switch case stops the elbow from moving when going towards high position
            // if it is too low, as to stop the elbow from colliding into a pole
            var s = 0.0;
            switch (this.state) {
                case start:
                case low:
                case mid:
                case loading:
                    if (carriagePosition > this.minCarriagePos && wristPosition > this.minWristPos) {
                        s = mainPid.calculate(elbowPosition, position);
                    }
                    break;
                case high:
                    if (elevatorPosition < ElbowConstants.SAFETY_ELEVATOR_LIMIT_HIGH
                            || elbowPosition < position) {
                        s = mainPid.calculate(elbowPosition, position);
                    }
                    break;
                default:
                    break;
            }
            return s < 0.75 ? s : 0.75;
        }

        @Override
        protected PositionConstants get_constants() {
            return new ElbowConstants();
        }
    }

    @Override
    public void periodic() {
        table.getEntry("Elbow").setNumber(getElbowRotationPosition());
        table.getEntry("ElbowSpeed").setNumber(elbowSpeed);
    }

}
