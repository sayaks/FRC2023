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
import edu.wpi.first.wpilibj.Counter.Mode;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ElbowConstants;
import frc.robot.Constants.ElbowData;
import frc.robot.input.DriverInputs;
import frc.robot.lib.NumberUtil;

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

    public static class ElbowState extends SafetyLogic<ElbowData> {
        public final double minCarriagePos;
        public final double minWristPos;

        public ElbowState(State state) {
            super(state);
            this.minCarriagePos = this.data.minCarriagePos;
            this.minWristPos = this.data.minWristPos;
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
        protected SafetyConstants<ElbowData> get_constants() {
            return ElbowConstants.SINGLETON;
        }
    }

    @Override
    public void periodic() {
        table.getEntry("Elbow").setNumber(getElbowRotationPosition());
        table.getEntry("ElbowSpeed").setNumber(elbowSpeed);
    }

}
