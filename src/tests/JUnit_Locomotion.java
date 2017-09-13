/*
 * Copyright (c) 2016, INTech.
 *
 * This file is part of INTech's HighLevel.
 *
 *  INTech's HighLevel is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  INTech's HighLevel is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with it.  If not, see <http://www.gnu.org/licenses/>.
 */

package tests;


import enums.DirectionStrategy;
import exceptions.Locomotion.BlockedException;
import exceptions.Locomotion.UnableToMoveException;
import exceptions.Locomotion.UnexpectedObstacleOnPathException;
import exceptions.serial.SerialConnexionException;
import hook.Hook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import robot.EthWrapper;
import robot.Locomotion;
import robot.SerialWrapper;
import smartMath.Vec2;

import java.util.ArrayList;

/**
 * Tests unitaires pour Deplacements.
 *
 * @author pf, spraakforskaren
 * 
 * 
 * Classe test pour les déplacements vus d'en haut(-niveau)
 * Si vous voulez effectuer une série de tests, il vous faudra beaucoup de place
 * Le mieux est donc de les faire bien séparément
 * Ce que je préconise pour tester les déplacement est la liste des action suivante
 * 1-testMoveLengthwise() | non fait par spraakforskaren
 * 2-testFollowPath() | non fait par spraakforskaren
 * 3-testTurn()
 * 4-testGetPosition()
 * 5-testGetOrientation()
 * 6-testImmobilise()
 * 7-testMoveToPointException()
 * 8-testDetectEnemyAtDistance() 
 */
public class JUnit_Locomotion extends JUnit_Test
{

	/** The deplacements. */
	private Locomotion mLocomotion;
	private EthWrapper cardWrapper;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		// appelé avant chaque batterie de test
		/*
		 * tearDownAfterClass
		 * @AfterClass apres chaque batterie de test de la meme classe JUnit
		 */
	}

	/* (non-Javadoc)
	 * @see tests.JUnit_Test#setUp()
	 */
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		log.debug("JUnit_DeplacementsTest.setUp()");
		mLocomotion = container.getService(Locomotion.class);
		cardWrapper= container.getService(EthWrapper.class);
		config.set("couleur", "vert");
		mLocomotion.updateConfig();
		mLocomotion.setPosition(new Vec2 (1381,1000));
		mLocomotion.setOrientation(Math.PI);
	}

	/**
	 * Ca teste la capacité à avancer et à reculer dans le cas où il n'y a pas d'obstacles, 
	 * et pas de hooks
	 */
	@Test
	public void testMoveLengthwise() 
	{
		try 
		{
			int distance = 210;
			while(true)
			{
				mLocomotion.moveLengthwise(distance, null, false, true);
				//cardWrapper.moveLengthwise(distance);
				while(cardWrapper.isRobotMovingAndAbnormal()[0])
				{
					if(cardWrapper.isRobotMovingAndAbnormal()[1])
						throw new Exception();
				}
				if(cardWrapper.isRobotMovingAndAbnormal()[1])
					throw new Exception();
				
				mLocomotion.moveLengthwise(-distance, null, false, true);
				while(cardWrapper.isRobotMovingAndAbnormal()[0])
				{
					if(cardWrapper.isRobotMovingAndAbnormal()[1])
						throw new Exception();
				}
				if(cardWrapper.isRobotMovingAndAbnormal()[1])
					throw new Exception();
			}
		}
		catch (Exception e)
		{
			log.debug(e);
			return;
		}
		
	}	

	/**
	 * Test_tourner.
	 * ATTENTION NE FONCTIONNE QUE DU COTE VERT !
	 * Test qui fait faire des allers-retours
	 * @throws Exception the exception
	 */
	//@Test
	public void testFollowPath()
	{
		ArrayList<Vec2> path = new ArrayList<Vec2>();
		path.add(new Vec2 (-500,1000));
		path.add(new Vec2 (500,1000));
		log.debug("JUnit_DeplacementsTest");
		Vec2 position = mLocomotion.getPosition();
		log.debug("en position : x="+position.getX()+"; y="+position.getY());
		try 
		{
			mLocomotion.moveLengthwise(2000,new ArrayList<Hook>(), false, false);
		} 
		catch (UnableToMoveException e) 
		{
			log.critical( e.logStack());
		}
		position = mLocomotion.getPosition();
		log.debug("en position : x="+position.getX()+"; y="+position.getY());
		log.debug("orientation : "+mLocomotion.getOrientation());
		while (true)
		{
			try 
			{
				mLocomotion.followPath(path, null, DirectionStrategy.FORCE_FORWARD_MOTION);
				position = mLocomotion.getPosition();
				log.debug("en position : x="+position.getX()+"; y="+position.getY()+" après le followpath");
			} 
			catch (UnableToMoveException e) 
			{
				log.critical( e.logStack());
			}
		}
	}

	/**
	 * 
	 * 
	 * 
	 */
	@Test
	public void testTurn()
	{
		try 
		{
			mLocomotion.turn(Math.PI/2, null, true, true);
		} 
		catch (UnableToMoveException e) 
		{
			log.debug("ça n'a pas bien tourné");
		}
	}
	/**On vérifie si on peut aller à un point voulu sans problème vers l'avant en détectant les ennemis
	 * @throws UnableToMoveException 
	 * 
	 */
	@Test
	public void testMoveToPointForwardBackward() throws UnableToMoveException
	{
		Vec2 position = mLocomotion.getPosition();
		log.debug("en position : x="+position.getX()+"; y="+position.getY());
		Vec2 aim = new Vec2(0,500);
		log.debug("position de l'objectif : x="+position.getX()+"; y="+position.getY());
		mLocomotion.JUNIT_moveToPointForwardBackward(aim, null, false, DirectionStrategy.FASTEST, 0, true);
		Assert.assertEquals(aim, mLocomotion.getPosition());
	}
	/**
	 * 
	 * @throws UnableToMoveException
	 */
	@Test
	public void testMoveToPointException() throws UnableToMoveException
	{
		Vec2 position = mLocomotion.getPosition();
		log.debug("en position : x="+position.getX()+"; y="+position.getY());
		Vec2 aim = new Vec2(0,500);
		log.debug("position de l'objectif : x="+position.getX()+"; y="+position.getY());
		mLocomotion.JUNIT_moveToPointException(aim, null, true, false, 0,true);
		Assert.assertEquals(aim, mLocomotion.getPosition());
	}
	/**
	 * 
	 * @throws UnexpectedObstacleOnPathException
	 * @throws BlockedException
	 * @throws SerialConnexionException
	 */
	@Test
	public void testMoveToPointCorrectAngleAndDetectEnnemy() throws UnexpectedObstacleOnPathException, BlockedException, SerialConnexionException
	{
		
		Vec2 position = mLocomotion.getPosition();
		log.debug("en position : x="+position.getX()+"; y="+position.getY());
		Vec2 aim =  new Vec2(0,500);
		log.debug("position de l'objectif : x="+position.getX()+"; y="+position.getY());
		mLocomotion.JUNIT_moveToPointCorrectAngleAndDetectEnnemy(aim, null, true, false, true);
	}
	/**
	 * 
	 * @throws BlockedException
	 * @throws UnexpectedObstacleOnPathException
	 */
	@Test
	public void testCorrectAngle() throws BlockedException, UnexpectedObstacleOnPathException
	{
		Vec2 position = mLocomotion.getPosition();
		log.debug("en position : x="+position.getX()+"; y="+position.getY());
		Vec2 aim = new Vec2(0,500);
		log.debug("position de l'objectif : x="+position.getX()+"; y="+position.getY());
		mLocomotion.JUNIT_correctAngle(aim, true, true);
	}
/**
 * 
 * @throws BlockedException
 * @throws UnexpectedObstacleOnPathException
 */
	@Test
	public void testMoveToPointSymmetry() throws BlockedException, UnexpectedObstacleOnPathException
	{
		Vec2 position = mLocomotion.getPosition();
		log.debug("en position : x="+position.getX()+"; y="+position.getY());
		Vec2 aim = new  Vec2(0,500);
		log.debug("position de l'objectif : x="+position.getX()+"; y="+position.getY());
		
		mLocomotion.JUNIT_moveToPointSymmetry(aim, true, true, false, false);
		
	}
	/**
	* Avance, envoi a la serie
    * @throws BlockedException si le robot rencontre un obstacle innatendu sur son chemin (par les capteurs)
    * @throws UnexpectedObstacleOnPathException 
    */
	@Test
	public void testmoveToPointSerialOrder() throws BlockedException, UnexpectedObstacleOnPathException
	{
		Vec2 position = mLocomotion.getPosition();
		log.debug("en position : x="+position.getX()+"; y="+position.getY());
		Vec2 aim = new Vec2(0,500);
		log.debug("position de l'objectif : x="+position.getX()+"; y="+position.getY());
		mLocomotion.JUNIT_moveToPointSerialOrder(aim, position, 0, 300, true, false, false);
	}
	/**
	 * 
	 * @throws BlockedException
	 * @throws UnexpectedObstacleOnPathException
	 */
	/*
	@Test
	public void testMoveToPointSerialOrder() throws BlockedException, UnexpectedObstacleOnPathException
	{
		Vec2 position = mLocomotion.getPosition();
		log.debug("en position : x="+position.x+"; y="+position.y, this);
		Vec2 aim = new Vec2(0,500);
		log.debug("position de l'objectif : x="+position.x+"; y="+position.y, this);
		mLocomotion.JUNIT_moveToPointSerialOrder(aim, position, 0, 300, true, false, false);
	}
	*/
	/**
	 * 
	 * @throws BlockedException
	 * @throws UnableToMoveException 
	 */
	@Test
	public void	testIsMotionEnded() throws BlockedException, UnableToMoveException 
	{
		mLocomotion.moveLengthwise(0, null, false, false);
		boolean res1 = mLocomotion.JUNIT_isMotionEnded();
		Assert.assertEquals(res1, false);
		mLocomotion.moveLengthwise(1000, null, false, false);
		boolean res2 = mLocomotion.JUNIT_isMotionEnded();
		Assert.assertEquals(res2, true);
	}
	@Test 
	public void testDetectEnemyInDisk(boolean front, boolean isTurnOnly, Vec2 aim) throws UnexpectedObstacleOnPathException 
	{
		mLocomotion.detectEnemyInDisk(front, isTurnOnly, aim);
	}
	/**
	 * 
	 * @param distance
	 * @param movementDirection
	 * @throws UnexpectedObstacleOnPathException
	 * @throws UnableToMoveException 
	 */
	@Test
	public void testDetectEnemyAtDistance(int distance, Vec2 movementDirection) throws UnexpectedObstacleOnPathException, UnableToMoveException 
	{
		System.out.println("Placez le robot en ...");
		System.out.println("Placez un ennemi à une distance inférieure à "+distance);
		mLocomotion.moveLengthwise(distance+200, null, false, true);
		mLocomotion.detectEnemyAtDistance( distance, new Vec2 (0,100));
		System.out.println("Le robot a dû s'arrêter");		
	}
	/**
	 * 
	 */
	@Test
	public void testUpdateCurrentPositionAndOrientation()
	{
		mLocomotion.JUNIT_updateCurrentPositionAndOrientation();
	}
	/**
	 * Adaptez le code à vos attentes, il y a trop de possibilités
	 */
	@Test
	public void testUpdateConfig()
	{
		mLocomotion.updateConfig();
		
	}
	@Test
	public void testImmobilise() throws UnableToMoveException
	{
		mLocomotion.moveLengthwise(200, null, false, true);
		mLocomotion.immobilise();
		log.debug("Le robot a dû s'arrêter");
	
	}
	@Test
	public void testSetPosition(Vec2 positionWanted)
	{
		mLocomotion.setPosition(positionWanted);
		Vec2 pos = mLocomotion.getPosition();
		Assert.assertEquals(positionWanted, pos);
	}
	/**
	 * Par la même occasion test getOrientation()
	 * @param orientation
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testSetOrientation(double orientation)
	{
		mLocomotion.setOrientation(orientation);
		double ori = mLocomotion.getOrientation();
		Assert.assertEquals((float)ori, (float)orientation);
	}
	@Test
	public void testGetPosition()
	{
		log.debug("Vérifiez que la position du robot est bien : "+mLocomotion.getPosition());
	}
	@Test
	
	public void testGetOrientation()
	{
		log.debug("Vérifiez que l'orientation du robot est bien : "+mLocomotion.getOrientation());
	}
	/**
	 * A voir avec la série
	 */
	@Test 
	public void testDesasservit()
	{
		mLocomotion.desasservit();
	}
	/**
	 * Je ne vois pas où on peut tester le pwm en rotation
	 * @param pwm
	 * @throws SerialConnexionException
	 */
	@Test
	public void testSetRotationnalSpeed(int pwm) throws SerialConnexionException
	{
		mLocomotion.setRotationnalSpeed(pwm);
	}
	/**
	 * Je vois pas où on peut vérifier le pwm en translation
	 * @param pwm
	 * @throws SerialConnexionException
	 */
	@Test
	public void testSetTranslationnalSpeed(int pwm) throws SerialConnexionException
	{
		mLocomotion.setTranslationnalSpeed(pwm);
	}
	/**
	 * Faut voir avec la série
	 * @throws SerialConnexionException
	 */
	/*
	@Test
	public void testAsservit() throws SerialConnexionException
	{
		mLocomotion.asservit();
	}
	*/
	/**
	 * Fonction qui ne fait rien de Locomotion
	 */
	/*
	@Test
	public void testInitialiser_deplacements()
	{
		mLocomotion.initialiser_deplacements();
	}
	*/
	/**
	 * Test  difficilement faisable à ce niveau, voir la série
	 * @throws SerialConnexionException
	 */
	/*
	@Test
	public void testDisableRotationnalFeedbackLoop() throws SerialConnexionException
	{
		mLocomotion.disableRotationnalFeedbackLoop();
	}
	*/
	/**
	 * Test  difficilement faisable à ce niveau, voir la série
	 * @throws SerialConnexionException
	 */
	/*
	@Test
	public void testEnableRotationnalFeedbackLoop() throws SerialConnexionException 
	{
		mLocomotion.enableRotationnalFeedbackLoop();
	}
	*/
	/**
	 * 
	 * Test  difficilement faisable à ce niveau, voir la série
	 * 
	 * @throws SerialConnexionException
	 */
	/*
	@Test 
	public void testDisableTranslationalFeedbackLoop() throws SerialConnexionException
	{
		mLocomotion.disableTranslationalFeedbackLoop();
		
	}
	*/
	/**
	 * Test  difficilement faisable à ce niveau, voir la sériev
	 * @throws SerialConnexionException 
	 * 
	 */
	/*
	@Test
	public void testEnableTranslationnalFeedbackLoop() throws SerialConnexionException
	{
		mLocomotion.enableTranslationnalFeedbackLoop();
	}
	/*
	/**
	 * 
	 * Test de fermeture de la série
	 * On teste un déplacement et on remarque que le robot n'a pas bougé
	 */
}
