package frc.robot;
import java.util.ArrayList;

import edu.wpi.first.networktables.*;
import edu.wpi.first.wpilibj.smartdashboard.*;
import io.github.pseudoresonance.pixy2api.*;
import io.github.pseudoresonance.pixy2api.Pixy2.LinkType;
import io.github.pseudoresonance.pixy2api.Pixy2CCC.Block;

//TODO: Optimize

public class PixyInterface {
  static enum BallColor {
    RED_BALL(1),
    BLUE_BALL(2);

    final int value;

    BallColor(int Value) {
      this.value = Value;
    }

    int getValue() {
      return this.value;
    }
  };

    Pixy2 pixy;   

    double pixyMinTargetAspectRatio;
    double pixyMaxTargetAspectRatio;
    int pixyMinBlockHeight;
    int pixyMinBlockWidth;

    private int state;
    private NetworkTable pixyTable;
    PixyInterface(LinkType type, double minTargetAspectRatio, double maxTargetAspectRatio, int minBlockHeight, int minBlockWidth) {
        SmartDashboard.putString("Pixy State:", "Initializing..."); 
        pixy = Pixy2.createInstance(type);
        state = pixy.init();
        SmartDashboard.putNumber("Pixy State Raw:", state);

        pixyTable = NetworkTableInstance.getDefault().getTable("pixy");
        
        pixyMinTargetAspectRatio = minTargetAspectRatio;
        pixyMaxTargetAspectRatio = maxTargetAspectRatio;
        pixyMinBlockHeight = minBlockHeight;
        pixyMinBlockWidth = minBlockWidth;

        /*switch (state){
          case 0: SmartDashboard.putString("Pixy State:", "PIXY_RESULT_OK"); break;
          case -1: SmartDashboard.putString("Pixy State:", "PIXY_RESULT_ERROR"); break;
          case -2: SmartDashboard.putString("Pixy State:", "PIXY_RESULT_BUSY"); break;
          case -3: SmartDashboard.putString("Pixy State:", "PIXY_RESULT_CHECKSUM_ERROR"); break;
          case -4: SmartDashboard.putString("Pixy State:", "PIXY_RESULT_TIMEOUT"); break;
          case -5: SmartDashboard.putString("Pixy State:", "PIXY_RESULT_BUTTON_OVERRIDE"); break;
          case -6: SmartDashboard.putString("Pixy State:", "PIXY_RESULT_PROG_CHANGING"); break;
          default: SmartDashboard.putString("Pixy State:", "UNKNOWN: " + String.valueOf(state)); break;

        }*/
    }

    double steeringSuggestion(BallColor color){
        if (state != 0) return 0;
    
        double result = 0;
    
        int worked = pixy.getCCC().getBlocks(false, 3, 5); //have pixy acquire data //Param 2 is "sigmap" What is sigmap?
        
        //int worked =pixy.getCCC().getBlocks();
        SmartDashboard.putNumber("GetBlocks Return Code:", worked);
        ArrayList<Block> blocks = pixy.getCCC().getBlockCache();
        int blockCount = blocks.size();
        SmartDashboard.putNumber("Target Number (blocks size)", blockCount);
    
        if (blocks.size() > 0) { //if blocks were found
          //will eventually contain data of optimal target
          Block block;
          double xcoord = 0;
          double ycoord = 0;
          int blockWidth = 0;
          int blockHeight = 0;
          int sig = 0;
          String data = "ND";
          boolean valid = false;
          //Find the best target. Largest approximately square, propery colored signature.
          for (int i = 0; i < blockCount; i++) {
            block = blocks.get(i); //get only data required for test
            blockWidth = block.getWidth();
            blockHeight = block.getHeight();
            sig = block.getSignature();
            if (sig == color.value && blockWidth / blockHeight >= pixyMinTargetAspectRatio
              && blockWidth / blockHeight <= pixyMaxTargetAspectRatio && blockHeight >= pixyMinBlockHeight
              && blockWidth >= pixyMinBlockWidth) {
               //proper signature. approx. square
              xcoord = block.getX(); //get remaining info
              ycoord = block.getY(); 
              data = block.toString();
              valid = true; //mark block data as a valid target
              break; //exit loop, with variables properly set
            }
          }
          if (valid) {
            //compute target position from center -1.0 to 1.0
            result = -1*((xcoord-157.5)/157.5);
            //print target info
            SmartDashboard.putNumber("Block Width", blockWidth);
            SmartDashboard.putNumber("Block Height", blockHeight);
            SmartDashboard.putNumber("Block Signature", sig);  
            SmartDashboard.putBoolean("Targets:", true); //send to smartdashboard
            SmartDashboard.putNumber("X Coordinate:", xcoord);
            SmartDashboard.putNumber("Y Coordinate:", ycoord);
            SmartDashboard.putString("Data String:", data);

            //set netTable value for pixy blocks
            double[] targetBlock = {sig, blockWidth, blockHeight, xcoord, ycoord};
            pixyTable.getEntry("targetBlock").forceSetDoubleArray(targetBlock);
          }
        }
        else {
          SmartDashboard.putBoolean("Targets:", false);
        }
    
        return result;
    }

    int getPixyState() {
        return state;
    }
}
