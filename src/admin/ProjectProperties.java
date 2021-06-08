/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;

/**
 *
 * @author gjm36
 */
public class ProjectProperties {

    Properties init(String propFile) {
        Properties projProps = new Properties();
        InputStream inputProps = this.getClass().getResourceAsStream(propFile);
        try {
            projProps.load(inputProps);
            inputProps.close();
        } catch (Exception ex) {
            Logger.getLogger(YtexManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return projProps;
    }

}
