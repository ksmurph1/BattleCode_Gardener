package botX;
import battlecode.common.*;

class Soldier extends Robot
{
     private MapLocation enemyHQLocation;
     private final static short MAX_BULLETS=100;
     
     Soldier()
     {
    	 MapLocation [] locations=
         		robotController.getInitialArchonLocations(enemy);
    
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
           	if (location.x > maxX)
           	{
           		maxX=location.x;
           	}
           	if (location.y <= minY)
           	{
           		minY=location.y;
           	}
           	if (location.y > maxY)
           	{
           		maxY=location.y;
           	}
         }
        
        enemyHQLocation=new MapLocation(maxX - (maxX-minX) * 0.5f,
     	    maxY - (maxY-minY) * 0.5f);
   
     }
	 public void onUpdate() 
	 {
		     
	        while (true)
            {
	        	MapLocation curLoc=robotController.getLocation();
	        	
	            try 
	            {  
	                for (BulletInfo bullet : robotController.senseNearbyBullets())
                    {
                  	  if (willCollideWithMe(bullet))
                  	  {
                  		  // if bullet will collide with me, move
                  		  if (robotController.canMove(bullet.dir.rotateLeftRads((float)Math.PI/2.0f)))
                  	      {
                  			robotController.move(bullet.dir.rotateLeftRads((float)Math.PI/2.0f));  
                  		  }
                  	  }
                    }
	            	 // try to detect enemy robots within radius
	                   RobotInfo[] enemyRobots=robotController.senseNearbyRobots(robotType.sensorRadius,enemy);
	                   if (robotController.getTeamBullets() >= MAX_BULLETS && 
	                		   enemyRobots.length > 0)
	                   {
	                	  if (robotController.canFirePentadShot()) 
	                	  {  
	                		  // fire at the closest enemy          
	                		  robotController.firePentadShot(new Direction(curLoc,enemyRobots[0].location));
	                	  }
	                  
	                   }
	                   else
	                   {
	                	   // no enemy robots
	                	   // not at location yet, keep moving
	                	  if (!tryMove(new Direction(curLoc,enemyHQLocation))) 
		                       {
		                    	   // if can't move in this direction, move in random direction
		                    	   tryMove(randomDirection());
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

