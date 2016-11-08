/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.dvdlibrary.ops;

import java.util.ArrayList;

/**
 *
 * @author apprentice
 */
public class ArrayListUtility{

    public static boolean containsSequenceIgnoreCase(ArrayList<String> stringList, String input) {
        boolean doesContain = false;
        for (String s : stringList) {
            if (s.toLowerCase().contains(input.toLowerCase())) {
                doesContain = true;
            }
        }
        return doesContain;
    }  
    
    public static boolean containsIgnoreCase(ArrayList<String> stringList, String input) {
        boolean doesContain = false;
        for (String s : stringList) {
            if (s.equalsIgnoreCase(input)) {
                doesContain = true;
            }
        }
        return doesContain;
    }
}
