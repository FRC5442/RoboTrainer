// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.drive.Vector2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.SharedMethods;

public class SwerveGroup extends SubsystemBase {
  
  SwerveModule frontRightModule;
  SwerveModule frontLeftModule;
  SwerveModule backLeftModule;
  SwerveModule backRightModule;

  double convertedGyroAngle = 0;

  public SwerveGroup() {
    frontRightModule = RobotContainer.frontRightModule;
    frontLeftModule = RobotContainer.frontLeftModule;
    backLeftModule = RobotContainer.backLeftModule;
    backRightModule = RobotContainer.backRightModule;
  }

  public void moveCrab(Vector2d translation, double rotation) {
    double yawAngle = RobotContainer.navX.getAngle()/* - Constants.GYRO_OFFSET*/;
    SmartDashboard.putNumber("Gyro Angle: ", getConvertedGyroAngle());

    double joystickAngle = (Math.atan2(-translation.y, translation.x) * (180/Math.PI)) + 180 + yawAngle;

    if (Math.abs(translation.magnitude()) > Constants.JOYSTICK_DEAD_ZONE) {
      frontRightModule.move(translation.magnitude(), joystickAngle);
      frontLeftModule.move(translation.magnitude(), joystickAngle);
      backLeftModule.move(translation.magnitude(), joystickAngle);
      backRightModule.move(translation.magnitude(), joystickAngle);
    }
    else if (Math.abs(rotation) > Constants.JOYSTICK_DEAD_ZONE) {
      frontLeftModule.move(rotation, 225);
      backRightModule.move(rotation, 45);

      frontRightModule.move(rotation, 135);
      backLeftModule.move(rotation, 315);
    }
    else {
      frontRightModule.stop();
      frontLeftModule.stop();
      backLeftModule.stop();
      backRightModule.stop();
    }
  }

  public void moveSwerve(Vector2d translation, double rotation) {
    double gyroRadians = getConvertedGyroAngle() * (Math.PI / 180); //in radians
    SmartDashboard.putNumber("Gyro Angle: ", getConvertedGyroAngle());

    double STR = translation.x;
    double FWD = translation.y;
    double RCW = -rotation * ((-0.75 * translation.magnitude()) + 1);
    /**
     * rotation linearly adjusted for translation speed
     * states that the rotation is 1x is the translation speed is 0
     *  and 0.25x is the translation speed = full (1)
     *  based on the equation of a line (0.75x + 1), where x is the translation speed
    */
    
    double temp = (FWD * Math.cos(gyroRadians)) + (STR * Math.sin(gyroRadians));
    STR = (-FWD * Math.sin(gyroRadians)) + (STR * Math.cos(gyroRadians));
    FWD = temp;
    

    if (Math.abs(translation.magnitude()) <= Constants.JOYSTICK_DEAD_ZONE) {
      FWD = 0;
      STR = 0;
    }
    if (Math.abs(rotation) <= Constants.JOYSTICK_DEAD_ZONE) RCW = 0;

    double A = STR - RCW * (Constants.ROBOT_LENGTH / Constants.ROBOT_RADIUS);
    double B = STR + RCW * (Constants.ROBOT_LENGTH / Constants.ROBOT_RADIUS);
    double C = FWD - RCW * (Constants.ROBOT_WIDTH / Constants.ROBOT_RADIUS);
    double D = FWD + RCW * (Constants.ROBOT_WIDTH / Constants.ROBOT_RADIUS);

    //B and C
    double frontRightSpeed = getMovementAttributes(A, C)[0]; //good
    double frontRightAngle = getMovementAttributes(A, C)[1];
    double maxSpeed = frontRightSpeed;

    //B and D
    double frontLeftSpeed = getMovementAttributes(A, D)[0]; 
    double frontLeftAngle = getMovementAttributes(A, D)[1];
    if (frontLeftSpeed > maxSpeed) frontLeftSpeed = maxSpeed;

    //A and D
    double backLeftSpeed = getMovementAttributes(B, D)[0]; //good
    double backLeftAngle = getMovementAttributes(B, D)[1];
    if (backLeftSpeed > maxSpeed) backLeftSpeed = maxSpeed;

    //A and C - Back Right
    double backRightSpeed = getMovementAttributes(B, C)[0];
    double backRightAngle = getMovementAttributes(B, C)[1];
    if (backRightSpeed > maxSpeed) backRightSpeed = maxSpeed;

    if (Math.abs(translation.magnitude()) > Constants.JOYSTICK_DEAD_ZONE || Math.abs(rotation) > Constants.JOYSTICK_DEAD_ZONE) {
      frontRightModule.move(frontRightSpeed, frontRightAngle);
      frontLeftModule.move(frontLeftSpeed, frontLeftAngle);
      backLeftModule.move(backLeftSpeed, backLeftAngle);
      backRightModule.move(backRightSpeed, backRightAngle);
    }
    else {
      frontRightModule.stop();
      frontLeftModule.stop();
      backLeftModule.stop();
      backRightModule.stop();
    }
  }

  public void calibrate() {
    frontRightModule.calibrate();
    frontLeftModule.calibrate();
    backLeftModule.calibrate();
    backRightModule.calibrate();
  }

  public double[] getMovementAttributes(double c1, double c2) {
    double speed = Math.sqrt(Math.pow(c1, 2) + Math.pow(c2, 2));
    double angle = Math.atan2(c1, c2) * (180 / Math.PI) + 90;

    return new double[] { speed, angle };
  }

  public void switchDriveState(Constants.DRIVE_STATE driveState) {
    frontRightModule.switchTranslationMod(driveState.getValue());
    frontLeftModule.switchTranslationMod(driveState.getValue());
    backLeftModule.switchTranslationMod(driveState.getValue());
    backRightModule.switchTranslationMod(driveState.getValue());
  }

  public double getConvertedGyroAngle() {
    double rawGyroAngle = (RobotContainer.navX.getAngle() + Constants.GYRO_OFFSET); //in degrees
    double convertedRawGyroAngle = ((360 - rawGyroAngle + 90) % 360);
    if (convertedRawGyroAngle < 0) {
      return SharedMethods.roundTo(360 + convertedRawGyroAngle, 0);
    }
    else {
      return SharedMethods.roundTo(convertedRawGyroAngle, 0);
    }
  }

  @Override
  public void periodic() {
  }
}
