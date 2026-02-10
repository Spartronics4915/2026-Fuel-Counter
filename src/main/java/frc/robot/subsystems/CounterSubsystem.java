package frc.robot.subsystems;


import java.util.function.BooleanSupplier;

import au.grapplerobotics.LaserCan;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.CounterConstants;

public class CounterSubsystem extends SubsystemBase{

  LaserCan can1 = new LaserCan(CounterConstants.CAN1_ID);
  public int totalCount = 0;
 
  public CounterSubsystem(){
    SmartDashboard.putNumber("count", 0);
    SmartDashboard.putBoolean("Reset Count", false);
    bindCommands();
  }
  
  private int getDistance(){
    if(can1.getMeasurement() == null ){
      return 999;
    } else {
      return can1.getMeasurement().distance_mm;
    }
  }
 

  BooleanSupplier ball = () -> {
    if( 0 <= getDistance() && getDistance() < CounterConstants.BALL_DETECT_DISTANCE ){
      return true;
    }else{
      return false;
    }
  };

  
  Trigger ballTrig = new Trigger(ball);

  private void bindCommands(){
    ballTrig.onFalse(runOnce(()-> {totalCount += 1;}));
    
  }

  private void resetChecker(){
    if(SmartDashboard.getBoolean("Reset Count", false)){
      totalCount = 0;
      SmartDashboard.putBoolean("Reset Count", false);
    }
  }

  @Override
  public void periodic() {
    
    
    
    SmartDashboard.putNumber("count",totalCount);
    SmartDashboard.putNumber("Can Distance", getDistance());
    resetChecker();


    
  }

}
