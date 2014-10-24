/*
 * Main.java
 *
 * Created on September 15, 2006, 4:59 PM
 *
 */

package ch.ethz.karto.gui;

import java.awt.BorderLayout;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.JFrame;

/**
 * The main class.
 * @author Bernhard Jenny, Institute of Cartography, ETH Zurich.
 */
public class Main {

    private static final String CONTINENTS_PATH = "/ch/ethz/karto/data/continents.ung";
        
    /**
     * The program starts with this method.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                createWindow();
            }
        });
    }
    
    /**
     * Create a window, ask the user for lines to display, import the lines,
     * and display them.
     **/
    private static void createWindow() {

        try {
            
            // create a new window
            JFrame mapWindow = new JFrame("JMapProjLib");
            mapWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ProjectionSelectionPanel panel = new ProjectionSelectionPanel();
            mapWindow.getContentPane().add(panel, BorderLayout.CENTER);
            mapWindow.pack();
            mapWindow.setLocationRelativeTo(null); // center on screen
            mapWindow.setVisible(true);

            URL url = Main.class.getResource(CONTINENTS_PATH);
            InputStream stream = url.openStream();
            ArrayList<MapLine> lines = UngenerateImporter.importData(stream);
           
            // pass the lines to the map component
            panel.setLines(lines);

        } catch (Exception exc) {
            exc.printStackTrace();
            System.exit(1);
        }        
        
    }
    
}
