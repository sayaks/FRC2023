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
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.WristConstants;
import frc.robot.Constants.WristData;
import frc.robot.input.DriverInputs;
import frc.robot.lib.NumberUtil;

public class WristSubsystem extends SubsystemBase {

    private final CANSparkMax wristMotor = new CANSparkMax(WristConstants.MOTOR_ID, MotorType.kBrushless);
    private final Counter wristAbsEncoder = new Counter(Mode.kSemiperiod);
    private double wristSpeed = 0.0;
    private Double positiveInfinity = Double.POSITIVE_INFINITY;
    private Double negativeInfinity = Double.NEGATIVE_INFINITY;

    public WristSubsystem() {
        wristMotor.setInverted(true);
        wristMotor.setIdleMode(IdleMode.kBrake);
        wristAbsEncoder.setSemiPeriodMode(true);
        wristAbsEncoder.setUpSource(WristConstants.ABS_ENCODER_ROTATION_ID);
        wristAbsEncoder.reset();
    }

    public Command runWrist(final DriverInputs inputs) {
        return runEnd(() -> {
            final double speed = inputs.axis(DriverInputs.rotateWrist).get();
            final double position = getWristRotationPosition();
            if (position != positiveInfinity && position != negativeInfinity) {
                rotateWrist(speed);

            } else {
                wristMotor.set(speed);
            }
        }, () -> rotateWrist(0));
    }

    public Command runWristSpeed(final double speed) {
        return runEnd(() -> rotateWrist(speed), () -> rotateWrist(0));
    }

    public Command stopWrist() {
        return runOnce(() -> rotateWrist(0));
    }

    public double getWristRotationPosition() {
        return NumberUtil.ticksToDegs(wristAbsEncoder.getPeriod());
    }

    public void rotateWrist(final double speed) {
        wristSpeed = speed;

        final double limitedSpeed = WristConstants.ROTATE_LIMITS.limitMotionWithinRange(
                speed, getWristRotationPosition());
        wristMotor.set(limitedSpeed);
    }

    public void test() {
        final var testTable = table.getSubTable("Test1");
        final var output = testTable.getEntry("Output");

        final var speedEntry = testTable.getEntry("Speed Input");
        speedEntry.setDefaultNumber(0);
        final var speed = speedEntry.getNumber(0).doubleValue();

        final var encoderEntry = testTable.getEntry("Encoder Input");
        encoderEntry.setDefaultNumber(0);
        final var position = encoderEntry.getNumber(0).doubleValue();

        final double limitedSpeed = WristConstants.ROTATE_LIMITS.limitMotionWithinRange(speed, position);
        output.setNumber(limitedSpeed);
    }

    public void stop() {
        runWristSpeed(0);
    }

    private final NetworkTable table = NetworkTableInstance.getDefault().getTable("157/Arm");

    @Override
    public void periodic() {
        table.getEntry("Wrist").setNumber(getWristRotationPosition());
        table.getEntry("WristSpeed").setNumber(wristSpeed);
        test();
    }

    public final Command turnDownToPos(double pos) {
        return runWristSpeed(-0.3).until(() -> getWristRotationPosition() < pos);
    }

    public static class WristState extends SafetyLogic<WristData> {

        /** The smallest value the elbow can have for the wrist to move down. */
        public final double minElbowPos;

        public WristState(State state) {
            super(state);
            this.minElbowPos = this.data.posMinArm;
            this.mainPid = new PIDController(0.01, 0, 0);
        }

        @Override
        public double stateCalculate(double speed, double elbowPosition, double wristPosition, double elevatorPosition,
                double carriagePosition) {
            // Runs the wrist for the states
            double pidVal = mainPid.calculate(wristPosition, position);
            if (elbowPosition > this.minElbowPos || pidVal > 0) {
                return pidVal;
            }

            return 0;
        }

        @Override
        protected SafetyConstants<WristData> get_constants() {
            return WristConstants.SINGLETON;
        }
    }

}
