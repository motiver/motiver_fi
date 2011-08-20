/*******************************************************************************
 * Copyright 2011 Antti Havanko
 * 
 * This file is part of Motiver.fi.
 * Motiver.fi is licensed under one open source license and one commercial license.
 * 
 * Commercial license: This is the appropriate option if you want to use Motiver.fi in 
 * commercial purposes. Contact license@motiver.fi for licensing options.
 * 
 * Open source license: This is the appropriate option if you are creating an open source 
 * application with a license compatible with the GNU GPL license v3. Although the GPLv3 has 
 * many terms, the most important is that you must provide the source code of your application 
 * to your users so they can be free to modify your application for their own needs.
 ******************************************************************************/
// $codepro.audit.disable codeInComments
/*******************************************************************************
 * Copyright 2011 Delect
 * 
 * Project: Motiver.fi
 * Author: Antti Havanko
 ******************************************************************************/
package com.delect.motiver.client.presenter.training;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;

import com.delect.motiver.client.Motiver;
import com.delect.motiver.client.MyAsyncCallback;
import com.delect.motiver.client.event.RoutineCreatedEvent;
import com.delect.motiver.client.lang.LangConstantsWizard;
import com.delect.motiver.client.lang.LangWizard;
import com.delect.motiver.client.presenter.Presenter;
import com.delect.motiver.client.service.MyServiceAsync;
import com.delect.motiver.client.view.Display;
import com.delect.motiver.shared.ExerciseModel;
import com.delect.motiver.shared.RoutineModel;
import com.delect.motiver.shared.WorkoutModel;

/**
 * Wizard where user can create bodybuilding or powerlifting routine
 * <br>Fires {@link com.delect.motiver.client.event.RoutineModelCreatedEvent RoutineModelCreatedEvent} when routine is created
 * @author Antti
 *
 */
public class RoutineWizardPresenter extends Presenter {

	/**
	* Abstract class for view to extend
	*/
	public abstract static class RoutineWizardDisplay extends Display {

		public abstract void setHandler(RoutineWizardHandler routineWizardHandler);
		public abstract void setMessageBodybuilding(String msg);
		public abstract void setMessagePowerlifting(String msg);
	}

	public interface RoutineWizardHandler {
		void newBodybuildingRoutine(int split, int muscleGroupDiv, int howOften, int trainingDays, int weakMuscle);
		void newPowerliftingRoutine(int priority, int howOften, int squat, int deadlift, int bench, int military);
	}
	public static LangWizard Lang = GWT.create(LangWizard.class);


	public static LangConstantsWizard LangConstants = GWT.create(LangConstantsWizard.class);
	private static int bench;
	private static int deadlift;
	private static final int EQUIPMENT_BARBELL = 1;
	private static final int EQUIPMENT_CABLE = 3;
	private static final int EQUIPMENT_DUMBBELL = 2;
	private static final int EQUIPMENT_EZ_BAR = 5;
	private static final int EQUIPMENT_LEVER = 4;
	private static final int EQUIPMENT_NONE = 0;
	private static int howOften;
	private static int military;
	private static int muscleGroupDiv;
	private static int priority;
	private static final int ROUTINE_MAX_DAYS = 28;
	private static final int ROUTINE_MIN_DAYS = 8;
	private static int split;
	private static int squat;
	private static int trainingDays;
	
	private static int weakMuscle;
	/**
	 * Creates 4-split
	 * @param index : 
	 * @param choice : 
	 * @return
	 */
	public static WorkoutModel createEgForFour(int index, int choice) {
    final WorkoutModel workout = new WorkoutModel();
		
		//make exercises
		workout.setExercises(createExerToFour(index));
		
		return workout;
		  
	}
	
	/**
	 * Creates full body workout
	 * @return
	 */
	public static WorkoutModel createEgForOne(int nro) {
    final WorkoutModel workout = new WorkoutModel();
		
		//make exercises
		workout.setExercises(createExerToOne(nro));
		
		return workout;
		  
	}


	/**
	 * Creates 3-split
	 * @param index : 
	 * @param choice : 
	 * @return
	 */
	public static WorkoutModel createEgForThree(int index, int choice) {
    final WorkoutModel workout = new WorkoutModel();
		
		//make exercises
		workout.setExercises(createExerToThree(index));
		
		return workout;
		  
	}

	/**
	 * Creates 2-split
	 * @param index : 
	 * @param choice : 
	 * @return
	 */
	public static WorkoutModel createEgForTwo(int index, int choice) {
    final WorkoutModel workout = new WorkoutModel();
		
		//make exercises
		workout.setExercises(createExerToTwo(index));
		
		return workout;
		  
	}


	//4-JAKOINEN
	///////////////
	static List<ExerciseModel> createExerToFour(int nro) {
		int v1 = 0;
		final List<ExerciseModel> exercises = new ArrayList<ExerciseModel>();
	  	
    switch(muscleGroupDiv) {
	  		case 0:
	  			/*
	  			1. Chest, biceps, forearms
  				-Bench press Barbell / kp x3
  				-Incline bench press kp / Barbell x3
  				-Ristitalja / Pec-deck / viparit maaten x2
  				-Hauis Barbell / kp x3
  				-Hauis scott kp / Barbell x2
  				-Concentration curl x2
  				-Rannek??nt? x3
  				
  				2. Legs
  				-Squat / front squat / Leg press x4
  				-Leg extension x3-4
  				-SLDL x 4
  				-Leg curls x 2-3
  				-Calves seated / standing 5-6
  				
  				3. Shoulders, triceps
  				-Pystypunnerrus Barbell / kp x3-4
  				-Lateral raise x2
  				-Rear lateral raise x2
  				-Close grip bench press x3
  				-Rankalainen punnerrus x3
  				-Ojentajapunnerrus x2
  				
  				4. Back, ep?kk??t
  				-Bent-over row Barbell / kp x 3
  				-Alatalja x 3
  				-Front pulldown leve? / leuanveto x3
  				-Front pulldown kapea x3
  				-Shrug x3
			  	*/

			  	//TREENI 1  
  				if(nro == 1) {
  			  	//weak chest
  			  	if(weakMuscle == 2) {
  			  		v1=4;
            }
  			  	else {
  			  		v1=3;
            }
  			  	//Bench press Barbell / kp x3
  			  	exercises.add(new ExerciseModel(Lang.Bench(), EQUIPMENT_BARBELL, v1, "6-10"));
  			
  			  	//Incline bench press kp / Barbell x3
  			  	exercises.add(new ExerciseModel(Lang.InclineBenchPress(), EQUIPMENT_DUMBBELL, 3, "6-10"));
  			  		
  			  	//Ristitalja / Pec-deck / viparit maaten x2
  			  	exercises.add(new ExerciseModel(Lang.StandingFlies(), EQUIPMENT_CABLE, 2, "6-10"));

  			  	//Hauis Barbell / kp x3
  			  	exercises.add(new ExerciseModel(Lang.BicepCurl(), EQUIPMENT_EZ_BAR, 3, "6-10"));
  			  		
  			  	//Hauis scott kp / Barbell x2
  			  	exercises.add(new ExerciseModel(Lang.BicepCurl(), EQUIPMENT_DUMBBELL, 2, "6-10"));
  			
  			  	//Concentration curl x2
  			  	exercises.add(new ExerciseModel(Lang.ConcentrationCurl(), EQUIPMENT_DUMBBELL, 2, "6-10"));
  				
  			  	//Rankek??nn?t x3
  			  	exercises.add(new ExerciseModel(Lang.WristCurl(), EQUIPMENT_BARBELL, 3, "10-15")); 
  					
  				}
  			  	
  		  	//TREENI 2  
  				if(nro == 2) {
  			  	
            //Squat / front squat / Leg press x4
            exercises.add(new ExerciseModel(Lang.Squat(), EQUIPMENT_BARBELL, 4, "6-8"));
            
            //weak etu
            if(weakMuscle == 0) {
            	exercises.add(new ExerciseModel(Lang.FrontSquat(), EQUIPMENT_BARBELL, 2, "6-8"));
            }
            
            //Leg extension x3-4
            exercises.add(new ExerciseModel(Lang.LegExtension(), EQUIPMENT_LEVER, 4, "8-12"));
            
            //weak taka
            if(weakMuscle == 1) {
            	v1=4;
            }
            else {
            	v1=3;
            }
            //SLDL x 4
            exercises.add(new ExerciseModel(Lang.StraightLegDeadlift(), EQUIPMENT_BARBELL, v1, "6-10"));
            
            //Leg curls x 2-3
            exercises.add(new ExerciseModel(Lang.SeatedLegCurl(), EQUIPMENT_LEVER, v1, "10-12"));
            
            //Calves seated / standing 5-6
            exercises.add(new ExerciseModel(Lang.SeatedCalfExtension(), EQUIPMENT_LEVER, 5, "10-15"));
  				
  				}
  			  	
  		  	//TREENI 3  
  				if(nro == 3) {
  			  	
            //weak olkap
            if(weakMuscle == 6) {
            	v1=4;
            }
            else {
            	v1=3;
            }
            //Pystypunnerrus Barbell / kp x3-4
            exercises.add(new ExerciseModel(Lang.ShoulderPress(), EQUIPMENT_BARBELL, v1, "6-10"));
            	
            //Viparit sivulle x2
            exercises.add(new ExerciseModel(Lang.LateralRaise(), EQUIPMENT_DUMBBELL, 3, "6-10"));
            	
            //Viparit taakse x2
            exercises.add(new ExerciseModel(Lang.RearLateralRaise(), EQUIPMENT_DUMBBELL, 2, "6-10"));
            	
            //Close grip bench press x3
            exercises.add(new ExerciseModel(Lang.LyingTricepsExtension(), EQUIPMENT_EZ_BAR, 3, "6-10"));
            
            //Rankalainen punnerrus x3
            exercises.add(new ExerciseModel(Lang.CloseGripBenchPress(), EQUIPMENT_BARBELL, 3, "6-10"));
            
            //Ojentajapunnerrus x2
            exercises.add(new ExerciseModel(Lang.TricepExtension(), EQUIPMENT_DUMBBELL, 2, "6-10"));
  				
  				}
  			  	
  		  	//TREENI 4  
  				if(nro == 4) {
  			  	
            //Bent-over row Barbell / kp x 3
            exercises.add(new ExerciseModel(Lang.BentOverRow(), EQUIPMENT_BARBELL, 3, "6-10"));
            
            //weak back
            if(weakMuscle == 3) {
            	v1=4;
            }
            else {
            	v1=3;
            }
            //Alatalja x 3
            exercises.add(new ExerciseModel(Lang.SeatedRow(), EQUIPMENT_CABLE, v1, "8-10"));
            
            //Front pulldown leve? / leanveto x3
            exercises.add(new ExerciseModel(Lang.FrontPulldown(), EQUIPMENT_CABLE, v1, "6-10"));
            
            //Front pulldown kapea x2
            exercises.add(new ExerciseModel(Lang.CloseGripPulldown(), EQUIPMENT_CABLE, 3, "6-10"));
            
            //Shrug x3
            exercises.add(new ExerciseModel(Lang.Shrug(), EQUIPMENT_BARBELL, 3, "6-10"));
            	  	
            //weak calves
          	if(weakMuscle == 7) {
            	//Calves seated / standing 4
            	exercises.add(new ExerciseModel(Lang.SeatedCalfExtension(), EQUIPMENT_LEVER, 4, "10-15"));
            }
  				}
  			  			  	
  		  	break;
			
	  		case 1:
	  			/*
  				1. Arms
  				-Hauis Barbell / kp x3
  				-Hauis scott kp / Barbell x2
  				-Concentration curl x2
  				-Rannek??nt? x3
  				-Close grip bench press x3
  				-Rankalainen punnerrus x3
  				-Ojentajapunnerrus x2
  	  			
  				2. Legs
  				-Squat / front squat / Leg press x4
  				-Leg extension x3-4
  				-SLDL x 4
  				-Leg curls x 2-3
  				-Calves seated / standing 5-6
  				
  				3. Chest, shoulders
  				-Bench press Barbell / kp x3
  				-Incline bench press kp / Barbell x3
  				-Ristitalja / Pec-deck / viparit maaten x2
  				-Pystypunnerrus Barbell / kp x3-4
  				-Lateral raise x2
  				-Rear lateral raise x2
  				
  				4. Back, ep?kk??t
  				-Bent-over row Barbell / kp x 3
  				-Alatalja x 3
  				-Front pulldown leve? / leuanveto x3
  				-Front pulldown kapea x3
  				-Shrug x3
			  	*/

			  	//TREENI 1  
	  		  if(nro == 1) {
			  	
	  		    //Hauis Barbell / kp x3
				  	exercises.add(new ExerciseModel(Lang.BicepCurl(), EQUIPMENT_EZ_BAR, 3, "6-10"));
				  		
				  	//Hauis scott kp / Barbell x2
				  	exercises.add(new ExerciseModel(Lang.BicepCurl(), EQUIPMENT_DUMBBELL, 2, "6-10"));
				
				  	//Concentration curl x2
				  	exercises.add(new ExerciseModel(Lang.ConcentrationCurl(), EQUIPMENT_DUMBBELL, 2, "6-10"));
					
  					//Rankek??nn?t x3
  					exercises.add(new ExerciseModel(Lang.WristCurl(), EQUIPMENT_BARBELL, 3, "10-15"));
					
  					//Close grip bench press x3
				  	exercises.add(new ExerciseModel(Lang.LyingTricepsExtension(), EQUIPMENT_EZ_BAR, 3, "6-10"));
			
				  	//Rankalainen punnerrus x3
				  	exercises.add(new ExerciseModel(Lang.CloseGripBenchPress(), EQUIPMENT_BARBELL, 3, "6-10"));
				  	
				  	//Ojentajapunnerrus x2
				  	exercises.add(new ExerciseModel(Lang.TricepExtension(), EQUIPMENT_DUMBBELL, 2, "6-10"));
				
	  		  }
			  	
			  	//TREENI 2  
  				if(nro == 2) {
  			  	
  					//Squat / front squat / Leg press x4
				  	exercises.add(new ExerciseModel(Lang.Squat(), EQUIPMENT_BARBELL, 4, "6-8"));
				  	
				  	//weak etu
				  	if(weakMuscle == 0) {
				  		exercises.add(new ExerciseModel(Lang.FrontSquat(), EQUIPMENT_BARBELL, 2, "6-8"));
				  	}
				  	
  					//Leg extension x3-4
  					exercises.add(new ExerciseModel(Lang.LegExtension(), EQUIPMENT_LEVER, 4, "8-12"));
					
				  	//weak taka
				  	if(weakMuscle == 1) {
				  	  v1=4;
				  	}
				  	else {
				  	  v1=3;
				  	}
					
				  	//SLDL x 4
				  	v1=2;
				  	exercises.add(new ExerciseModel(Lang.StraightLegDeadlift(), EQUIPMENT_BARBELL, v1, "6-10"));
				
				  	//Leg curls x 2-3
				  	exercises.add(new ExerciseModel(Lang.SeatedLegCurl(), EQUIPMENT_LEVER, v1, "10-12"));
				  		
				  	//Calves seated / standing 5-6
				  	exercises.add(new ExerciseModel(Lang.SeatedCalfExtension(), EQUIPMENT_LEVER, 5, "10-15"));
				
  				}
			  	
			  	//TREENI 3  
  				if(nro == 3) {
			  	
				  	//weak chest
				  	if(weakMuscle == 2) {
				  		v1=4;
				  	}
				  	else {
				  		v1=3;
				  	}
				  	
				  	//Bench press Barbell / kp x3
				  	exercises.add(new ExerciseModel(Lang.Bench(), EQUIPMENT_BARBELL, v1, "6-10"));
				
				  	//Incline bench press kp / Barbell x3
				  	exercises.add(new ExerciseModel(Lang.InclineBenchPress(), EQUIPMENT_DUMBBELL, 3, "6-10"));
				  		
				  	//Ristitalja / Pec-deck / viparit maaten x2
				  	exercises.add(new ExerciseModel(Lang.StandingFlies(), EQUIPMENT_CABLE, 2, "6-10"));
				  		
				  	//weak olkap
				  	if(weakMuscle == 6) {
				  		v1=4;
				  	}
				  	else {
				  		v1=3;
				  	}
					
				  	//Pystypunnerrus Barbell / kp x3-4
				  	exercises.add(new ExerciseModel(Lang.ShoulderPress(), EQUIPMENT_BARBELL, v1, "6-10"));
				  		
				  	//Viparit sivulle x2
				  	exercises.add(new ExerciseModel(Lang.LateralRaise(), EQUIPMENT_DUMBBELL, 3, "6-10"));
				  		
				  	//Viparit taakse x2
				  	exercises.add(new ExerciseModel(Lang.RearLateralRaise(), EQUIPMENT_DUMBBELL, 2, "6-10"));
				
				  }
			  	
			  	//TREENI 4  
  				if(nro == 4) {
			  	
  				  //Bent-over row Barbell / kp x 3
				  	exercises.add(new ExerciseModel(Lang.BentOverRow(), EQUIPMENT_BARBELL, 3, "6-10"));
					
				  	//weak back
				  	if(weakMuscle == 3) {
				  		v1=4;
				  	}
				  	else {
				  		v1=3;
				  	}
				  	
  					//Alatalja x 3
  					exercises.add(new ExerciseModel(Lang.SeatedRow(), EQUIPMENT_CABLE, v1, "8-10"));
				
  					//Front pulldown leve? / leanveto x3
				  	exercises.add(new ExerciseModel(Lang.FrontPulldown(), EQUIPMENT_CABLE, v1, "6-10"));
					
				  	//Front pulldown kapea x2
				  	exercises.add(new ExerciseModel(Lang.CloseGripPulldown(), EQUIPMENT_CABLE, 3, "6-10"));
				  	
				  	//Shrug x3
				  	exercises.add(new ExerciseModel(Lang.Shrug(), EQUIPMENT_BARBELL, 3, "6-10"));
							  	
				  	//weak calves
				  	if(weakMuscle == 7) {
				  	  //Calves seated / standing 4
					  	exercises.add(new ExerciseModel(Lang.SeatedCalfExtension(), EQUIPMENT_LEVER, 4, "10-15"));
				  	}
  				}
			  			  	
			  	break;
			  	
	  		case 2: 
	  			/*
  				1. Chest, shoulders
  				-Bench press Barbell / kp x3
  				-Incline bench press kp / Barbell x3
  				-Ristitalja / Pec-deck / viparit maaten x2
  				-Pystypunnerrus Barbell / kp x3-4
  				-Lateral raise x2
  				-Rear lateral raise x2
  				-Shrug x3
  				
  				2. Quads,calves
  				-Squat / front squat x4
  				-Leg press x3
  				-Leg extension x3
  				-Calves seated / standing 4
  				
  				3. Back,triceps
  				-Bent-over row Barbell / kp x 3
  				-Alatalja x 3
  				-Front pulldown leve? / leuanveto x3
  				-Front pulldown kapea x3
  				-Close grip bench press x3
  				-Rankalainen punnerrus x3
  				-Ojentajapunnerrus x2
  				
  				4. Hamstrings,calves,biceps
  				-SLDL x 4
  				-Leg curls x 3
  				-Calves seated / standing 4
  				-Hauis Barbell / kp x3
  				-Hauis scott kp / Barbell x2
  				-Concentration curl x2
  				-Rannek??nt? x3
			  	*/
			  				    
			    //TREENI 1  
  				if(nro == 1) {

				  	//weak chest
				  	if(weakMuscle == 2) {
				  		v1=4;
				  	}
				  	else {
				  		v1=3;
				  	}
					
				  	//Bench press Barbell / kp x3
				  	exercises.add(new ExerciseModel(Lang.Bench(), EQUIPMENT_BARBELL, v1, "6-10"));
				
				  	//Incline bench press kp / Barbell x3
				  	exercises.add(new ExerciseModel(Lang.InclineBenchPress(), EQUIPMENT_DUMBBELL, 3, "6-10"));
				  		
				    //Ristitalja / Pec-deck / viparit maaten x2
				  	exercises.add(new ExerciseModel(Lang.StandingFlies(), EQUIPMENT_CABLE, 2, "6-10"));
				  		
				  	//weak olkap
				  	if(weakMuscle == 6) {
				  		v1=4;
				  	}
				  	else {
				  		v1=3;
				  	}
					
				  	//Pystypunnerrus Barbell / kp x3-4
				  	exercises.add(new ExerciseModel(Lang.ShoulderPress(), EQUIPMENT_BARBELL, v1, "6-10"));
				  		
				  	//Viparit sivulle x2
				  	exercises.add(new ExerciseModel(Lang.LateralRaise(), EQUIPMENT_DUMBBELL, 3, "6-10"));
				  		
				  	//Viparit taakse x2
				  	exercises.add(new ExerciseModel(Lang.RearLateralRaise(), EQUIPMENT_DUMBBELL, 2, "6-10"));
				  		
				  	//Shrug x3
				  	exercises.add(new ExerciseModel(Lang.Shrug(), EQUIPMENT_BARBELL, 3, "6-10"));
				
  				}
				
			    //TREENI 2  
  				if(nro == 2) {
			    
  				  //Squat / front squat / Leg press x4
				  	exercises.add(new ExerciseModel(Lang.Squat(), EQUIPMENT_BARBELL, 4, "6-8"));
				  	
				  	//weak etu
				  	if(weakMuscle == 0) {
				  		exercises.add(new ExerciseModel(Lang.FrontSquat(), EQUIPMENT_BARBELL, 2, "6-8"));
				  	}
				  							
				  	//Leg press x3
				  	exercises.add(new ExerciseModel(Lang.LegPress(), EQUIPMENT_LEVER, 2, "8-10"));
				  	
				  	//Leg extension x3
				  	exercises.add(new ExerciseModel(Lang.LegExtension(), EQUIPMENT_LEVER, 3, "8-12"));
					
				  	//Calves seated / standing 4
				  	exercises.add(new ExerciseModel(Lang.SeatedCalfExtension(), EQUIPMENT_LEVER, 4, "10-15"));
				
  				}
				
			    //TREENI 3  
  				if(nro == 3) {
			    
  				  //Bent-over row Barbell / kp x 3
				  	exercises.add(new ExerciseModel(Lang.BentOverRow(), EQUIPMENT_BARBELL, 3, "6-10"));
					
				  	//weak back
				  	if(weakMuscle == 3) {
				  		v1=4;
				  	}
				  	else {
				  		v1=3;
				  	}
				  	
  					//Alatalja x 3
  					exercises.add(new ExerciseModel(Lang.SeatedRow(), EQUIPMENT_CABLE, v1, "8-10"));
				
  					//Front pulldown leve? / leanveto x3
				  	exercises.add(new ExerciseModel(Lang.FrontPulldown(), EQUIPMENT_CABLE, v1, "6-10"));
					
				  	//Front pulldown kapea x2
				  	exercises.add(new ExerciseModel(Lang.CloseGripPulldown(), EQUIPMENT_CABLE, 3, "6-10"));
					
				  	//Close grip bench press x3
				  	exercises.add(new ExerciseModel(Lang.LyingTricepsExtension(), EQUIPMENT_EZ_BAR, 3, "6-10"));
			
				  	//Rankalainen punnerrus x3
				  	exercises.add(new ExerciseModel(Lang.CloseGripBenchPress(), EQUIPMENT_BARBELL, 3, "6-10"));
				  	
				  	//Ojentajapunnerrus x2
				  	exercises.add(new ExerciseModel(Lang.TricepExtension(), EQUIPMENT_DUMBBELL, 2, "6-10"));
				
  				}
			  	
			  	//TREENI 4  
  				if(nro == 4) {
			  	
				  	//weak taka
				  	if(weakMuscle == 1) {
  						v1=4;
				  	}
  					else {
  						v1=3;
  					}
				  	
				  	//SLDL x 4
			  		exercises.add(new ExerciseModel(Lang.StraightLegDeadlift(), EQUIPMENT_BARBELL, (v1 + 1), "6-10"));
				
			  		//Leg curls x 2-3
				  	exercises.add(new ExerciseModel(Lang.SeatedLegCurl(), EQUIPMENT_LEVER, v1, "10-12"));
				  		
				  	//Hauis Barbell / kp x3
				  	exercises.add(new ExerciseModel(Lang.BicepCurl(), EQUIPMENT_EZ_BAR, 3, "6-10"));
				  		
				  	//Hauis scott kp / Barbell x2
				  	exercises.add(new ExerciseModel(Lang.BicepCurl(), EQUIPMENT_DUMBBELL, 2, "6-10"));
				
				  	//Concentration curl x2
				  	exercises.add(new ExerciseModel(Lang.ConcentrationCurl(), EQUIPMENT_DUMBBELL, 2, "6-10"));
					
  					//Rankek??nn?t x3
  					exercises.add(new ExerciseModel(Lang.WristCurl(), EQUIPMENT_BARBELL, 3, "10-15"));
								  	
				  	//weak calves
				  	if(weakMuscle == 7) {
  						//Calves seated / standing 4
              exercises.add(new ExerciseModel(Lang.SeatedCalfExtension(), EQUIPMENT_LEVER, 4, "10-15"));
  					}
  				}
  				
			    break;
			    
    }
			
    return exercises;
	  
	}

	//EXERCISES
	////////////////
	//FULL BODY WORKOUT
	///////////////
	static List<ExerciseModel> createExerToOne(int nro) {
		int v1 = 0;
		final List<ExerciseModel> exercises = new ArrayList<ExerciseModel>();
		/*
		front squat 4x6-8
		leg curl 2-3x10-12
		calves seated 3x10-15
		Bent-over row barbell 2x8-12
		pystypunnerrus k?sipainoilla 2x6-10
		Close grip bench press 2x6-10
		
		Straight-leg DL 2-3x8-12
		Leg press 4x6-10
		calves standing 3x8-12
		Chin-up 2x6-8
		Incline bench press barbell 2x6-10
		Bicep curl barbell my?t?otteella 2x8-12
		*/

		//TREENI 1   
		if(nro == 1) {
		
			//Front squat
			exercises.add(new ExerciseModel(Lang.FrontSquat(), EQUIPMENT_BARBELL, 4, "6-8"));
						
			//weak etu
			if(weakMuscle == 0) {
				exercises.add(new ExerciseModel(Lang.LegExtension(), EQUIPMENT_LEVER, 2, "8-10"));
			}

			//weak taka
			if(weakMuscle == 1) {
				v1=5;
			}
			else {
				v1=3;
			}
				
			//leg curl
			exercises.add(new ExerciseModel(Lang.SeatedLegCurl(), EQUIPMENT_LEVER, v1, "10-12"));
			
			//weak pohk
			if(weakMuscle == 7) {
				v1=5;
			}
			else {
				v1=3;
			}
			
			//POHKEET
			exercises.add(new ExerciseModel(Lang.SeatedCalfExtension(), EQUIPMENT_LEVER, v1, "10-12"));
				
			//weak chest
			if(weakMuscle == 2) {
				exercises.add(new ExerciseModel(Lang.InclineBenchPress(), EQUIPMENT_BARBELL, 2, "6-10"));
			}
			
			//Bent-over row
			exercises.add(new ExerciseModel(Lang.BentOverRow(), EQUIPMENT_BARBELL, 2, "8-12"));

			//weak back
			if(weakMuscle == 3) {
				//Chin-up
				exercises.add(new ExerciseModel(Lang.ChinUp(), EQUIPMENT_NONE, 1, "6-8"));
			}
			
			//weak olkap
			if(weakMuscle == 6) {
				v1=4;
			}
			else {
				v1=2;
			}
			
			//PYSTYP
			exercises.add(new ExerciseModel(Lang.ShoulderPress(), EQUIPMENT_DUMBBELL, v1, "6-10"));
			
			//KAPEA PENA
			exercises.add(new ExerciseModel(Lang.CloseGripBenchPress(), EQUIPMENT_BARBELL, 2, "6-10"));
			
		}
			
		//TREENI 2
		if(nro == 2) {
			//weak taka
			if(weakMuscle == 1) {
				v1=5;
      }
			else {
				v1=3;
      }
				
			//SLDL
			exercises.add(new ExerciseModel(Lang.StraightLegDeadlift(), EQUIPMENT_BARBELL, v1, "8-12"));
			
			//Leg press
			exercises.add(new ExerciseModel(Lang.LegPress(), EQUIPMENT_LEVER, 4, "6-10"));
			
			//weak etu
			if(weakMuscle == 0) {
				exercises.add(new ExerciseModel(Lang.LegExtension(), EQUIPMENT_LEVER, 2, "8-10"));
			}
			
			//weak pohk
			if(weakMuscle == 7) {
				v1=5;
      }
			else {
				v1=3;
      }
				  	
			//POHKEET
			exercises.add(new ExerciseModel(Lang.StandingCalfRaise(), EQUIPMENT_BARBELL, v1, "10-15"));

			//Chin-up
			exercises.add(new ExerciseModel(Lang.ChinUp(), EQUIPMENT_NONE, 2, "6-8"));
			
			//weak back
			if(weakMuscle == 3) {
				//Bent-over row
				exercises.add(new ExerciseModel(Lang.BentOverRow(), EQUIPMENT_BARBELL, 1, "8-12"));
			}
			
			//weak chest
			if(weakMuscle == 2) {
				v1=4;
      }
			else {
				v1=2;
      }
				
			//VINOP
			exercises.add(new ExerciseModel(Lang.InclineBenchPress(), EQUIPMENT_BARBELL, v1, "6-10"));
			
			//HAUIS
			exercises.add(new ExerciseModel(Lang.BicepCurl(), EQUIPMENT_EZ_BAR, 2, "6-10"));
				
		}
		
		return exercises;
	
	}

	//3-JAKOINEN
	///////////////
	static List<ExerciseModel> createExerToThree(int nro) {
		int v1 = 0;
		final List<ExerciseModel> exercises = new ArrayList<ExerciseModel>();
	  	
    switch(muscleGroupDiv) {
	  		case 0:
        /*
        1. Chest, shoulders, ojenatajat, ep?kk??t
				-Bench press Barbell / kp x 2
				-Incline bench press kp / Barbell x 2
				-Ristitalja / viparit maaten / Pec-deck x2
				-Viparit sivulle x2
				-Viparit taakse x2
				-Pystypunnerrus x2
				-Close grip bench press / Lying triceps extension x3
				-Ojentajapunnerrus x3
				-Shrug x3
				
				2. Legs
				-Squat / front squat / Leg press x4
				-Leg extension x3-4
				-SLDL x 4
				-Leg curls x 2-3
				-Calves seated / standing 5-6
				
				3. Back, biceps, forearms
				-Bent-over row x 3
				-Alatalja x 2
				-Front pulldown leve? / leanveto x3
				-Front pulldown kapea x2
				-Biceps Barbell / kp x3
				-Biceps scott Barbell / kp x3
				-Rankek??nn?t x3
        */

        //TREENI 1  
				if(nro == 1) {
								  
          //weak chest
          if(weakMuscle == 2) {
            v1=3;
          }
          else {
            v1=2;
          }
					//Bench press Barbell / kp x 2
          exercises.add(new ExerciseModel(Lang.Bench(), EQUIPMENT_BARBELL, v1, "6-10"));
				
					//Incline bench press kp / Barbell x 2
          exercises.add(new ExerciseModel(Lang.InclineBenchPress(), EQUIPMENT_DUMBBELL, v1, "6-10"));
				  		
					//Ristitalja / viparit maaten / Pec-deck x2
          exercises.add(new ExerciseModel(Lang.StandingFlies(), EQUIPMENT_CABLE, 2, "6-10"));
				  		
          //weak olkap
          if(weakMuscle == 6) {
            v1=3;
          }
          else {
            v1=2;
          }
					//Pystypunnerrus x2
          exercises.add(new ExerciseModel(Lang.ShoulderPress(), EQUIPMENT_BARBELL, v1, "6-10"));
				  		
					//Viparit sivulle x2
          exercises.add(new ExerciseModel(Lang.LateralRaise(), EQUIPMENT_DUMBBELL, v1, "6-10"));
				  		
					//Viparit taakse x2
          exercises.add(new ExerciseModel(Lang.RearLateralRaise(), EQUIPMENT_DUMBBELL, 2, "6-10"));
					
					//Close grip bench press / Lying triceps extension x3
          exercises.add(new ExerciseModel(Lang.LyingTricepsExtension(), EQUIPMENT_EZ_BAR, 3, "6-10"));
				  		
					//Ojentajapunnerrus x3
          exercises.add(new ExerciseModel(Lang.TricepExtension(), EQUIPMENT_DUMBBELL, 3, "6-10"));
				  		
					//Shrug x3
          exercises.add(new ExerciseModel(Lang.Shrug(), EQUIPMENT_BARBELL, 3, "6-10"));
				  	
				}
			  	
        //TREENI 2  
				if(nro == 2) {
				  
					//Squat / front squat / Leg press x4
          exercises.add(new ExerciseModel(Lang.Squat(), EQUIPMENT_BARBELL, 4, "6-8"));
				  	
          //weak etu
          if(weakMuscle == 0) {
            exercises.add(new ExerciseModel(Lang.FrontSquat(), EQUIPMENT_BARBELL, 2, "6-8"));
          }
				  		
					//Leg extension x3-4
					exercises.add(new ExerciseModel(Lang.LegExtension(), EQUIPMENT_LEVER, 4, "8-12"));
					
					//SLDL x 4
					v1=2;
          exercises.add(new ExerciseModel(Lang.StraightLegDeadlift(), EQUIPMENT_BARBELL, 4, "6-10"));
				
          //weak taka
          if(weakMuscle == 1) {
            v1+=1;
          }
				  	
					//Leg curls x 2-3
          exercises.add(new ExerciseModel(Lang.SeatedLegCurl(), EQUIPMENT_LEVER, v1, "10-12"));

					//Calves seated / standing 5-6
          exercises.add(new ExerciseModel(Lang.SeatedCalfExtension(), EQUIPMENT_LEVER, 5, "10-15"));

				}
			  	
        //TREENI 3  
				if(nro == 3) {
					//Bent-over row x 3
					v1=2;
          exercises.add(new ExerciseModel(Lang.BentOverRow(), EQUIPMENT_BARBELL, 3, "6-10"));
					
					//Alatalja x 2
					exercises.add(new ExerciseModel(Lang.SeatedRow(), EQUIPMENT_CABLE, v1, "8-10"));
				
					//Front pulldown leve? / leanveto x3
          exercises.add(new ExerciseModel(Lang.FrontPulldown(), EQUIPMENT_CABLE, 3, "6-10"));
					
          //weak back
          if(weakMuscle == 3) {
            v1=5;
          }
          else {
            v1=3;
          }
					//Front pulldown kapea x2
          exercises.add(new ExerciseModel(Lang.CloseGripPulldown(), EQUIPMENT_CABLE, v1, "6-10"));
					
					//Biceps Barbell / kp x3
          exercises.add(new ExerciseModel(Lang.BicepCurl(), EQUIPMENT_EZ_BAR, 3, "6-10"));
				  		
					//Biceps scott Barbell / kp x3
          exercises.add(new ExerciseModel(Lang.BicepCurl(), EQUIPMENT_DUMBBELL, 3, "6-10"));
				
					//Rankek??nn?t x3
					exercises.add(new ExerciseModel(Lang.WristCurl(), EQUIPMENT_BARBELL, 3, "10-15")); 
							  	
					//weak pohk
          if(weakMuscle == 7) {
						//Calves seated / standing 5-6
            exercises.add(new ExerciseModel(Lang.SeatedCalfExtension(), EQUIPMENT_LEVER, 3, "10-15"));
					}
				}
			  			  	
        break;
			
	  		case 1:
        /*
        1. Chest,back
				-Bench press Barbell / kp x 2
				-Incline bench press kp / Barbell x 2
				-Ristitalja / viparit maaten / Pec-deck x2
				-Bent-over row x 3
				-Alatalja x 2
				-Front pulldown leve? / leanveto x3
				-Front pulldown kapea x2
				
				2. Legs
				-Squat / front squat / Leg press x4
				-Leg extension x3-4
				-SLDL x 4
				-Leg curls x 2-3
				-Calves seated / standing 5-6
				
				3. Shoulders,arms
				-Viparit sivulle x2
				-Viparit taakse x2
				-Pystypunnerrus x2
				-Shrug x3
				-Close grip bench press / Lying triceps extension x3
				-Ojentajapunnerrus x3
				-Biceps Barbell / kp x3
				-Biceps scott Barbell / kp x3
				-Rankek??nn?t x3
        */

        //TREENI 1  
				if(nro == 1) {
			  	
          //weak chest
          if(weakMuscle == 2) {
            v1=3;
          }
          else {
            v1=2;
          }
					//Bench press Barbell / kp x 2
          exercises.add(new ExerciseModel(Lang.Bench(), EQUIPMENT_BARBELL, v1, "6-10"));
				
					//Incline bench press kp / Barbell x 2
          exercises.add(new ExerciseModel(Lang.InclineBenchPress(), EQUIPMENT_DUMBBELL, v1, "6-10"));
				  		
					//Ristitalja / viparit maaten / Pec-deck x2
          exercises.add(new ExerciseModel(Lang.StandingFlies(), EQUIPMENT_CABLE, 2, "6-10"));
				  	
					//Bent-over row x 3
          exercises.add(new ExerciseModel(Lang.BentOverRow(), EQUIPMENT_BARBELL, 3, "6-10"));
					
					//Alatalja x 2
					exercises.add(new ExerciseModel(Lang.SeatedRow(), EQUIPMENT_CABLE, 2, "8-10"));
				
          //weak back
          v1=2;
          if(weakMuscle == 3) {
            v1=5;
          }
          else {
            v1=3;
          }
					//Front pulldown leve? / leanveto x3
          exercises.add(new ExerciseModel(Lang.FrontPulldown(), EQUIPMENT_CABLE, v1, "6-10"));
					
					//Front pulldown kapea x2
          exercises.add(new ExerciseModel(Lang.CloseGripPulldown(), EQUIPMENT_CABLE, 2, "6-10"));
				  				  	
					//weak pohk
          if(weakMuscle == 7) {
						//Calves seated / standing 5-6
            exercises.add(new ExerciseModel(Lang.SeatedCalfExtension(), EQUIPMENT_LEVER, 3, "10-15"));
					}
				}
			  	
        //TREENI 2  
				if(nro == 2) {
				  
					//Squat / front squat / Leg press x4
					exercises.add(new ExerciseModel(Lang.Squat(), EQUIPMENT_BARBELL, 4, "6-8"));
				  	
          //weak etu
          if(weakMuscle == 0) {
            exercises.add(new ExerciseModel(Lang.FrontSquat(), EQUIPMENT_BARBELL, 2, "6-8"));
          }
				  		
					//Leg extension x3-4
					exercises.add(new ExerciseModel(Lang.LegExtension(), EQUIPMENT_LEVER, 4, "8-12"));
					
					//SLDL x 4
					v1=2;
          exercises.add(new ExerciseModel(Lang.StraightLegDeadlift(), EQUIPMENT_BARBELL, 4, "6-10"));
				
          //weak taka
          if(weakMuscle == 1) {
            v1+=1;
          }
					//Leg curls x 2-3
          exercises.add(new ExerciseModel(Lang.SeatedLegCurl(), EQUIPMENT_LEVER, v1, "10-12"));

					//Calves seated / standing 5-6
          exercises.add(new ExerciseModel(Lang.SeatedCalfExtension(), EQUIPMENT_LEVER, 5, "10-15"));

				}
			  	
        //TREENI 3  
				if(nro == 3) {
			  	
          //weak olkap
          if(weakMuscle == 6) {
            v1=3;
          }
          else {
            v1=2;
          }
					//Pystypunnerrus x2
          exercises.add(new ExerciseModel(Lang.ShoulderPress(), EQUIPMENT_BARBELL, v1, "6-10"));
				  		
					//Viparit sivulle x2
          exercises.add(new ExerciseModel(Lang.LateralRaise(), EQUIPMENT_DUMBBELL, v1, "6-10"));
				  		
					//Viparit taakse x2
          exercises.add(new ExerciseModel(Lang.RearLateralRaise(), EQUIPMENT_DUMBBELL, 2, "6-10"));
				  		
					//Shrug x3
          exercises.add(new ExerciseModel(Lang.Shrug(), EQUIPMENT_BARBELL, 3, "6-10"));
				  		
					//Close grip bench press / Lying triceps extension x3
          exercises.add(new ExerciseModel(Lang.LyingTricepsExtension(), EQUIPMENT_EZ_BAR, 3, "6-10"));
				  		
					//Ojentajapunnerrus x3
          exercises.add(new ExerciseModel(Lang.TricepExtension(), EQUIPMENT_DUMBBELL, 3, "6-10"));
					
					//Biceps Barbell / kp x3
          exercises.add(new ExerciseModel(Lang.BicepCurl(), EQUIPMENT_EZ_BAR, 3, "6-10"));
				  		
					//Biceps scott Barbell / kp x3
          exercises.add(new ExerciseModel(Lang.BicepCurl(), EQUIPMENT_DUMBBELL, 3, "6-10"));
				
					//Rankek??nn?t x3
					exercises.add(new ExerciseModel(Lang.WristCurl(), EQUIPMENT_BARBELL, 3, "10-15")); 
				
				}
			  			  	
        break;
			  	
	  		case 2: 
        /*
        1. Chest,arms
				-Bench press Barbell / kp x 2
				-Incline bench press kp / Barbell x 2
				-Ristitalja / viparit maaten / Pec-deck x2
				-Close grip bench press / Lying triceps extension x3
				-Ojentajapunnerrus x3
				-Biceps Barbell / kp x3
				-Biceps scott Barbell / kp x3
				-Rankek??nn?t x3
				
				2. Legs
				-Squat / front squat / Leg press x4
				-Leg extension x3-4
				-SLDL x 4
				-Leg curls x 2-3
				-Calves seated / standing 5-6
				
				3. Back,shoulders
				-Bent-over row x 3
				-Alatalja x 2
				-Front pulldown leve? / leanveto x3
				-Front pulldown kapea x2
				-Viparit sivulle x2
				-Viparit taakse x2
				-Pystypunnerrus x2
				-Shrug x3
        */
			  				    
        //TREENI 1  
				if(nro == 1) {
				  
          //weak chest
          if(weakMuscle == 2) {
            v1=3;
          }
          else {
            v1=2;
          }
					//Bench press Barbell / kp x 2
          exercises.add(new ExerciseModel(Lang.Bench(), EQUIPMENT_BARBELL, v1, "6-10"));
				
					//Incline bench press kp / Barbell x 2
          exercises.add(new ExerciseModel(Lang.InclineBenchPress(), EQUIPMENT_DUMBBELL, v1, "6-10"));
				  		
					//Ristitalja / viparit maaten / Pec-deck x2
          exercises.add(new ExerciseModel(Lang.StandingFlies(), EQUIPMENT_CABLE, 2, "6-10"));
					
					//Close grip bench press / Lying triceps extension x3
          exercises.add(new ExerciseModel(Lang.LyingTricepsExtension(), EQUIPMENT_EZ_BAR, 3, "6-10"));
				  		
					//Ojentajapunnerrus x3
          exercises.add(new ExerciseModel(Lang.TricepExtension(), EQUIPMENT_DUMBBELL, 3, "6-10"));
				  		
					//Biceps Barbell / kp x3
          exercises.add(new ExerciseModel(Lang.BicepCurl(), EQUIPMENT_EZ_BAR, 3, "6-10"));
				  		
					//Biceps scott Barbell / kp x3
          exercises.add(new ExerciseModel(Lang.BicepCurl(), EQUIPMENT_DUMBBELL, 3, "6-10"));
				
					//Rankek??nn?t x3
					exercises.add(new ExerciseModel(Lang.WristCurl(), EQUIPMENT_BARBELL, 3, "10-15")); 

				}
				
        //TREENI 2  
				if(nro == 2) {
				  
					//Squat / front squat / Leg press x4
          exercises.add(new ExerciseModel(Lang.Squat(), EQUIPMENT_BARBELL, 4, "6-8"));
				  	
          //weak etu
          if(weakMuscle == 0) {
            exercises.add(new ExerciseModel(Lang.FrontSquat(), EQUIPMENT_BARBELL, 2, "6-8"));
          }
				  		
					//Leg extension x3-4
					exercises.add(new ExerciseModel(Lang.LegExtension(), EQUIPMENT_LEVER, 4, "8-12"));
					
					//SLDL x 4
          exercises.add(new ExerciseModel(Lang.StraightLegDeadlift(), EQUIPMENT_BARBELL, 4, "6-10"));
				
          //weak taka
          v1=2;
          if(weakMuscle == 1) {
            v1+=1;
          }
					//Leg curls x 2-3
          exercises.add(new ExerciseModel(Lang.SeatedLegCurl(), EQUIPMENT_LEVER, v1, "10-12"));

					//Calves seated / standing 5-6
          exercises.add(new ExerciseModel(Lang.SeatedCalfExtension(), EQUIPMENT_LEVER, 5, "10-15"));

				}
				
        //TREENI 3  
				if(nro == 3) {
			    
					//Bent-over row x 3
          exercises.add(new ExerciseModel(Lang.BentOverRow(), EQUIPMENT_BARBELL, 3, "6-10"));
					
					//Alatalja x 2
					exercises.add(new ExerciseModel(Lang.SeatedRow(), EQUIPMENT_CABLE, 2, "8-10"));
				
          //weak back
          v1=2;
          if(weakMuscle == 3) {
            v1=5;
          }
          else {
            v1=3;
          }
					//Front pulldown leve? / leanveto x3
          exercises.add(new ExerciseModel(Lang.FrontPulldown(), EQUIPMENT_CABLE, v1, "6-10"));
					
					//Front pulldown kapea x2
          exercises.add(new ExerciseModel(Lang.CloseGripPulldown(), EQUIPMENT_CABLE, 2, "6-10"));

          //weak olkap
          if(weakMuscle == 6) {
            v1=3;
          }
          else {
            v1=2;
          }
					//Pystypunnerrus x2
          exercises.add(new ExerciseModel(Lang.ShoulderPress(), EQUIPMENT_BARBELL, v1, "6-10"));
				  		
					//Viparit sivulle x2
          exercises.add(new ExerciseModel(Lang.LateralRaise(), EQUIPMENT_DUMBBELL, v1, "6-10"));
				  		
					//Viparit taakse x2
          exercises.add(new ExerciseModel(Lang.RearLateralRaise(), EQUIPMENT_DUMBBELL, 2, "6-10"));
				  		
					//Shrug x3
          exercises.add(new ExerciseModel(Lang.Shrug(), EQUIPMENT_BARBELL, 3, "6-10"));
				
					//weak pohk
          if(weakMuscle == 7) {
						//Calves seated / standing 5-6
            exercises.add(new ExerciseModel(Lang.SeatedCalfExtension(), EQUIPMENT_LEVER, 3, "10-15"));
					}
				}

        break;
			    
    }
			
    return exercises;
	}


	//2-JAKOINEN
	///////////////
	static List<ExerciseModel> createExerToTwo(int nro) {
		int v1 = 0;
		final List<ExerciseModel> exercises = new ArrayList<ExerciseModel>();
	  	
    switch(muscleGroupDiv) {
	  		case 0:
        /*
				1. Legs, abs
				Squat (Barbell)	3	6-8
				Maastaveto suorinjaloin (Barbell)	3	6-8
				Leg press (Lait[4])	2	6-8	
				Olankohautus (Barbell)	2	6-8
				Standing Calf Raise (Lait[4]) 4
				Crunches (Talja)

				2. Upper body
				Alatalja (talja)	2	6-8
				Incline bench press (Barbell)	2	6-8
				Front pulldownveto rinnalle (Talja)	3	6-8	
				Pystypunnerrus (Barbell)	2	6-8
				Lateral raise (Dumbbell)	2	6-8	
				Dippi	2	6-8
				Bicep curl scott-penkiss? (K?yr?Barbell)	2	6-8	
				Rannek??nt? sel?n takana (Barbell)	2	10	
        */

        //TREENI 1  
				if(nro == 1) {
			  	
          //weak etu
          if(weakMuscle == 0) {
            v1=5;
          }
          else {
            v1=3;
          }
          //Squat
          exercises.add(new ExerciseModel(Lang.Squat(), EQUIPMENT_BARBELL, v1, "6-8"));

          //weak taka
          if(weakMuscle == 1) {
            v1=4;
          }
          else {
            v1=3;
          }
				  		
          //SLDL
          exercises.add(new ExerciseModel(Lang.StraightLegDeadlift(), EQUIPMENT_BARBELL, v1, "6-8"));
				  	
          //Leg press
          exercises.add(new ExerciseModel(Lang.LegPress(), EQUIPMENT_LEVER, 2, "8-12"));
				  	
          //Shrug
          exercises.add(new ExerciseModel(Lang.Shrug(), EQUIPMENT_BARBELL, 2, "6-8"));
				  	
          //weak pohk
          if(weakMuscle == 7) {
            v1=6;
          }
          else {
            v1=4;
          }
				  		
          //calves
          exercises.add(new ExerciseModel(Lang.SeatedCalfExtension(), EQUIPMENT_LEVER, v1, "10-15"));
				  	
          //Crunches
          exercises.add(new ExerciseModel(Lang.Crunches(), EQUIPMENT_NONE, 3, "10-15"));
				  	
				}

        //TREENI 2  
				if(nro == 2) {
			  	
          //weak back
          if(weakMuscle == 3) {
            v1=3;
          }
          else {
            v1=2;
          }
          //alatalja
          exercises.add(new ExerciseModel(Lang.SeatedRow(), EQUIPMENT_CABLE, v1, "6-10"));
				  	
          //vinop.
          exercises.add(new ExerciseModel(Lang.InclineBenchPress(), EQUIPMENT_BARBELL, 2, "6-10"));
				  	
          //weak rint
          if(weakMuscle == 2) {
            exercises.add(new ExerciseModel(Lang.StandingFlies(), EQUIPMENT_CABLE, 2, "6-10"));
          }
		  	
          //weak back
          if(weakMuscle == 3) {
            v1=4;
          }
          else {
            v1=3;
          }
				  		
          //Front pulldown
          exercises.add(new ExerciseModel(Lang.FrontPulldown(), EQUIPMENT_CABLE, v1, "6-10"));
				  	
          //pystypunnerrus
          exercises.add(new ExerciseModel(Lang.ShoulderPress(), EQUIPMENT_BARBELL, 2, "6-8"));
				  	
          //weak olkap
          if(weakMuscle == 6) {
            exercises.add(new ExerciseModel(Lang.LateralRaise(), EQUIPMENT_DUMBBELL, 1, "10-12"));
          }
				  		
          //Rear lateral raise
          exercises.add(new ExerciseModel(Lang.RearLateralRaise(), EQUIPMENT_DUMBBELL, 2, "8-12"));
				  	
          //dippi
          exercises.add(new ExerciseModel(Lang.Dip(), EQUIPMENT_NONE, 2, "6-8"));
				  	
          //hauis
          exercises.add(new ExerciseModel(Lang.BicepCurl(), EQUIPMENT_EZ_BAR, 2, "6-10"));
				  	
          //ranne
          exercises.add(new ExerciseModel(Lang.WristCurl(), EQUIPMENT_BARBELL, 2, "10"));
				}
			  			  	
        break;
			
	  		case 1:
        /*
				1. Legs, arms
				Squat (Barbell)	2	6-8
				Maastaveto suorinjaloin (Barbell)	3	6-8
				Leg press (Lait[4])	2	6-8
				Dippi	2	6-8
				Bicep curl scott-penkiss? (K?yr?Barbell)	2	6-8	
				Rannek??nt? sel?n takana (Barbell)	2	10		
				Olankohautus (Barbell)	2	6-8
				Crunches (Talja)

				2. Back, chest, shoulders, abs
				Alatalja (talja)	2	6-8
				Incline bench press (Barbell)	2	6-8
				Front pulldownveto rinnalle (Talja)	3	6-8	
				Pystypunnerrus (Barbell)	2	6-8
				Flies (Dumbbell)	2	6-8	
				Standing Calf Raise (Lait[4])
        */

        //TREENI 1  
				if(nro == 1) {
			  	
          //weak etu
          if(weakMuscle == 0) {
            v1=5;
          }
          else {
            v1=3;
          }
				  		
          //Squat
          exercises.add(new ExerciseModel(Lang.Squat(), EQUIPMENT_BARBELL, v1, "6-8"));
				  	
          //weak taka
          if(weakMuscle == 1) {
            v1=4;
          }
          else {
            v1=3;
          }
          //SLDL
          exercises.add(new ExerciseModel(Lang.StraightLegDeadlift(), EQUIPMENT_BARBELL, v1, "6-8"));
				  	
          //Leg press
          exercises.add(new ExerciseModel(Lang.LegPress(), EQUIPMENT_LEVER, 2, "8-12"));
				  	
          //dippi
          exercises.add(new ExerciseModel(Lang.Dip(), EQUIPMENT_NONE, 2, "6-8"));
				  	
          //hauis
          exercises.add(new ExerciseModel(Lang.BicepCurl(), EQUIPMENT_EZ_BAR, 2, "6-10"));
				  	
          //ranne
          exercises.add(new ExerciseModel(Lang.WristCurl(), EQUIPMENT_BARBELL, 2, "10"));
				  	
          //Shrug
					exercises.add(new ExerciseModel(Lang.Shrug(), EQUIPMENT_BARBELL, 2, "6-8"));
				  	
          //Crunches
					exercises.add(new ExerciseModel(Lang.Crunches(), EQUIPMENT_NONE, 3, "10-15"));
				}

        //TREENI 2  
				if(nro == 2) {
			  	
          //weak back
          if(weakMuscle == 3) {
            v1=3;
          }
          else {
            v1=2;
          }
				  		
          //alatalja
          exercises.add(new ExerciseModel(Lang.SeatedRow(), EQUIPMENT_CABLE, v1, "6-10"));
				  	
          //vinop.
          exercises.add(new ExerciseModel(Lang.InclineBenchPress(), EQUIPMENT_BARBELL, 2, "6-10"));
				  	
          //weak rint
          if(weakMuscle == 2) {
            exercises.add(new ExerciseModel(Lang.StandingFlies(), EQUIPMENT_CABLE, 2, "6-10"));
          }
				  		
          //weak back
          if(weakMuscle == 3) {
            v1=4;
          }
          else {
            v1=3;
          }
				  		
          //Front pulldown
          exercises.add(new ExerciseModel(Lang.FrontPulldown(), EQUIPMENT_CABLE, v1, "6-10"));
				  	
          //pystypunnerrus
          exercises.add(new ExerciseModel(Lang.ShoulderPress(), EQUIPMENT_BARBELL, 2, "6-8"));
				  	
          //weak olkap
          if(weakMuscle == 6) {
            exercises.add(new ExerciseModel(Lang.LateralRaise(), EQUIPMENT_DUMBBELL, 1, "10-12"));
          }
						
          //Rear lateral raise
          exercises.add(new ExerciseModel(Lang.RearLateralRaise(), EQUIPMENT_DUMBBELL, 2, "8-12"));
				  	
          //Flies
					exercises.add(new ExerciseModel(Lang.Flies(), EQUIPMENT_DUMBBELL, 2, "8-12"));
				  	
          //weak pohk
          if(weakMuscle == 7) {
            v1=6;
          }
          else {
            v1=4;
          }
				  	
          //calves
          exercises.add(new ExerciseModel(Lang.SeatedCalfExtension(), EQUIPMENT_LEVER, v1, "10-15"));
				}
			  			  	
        break;
			  	
	  		case 2: 
        /*
				-Front squat / Squat / Leg press x 3
				-Leg extension / x3
				-Bench press Barbell / Incline bench presspunnerrus Barbell x2
				-Incline bench press kp / tasapenkki kp x2
				-Rear lateral raise / Lateral raise x 2
				-Pystypunnerrus Barbell / pystypunnerrus Dumbbell x 2
				-Rankalainen punnerrus / ojentajapunnerrus / Close grip bench press x3
				-Situps
				
				- Alatalja / Bent-over row Barbell / Bent-over row kp x3
				- Front pulldown / leuanveto x3
				- SLDL (=Suorin Jaloin MaastaVeto) x3
				- Koukistukset x 2
				- Calves standing / calves seated x4
				- Biceps barbell / biceps scott / biceps kp x3
				- Wrist curl / Shrug x2
        */
			  				    
        //TREENI 1  
				if(nro == 1) {
				  
          //weak etu
          if(weakMuscle == 0) {
            v1=5;
          }
          else {
            v1=3;
          }
				  		
          //Front squat
          exercises.add(new ExerciseModel(Lang.FrontSquat(), EQUIPMENT_BARBELL, v1, "6-8"));

          //REisioj.
          exercises.add(new ExerciseModel(Lang.LegExtension(), EQUIPMENT_LEVER, 3, "8-12"));

          //weak rint
          if(weakMuscle == 2) {
            v1=3;
          }
          else {
            v1=2;
          }
				  		
          //Penkki
          exercises.add(new ExerciseModel(Lang.Bench(), EQUIPMENT_BARBELL, v1, "6-10"));
				
          //Vinop kp
          exercises.add(new ExerciseModel(Lang.InclineBenchPress(), EQUIPMENT_DUMBBELL, 2, "6-10"));
				
          //pystyp.
          exercises.add(new ExerciseModel(Lang.ShoulderPress(), EQUIPMENT_BARBELL, 2, "6-10"));
				
          //weak olkap
          if(weakMuscle == 6) {
            exercises.add(new ExerciseModel(Lang.LateralRaise(), EQUIPMENT_DUMBBELL, 1, "10-12"));
          }
						
          //Rear lateral raise,siv.
          exercises.add(new ExerciseModel(Lang.RearLateralRaise(), EQUIPMENT_DUMBBELL, 2, "6-10"));
					
          //ransk. punn.
          exercises.add(new ExerciseModel(Lang.LyingTricepsExtension(), EQUIPMENT_EZ_BAR, 3, "6-10"));
				  	
          exercises.add(new ExerciseModel(Lang.Situps(), EQUIPMENT_NONE, 3, "10-15"));
				}
				
        //TREENI 2  
				if(nro == 2) {
			    
          //weak back
          if(weakMuscle == 3) {
            v1=3;
          }
          else {
            v1=2;
          }
          //alatalja
          exercises.add(new ExerciseModel(Lang.SeatedRow(), EQUIPMENT_CABLE, v1, "8-12"));
				
          //weak back
          if(weakMuscle == 3) {
            v1=4;
          }
          else {
            v1=3;
          }
          //Front pulldown
          exercises.add(new ExerciseModel(Lang.FrontPulldown(), EQUIPMENT_CABLE, v1, "6-10"));
				
          //weak taka
          if(weakMuscle == 1) {
            v1=1;
          }
          else {
            v1=0;
          }
				  		
          //SLDL
          v1=2;
          exercises.add(new ExerciseModel(Lang.StraightLegDeadlift(), EQUIPMENT_BARBELL, (3 + v1), "8-12"));

          //koukistukset
          exercises.add(new ExerciseModel(Lang.SeatedLegCurl(), EQUIPMENT_LEVER, v1, "10-12"));
				
          //weak pohk
          if(weakMuscle == 7) {
            v1=6;
          }
          else {
            v1=4;
          }
          //calves
          exercises.add(new ExerciseModel(Lang.SeatedCalfExtension(), EQUIPMENT_LEVER, v1, "10-15"));
				
          //HAUIS
          exercises.add(new ExerciseModel(Lang.BicepCurl(), EQUIPMENT_EZ_BAR, 3, "6-10"));
				
          //Wrist curl
          exercises.add(new ExerciseModel(Lang.WristCurl(), EQUIPMENT_BARBELL, 2, "10-15"));  
				}

        break;
			    
    }
			
    return exercises;
	}

	private final RoutineWizardDisplay display;


	/**
	 * 
	 * @param rpcService
	 * @param eventBus
	 * @param display
	 */
	public RoutineWizardPresenter(MyServiceAsync rpcService, SimpleEventBus eventBus, RoutineWizardDisplay display) {
		super(rpcService, eventBus);
		this.display = display;
	}

	@Override
	public Display getView() {
		return display;
	}

	@Override
	public void onBind() {
		
		display.setHandler(new RoutineWizardHandler() {

			@Override
			public void newBodybuildingRoutine(int v_split, int v_muscleGroupDiv, int v_howOften, int v_trainingDays, int v_weakMuscle) {
				
				split = v_split;
				muscleGroupDiv = v_muscleGroupDiv;
				howOften = v_howOften;
				trainingDays = v_trainingDays;
				
				createBodyBuildingRoutine();
			}

			@Override
			public void newPowerliftingRoutine(int v_priority, int v_howOften, int v_squat, int v_deadlift, int v_bench, int v_military) {
				
				priority = v_priority;
				howOften = v_howOften;
				squat = v_squat;
				deadlift = v_deadlift;
				bench = v_bench;
				military = v_military;
				
				createPowerliftingRoutine();
			}
			
		});
	}

	private static String getBBWorkoutName(int c) {

		//name
		String name = "";
		try {
			if(split == 1) {
				name = LangConstants.Split()[0];
			}
			else if(split == 2) {
				final String[] arr = LangConstants.SplitDivision2();
				final String[] arr2 = arr[muscleGroupDiv].split("/");
				name = arr2[c - 1].trim();
			}
			else if(split == 3) {
				final String[] arr = LangConstants.SplitDivision3();
				final String[] arr2 = arr[muscleGroupDiv].split("/");
				name = arr2[c - 1].trim();
			}
			else if(split == 4) {
				final String[] arr = LangConstants.SplitDivision4();
				final String[] arr2 = arr[muscleGroupDiv].split("/");
				name = arr2[c - 1].trim();
			}
		} catch (Exception e) {
      Motiver.showException(e);
		}
		
		return name;
	}

	/**
	 * Creates bodybuilding routine
	 * @param split
	 * @param muscleGroupDiv
	 * @param howOften
	 * @param trainingDays
	 */
	protected void createBodyBuildingRoutine() {
		
		display.setContentEnabled(false);
		display.setMessageBodybuilding(null);
		
		//routine's model
		RoutineModel routine = null;
		
		try {
			int[] days = new int[ROUTINE_MAX_DAYS];	//"1"=1, "-"=-1, "x"=-2
			int cycle_length = 0;	//how long cycle routine have
			
			//days array
			switch(howOften) {
			  
      //1 treenip�iv�, 2 lepop�iv�� (1on, 2off)
				case 0:
					
					//1,-,1,-,-,1,-, 1,-,-,x,x,x,x (4)
					days = new int[] {1, -1, 1, -1, -1};
					cycle_length = 5;
					break;
					
          //3-4
				case 1:
					
					//joka toinen p�iv�
					if(trainingDays == 0) { //1,-,1,-,1,-,1, -,1,-,1,-,1,- (7)
						days = new int[] {1, -1, 1, -1, 1, -1, 1, -1, 1, -1, 1, -1, 1, -1};
						cycle_length = 2;
					}
						
					//1on 1off, 1on 1off, 1on 2off (esim. ma,ke,pe)
					if(trainingDays == 1) { //1,-,1,-,1,-,-, 1,-,1,-,1,-,- (6)
						days = new int[] {1, -1, 1, -1, 1, -1, -1, 1, -1, 1, -1, 1, -1, -1};
						cycle_length = 7;
					}
					
					//2on 1off, 2on 2off (esim. ma,ti,to,pe)
					if(trainingDays == 2) { //1,1,-,1,1,-,-, 1,1,-,1,1,-,- (8)
						days = new int[] {1, 1, -1, 1, 1, -1, -1, 1, 1, -1, 1, 1, -1, -1};
						cycle_length = 7;
					}
					
					break;
					
          //5-6
				case 2:
				
					//3on 1off, 2on 1off
					if(trainingDays == 0) { //1,1,1,-,1,1,-, 1,1,1,-,1,1,- (10)
						days = new int[] {1, 1, 1, -1, 1, 1, -1, 1, 1, 1, -1, 1, 1, -1};
						cycle_length = 7;
					}
					
					//Viisi treenip�iv��, 2 lepo (5on, 2off)
					if(trainingDays == 1) { //1,1,1,1,1,-,-, 1,1,1,1,1,-,- (10)
						days = new int[] {1, 1, 1, 1, 1, -1, -1, 1, 1, 1, 1, 1, -1, -1};
						cycle_length = 7;
					}
					
					//Kuusi treenip�iv��, 1 lepo (6on, 1off)
					if(trainingDays == 2) { //1,1,1,1,1,1,-, 1,1,1,1,1,1,- (12)
						days = new int[] {1, 1, 1, 1, 1, 1, -1, 1, 1, 1, 1, 1, 1, -1};
						cycle_length = 7;
					}
					
					break;
						
			}

			int c=1;
			int d=0;

			routine = new RoutineModel();
			routine.setName(LangConstants.Split()[split]);

			final List<WorkoutModel> arrW = new ArrayList<WorkoutModel>();
			
			//add workout until cycle is complete and day count match
			int i=0;
			for(i=0; i < ROUTINE_MAX_DAYS; i++) {
				WorkoutModel workout = null;
				
				//jos 1: kopioi harjoitus
				int curr_day = days[i - (i / cycle_length) * cycle_length];
				if(curr_day == 1) {
					//when training cycle and training day cycle are equal
					if(c == 1 && i > 0 && i % cycle_length == 0 && i >= ROUTINE_MIN_DAYS) {
						break;
					}

					final int cFinal = c;
					
          d++;
					//1-SPLIT:
					if(split == 0) {
            //jos ei tarpeeksi treenip�ivi�
            //						if(days_traindays<d + 1)
            //						{
            //							days[i]=-2;
            //							break;
            //						}

						workout = createEgForOne(c);
							
					}
					
					//2-SPLIT:
					else if(split == 1) {
						switch(c) {
							case 1:
								workout = createEgForTwo(c, muscleGroupDiv);
                c++;
                break;
              case 2:
                workout = createEgForTwo(c, muscleGroupDiv);
                c=1;
								break;
						}
					}
						
					//3-SPLIT:
					else if(split == 2) {
						switch(c) {
						  	case 1:
						  	
                //if not enough training days
                //								if(days_traindays<d + 2)
                //								{
                //									days[i]=-2;
                //									break;
                //								}
								
								workout = createEgForThree(c, muscleGroupDiv);
                c++;
                break;
						  	case 2:
                workout = createEgForThree(c, muscleGroupDiv);
                c++;
                break;
						  	case 3:
                workout = createEgForThree(c, muscleGroupDiv);
                c=1;
                break;
						}
					}
					
					//4-SPLIT:
					else if(split == 3) {
						switch(c) {
						  	case 1:
                //if not enough training days
                //								if(days_traindays<d + 3)
                //								{
                //									days[i]=-2;
                //									break;
                //								}
								workout = createEgForFour(c, muscleGroupDiv);
                c++;
                break;
						  	case 2:
                workout = createEgForFour(c, muscleGroupDiv);
                c++;
                break;
						  	case 3:
                workout = createEgForFour(c, muscleGroupDiv);
                c++;
                break;
						  	case 4:
                workout = createEgForFour(c, muscleGroupDiv);
                c=1;
                break;
						  	default:
                break;
						}
					}
					
					workout.setName(getBBWorkoutName(cFinal));
					workout.setDayInRoutine(i);
					arrW.add(workout);

				}
			}
			
			routine.setWorkouts(arrW);
			routine.setDays(i + 1);
			
			//save routine
			rpcService.addRoutine(routine, new MyAsyncCallback<RoutineModel>() {
				@Override
				public void onSuccess(RoutineModel result) {
					final RoutineModel routine = result;
					
					display.setContentEnabled(true);
					
					//show text 'routine created'
					display.setMessageBodybuilding(Lang.RoutineCreated());
					
					//fire event
					eventBus.fireEvent(new RoutineCreatedEvent(routine));
				}
			});
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

	/**
	 * Creates powerlifting routine
	 * @param priority
	 * @param howOften
	 * @param squat
	 * @param deadlift
	 * @param bench
	 * @param military
	 */
	protected void createPowerliftingRoutine() {
		
		display.setContentEnabled(false);
		display.setMessagePowerlifting(null);
		
		//routine's model
		RoutineModel routine = null;

		try {
			
			switch(priority) {
			
      //all
				case 0:
					//2 days
					if(howOften == 0) {
						routine = getFullBodyRoutine2Days();
          }
					//3 days
					else if(howOften == 1) {
						routine = getFullBodyRoutine();
          }
					//4 days
					else {
						routine = getFullBodyRoutine4Days();
          }
					break;
          //just squat
				case 1:
					//2 days
					if(howOften == 0) {
						routine = getSquatRoutine2Days();
					}
					//3 days
					else if(howOften == 1) {
						routine = getSquatRoutine();
          }
					//4 days
					else {
						routine = getSquatRoutine();
          }
					break;
          //just deadlift
				case 2:
					//2 days
					if(howOften == 0) {
						routine = getDeadliftRoutine2Days();
          }
					//3 days
					else if(howOften == 1) {
						routine = getDeadliftRoutine();
          }
					//4 days
					else {
						routine = getDeadliftRoutine();
          }
					break;
          //just bench
				case 3:
					//2 days
					if(howOften == 0) {
						routine = getBenchRoutine2Days();
          }
					//3 days
					else if(howOften == 1) {
						routine = getBenchRoutine();
          }
					//4 days
					else {
						routine = getBenchRoutine();
					}
					break;
			}
			
			routine.setName(Lang.PowerliftingRoutine());
			
			//save routine
			rpcService.addRoutine(routine, new MyAsyncCallback<RoutineModel>() {
				@Override
				public void onSuccess(RoutineModel result) {
					final RoutineModel routine = result;
					
					display.setContentEnabled(true);
					
					//show text 'routine created'
					display.setMessagePowerlifting(Lang.RoutineCreated());
					
					//fire event
					eventBus.fireEvent(new RoutineCreatedEvent(routine));
				}
			});

		} catch (Exception e) {
      Motiver.showException(e);
		}
	}

	/**
  * Returns bench routine (3 times a week)
  * @return
  */
  protected static RoutineModel getBenchRoutine() {

		RoutineModel routine = null;
		
		try {
			routine = new RoutineModel();
			routine.setDays(84);
			
			//sets
			final int sets_bench1a = 1;	//day 1 bench
			final int[] sets_bench1b = new int[] { 3, 5, 5, 5, 3, 5, 5, 5, 3, 5, 5, 5 };		//day 1 bench	
			final int[] sets_bench3a = new int[] { 2, 8, 2, 8, 2, 8, 2, 8, 2, 8, 2, 8 };		//day 3 bench
			
			//reps
			final int reps_bench1a = 1;	//day 1 bench
			final int[] reps_bench1b = new int[] { 5, 3, 3, 3, 5, 3, 3, 3, 5, 3, 3, 3 };		//day 1 bench	
			final int[] reps_bench3a = new int[] { 8, 3, 8, 3, 8, 3, 8, 3, 8, 3, 8, 3 };		//day 3 bench
			
			//weights
			final double[] percent_bench1a = new double[] { 0.9, 0.925, 0.95, 0.975, 0.925, 0.95, 0.975, 1, 0.95, 0.975, 1, 1.025 };		//day 1 bench	
			final double[] percent_bench1b = new double[] { 0.8, 0.825, 0.85, 0.875, 0.825, 0.85, 0.875, 0.9, 0.825, 0.85, 875, 0.9 };		//day 1 bench	
			final double[] percent_bench3a = new double[] { 0.65, 0.6, 0.675, 0.60, 0.7, 0.6, 0.725, 0.6, 0.75, 0.6, 0.775, 0.6 };		//day 1 bench	

			final List<WorkoutModel> arrWorkouts = new ArrayList<WorkoutModel>();
			
			//12 weeks
			for(int i=0; i < 12; i++) {
				//day 1
				WorkoutModel w1 = new WorkoutModel();
				w1.setDayInRoutine(1 + i * 7);
				w1.setName(Lang.Bench());
				List<ExerciseModel> e1 = new ArrayList<ExerciseModel>();
				e1.add(new ExerciseModel(Lang.Bench(), EQUIPMENT_BARBELL, sets_bench1a, reps_bench1a + "", percent_bench1a[i] * bench + ""));
				e1.add(new ExerciseModel(Lang.Bench(), EQUIPMENT_BARBELL, sets_bench1b[i], reps_bench1b[i] + "", percent_bench1b[i] * bench + ""));
				//apuliikkeet
				e1.add(new ExerciseModel(Lang.Flies(), EQUIPMENT_DUMBBELL, 4, "10", ""));
				e1.add(new ExerciseModel(Lang.FrontPulldown(), EQUIPMENT_CABLE, 3, "8", "")); 
				w1.setExercises(e1);
				routine.getWorkouts().add(w1);
				
				//day 3
				WorkoutModel w3 = new WorkoutModel();
				w3.setDayInRoutine(3 + i * 7);
				w3.setName(Lang.MuscleBack());
				List<ExerciseModel> e3 = new ArrayList<ExerciseModel>();
				//apuliikkeet
				e3.add(new ExerciseModel(Lang.BentOverRow(), EQUIPMENT_BARBELL, 4, "8", ""));
				e3.add(new ExerciseModel(Lang.ShoulderPress(), EQUIPMENT_DUMBBELL, 5, "5", ""));
				e3.add(new ExerciseModel(Lang.FrontPulldown(), EQUIPMENT_CABLE, 4, "8", ""));
				e3.add(new ExerciseModel(Lang.BicepCurl(), EQUIPMENT_DUMBBELL, 3, "10", ""));
				e3.add(new ExerciseModel(Lang.Crunches(), EQUIPMENT_DUMBBELL, 4, "10", ""));
				w3.setExercises(e3);
				routine.getWorkouts().add(w3);
				
				//day 5
				WorkoutModel w5 = new WorkoutModel();
				w5.setDayInRoutine(5 + i * 7);
				w5.setName(Lang.Bench());
				List<ExerciseModel> e5 = new ArrayList<ExerciseModel>();
				e5.add(new ExerciseModel(Lang.Bench(), EQUIPMENT_BARBELL, sets_bench3a[i], reps_bench3a[i] + "", percent_bench3a[i] * bench + ""));
				//apuliikkeet
				e5.add(new ExerciseModel(Lang.ShoulderPress(), EQUIPMENT_DUMBBELL, 3, "10", ""));
				e5.add(new ExerciseModel(Lang.LyingTricepsExtension(), EQUIPMENT_EZ_BAR, 3, "10", ""));
				e5.add(new ExerciseModel(Lang.RearLateralRaise(), EQUIPMENT_DUMBBELL, 3, "12", ""));
				e5.add(new ExerciseModel(Lang.Crunches(), EQUIPMENT_NONE, 4, "10", "")); 
				w5.setExercises(e5);
				routine.getWorkouts().add(w5);
				
			}
			routine.setWorkouts(arrWorkouts);
			
		} catch (Exception e) {
      Motiver.showException(e);
		}

		return routine;
	}
    

	/**
  * Returns bench routine (3 times a week)
  * @return
  */
  protected static RoutineModel getBenchRoutine2Days() {

		RoutineModel routine = null;
		
		try {
			routine = new RoutineModel();
			routine.setDays(84);
			
			//sets
			final int sets_bench1a = 1;	//day 1 bench
			final int[] sets_bench1b = new int[] { 3, 5, 5, 5, 3, 5, 5, 5, 3, 5, 5, 5 };		//day 1 bench	
			final int[] sets_bench3a = new int[] { 2, 8, 2, 8, 2, 8, 2, 8, 2, 8, 2, 8 };		//day 3 bench
			
			//reps
			final int reps_bench1a = 1;	//day 1 bench
			final int[] reps_bench1b = new int[] { 5, 3, 3, 3, 5, 3, 3, 3, 5, 3, 3, 3 };		//day 1 bench	
			final int[] reps_bench3a = new int[] { 8, 3, 8, 3, 8, 3, 8, 3, 8, 3, 8, 3 };		//day 3 bench
			
			//weights
			final double[] percent_bench1a = new double[] { 0.9, 0.925, 0.95, 0.975, 0.925, 0.95, 0.975, 1, 0.95, 0.975, 1, 1.025 };		//day 1 bench	
			final double[] percent_bench1b = new double[] { 0.8, 0.825, 0.85, 0.875, 0.825, 0.85, 0.875, 0.9, 0.825, 0.85, 875, 0.9 };		//day 1 bench	
			final double[] percent_bench3a = new double[] { 0.65, 0.6, 0.675, 0.60, 0.7, 0.6, 0.725, 0.6, 0.75, 0.6, 0.775, 0.6 };		//day 1 bench	
			
			final List<WorkoutModel> arrWorkouts = new ArrayList<WorkoutModel>();
			
			//12 weeks
			for(int i=0; i < 12; i++) {
				//day 1
				WorkoutModel w1 = new WorkoutModel();
				w1.setDayInRoutine(1 + i * 7);
				w1.setName(Lang.Bench());
				List<ExerciseModel> e1 = new ArrayList<ExerciseModel>();
				e1.add(new ExerciseModel(Lang.Bench(), EQUIPMENT_BARBELL, sets_bench1a, reps_bench1a + "", percent_bench1a[i] * bench + ""));
				e1.add(new ExerciseModel(Lang.Bench(), EQUIPMENT_BARBELL, sets_bench1b[i], reps_bench1b[i] + "", percent_bench1b[i] * bench + ""));
				//apuliikkeet
				e1.add(new ExerciseModel(Lang.Flies(), EQUIPMENT_DUMBBELL, 4, "10", ""));
				e1.add(new ExerciseModel(Lang.BentOverRow(), EQUIPMENT_BARBELL, 4, "8", ""));
				e1.add(new ExerciseModel(Lang.FrontPulldown(), EQUIPMENT_CABLE, 3, "8", "")); 
				w1.setExercises(e1);
				arrWorkouts.add(w1);
				
				//day 4
				WorkoutModel w5 = new WorkoutModel();
				w5.setDayInRoutine(4 + i * 7);
				w5.setName(Lang.Bench());
				List<ExerciseModel> e5 = new ArrayList<ExerciseModel>();
				e5.add(new ExerciseModel(Lang.Bench(), EQUIPMENT_BARBELL, sets_bench3a[i], reps_bench3a[i] + "", percent_bench3a[i] * bench + ""));
				//apuliikkeet
				e5.add(new ExerciseModel(Lang.ShoulderPress(), EQUIPMENT_DUMBBELL, 3, "10", ""));
				e5.add(new ExerciseModel(Lang.LyingTricepsExtension(), EQUIPMENT_EZ_BAR, 3, "10", ""));
				e5.add(new ExerciseModel(Lang.BicepCurl(), EQUIPMENT_DUMBBELL, 3, "10", ""));
				e5.add(new ExerciseModel(Lang.RearLateralRaise(), EQUIPMENT_DUMBBELL, 3, "12", ""));
				e5.add(new ExerciseModel(Lang.Crunches(), EQUIPMENT_NONE, 4, "10", "")); 
				w5.setExercises(e5);
				arrWorkouts.add(w5);
				
			}
			routine.setWorkouts(arrWorkouts);
			
		} catch (Exception e) {
      Motiver.showException(e);
		}

		return routine;
	}

	/**
  * Returns bench routine (3 times a week)
  * @return
  */
  protected static RoutineModel getDeadliftRoutine() {

		RoutineModel routine = null;
		
		try {
			routine = new RoutineModel();
			routine.setDays(84);
			
			//sets
			final int sets_bench1a = 1;	//day 1 bench
			final int[] sets_bench1b = new int[] { 3, 5, 5, 5, 3, 5, 5, 5, 3, 5, 5, 5 };		//day 1 bench	
			final int[] sets_bench3a = new int[] { 2, 8, 2, 8, 2, 8, 2, 8, 2, 8, 2, 8 };		//day 3 bench
			
			//reps
			final int reps_bench1a = 1;	//day 1 bench
			final int[] reps_bench1b = new int[] { 5, 3, 3, 3, 5, 3, 3, 3, 5, 3, 3, 3 };		//day 1 bench	
			final int[] reps_bench3a = new int[] { 8, 3, 8, 3, 8, 3, 8, 3, 8, 3, 8, 3 };		//day 3 bench
			
			//weights
			final double[] percent_bench1a = new double[] { 0.9, 0.925, 0.95, 0.975, 0.925, 0.95, 0.975, 1, 0.95, 0.975, 1, 1.025 };		//day 1 bench	
			final double[] percent_bench1b = new double[] { 0.8, 0.825, 0.85, 0.875, 0.825, 0.85, 0.875, 0.9, 0.825, 0.85, 875, 0.9 };		//day 1 bench	
			final double[] percent_bench3a = new double[] { 0.65, 0.6, 0.675, 0.60, 0.7, 0.6, 0.725, 0.6, 0.75, 0.6, 0.775, 0.6 };		//day 1 bench	

			final List<WorkoutModel> arrWorkouts = new ArrayList<WorkoutModel>();
			
			//12 weeks
			for(int i=0; i < 12; i++) {
				//day 1
				WorkoutModel w1 = new WorkoutModel();
				w1.setDayInRoutine(1 + i * 7);
				w1.setName(Lang.Deadlift());
				List<ExerciseModel> e1 = new ArrayList<ExerciseModel>();
				e1.add(new ExerciseModel(Lang.Deadlift(), EQUIPMENT_BARBELL, sets_bench1a, reps_bench1a + "", percent_bench1a[i] * deadlift + ""));
				e1.add(new ExerciseModel(Lang.Deadlift(), EQUIPMENT_BARBELL, sets_bench1b[i], reps_bench1b[i] + "", percent_bench1b[i] * deadlift + ""));
				//apuliikkeet
				e1.add(new ExerciseModel(Lang.Flies(), EQUIPMENT_DUMBBELL, 4, "10", ""));
				e1.add(new ExerciseModel(Lang.FrontPulldown(), EQUIPMENT_CABLE, 3, "8", "")); 
				w1.setExercises(e1);
				arrWorkouts.add(w1);
				
				//day 3
				WorkoutModel w3 = new WorkoutModel();
				w3.setDayInRoutine(3 + i * 7);
				w3.setName(Lang.MuscleBack());
				List<ExerciseModel> e3 = new ArrayList<ExerciseModel>();
				//apuliikkeet
				e3.add(new ExerciseModel(Lang.BentOverRow(), EQUIPMENT_BARBELL, 4, "8", ""));
				e3.add(new ExerciseModel(Lang.ShoulderPress(), EQUIPMENT_DUMBBELL, 5, "5", ""));
				e3.add(new ExerciseModel(Lang.FrontPulldown(), EQUIPMENT_CABLE, 4, "8", ""));
				e3.add(new ExerciseModel(Lang.BicepCurl(), EQUIPMENT_DUMBBELL, 3, "10", ""));
				e3.add(new ExerciseModel(Lang.Crunches(), EQUIPMENT_DUMBBELL, 4, "10", ""));
				w3.setExercises(e3);
				arrWorkouts.add(w3);
				
				//day 5
				WorkoutModel w5 = new WorkoutModel();
				w5.setDayInRoutine(5 + i * 7);
				w5.setName(Lang.Squat());
				List<ExerciseModel> e5 = new ArrayList<ExerciseModel>();
				e5.add(new ExerciseModel(Lang.Squat(), EQUIPMENT_BARBELL, sets_bench3a[i], reps_bench3a[i] + "", percent_bench3a[i] * deadlift + ""));
				//apuliikkeet
				e5.add(new ExerciseModel(Lang.Bench(), EQUIPMENT_BARBELL, 3, "10", ""));
				e5.add(new ExerciseModel(Lang.LyingTricepsExtension(), EQUIPMENT_EZ_BAR, 3, "10", ""));
				e5.add(new ExerciseModel(Lang.RearLateralRaise(), EQUIPMENT_DUMBBELL, 3, "12", ""));
				e5.add(new ExerciseModel(Lang.Crunches(), EQUIPMENT_NONE, 4, "10", "")); 
				w5.setExercises(e5);
				arrWorkouts.add(w5);
				
			}
			routine.setWorkouts(arrWorkouts);
			
		} catch (Exception e) {
      Motiver.showException(e);
		}

		return routine;
	}

	/**
  * Returns bench routine (3 times a week)
  * @return
  */
  protected static RoutineModel getDeadliftRoutine2Days() {

		RoutineModel routine = null;
		
		try {
			routine = new RoutineModel();
			routine.setDays(84);
			
			//sets
			final int sets_bench1a = 1;	//day 1 bench
			final int[] sets_bench1b = new int[] { 3, 5, 5, 5, 3, 5, 5, 5, 3, 5, 5, 5 };		//day 1 bench	
			final int[] sets_bench3a = new int[] { 2, 8, 2, 8, 2, 8, 2, 8, 2, 8, 2, 8 };		//day 3 bench
			
			//reps
			final int reps_bench1a = 1;	//day 1 bench
			final int[] reps_bench1b = new int[] { 5, 3, 3, 3, 5, 3, 3, 3, 5, 3, 3, 3 };		//day 1 bench	
			final int[] reps_bench3a = new int[] { 8, 3, 8, 3, 8, 3, 8, 3, 8, 3, 8, 3 };		//day 3 bench
			
			//weights
			final double[] percent_bench1a = new double[] { 0.9, 0.925, 0.95, 0.975, 0.925, 0.95, 0.975, 1, 0.95, 0.975, 1, 1.025 };		//day 1 bench	
			final double[] percent_bench1b = new double[] { 0.8, 0.825, 0.85, 0.875, 0.825, 0.85, 0.875, 0.9, 0.825, 0.85, 875, 0.9 };		//day 1 bench	
			final double[] percent_bench3a = new double[] { 0.65, 0.6, 0.675, 0.60, 0.7, 0.6, 0.725, 0.6, 0.75, 0.6, 0.775, 0.6 };		//day 1 bench	

			final List<WorkoutModel> arrWorkouts = new ArrayList<WorkoutModel>();
			
			//12 weeks
			for(int i=0; i < 12; i++) {
				//day 1
				WorkoutModel w1 = new WorkoutModel();
				w1.setDayInRoutine(1 + i * 7);
				w1.setName(Lang.Deadlift());
				List<ExerciseModel> e1 = new ArrayList<ExerciseModel>();
				e1.add(new ExerciseModel(Lang.Deadlift(), EQUIPMENT_BARBELL, sets_bench1a, reps_bench1a + "", percent_bench1a[i] * deadlift + ""));
				e1.add(new ExerciseModel(Lang.Deadlift(), EQUIPMENT_BARBELL, sets_bench1b[i], reps_bench1b[i] + "", percent_bench1b[i] * deadlift + ""));
				//apuliikkeet
				e1.add(new ExerciseModel(Lang.Flies(), EQUIPMENT_DUMBBELL, 4, "10", ""));
				e1.add(new ExerciseModel(Lang.BentOverRow(), EQUIPMENT_BARBELL, 4, "8", ""));
				e1.add(new ExerciseModel(Lang.FrontPulldown(), EQUIPMENT_CABLE, 3, "8", "")); 
				w1.setExercises(e1);
				arrWorkouts.add(w1);
				
				//day 4
				WorkoutModel w4 = new WorkoutModel();
				w4.setDayInRoutine(4 + i * 7);
				w4.setName(Lang.Squat());
				List<ExerciseModel> e4 = new ArrayList<ExerciseModel>();
				e4.add(new ExerciseModel(Lang.Squat(), EQUIPMENT_BARBELL, sets_bench3a[i], reps_bench3a[i] + "", percent_bench3a[i] * squat + ""));
				//apuliikkeet
				e4.add(new ExerciseModel(Lang.Bench(), EQUIPMENT_BARBELL, 3, "10", ""));
				e4.add(new ExerciseModel(Lang.LyingTricepsExtension(), EQUIPMENT_EZ_BAR, 3, "10", ""));
				e4.add(new ExerciseModel(Lang.BicepCurl(), EQUIPMENT_DUMBBELL, 3, "10", ""));
				e4.add(new ExerciseModel(Lang.RearLateralRaise(), EQUIPMENT_DUMBBELL, 3, "12", ""));
				e4.add(new ExerciseModel(Lang.Crunches(), EQUIPMENT_NONE, 4, "10", "")); 
				w4.setExercises(e4);
				arrWorkouts.add(w4);
				
			}
			routine.setWorkouts(arrWorkouts);
			
		} catch (Exception e) {
      Motiver.showException(e);
		}

		return routine;
	}

	/**
  * Returns "full" powerlifting routine
  * @return
  */
	protected static RoutineModel getFullBodyRoutine() {

		RoutineModel routine = null;
		
		try {
			routine = new RoutineModel();
			routine.setDays(63);
			
			//sets
			final int sets_squat1 = 6;	//day 1 squat
			final int[] sets_bench1 = new int[] { 6, 6, 6, 6, 5, 4, 3, 2, 1 };		//day 1 bench	
			final int[] sets_deadlift2 = new int[] { 6, 6, 6, 6, 5, 4, 3, 2, 1 };		//day 2 deadlift
			final int[] sets_squat3 = new int[] { 6, 6, 6, 6, 5, 4, 3, 2, 1 };		//day 3 squat
			final int sets_bench3 = 6;	//day 3 bench
			
			//reps
			final int reps_squat1 = 2;	//day 1 squat
			final int[] reps_bench1 = new int[] { 3, 4, 5, 6, 5, 4, 3, 2, 1 };		//day 1 bench	
			final int[] reps_deadlift2 = new int[] { 3, 4, 5, 6, 5, 4, 3, 2, 1 };		//day 2 deadlift
			final int[] reps_squat3 = new int[] { 3, 4, 5, 6, 5, 4, 3, 2, 1 };		//day 3 squat
			final int reps_bench3 = 2;	//day 3 bench
			
			//weights
			final double percent_squat1 = 0.8;	//day 1 squat
			final double[] percent_bench1 = new double[] { 0.8, 0.8, 0.8, 0.8, 0.85, 0.9, 0.95, 1, 1.05 };		//day 1 bench	
			final double[] percent_deadlift2 = new double[] { 0.8, 0.8, 0.8, 0.8, 0.85, 0.9, 0.95, 1, 1.05 };		//day 2 deadlift
			final double[] percent_squat3 = new double[] { 0.8, 0.8, 0.8, 0.8, 0.85, 0.9, 0.95, 1, 1.05 };		//day 3 squat
			final double percent_bench3 = 0.8;	//day 3 bench

			final List<WorkoutModel> arrWorkouts = new ArrayList<WorkoutModel>();
			
			//9 weeks
			for(int i=0; i < 9; i++) {
				//day 1
				WorkoutModel w1 = new WorkoutModel();
				w1.setDayInRoutine(1 + i * 7);
				w1.setName(Lang.SquatBench());
				List<ExerciseModel> e1 = new ArrayList<ExerciseModel>();
				e1.add(new ExerciseModel(Lang.Squat(), EQUIPMENT_BARBELL, sets_squat1, reps_squat1 + "", percent_squat1 * squat + ""));
				e1.add(new ExerciseModel(Lang.Bench(), EQUIPMENT_BARBELL, sets_bench1[i], reps_bench1[i] + "", percent_bench1[i] * bench + ""));
				//apuliikkeet
				e1.add(new ExerciseModel(Lang.CloseGripBenchPress(), EQUIPMENT_BARBELL, 3, "10", ""));
				e1.add(new ExerciseModel(Lang.RearLateralRaise(), EQUIPMENT_DUMBBELL, 3, "15", ""));
				e1.add(new ExerciseModel(Lang.Crunches(), EQUIPMENT_NONE, 3, "10", "")); 
				w1.setExercises(e1);
				arrWorkouts.add(w1);
				
				//day 3
				WorkoutModel w3 = new WorkoutModel();
				w3.setDayInRoutine(3 + i * 7);
				w3.setName(Lang.Deadlift());
				List<ExerciseModel> e3 = new ArrayList<ExerciseModel>();
				e3.add(new ExerciseModel(Lang.Deadlift(), EQUIPMENT_BARBELL, sets_deadlift2[i], reps_deadlift2[i] + "", percent_deadlift2[i] * deadlift + ""));
				//apuliikkeet
				e3.add(new ExerciseModel(Lang.FrontPulldown(), EQUIPMENT_CABLE, 3, "10", ""));
				e3.add(new ExerciseModel(Lang.BicepCurl(), EQUIPMENT_DUMBBELL, 3, "10", ""));
				e3.add(new ExerciseModel(Lang.RearLateralRaise(), EQUIPMENT_DUMBBELL, 3, "15", ""));
				w3.setExercises(e3);
				arrWorkouts.add(w3);
				
				//day 5
				WorkoutModel w5 = new WorkoutModel();
				w5.setDayInRoutine(5 + i * 7);
				w5.setName(Lang.SquatBench());
				List<ExerciseModel> e5 = new ArrayList<ExerciseModel>();
				e5.add(new ExerciseModel(Lang.Squat(), EQUIPMENT_BARBELL, sets_squat3[i], reps_squat3[i] + "", percent_squat3[i] * squat + ""));
				e5.add(new ExerciseModel(Lang.Bench(), EQUIPMENT_BARBELL, sets_bench3, reps_bench3 + "", percent_bench3 * bench + ""));
				//apuliikkeet
				e5.add(new ExerciseModel(Lang.ShoulderPress(), EQUIPMENT_DUMBBELL, 3, "10", ""));
				e5.add(new ExerciseModel(Lang.Flies(), EQUIPMENT_DUMBBELL, 3, "10", ""));
				e5.add(new ExerciseModel(Lang.Crunches(), EQUIPMENT_NONE, 3, "10", "")); 
				w5.setExercises(e5);
				arrWorkouts.add(w5);
				
			}
			routine.setWorkouts(arrWorkouts);
			
		} catch (Exception e) {
      Motiver.showException(e);
		}

		return routine;
	}
	
	
	/**
	 * Fullbody routine 2 times a week
	 * @return
	 */
  protected static RoutineModel getFullBodyRoutine2Days() {

		RoutineModel routine = null;
		
		try {
			routine = new RoutineModel();
			routine.setDays(28);
			
			//reps
			final int[] reps_a = new int[] { 5, 3, 5, 5 };
			final int[] reps_b = new int[] { 5, 3, 3, 5 };
			final int[] reps_c = new int[] { 5, 3, 1, 5 };
			
			//weights
			final double[] percent_a = new double[] { 0.6, 0.65, 0.7, 0.35 };
			final double[] percent_b = new double[] { 0.7, 0.70, 0.75, 0.45 };
			final double[] percent_c = new double[] { 0.75, 0.80, 0.85, 0.55 };

			final List<WorkoutModel> arrWorkouts = new ArrayList<WorkoutModel>();
			
			//4 weeks
			for(int i=0; i < 1; i++) {
				//day 1
				WorkoutModel w1 = new WorkoutModel();
				w1.setDayInRoutine(1 + i * 7);
				w1.setName(Lang.SquatBench());
				List<ExerciseModel> e1 = new ArrayList<ExerciseModel>();
				//squat
				e1.add(new ExerciseModel(Lang.Squat(), EQUIPMENT_BARBELL, 3, reps_a[i] + "," + reps_b[i] + "," + reps_c[i], (percent_a[i] * squat) + "," + (percent_b[i] * squat) + "," + (percent_c[i] * squat)));
				//bench
				e1.add(new ExerciseModel(Lang.Bench(), EQUIPMENT_BARBELL, 3, reps_a[i] + "," + reps_b[i] + "," + reps_c[i], (percent_a[i] * bench) + "," + (percent_b[i] * bench) + "," + (percent_c[i] * bench)));
				//apuliikkeet
				e1.add(new ExerciseModel(Lang.CloseGripBenchPress(), EQUIPMENT_BARBELL, 3, "10", ""));
				e1.add(new ExerciseModel(Lang.RearLateralRaise(), EQUIPMENT_DUMBBELL, 3, "15", ""));
				e1.add(new ExerciseModel(Lang.Crunches(), EQUIPMENT_NONE, 3, "10", "")); 
				w1.setExercises(e1);
				arrWorkouts.add(w1);
				
				//day 4
				WorkoutModel w4 = new WorkoutModel();
				w4.setDayInRoutine(4 + i * 7);
				w4.setName(Lang.Deadlift());
				List<ExerciseModel> e3 = new ArrayList<ExerciseModel>();
				//deadlift
				e3.add(new ExerciseModel(Lang.Deadlift(), EQUIPMENT_BARBELL, 3, reps_a[i] + "," + reps_b[i] + "," + reps_c[i], (percent_a[i] * deadlift) + "," + (percent_b[i] * deadlift) + "," + (percent_c[i] * deadlift)));
				//military
				e3.add(new ExerciseModel(Lang.ShoulderPress(), EQUIPMENT_BARBELL, 3, reps_a[i] + "," + reps_b[i] + "," + reps_c[i], (percent_a[i] * military) + "," + (percent_b[i] * military) + "," + (percent_c[i] * military)));
				//apuliikkeet
				e3.add(new ExerciseModel(Lang.FrontPulldown(), EQUIPMENT_CABLE, 3, "10", ""));
				e3.add(new ExerciseModel(Lang.BicepCurl(), EQUIPMENT_DUMBBELL, 3, "10", ""));
				e3.add(new ExerciseModel(Lang.RearLateralRaise(), EQUIPMENT_DUMBBELL, 3, "15", ""));
				w4.setExercises(e3);
				arrWorkouts.add(w4);
				
			}
			routine.setWorkouts(arrWorkouts);
			
		} catch (Exception e) {
      Motiver.showException(e);
		}

		return routine;
	}
		
		

	/**
	 * Fullbody routine 4 times a week
	 * @return
	 */
  protected static RoutineModel getFullBodyRoutine4Days() {

		RoutineModel routine = null;
		
		try {
			routine = new RoutineModel();
			routine.setDays(28);
			
			//reps
			final int[] reps_a = new int[] { 5, 3, 5, 5 };
			final int[] reps_b = new int[] { 5, 3, 3, 5 };
			final int[] reps_c = new int[] { 5, 3, 1, 5 };
			
			//weights
			final double[] percent_a = new double[] { 0.6, 0.65, 0.7, 0.35 };
			final double[] percent_b = new double[] { 0.7, 0.70, 0.75, 0.45 };
			final double[] percent_c = new double[] { 0.75, 0.80, 0.85, 0.55 };

			final List<WorkoutModel> arrWorkouts = new ArrayList<WorkoutModel>();
			
			//4 weeks
			for(int i=0; i < 4; i++) {

				//day 1
				WorkoutModel w1 = new WorkoutModel();
				w1.setDayInRoutine(1 + i * 7);
				w1.setName(Lang.Military());
				List<ExerciseModel> e1 = new ArrayList<ExerciseModel>();
				//military
				e1.add(new ExerciseModel(Lang.ShoulderPress(), EQUIPMENT_BARBELL, 3, reps_a[i] + "," + reps_b[i] + "," + reps_c[i], (percent_a[i] * military) + "," + (percent_b[i] * military) + "," + (percent_c[i] * military)));
				//apuliikkeet
				e1.add(new ExerciseModel(Lang.FrontPulldown(), EQUIPMENT_CABLE, 3, "10", ""));
				e1.add(new ExerciseModel(Lang.RearLateralRaise(), EQUIPMENT_DUMBBELL, 3, "15", ""));
				w1.setExercises(e1);
				arrWorkouts.add(w1);

				//day 2
				WorkoutModel w2 = new WorkoutModel();
				w2.setDayInRoutine(2 + i * 7);
				w2.setName(Lang.Deadlift());
				List<ExerciseModel> e2 = new ArrayList<ExerciseModel>();
				//deadlift
				e2.add(new ExerciseModel(Lang.Deadlift(), EQUIPMENT_BARBELL, 3, reps_a[i] + "," + reps_b[i] + "," + reps_c[i], (percent_a[i] * deadlift) + "," + (percent_b[i] * deadlift) + "," + (percent_c[i] * deadlift)));
				//apuliikkeet
				e2.add(new ExerciseModel(Lang.FrontPulldown(), EQUIPMENT_CABLE, 3, "10", ""));
				e2.add(new ExerciseModel(Lang.BicepCurl(), EQUIPMENT_DUMBBELL, 3, "10", ""));
				w2.setExercises(e2);
				arrWorkouts.add(w2);
				
				//day 4
				WorkoutModel w4 = new WorkoutModel();
				w4.setDayInRoutine(4 + i * 7);
				w4.setName(Lang.Squat());
				List<ExerciseModel> e3 = new ArrayList<ExerciseModel>();
				//squat
				e3.add(new ExerciseModel(Lang.Squat(), EQUIPMENT_BARBELL, 3, reps_a[i] + "," + reps_b[i] + "," + reps_c[i], (percent_a[i] * squat) + "," + (percent_b[i] * squat) + "," + (percent_c[i] * squat)));
				//apuliikkeet
				e3.add(new ExerciseModel(Lang.CloseGripBenchPress(), EQUIPMENT_BARBELL, 3, "10", ""));
				e3.add(new ExerciseModel(Lang.RearLateralRaise(), EQUIPMENT_DUMBBELL, 3, "15", ""));
				w4.setExercises(e3);
				arrWorkouts.add(w4);

				//day 5
				WorkoutModel w5 = new WorkoutModel();
				w5.setDayInRoutine(5 + i * 7);
				w5.setName(Lang.Bench());
				List<ExerciseModel> e5 = new ArrayList<ExerciseModel>();
				//bench
				e5.add(new ExerciseModel(Lang.Bench(), EQUIPMENT_BARBELL, 3, reps_a[i] + "," + reps_b[i] + "," + reps_c[i], (percent_a[i] * bench) + "," + (percent_b[i] * bench) + "," + (percent_c[i] * bench)));
				//apuliikkeet
				e5.add(new ExerciseModel(Lang.RearLateralRaise(), EQUIPMENT_DUMBBELL, 3, "15", ""));
				e5.add(new ExerciseModel(Lang.Crunches(), EQUIPMENT_NONE, 3, "10", "")); 
				w5.setExercises(e5);
				arrWorkouts.add(w5);
			}
			routine.setWorkouts(arrWorkouts);
			
		} catch (Exception e) {
      Motiver.showException(e);
		}
		
		return routine;
	}


	/**
  * Returns bench routine (3 times a week)
  * @return
  */
  protected static RoutineModel getSquatRoutine() {

		RoutineModel routine = null;
		
		try {
			routine = new RoutineModel();
			routine.setDays(84);
			
			//sets
			final int sets_bench1a = 1;	//day 1 bench
			final int[] sets_bench1b = new int[] { 3, 5, 5, 5, 3, 5, 5, 5, 3, 5, 5, 5 };		//day 1 bench	
			final int[] sets_bench3a = new int[] { 2, 8, 2, 8, 2, 8, 2, 8, 2, 8, 2, 8 };		//day 3 bench
			
			//reps
			final int reps_bench1a = 1;	//day 1 bench
			final int[] reps_bench1b = new int[] { 5, 3, 3, 3, 5, 3, 3, 3, 5, 3, 3, 3 };		//day 1 bench	
			final int[] reps_bench3a = new int[] { 8, 3, 8, 3, 8, 3, 8, 3, 8, 3, 8, 3 };		//day 3 bench
			
			//weights
			final double[] percent_bench1a = new double[] { 0.9, 0.925, 0.95, 0.975, 0.925, 0.95, 0.975, 1, 0.95, 0.975, 1, 1.025 };		//day 1 bench	
			final double[] percent_bench1b = new double[] { 0.8, 0.825, 0.85, 0.875, 0.825, 0.85, 0.875, 0.9, 0.825, 0.85, 875, 0.9 };		//day 1 bench	
			final double[] percent_bench3a = new double[] { 0.65, 0.6, 0.675, 0.60, 0.7, 0.6, 0.725, 0.6, 0.75, 0.6, 0.775, 0.6 };		//day 1 bench	

			final List<WorkoutModel> arrWorkouts = new ArrayList<WorkoutModel>();
			
			//12 weeks
			for(int i=0; i < 12; i++) {
				//day 1
				WorkoutModel w1 = new WorkoutModel();
				w1.setDayInRoutine(1 + i * 7);
				w1.setName(Lang.Squat());
				List<ExerciseModel> e1 = new ArrayList<ExerciseModel>();
				e1.add(new ExerciseModel(Lang.Squat(), EQUIPMENT_BARBELL, sets_bench1a, reps_bench1a + "", percent_bench1a[i] * squat + ""));
				e1.add(new ExerciseModel(Lang.Squat(), EQUIPMENT_BARBELL, sets_bench1b[i], reps_bench1b[i] + "", percent_bench1b[i] * squat + ""));
				//apuliikkeet
				e1.add(new ExerciseModel(Lang.Bench(), EQUIPMENT_BARBELL, 3, "6", ""));
				e1.add(new ExerciseModel(Lang.FrontPulldown(), EQUIPMENT_CABLE, 3, "8", "")); 
				w1.setExercises(e1);
				arrWorkouts.add(w1);
				
				//day 3
				WorkoutModel w3 = new WorkoutModel();
				w3.setDayInRoutine(3 + i * 7);
				w3.setName(Lang.Deadlift());
				List<ExerciseModel> e3 = new ArrayList<ExerciseModel>();
				//apuliikkeet
				e3.add(new ExerciseModel(Lang.Deadlift(), EQUIPMENT_BARBELL, 5, "5", ""));
				e3.add(new ExerciseModel(Lang.FrontPulldown(), EQUIPMENT_CABLE, 4, "8", ""));
				e3.add(new ExerciseModel(Lang.BicepCurl(), EQUIPMENT_DUMBBELL, 3, "10", ""));
				e3.add(new ExerciseModel(Lang.Crunches(), EQUIPMENT_DUMBBELL, 4, "10", ""));
				w3.setExercises(e3);
				arrWorkouts.add(w3);
				
				//day 5
				WorkoutModel w5 = new WorkoutModel();
				w5.setDayInRoutine(5 + i * 7);
				w5.setName(Lang.Squat());
				List<ExerciseModel> e5 = new ArrayList<ExerciseModel>();
				e5.add(new ExerciseModel(Lang.Squat(), EQUIPMENT_BARBELL, sets_bench3a[i], reps_bench3a[i] + "", percent_bench3a[i] * squat + ""));
				//apuliikkeet
				e5.add(new ExerciseModel(Lang.StraightLegDeadlift(), EQUIPMENT_BARBELL, 3, "5", ""));
				e5.add(new ExerciseModel(Lang.LyingTricepsExtension(), EQUIPMENT_EZ_BAR, 3, "10", ""));
				e5.add(new ExerciseModel(Lang.RearLateralRaise(), EQUIPMENT_DUMBBELL, 3, "12", ""));
				e5.add(new ExerciseModel(Lang.Crunches(), EQUIPMENT_NONE, 4, "10", "")); 
				w5.setExercises(e5);
				arrWorkouts.add(w5);
				
			}
			routine.setWorkouts(arrWorkouts);
			
		} catch (Exception e) {
      Motiver.showException(e);
		}

		return routine;
	}


	/**
  * Returns bench routine (3 times a week)
  * @return
  */
  protected static RoutineModel getSquatRoutine2Days() {

		RoutineModel routine = null;
		
		try {
			routine = new RoutineModel();
			routine.setDays(84);
			
			//sets
			final int sets_bench1a = 1;	//day 1 bench
			final int[] sets_bench1b = new int[] { 3, 5, 5, 5, 3, 5, 5, 5, 3, 5, 5, 5 };		//day 1 bench	
			final int[] sets_bench3a = new int[] { 2, 8, 2, 8, 2, 8, 2, 8, 2, 8, 2, 8 };		//day 3 bench
			
			//reps
			final int reps_bench1a = 1;	//day 1 bench
			final int[] reps_bench1b = new int[] { 5, 3, 3, 3, 5, 3, 3, 3, 5, 3, 3, 3 };		//day 1 bench	
			final int[] reps_bench3a = new int[] { 8, 3, 8, 3, 8, 3, 8, 3, 8, 3, 8, 3 };		//day 3 bench
			
			//weights
			final double[] percent_bench1a = new double[] { 0.9, 0.925, 0.95, 0.975, 0.925, 0.95, 0.975, 1, 0.95, 0.975, 1, 1.025 };		//day 1 bench	
			final double[] percent_bench1b = new double[] { 0.8, 0.825, 0.85, 0.875, 0.825, 0.85, 0.875, 0.9, 0.825, 0.85, 875, 0.9 };		//day 1 bench	
			final double[] percent_bench3a = new double[] { 0.65, 0.6, 0.675, 0.60, 0.7, 0.6, 0.725, 0.6, 0.75, 0.6, 0.775, 0.6 };		//day 1 bench	

			final List<WorkoutModel> arrWorkouts = new ArrayList<WorkoutModel>();
			
			//12 weeks
			for(int i=0; i < 12; i++) {
				//day 1
				WorkoutModel w1 = new WorkoutModel();
				w1.setDayInRoutine(1 + i * 7);
				w1.setName(Lang.Squat());
				List<ExerciseModel> e1 = new ArrayList<ExerciseModel>();
				e1.add(new ExerciseModel(Lang.Squat(), EQUIPMENT_BARBELL, sets_bench1a, reps_bench1a + "", percent_bench1a[i] * squat + ""));
				e1.add(new ExerciseModel(Lang.Squat(), EQUIPMENT_BARBELL, sets_bench1b[i], reps_bench1b[i] + "", percent_bench1b[i] * squat + ""));
				//apuliikkeet
				e1.add(new ExerciseModel(Lang.Bench(), EQUIPMENT_BARBELL, 3, "6", ""));
				e1.add(new ExerciseModel(Lang.BentOverRow(), EQUIPMENT_BARBELL, 4, "8", ""));
				e1.add(new ExerciseModel(Lang.FrontPulldown(), EQUIPMENT_CABLE, 3, "8", "")); 
				w1.setExercises(e1);
				arrWorkouts.add(w1);
				
				//day 4
				WorkoutModel w4 = new WorkoutModel();
				w4.setDayInRoutine(4 + i * 7);
				w4.setName(Lang.Squat());
				List<ExerciseModel> e5 = new ArrayList<ExerciseModel>();
				e5.add(new ExerciseModel(Lang.Squat(), EQUIPMENT_BARBELL, sets_bench3a[i], reps_bench3a[i] + "", percent_bench3a[i] * squat + ""));
				//apuliikkeet
				e5.add(new ExerciseModel(Lang.StraightLegDeadlift(), EQUIPMENT_BARBELL, 4, "5", ""));
				e5.add(new ExerciseModel(Lang.LyingTricepsExtension(), EQUIPMENT_EZ_BAR, 3, "10", ""));
				e5.add(new ExerciseModel(Lang.BicepCurl(), EQUIPMENT_DUMBBELL, 3, "10", ""));
				e5.add(new ExerciseModel(Lang.RearLateralRaise(), EQUIPMENT_DUMBBELL, 3, "12", ""));
				e5.add(new ExerciseModel(Lang.Crunches(), EQUIPMENT_NONE, 4, "10", "")); 
				w4.setExercises(e5);
				arrWorkouts.add(w4);
				
			}
			routine.setWorkouts(arrWorkouts);
			
		} catch (Exception e) {
      Motiver.showException(e);
		}

		return routine;
	}
}
