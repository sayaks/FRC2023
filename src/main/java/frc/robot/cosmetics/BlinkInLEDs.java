// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.cosmetics;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.CosmeticConstants;

public class BlinkInLEDs extends SubsystemBase {
    public final Spark lightController = new Spark(CosmeticConstants.LIGHT_ID);
    private ShuffleboardTab tab = Shuffleboard.getTab("LED");
    private GenericEntry lightColor = tab.add("led color", CosmeticConstants.SOLID_YELLOW_VALUE).getEntry();

    /** Creates a new lights. */
    public BlinkInLEDs() {
    }

    public void setYellow() {
        lightController.set(CosmeticConstants.SOLID_YELLOW_VALUE);
    }

    public void setPurple() {
        lightController.set(CosmeticConstants.SOLID_PURPLE_VALUE);
    }

    @Override
    public void periodic() {
        // This method will be called once per scheduler run
        // lightController.set(lightColor.getDouble(CosmeticConstants.SOLID_YELLOW_VALUE));
    }
}
