package frc.robot;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.Timer;

enum LedMode {
    SOLID,
    BLINK,
    MAX_TO_MIN,
    MIN_TO_MAX
}

class LedState {
    LedMode mode;
    int colorR;
    int colorG;
    int colorB;

    LedState(LedMode Mode, int R, int G, int B) {
        mode = Mode;
        colorR = R;
        colorG = G;
        colorB = B;
    }
}

class LedRange {
    int min, max;
    LedState state;
    
    LedRange(int Min, int Max, LedState defaultState) {
        min = Min;
        max = Max;
    }
}

public class LedInterface {
    Timer time = new Timer();

    AddressableLED leds;
    AddressableLEDBuffer ledBuffer; 
    int LedCount;

    private boolean doBlink;
    private int blinkCounter;
    private boolean blinkOn;
    int blinkRate = 11;

    private final LedState defaultState = new LedState(LedMode.SOLID, 255, 255, 255);

    LedRange All;

    LedRange FLUpper;
    LedRange FRUpper;
    LedRange RLUpper;
    LedRange RRUpper;

    LedRange FLMid;
    LedRange FRMid;
    LedRange RLMid;
    LedRange RRMid;

    LedRange FLLower;
    LedRange FRLower;
    LedRange RLLower;
    LedRange RRLower;

    int lastR;
    int lastG;
    int lastB;

    LedInterface(int port, int size) {
        leds = new AddressableLED(port);
        ledBuffer = new AddressableLEDBuffer(size);
        leds.setLength(ledBuffer.getLength());

        lockinColors();
        leds.start();
        blinkCounter = 1;
        doBlink = false;




        All = new LedRange(0, 99, defaultState);

        FLUpper = new LedRange(0, 4, defaultState);
        FRUpper = new LedRange(25, 29, defaultState);
        RLUpper = new LedRange(75, 79, defaultState);
        RRUpper = new LedRange(50, 54, defaultState);
    
        FLMid = new LedRange(5, 19, defaultState);
        FRMid = new LedRange(30, 44, defaultState);
        RLMid = new LedRange(80, 94, defaultState);
        RRMid = new LedRange(55, 69, defaultState);
    
        FLLower = new LedRange(20, 24, defaultState);
        FRLower = new LedRange(45, 49, defaultState);
        RLLower = new LedRange(95, 99, defaultState);
        RRLower = new LedRange(70, 74, defaultState);
    }

    //updates buffer only
    private void updateRange(LedRange range) {
        switch (range.state.mode) {
            case SOLID:
                break; //Leds were already set, do not set again
            case BLINK: {

            }

            case MAX_TO_MIN: {

            }
            
            case MIN_TO_MAX: {

            }
        }
    }

    void setState(LedMode mode, int r, int g, int b) {
        if (mode == LedMode.SOLID) {
            
        }
    }

    void setAllSolid(int r, int g, int b){
        lastR = r;
        lastG = g;
        lastB = b;
        // https://docs.wpilib.org/en/stable/docs/software/hardware-apis/misc/addressable-leds.html
        for (var i = 0; i < ledBuffer.getLength(); i++) {
            ledBuffer.setRGB(i,  r, g, b);
         }
         leds.setData(ledBuffer);
    }

    void setAllSolid(int r, int g, int b, boolean blinkMode){
        lastR = r;
        lastG = g;
        lastB = b;
        doBlink = blinkMode;
        // https://docs.wpilib.org/en/stable/docs/software/hardware-apis/misc/addressable-leds.html
        for (var i = 0; i < ledBuffer.getLength(); i++) {
            ledBuffer.setRGB(i,  r, g, b);
         }
         lockinColors();
    }

    void setAllOff(){
        // https://docs.wpilib.org/en/stable/docs/software/hardware-apis/misc/addressable-leds.html
        for (var i = 0; i < ledBuffer.getLength(); i++) {
            ledBuffer.setRGB(i,  0,0,0);
         }
         lockinColors();
    }

    void update() {
        blinkCounter += 1;
        if (blinkCounter > blinkRate * 2) blinkCounter = 0;
        if (blinkCounter > 0 && blinkCounter < blinkRate ) {
            blinkOn = true;
        }
        if (blinkCounter > blinkRate && blinkCounter < blinkRate * 2 ) {
            blinkOn = false;
        }

        if (blinkOn && doBlink) {
            setAllOff();
        } else {
            setAllSolid(lastR, lastG, lastB);
        }
        lockinColors();
        //drone lights

        /*

        updateRange(FLUpper);
        updateRange(FRUpper);
        updateRange(RLUpper);
        updateRange(RRUpper);

        updateRange(FLMid);
        updateRange(FRMid);
        updateRange(RLMid);
        updateRange(RRMid);

        updateRange(FLLower);
        updateRange(FRLower);
        updateRange(RLLower);
        updateRange(RRLower);

        leds.setData(ledBuffer);
        */
    }

    public void lockinColors(){
        //ledBuffer.setRGB(0,  255, 255, 255);
        //ledBuffer.setRGB(25, 255, 255, 255);
        //ledBuffer.setRGB(75,  255, 0, 0);
        //ledBuffer.setRGB(50,  255, 0, 0);
        leds.setData(ledBuffer);
    }
}
