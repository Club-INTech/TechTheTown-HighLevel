package patternRecognition.shootPicture;

import patternRecognition.shootPicture.RPiCamera.*;
import patternRecognition.shootPicture.cameraEnums.*;
import patternRecognition.shootPicture.cameraExceptions.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * ShootBufferedStill is an example of how to take and buffer a still image using JRPiCam.
 * 
 * @author Andrew Dillon
 */
public class ShootBufferedStill {
	
	public static String TakeBufferedPicture() {
		RPiCamera piCamera = null;
		// Attempt to create an instance of RPiCamera.RPiCamera, will fail if raspistill is not properly installed
		String saveDir = "/home/pi";
		try {
			piCamera = new RPiCamera(saveDir);
		} catch (FailedToRunRaspistillException e) {
			System.out.println("L'image n'a pas pu etre prise (erreur0)");
			e.printStackTrace();
		}
		// Take a still image, buffer, and save it
		if (piCamera != null) {
			String imageName="ImageRaspberryPi";
			String encoding="png";
			shootBufferedStill(piCamera, imageName, encoding);
			String pathToReturn=saveDir+"/"+imageName+"."+encoding;
			return pathToReturn;
		}
		else{
			System.out.println("L'image n'a pas pu etre prise (erreur1)");
			return null;
		}
	}
	
	private static void shootBufferedStill(RPiCamera piCamera, String imageName, String encoding) {
		piCamera.setRotation(180)                   //Tourne l'image à 180°
				.setTimeout(500)                   	//Temps d'attente avant la prise de photo (on peut bouger après T=timeout+shutter~=1s)
				.setSharpness(100)
				.setQuality(100)
				.setAWB(AWB.TUNGSTEN)				//Rend la photo froide, permettant de faire une distinction plus facile entre les couleurs
				.setExposure(Exposure.ANTISHAKE);
		try {
			BufferedImage buffImg = piCamera.takeBufferedStill(2592, 1944); // Take image and store in BufferedImage
			File saveFile = new File(imageName); // Create file to save image to
			ImageIO.write(buffImg, encoding, saveFile); // Save image to file
			//System.out.println("New PNG image saved to:\n\t" + saveFile.getAbsolutePath()); // Print out location of image
		} catch (IOException e) {
			System.out.println("L'image n'a pas pu etre prise (erreur2)");
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("L'image n'a pas pu etre prise (erreur3)");
			e.printStackTrace();
		}
	}
}
