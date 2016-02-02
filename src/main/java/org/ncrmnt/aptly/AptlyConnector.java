/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ncrmnt.aptly;

import hudson.triggers.SCMTrigger.Runner;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author necromant
 */
public class AptlyConnector {

    private static final AptlyConnector INSTANCE = new AptlyConnector();
    private static final Logger LOGGER = Logger.getLogger(AptlyConnector.class.getName());

    static AptlyConnector getInstance() {
        return INSTANCE;
    }

    private List<String> getCmdOutput(String cmd) {
        List<String> ret = new ArrayList<String>();

        try {
            String s = null;
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            while ((s = stdInput.readLine()) != null) {
                ret.add(s);
            }

            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                LOGGER.log(Level.SEVERE, "{0}{1}", new Object[]{"APTLY: ", s});
            }
        } catch (IOException ex) {
            Logger.getLogger(AptlyConnector.class.getName()).log(Level.SEVERE, "Exception while running shell command", ex);
        }
        return ret;
    }

    private int runCmd(PrintStream logger, String cmd) {
        int ret;
        Process p;
        logger.println("APTLY: " + cmd);
        try {
            p = Runtime.getRuntime().exec(cmd);
        } catch (IOException ex) {
            return -127;
        }
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String s;

        try {
            while ((s = stdInput.readLine()) != null) {
                logger.println("APTLY: " + s);
            }
            
        } catch (IOException iOException) {
            /* Blah */
        }
        
        try {
            ret = p.waitFor();
        } catch (InterruptedException ex) {
            p.destroy();
            return -127;

        }
        return ret;
    }

    //////////////
    public List<String> getRepoList() {
        return getCmdOutput("aptly repo --raw list");
    }

    boolean getHaveAptly() {
        Process p;
        try {
            p = Runtime.getRuntime().exec("aptly version");
        } catch (IOException ex) {
            Logger.getLogger(AptlyConnector.class.getName()).log(Level.SEVERE, "Failed to detect aptly", ex);
            return false;
        }
        try {
            return (0 == p.waitFor());
        } catch (InterruptedException ex) {
            Logger.getLogger(AptlyConnector.class.getName()).log(Level.SEVERE, "Interrupted while getting aptly version", ex);
            return false;
        }
    }

    String getAptlyVersion() {
        if (getHaveAptly()) {
            List<String> v = getCmdOutput("aptly version");
            return v.get(0);
        }
        return "invalid";
    }

    boolean repoExists(String name) {
        return false;
    }

    int addPackage(PrintStream logger, String name, String path) {
            return runCmd(logger, "aptly repo add " + name + " " + path + "");
    }
}
