package frc.robot.subsystems;


import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.CounterConstants;

public class CounterSubsystem extends SubsystemBase{

  public int totalScore = 0;
  public int autoScore = 0;
  public int transitionScore = 0; 
  public int shift1Score = 0;
  public int shift2Score = 0;
  public int shift3Score = 0;
  public int shift4Score = 0;
  public int endgameScore = 0;

  private DigitalInput sensor1 = new DigitalInput(CounterConstants.SENSOR1_ID);
  private DigitalInput sensor2 = new DigitalInput(CounterConstants.SENSOR2_ID);
  private DigitalInput sensor3 = new DigitalInput(CounterConstants.SENSOR3_ID);
  private DigitalInput sensor4 = new DigitalInput(CounterConstants.SENSOR4_ID);
  
  BooleanSupplier sensor1On = () -> {return sensor1.get();};
  BooleanSupplier sensor2On = () -> {return sensor2.get();};
  BooleanSupplier sensor3On = () -> {return sensor3.get();};
  BooleanSupplier sensor4On = () -> {return sensor4.get();};

  public Trigger sensor1Trig = new Trigger(sensor1On);
  public Trigger sensor2Trig = new Trigger(sensor2On);
  public Trigger sensor3Trig = new Trigger(sensor3On);
  public Trigger sensor4Trig = new Trigger(sensor4On);

  BooleanSupplier resetClicked = () -> {return SmartDashboard.getBoolean("Reset Count", false);};
  public Trigger resetButton = new Trigger(resetClicked);

  public void resetCount(){
    totalScore = 0;
    autoScore = 0;
    transitionScore = 0;
    shift1Score = 0;
    shift2Score = 0;
    shift3Score = 0;
    shift4Score = 0;
    endgameScore = 0;
    SmartDashboard.putBoolean("Reset Count", false);
  }

  public void ballScored(){
    totalScore += 1;
    if( HubLightsSubsystem.state == "Auto"){
      autoScore += 1; 
    } else if(HubLightsSubsystem.state == "Transition"){
      transitionScore += 1;
    }else if(HubLightsSubsystem.state == "Shift1"){
      shift1Score += 1;
    }else if(HubLightsSubsystem.state == "Shift2"){
      shift2Score += 1;
    }else if(HubLightsSubsystem.state == "Shift3"){
      shift3Score += 1;
    }else if(HubLightsSubsystem.state == "Shift4"){
      shift4Score += 1;
    }else if(HubLightsSubsystem.state == "EndGame"){
      endgameScore += 1;
    }
  }

  private void bindCommands(){
    sensor1Trig.onTrue(runOnce(()-> {ballScored();}));
    sensor2Trig.onTrue(runOnce(()-> {ballScored();}));
    sensor3Trig.onTrue(runOnce(()-> {ballScored();}));
    sensor4Trig.onTrue(runOnce(()-> {ballScored();}));

    resetButton.onTrue(runOnce(() -> {resetCount();}));
  }

  public CounterSubsystem(){
    SmartDashboard.putNumber("Total Score", 0);
    SmartDashboard.putBoolean("Reset Count", false);
    bindCommands();
  }
  
  @Override
  public void periodic() {
    SmartDashboard.putNumber("Total Score",totalScore);
    SmartDashboard.putNumber("Auto Score", autoScore);
    SmartDashboard.putNumber("Transition Score", transitionScore);
    SmartDashboard.putNumber("Shift 1 Score", shift1Score);
    SmartDashboard.putNumber("Shift 2 Score", shift2Score);
    SmartDashboard.putNumber("Shift 3 Score", shift3Score);
    SmartDashboard.putNumber("Shift 4 Score", shift4Score);
    SmartDashboard.putNumber("Endgame Score", endgameScore);

    boolean[] outputs = {sensor1.get(), sensor2.get(), sensor3.get(), sensor4.get()};
    SmartDashboard.putBooleanArray("Sensor Values", outputs);
  }

}
