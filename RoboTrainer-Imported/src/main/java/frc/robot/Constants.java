// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants.  This class should not be used for any other purpose.  All constants should be
 * declared globally (i.e. public static).  Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
    public static final double ROBOT_WIDTH = 28; //in inches
    public static final double ROBOT_LENGTH = 28; //in inches
    public static final double ROBOT_RADIUS = Math.sqrt(Math.pow(ROBOT_WIDTH, 2) + Math.pow(ROBOT_LENGTH, 2));
    
    public static final double JOYSTICK_DEAD_ZONE = 0.1; //joystick values 0-1

    public static final double ENCODER_OFFSET = 15.0; //in degrees
    public static double GYRO_OFFSET = 0; //in degrees

    public static enum DRIVE_STATE {
        HIGH_GEAR(0.5), LOW_GEAR(0.2);

        private double value;

        private DRIVE_STATE(double value) {
            this.value = value;
        }

        public double getValue() {
            return this.value;
        }
    }
}
