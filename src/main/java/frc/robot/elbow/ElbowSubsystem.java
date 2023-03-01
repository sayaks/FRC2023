// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.elbow;

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
import frc.robot.input.DriverInputs;
import frc.robot.lib.NumberUtil;
import frc.robot.statemachines.SubsystemGroup.SafetyLogic;

public class ElbowSubsystem extends SubsystemBase {

    private final CANSparkMax elbowMotor = new CANSparkMax(ElbowConstants.MOTOR_ID, MotorType.kBrushless);
    private final Counter elbowAbsEncoder = new Counter(Mode.kSemiperiod);
    private double elbowSpeed = 0.0;

    public ElbowSubsystem() {
        elbowMotor.setInverted(false);
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
        return NumberUtil.ticksToDegs(elbowAbsEncoder.getPeriod());
    }

    public void rotateElbow(final double speed) {
        elbowSpeed = speed;

        final double limitedSpeed = ElbowConstants.ROTATE_LIMITS.limitMotionWithinRange(
                speed, getElbowRotationPosition());
        elbowMotor.set(limitedSpeed);
    }

    private final NetworkTable table = NetworkTableInstance.getDefault().getTable("157/Elbow");

    public static class ElbowState implements SafetyLogic {

        private double elbowPosition;
        private static PIDController mainPID = new PIDController(0.03, 0, 0);
        private double minCarriagePos;
        private double minWristPos;
        private PIDController elbowDownPid;

        public ElbowState(final double elbowPosition, final PIDController elbowDownPid,
                final double minCarriagePos, double minWristPos) {
            this.elbowPosition = elbowPosition;
            this.elbowDownPid = elbowDownPid;
            this.minCarriagePos = minCarriagePos;
            this.minWristPos = minWristPos;
        }

        public static final ElbowState start = new ElbowState(ElbowConstants.startPos, mainPID,
                ElbowConstants.startPosMinCarriage,
                ElbowConstants.startPosMinWrist);
        public static final ElbowState low = new ElbowState(ElbowConstants.lowPos, mainPID,
                ElbowConstants.lowPosMinCarriage,
                ElbowConstants.lowPosMinWrist);
        public static final ElbowState mid = new ElbowState(ElbowConstants.midPos, mainPID,
                ElbowConstants.midPosMinCarriage,
                ElbowConstants.midPosMinWrist);
        public static final ElbowState loading = new ElbowState(ElbowConstants.loadingPos, mainPID,
                ElbowConstants.loadingPosMinCarriage, ElbowConstants.loadingPosMinWrist);
        public static final ElbowState high = new ElbowState(ElbowConstants.highPos, mainPID,
                ElbowConstants.highPosMinCarriage,
                ElbowConstants.highPosMinWrist);

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
        public double stateCalculate(double speed, double elbowPosition, double wristPosition, double elevatorPosition,
                double carriagePosition) {
            // TODO Auto-generated method stub

            if (carriagePosition > this.minCarriagePos && wristPosition > this.minWristPos) {
                return this.elbowDownPid.calculate(elbowPosition, this.elbowPosition);
            }
            return 0;
        }

    }

    @Override
    public void periodic() {
        table.getEntry("Elbow").setNumber(getElbowRotationPosition());
        table.getEntry("ElbowSpeed").setNumber(elbowSpeed);
    }

}
