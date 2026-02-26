package frc.robot.subsystems;


import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.CounterConstants;

public class CounterSubsystem extends SubsystemBase{

  public int totalCount = 0;

  private DigitalInput sensor1 = new DigitalInput(CounterConstants.SENSOR1_ID);
  private DigitalInput sensor2 = new DigitalInput(CounterConstants.SENSOR2_ID);
  private DigitalInput sensor3 = new DigitalInput(CounterConstants.SENSOR3_ID);
  private DigitalInput sensor4 = new DigitalInput(CounterConstants.SENSOR4_ID);
  
  BooleanSupplier sensor1On = () -> {return sensor1.get();};
  BooleanSupplier sensor2On = () -> {return sensor2.get();};
  BooleanSupplier sensor3On = () -> {return sensor3.get();};
  BooleanSupplier sensor4On = () -> {return sensor4.get();};

  Trigger sensor1Trig = new Trigger(sensor1On);
  Trigger sensor2Trig = new Trigger(sensor2On);
  Trigger sensor3Trig = new Trigger(sensor3On);
  Trigger sensor4Trig = new Trigger(sensor4On);

  BooleanSupplier resetClicked = () -> {
    if(SmartDashboard.getBoolean("Reset Count", false)){
      return true;
     }else{
      return false;
     }
  };
  Trigger resetButton = new Trigger(resetClicked);

  public void resetCount(){
    totalCount = 0;
    SmartDashboard.putBoolean("Reset Count", false);
  }

  private void bindCommands(){
    sensor1Trig.onFalse(runOnce(()-> {totalCount += 1;}));
    sensor2Trig.onFalse(runOnce(()-> {totalCount += 1;}));
    sensor3Trig.onFalse(runOnce(()-> {totalCount += 1;}));
    sensor4Trig.onFalse(runOnce(()-> {totalCount += 1;}));

    resetButton.onTrue(runOnce(() -> {resetCount();}));
  }

  public CounterSubsystem(){
    SmartDashboard.putNumber("count", 0);
    SmartDashboard.putBoolean("Reset Count", false);
    bindCommands();
  }
  
  @Override
  public void periodic() {
    SmartDashboard.putNumber("count",totalCount);

    boolean[] outputs = {sensor1.get(), sensor2.get(), sensor3.get(), sensor4.get()};
    SmartDashboard.putBooleanArray("Sensor Values", outputs);
  }

}
