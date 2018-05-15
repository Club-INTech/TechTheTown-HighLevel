package image.patternRecognition.setPatternPosition.shootPicture.piCam;

import image.patternRecognition.setPatternPosition.shootPicture.piCam.RPiCamera.RPiCamera;
import image.patternRecognition.setPatternPosition.shootPicture.piCam.cameraEnums.AWB;
import image.patternRecognition.setPatternPosition.shootPicture.piCam.cameraEnums.Exposure;
import image.patternRecognition.setPatternPosition.shootPicture.piCam.cameraExceptions.FailedToRunRaspistillException;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

/**
 * ShootBufferedStill is an example of how to take and buffer a still image using JRPiCam.
 * 
 * @author Andrew Dillon
 */
public class ShootBufferedStill {
	
	public static BufferedImage TakeBufferedPicture() {
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
			BufferedImage buffImg = shootBufferedStill(piCamera);
			return buffImg;
		}
		else{
			System.out.println("L'image n'a pas pu etre prise (erreur1)");
			return null;
		}
	}
	
	private static BufferedImage shootBufferedStill(RPiCamera piCamera) {
		piCamera.setRotation(180)                   //Tourne l'image à 180°
				.setTimeout(500)                   	//Temps d'attente avant la prise de photo (on peut bouger après T=timeout+shutter~=1s)
				.setSharpness(100)
				.setQuality(100)
				.setAWB(AWB.TUNGSTEN)				//Rend la photo froide, permettant de faire une distinction plus facile entre les couleurs
				.setExposure(Exposure.ANTISHAKE);
		try {
			BufferedImage buffImg = piCamera.takeBufferedStill(2592, 1944); // Take image and store in BufferedImage
			return buffImg;
		} catch (IOException e) {
			System.out.println("L'image n'a pas pu etre prise (erreur2)");
			e.printStackTrace();
			BufferedImage toReturn = new BufferedImage(3000,3000,TYPE_INT_RGB);
			return toReturn;
		} catch (InterruptedException e) {
			System.out.println("L'image n'a pas pu etre prise (erreur3)");
			e.printStackTrace();
			BufferedImage toReturn = new BufferedImage(3000,3000,TYPE_INT_RGB);
			return toReturn;
		}
	}
}
