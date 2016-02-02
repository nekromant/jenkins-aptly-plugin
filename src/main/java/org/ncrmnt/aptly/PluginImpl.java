/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ncrmnt.aptly;

/**
 *
 * @author Andrew 'Necromant' Andrianov <andrew@ncrmnt.org>
 */
import hudson.Plugin;
import java.util.logging.Logger;

public class PluginImpl extends Plugin {
    private final static Logger LOG = Logger.getLogger(PluginImpl.class.getName());
 
    @Override
    public void start() throws Exception {
        LOG.info("Starting aptly plugin");
    }
}