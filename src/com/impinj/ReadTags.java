package com.impinj;

import com.impinj.octane.*;

import java.util.Date;
import java.util.Scanner;


public class ReadTags {

    public static void main(String[] args) {

        try {
            String hostname = System.getProperty(Properties.hostname);

            if (hostname == null) {
                throw new Exception("Must specify the '"
                        + Properties.hostname + "' property");
            }

            ImpinjReader reader = new ImpinjReader();

            System.out.println("Connecting");
            reader.connect(hostname);

            Settings settings = reader.queryDefaultSettings();

            ReportConfig report = settings.getReport();
            report.setIncludeAntennaPortNumber(true);
            report.setIncludeChannel(true);
            //report.setIncludeCrc(true);
            //report.setIncludeDopplerFrequency(true);
            //report.setIncludeFastId(true)
            //report.setIncludeGpsCoordinates(true);
            report.setIncludeFirstSeenTime(true);
            //report.setIncludeLastSeenTime(true);
            //report.setIncludePcBits(true);

            //report.setIncludePeakRssi(true);
            //report.setIncludePhaseAngle(true);
            //report.setIncludeSeenCount(true);
            report.setMode(ReportMode.Individual);

            // The reader can be set into various modes in which reader
            // dynamics are optimized for specific regions and environments.
            // The following mode, AutoSetDenseReader, monitors RF noise and interference and then automatically
            // and continuously optimizes the reader’s configuration
            settings.setReaderMode(ReaderMode.AutoSetDenseReader);

            // set some special settings for antenna 1
            AntennaConfigGroup antennas = settings.getAntennas();
            antennas.disableAll();
            antennas.enableById(new short[]{1});
            antennas.getAntenna((short) 1).setIsMaxRxSensitivity(false);
            antennas.getAntenna((short) 1).setIsMaxTxPower(false);
            antennas.getAntenna((short) 1).setTxPowerinDbm(20.0);
            antennas.getAntenna((short) 1).setRxSensitivityinDbm(-70);

            reader.setTagReportListener(new TagReportListenerImplementation());

            System.out.println("Applying Settings");
            reader.applySettings(settings);

            System.out.println("Starting");
            reader.start();

            System.out.println("Type STOP and Enter to exit.");
            Scanner s = new Scanner(System.in);
            //s.nextLine();

            while (s.hasNextLine()) {
                String line = s.nextLine();
                System.out.println(line);
                if (line.equals("STOP")) {
                    break;
                }
            }
            System.out.println("Disconnecting.");
            reader.stop();
            reader.disconnect();
        } catch (OctaneSdkException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace(System.out);
        }
    }
}
