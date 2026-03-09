package frc.robot.subsystems;

import java.lang.Thread.State;
import java.util.Map;
import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;

import static edu.wpi.first.units.Units.Percent;
import static edu.wpi.first.units.Units.Second;
import static frc.robot.Constants.LightConstants.*;
public class HubLightsSubsystem extends SubsystemBase{

    private BooleanSupplier matchSequenceStarted = () -> { return SmartDashboard.getBoolean("Start Hub Sequence", false);};
    private Trigger matchSequenceStartedTrig = new Trigger(matchSequenceStarted);

    private AddressableLED ledStrip = new AddressableLED(LED_PORT);
    private AddressableLEDBuffer ledBuffer =  new AddressableLEDBuffer(LED_LENGTH);
    public static String state = "None";

    private LEDPattern blue = LEDPattern.solid(Color.kBlue);
    private LEDPattern red = LEDPattern.solid(Color.kRed);
    private LEDPattern green = LEDPattern.solid(Color.kGreen);
    private LEDPattern black = LEDPattern.solid(Color.kBlack);
    private LEDPattern transition = LEDPattern.steps(Map.of(
        0, Color.kRed, 0.1, Color.kBlue, 0.2, Color.kRed, 0.3, Color.kBlue,0.4, Color.kRed, 
        0.5, Color.kBlue, 0.6, Color.kRed, 0.7, Color.kBlue,0.8, Color.kRed, 0.9, Color.kBlue));
    private LEDPattern spartyBase = LEDPattern.steps(Map.of(
        0, Color.kGold, 0.1, Color.kBlue, 0.2, Color.kGold, 0.3, Color.kBlue,0.4, Color.kGold, 
        0.5, Color.kBlue, 0.6, Color.kGold, 0.7, Color.kBlue,0.8, Color.kGold, 0.9, Color.kBlue));
    private LEDPattern spartyScroll = spartyBase.scrollAtRelativeSpeed(Percent.per(Second).of(SCROLL_SPEED));

    

    public void setState(String input){
        state = input;
    }
    public void matchLightSequence(){
        CommandScheduler.getInstance().schedule(Commands.sequence(
            runOnce(() -> {state = "Auto";}),
            runOnce(() -> {green.applyTo(ledBuffer);}),
            Commands.waitSeconds(20),
            runOnce(() -> {transition.applyTo(ledBuffer);}),
            runOnce(() -> {state = "Transition";}),
            Commands.waitSeconds(10),
            runOnce(() -> {blue.applyTo(ledBuffer);}),
            runOnce(() -> {state = "Shift1";}),
            Commands.waitSeconds(25),
            runOnce(() -> {red.applyTo(ledBuffer);}),
            runOnce(() -> {state = "Shift2";}),
            Commands.waitSeconds(25),
            runOnce(() -> {blue.applyTo(ledBuffer);}),
            runOnce(() -> {state = "Shift3";}),
            Commands.waitSeconds(25),
            runOnce(() -> {red.applyTo(ledBuffer);}),
            runOnce(() -> {state = "Shift4";}),
            Commands.waitSeconds(25),
            runOnce(() -> {transition.applyTo(ledBuffer);}),
            runOnce(() -> {state = "EndGame";}),
            Commands.waitSeconds(30),
            runOnce(() -> {spartyScroll.applyTo(ledBuffer);}),
            runOnce(() -> {state = "Scroll";})
        ));   
        SmartDashboard.putBoolean("Start Hub Sequence", false);
    }
    
    // public void 
    public HubLightsSubsystem(){
        state = "Scroll";
        ledStrip.setLength(LED_LENGTH);
        matchSequenceStartedTrig.onTrue(runOnce(() -> {matchLightSequence();}));
        SmartDashboard.putBoolean("Start Hub Sequence", false);
        ledStrip.setData(ledBuffer);
        ledStrip.start();
    }

    @Override
    public void periodic(){
        if(state == "Scroll"){
            spartyScroll.applyTo(ledBuffer);}
        ledStrip.setData(ledBuffer);
        SmartDashboard.putString("State",state);
    }
}
