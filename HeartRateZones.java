package com.wattathlon.wattathlon2;

public class HeartRateZones {
    private int ftp;
    private String imageName = "";

    public HeartRateZones(int ftp) {
        this.ftp = ftp;
    }

    //The following methods return true if your current watts produced match the heart rate zone.

    //white zone
    public boolean activeRecovery (int watts) {
        int activeRecoveryCeil = (55 * ftp) / 100;

        if(watts <= activeRecoveryCeil)
            return true;
        else
            return false;
    }

    //blue zone
    public boolean endurance (int watts) {
        int enduranceFloor = (56 * ftp) / 100;
        int enduranceCeil = (75 * ftp) / 100;

        if(watts >= enduranceFloor && watts <= enduranceCeil)
            return true;
        else
            return false;
    }

    //green zone
    public boolean tempo (int watts) {
       int tempoFloor = (76 * ftp) / 100;
       int tempoCeil = (90 * ftp) / 100;

       if(watts >= tempoFloor && watts <= tempoCeil)
           return true;
       else
           return false;
    }

    //yellow zone
    public boolean threshold (int watts) {
        int thresholdFloor = (91 * ftp) / 100;
        int thresholdCeil = (105 * ftp) / 100;

        if(watts >= thresholdFloor && watts <= thresholdCeil)
            return true;
        else return false;
    }

    //red zone
    public boolean vo2max (int watts) {
        int vo2maxFloor = (106 * ftp) / 100;
        int vo2maxCeil = (120 * ftp) / 100;

        if(watts >= vo2maxFloor && watts <= vo2maxCeil)
            return true;
        else
            return false;
    }

    //purple zone
    public boolean anaerobicCapacity (int watts) {
        int anaerobicFloor = (121 * ftp) / 100;

        if(watts >= anaerobicFloor)
            return true;
        else
            return false;
    }
}
