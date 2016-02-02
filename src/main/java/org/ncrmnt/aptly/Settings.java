/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ncrmnt.aptly;

import hudson.tasks.Builder;

/**
 *
 * @author necromant
 */
public class Settings extends Builder {

    public String getMyString() {
        return "Hello Jenkins!";
    }

}
