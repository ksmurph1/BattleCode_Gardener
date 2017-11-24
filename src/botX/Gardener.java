package botX;

import battlecode.common.*;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Optional;


class Gardener extends Robot 
{
	private boolean settled = false;
    private Direction gardnerDir = null;
    private boolean builtSold=false;
    private final MapLocation centerHQLocation;
    private final Direction[] dirList={Direction.EAST, Direction.NORTH, Direction.SOUTH,Direction.WEST};
    private final static float ANGLE_ADJ=(float)(Math.PI/18.0f); //current angle in radians
    private final static byte RADIUS=45;
    private static float curAngle;
    Gardener()
    {
    	 MapLocation[] locations=
          		robotController.getInitialArchonLocations(myTeam);
      	// find center of all locations
          float minX=Float.MAX_VALUE;
          float maxX=Float.MIN_VALUE;
          float minY=Float.MAX_VALUE;
          float maxY=Float.MIN_VALUE;
          
         
          for (MapLocation location : locations)
          {
            	if (location.x <= minX)
            	{
            		minX=location.x;
            	}
            	else if (location.x > maxX)
            	{
            		maxX=location.x;
            	}
            	if (location.y <= minY)
            	{
            		minY=location.y;
            	}
            	else if (location.y > maxY)
            	{
            		maxY=location.y;
            	}
          }
         
         centerHQLocation=new MapLocation(maxX - (maxX-minX) * 0.5f,
      	    maxY - (maxY-minY) * 0.5f);
         curAngle+=ANGLE_ADJ;
    	
    }
    public void onUpdate() 
    {
    	float xcircle= centerHQLocation.x + RADIUS * (float)Math.cos(curAngle); 
   	    float ycircle= centerHQLocation.y + RADIUS * (float)Math.sin(curAngle);
   	 MapLocation circleLoc=new MapLocation(xcircle, ycircle);
 	MapLocation enemyLocation=robotController.getInitialArchonLocations(enemy)[0];
        while (true) 
        {
        	
            try 
            { 
                if (gardnerDir == null) 
                {   
                	
                	 // First, try intended direction
                	 gardnerDir=new Direction(spawnLocation,circleLoc);
                	Direction enemyDir=new Direction(enemyLocation,circleLoc);
                	
                	float angle=Math.abs(gardnerDir.radiansBetween(enemyDir));
                    if (angle < Math.PI/1.5f) // pointing in same direction
                    {
                    	gardnerDir=gardnerDir.opposite();
                    }
                	 // see if can move
                	 if ( robotController.canMove(gardnerDir)) 
                     {
                         robotController.move(gardnerDir);
                     }
               
                }
                else if (robotController.canSenseLocation(circleLoc))                		
                {
                	// if not at destination
                	// see if can move
                	if (robotController.canMove(gardnerDir)) 
                    {
                        robotController.move(gardnerDir);
                    }
                }
                else
                   settled=true;  // reached goal
                /*if (!(robotController.isCircleOccupiedExceptByThisRobot(robotController.getLocation(), robotController.getType().bodyRadius * 4.0f))) {
                    settled = true;
                    if (robotController.canPlantTree(dir)) {
                        robotController.plantTree(dir);
                    }
                }*/
               

                TreeInfo[] trees = robotController.senseNearbyTrees(robotType.bodyRadius * 2, myTeam);
                TreeInfo minHealthTree = null;
                for (TreeInfo tree : trees) 
                {
                    if (tree.health < 70) 
                    {
                        if (minHealthTree == null || tree.health < minHealthTree.health) 
                        {
                        	minHealthTree=tree;
                        }
                        	if (robotController.canWater())
                        	{
                        	   robotController.water(minHealthTree.ID);
                        	}
                        
                    }
                }
                //boolean obstacle=robotController.isCircleOccupiedExceptByThisRobot(robotController.getLocation(), robotType.bodyRadius * 1.5f);
                // check threshold for creating a soldier 
                if (!builtSold)
                {
                	Optional<Direction> result=Arrays.stream(dirList).filter(d->robotController.canBuildRobot(
                			RobotType.SOLDIER, d)).findFirst();
                	if (result.isPresent())
                	{
                		robotController.buildRobot(RobotType.SOLDIER, result.get());
                		builtSold=true;
                	}	
                }
                if (settled) 
                {
                	ArrayDeque<Direction> directions=new ArrayDeque<Direction>(Arrays.asList(dirList));
               
                	directions.addFirst(gardnerDir);
                	Optional<Direction> result=directions.stream().filter(d->robotController.canPlantTree(d)).
                        	findFirst();
                        	if (result.isPresent())
                        	{
                        		robotController.plantTree(result.get());
                        	}
                }
                

                Clock.yield();
            } 
            catch (Exception e) 
            {
                System.out.println("A robotController Exception");
                e.printStackTrace();
            }
        }
    }
}
