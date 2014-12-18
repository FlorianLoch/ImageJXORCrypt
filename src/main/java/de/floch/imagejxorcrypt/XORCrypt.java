/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.floch.imagejxorcrypt;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.io.OpenDialog;
import ij.io.Opener;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import java.util.Random;
import javax.swing.JOptionPane;

/**
 *
 * @author Florian Loch (florian dot loch at gmail dot com)
 */
public class XORCrypt implements PlugInFilter {

    private static Random randomizer;
    private static int KEY_RANDOM_IMAGE = 0;
    private static int KEY_CHOOSE_IMAGE = 1;

    @Override
    public int setup(String string, ImagePlus ip) {
        return PlugInFilter.DOES_RGB;
    }

    @Override
    public void run(ImageProcessor ip) {
        ImagePlus keyImage = getKeyImage(ip);
        int[] pixelsM = (int[]) ip.getPixels();
        int[] pixelsK;

        if (keyImage == null) {
            JOptionPane.showMessageDialog(null, "The image given image could not be loaded by ImageJ - please make sure it is a valid image file.", "Image could not be loaded", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (keyImage.getType() != ImagePlus.COLOR_RGB) {
            JOptionPane.showMessageDialog(null, "The image given image is not of type COLOR_RGB.", "Image not valid", JOptionPane.ERROR_MESSAGE);
            return;
        }

        pixelsK = (int[]) keyImage.getProcessor().getPixels();

        if (pixelsK.length != pixelsM.length) {
            JOptionPane.showMessageDialog(null, "The image given image does not match the pixel count of the source image - therefore it cannot be used.", "Image not valid", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ImagePlus chiffreImage = NewImage.createRGBImage("Encrypted/Decrypted image", ip.getWidth(), ip.getHeight(), 0, 0);
        int[] pixelsC = (int[]) chiffreImage.getProcessor().getPixels();

        for (int i = 0; i < pixelsC.length; i++) {
            pixelsC[i] = pixelsM[i] ^ pixelsK[i];
        }

        keyImage.show();
        chiffreImage.show();
    }

    private static ImagePlus getKeyImage(ImageProcessor messageImageProcessor) {
        if (showKeySelectionDialog() == KEY_CHOOSE_IMAGE) {
            String path = new OpenDialog("Please select the file to encrypt with").getPath();

            return new Opener().openImage(path);
        }

        return generateRandomKeyImage(messageImageProcessor.getWidth(), messageImageProcessor.getHeight());
    }

    private static int showKeySelectionDialog() {
        GenericDialog gD = new GenericDialog("ImageCryptor");
        String[] imageSourceOptions = new String[]{"Select key image by path", "Use randomly generated key image"};
        gD.addRadioButtonGroup("Image source used for en/decryption:", imageSourceOptions, 2, 1, imageSourceOptions[0]);
        gD.showDialog();
        String selectedImageSource = gD.getNextRadioButton();
        if (selectedImageSource.equals(imageSourceOptions[0])) {
            return KEY_CHOOSE_IMAGE;
        }

        return KEY_RANDOM_IMAGE;
    }

    private static ImagePlus generateRandomKeyImage(int width, int height) {
        ImagePlus keyImage = NewImage.createRGBImage("Randomly generated key image", width, height, 0, 0);
        int[] pixels = (int[]) keyImage.getProcessor().getPixels();

        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = getRandomInt();
        }

        return keyImage;
    }

    private static int getRandomInt() {
        if (randomizer == null) {
            randomizer = new Random();
        }

        return randomizer.nextInt(16777216);
    }

}
