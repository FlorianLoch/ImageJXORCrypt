/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.floch.imagejgradiationsanalyzer;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JOptionPane;

/**
 *
 * @author Florian Loch (florian dot loch at gmail dot com)
 */
public class GradiationsAnalyzerFilter implements PlugInFilter {

    private double boxHeight = 20;
    private double boxWidth = 20;
    private ImageProcessor ip;
    
    @Override
    public int setup(String string, ImagePlus ip) {
        return PlugInFilter.DOES_8G;
    }

    @Override
    public void run(ImageProcessor ip) {
        this.ip = ip;
        
        int[][] medians = new int[(int) Math.ceil(ip.getHeight() / boxHeight)][(int) Math.ceil(ip.getWidth() / boxWidth)];
        
        for (int i = 0; i < Math.ceil(ip.getWidth() / boxWidth); i++) {
            System.out.println(ip.getHeight());
            System.out.println(Math.ceil(ip.getHeight() / boxHeight));
            for (int j = 0; j < Math.ceil(ip.getHeight() / boxHeight); j++) {
                int areaWidth = ((i + 1) * boxWidth <= ip.getWidth()) ? (int) boxWidth : ip.getWidth() % (int) boxWidth;
                int areaHeight = ((j + 1) * boxHeight <= ip.getHeight()) ? (int) boxHeight : ip.getHeight() % (int) boxHeight;
                System.out.println("Area width: " +  areaWidth + ", area height: " + areaHeight);
                medians[j][i] = this.computeMedianOfArea(i * (int) boxWidth, j * (int) boxHeight, areaWidth, areaHeight);
            }
        }
        
        System.out.println("Finished!");
    }

    private int computeMedianOfArea(int x, int y, int width, int height) {
        ArrayList<Integer> values = new ArrayList<>();
        
        for (int i = y; i < y + height; i++) {
            for (int j = x; j < x + width ; j++) {
                Integer gradiant = this.getGradiationForPixel(j, i);
                
                if (null == gradiant) continue;
                values.add(gradiant);
            }
        }
        
        Collections.sort(values);
        
        int median = values.get((int) Math.ceil(values.size() / 2.0));
        return median;
    }
    
    private Integer getGradiationForPixel(int x, int y) {
        if (x + 1 == this.ip.getWidth()) {
            return null; 
        }
        
        return this.ip.getPixel(x, y) - this.ip.getPixel(x + 1, y);
    }
}
