/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.floch.imagejgradiationsanalyzer;

import ij.ImagePlus;
import ij.gui.NewImage;
import ij.io.OpenDialog;
import ij.io.Opener;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import javax.swing.JOptionPane;

/**
 *
 * @author Florian Loch (florian dot loch at gmail dot com)
 */
public class ImageCryptor implements PlugInFilter {

    private ImageProcessor src;
    private ImageProcessor k;
    
    @Override
    public int setup(String string, ImagePlus ip) {
        int a = 0b00000011;
        int b = 0b00000001;
        int c = a ^ b;
        System.out.println(c);
        
        return PlugInFilter.DOES_RGB;
    }

    @Override
    public void run(ImageProcessor ip) {
        //Get "key"-file
        String path = new OpenDialog("Please select the file to encrypt with").getPath();
        ImagePlus keyImage = new Opener().openImage(path);
        if (keyImage == null) {
            JOptionPane.showMessageDialog(null, "The image given (" + path + ") could not be loaded by ImageJ - please make sure it is a valid image file.", "Image could not be loaded", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (keyImage.getType() != ImagePlus.COLOR_RGB) {
            JOptionPane.showMessageDialog(null, "The image given (" + path + ") is not of type COLOR_RGB.", "Image not valid", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int[] pixelsM = (int[]) ip.getPixels();
        int[] pixelsK = (int[]) keyImage.getProcessor().getPixels();
        
        if (pixelsK.length != pixelsM.length) {
            JOptionPane.showMessageDialog(null, "The image given (" + path + ") does not match the pixel count of the source image - therefore it cannot be used.", "Image not valid", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        ImagePlus chiffreImage = NewImage.createRGBImage("Encrypted/Decrypted image", ip.getWidth(), ip.getHeight(), 0, 0);
        ImageProcessor ipC = chiffreImage.getProcessor();
        int[] pixelsC = (int[]) ipC.getPixels();
        
        for (int i = 0; i < pixelsC.length; i++) {
            pixelsC[i] = pixelsM[i] ^ pixelsK[i];
        }
        
        chiffreImage.show();
    }

}
