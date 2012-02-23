package com.delect.motiver.shared.util;

public class WorkoutUtils {
  
  public static void parseExercise(ExerciseInfo info) {
    
    final int sets = info.origSets;
    final String reps = info.origReps;
    final String weights = info.origWeights;
    
    //remove everything except numbers
    final String repsTrimmed = reps.replaceAll("[a-zA-Z]{1,}[.]", "").replaceAll("\\(.*\\)", "").replaceAll("[^0-9x.+,-]", "");
    final String weightsTrimmed = weights.replaceAll("[a-zA-Z]{1,}[.]", "").replaceAll("\\(.*\\)", "").replaceAll("[^0-9x.+,-]", "");
  
    //parse strings
    double[] re = parseReps(sets, repsTrimmed);
    double[] we = parseWeights(sets, weightsTrimmed);
    
    //if no reps found -> jump to next exercise
    if(re.length == 0) {
      return;
    }
    
    //find which group it belongs
    double max = -1;
    double work = 0;   //how good/bad the sets were
    int length = 2;   //how many sets
    if(re.length == we.length) {
      for(int i = 0; i < re.length; i++) {
        //if only one rep
        if(re[i] == 1) {
          //if max
          if(we[i] > max) {
            max = we[i];
          }                      
        }
        
        //calculate work
        double factor = (sets < 5)? 1 - (sets * 0.1) : 0.5;
        // (weights*weights) * (reps + 10) * factor
        work += (we[i]*we[i]) * (re[i]+10) * factor;
        
        //calculate length (2-4, 5-12 tai 12-...)
        //2-4
        if(re[i] >= 2 && re[i] <  5) {
          length = 0;
        }
        //5-12
        else if(length != 0 && re[i] >= 5 && re[i] <  13) {
          length = 1;
        }
        
      }
    }
    
    info.max = max;
    info.reps = re;
    info.sets = length;
    info.weights = we;
    info.work = work;
    info.setOk(true);
  }
  
  /**
   * Parses reps value and return reps
   * @param reps
   * @param sets
   * @return reps
   */
  private static double[] parseReps(int sets, String reps) {

    //split reps
    String[] reps_arr = reps.split(",");
    
    double[] repsArray = new double[50];
    
    //go through each value
    int c = 0;
    for(int i=0; i < reps_arr.length; i++) {
      try {
        String rep = reps_arr[i];
        
        //remove spaces
        rep = rep.replace(" ", "");
        
        if(rep.length() > 0) {
          //if numeric
          boolean isNumeric = false;
          double nro = 0;
          try {
            nro = Double.parseDouble(rep);
            isNumeric = true;
          } catch (NumberFormatException e) {
          }
          
          //single number
          if(isNumeric) {
            //if single rep found -> use this value for rest of sets
            if(reps_arr.length == 1) {
              for(int k = 0; k < sets ; k++) {
                repsArray[c] = nro;
                c++; 
              }
            }
            else {
              repsArray[c] = nro;
              c++; 
            }
          }
          //if 2+1 (=3)
          else if(rep.matches("([0-9]*)([+]{1}([0-9]*))*")) {
            String[] arr = rep.split("\\+");
            repsArray[c] = Double.parseDouble(arr[0]);
            c++;
          }
          //if 3x10
          else if(rep.matches("([0-9]*)x([0-9]*)")) {
            String[] arr = rep.split("x");
            double s = Double.parseDouble(arr[0]);
            double r = Double.parseDouble(arr[1]);
            //add each sub set
            for(int j = 0; j < s; j++) {
              repsArray[c] = r;
              c++;
            }
          }
          //if 6-10 (means for example 6,7,8,9,10)
          else if(rep.matches("([0-9]*)-([0-9]*)")) {
            String[] arr = rep.split("-");
            //get total reps
            double first_rep = Double.parseDouble(arr[0]);
            double last_rep = Double.parseDouble(arr[1]);
            int sets_count = (sets - (reps_arr.length) + 1);
            //check each set
            //if increasing
            if(last_rep > first_rep) {
              for(double j=first_rep; j <= last_rep; j += ((last_rep - first_rep) / (sets_count - 1))) {
                repsArray[c] = j;
                c++;
              }
            }
            //decreasing
            else {
              for(double j=first_rep; j >= last_rep; j -= ((first_rep - last_rep) / (sets_count - 1))) {
                repsArray[c] = j;
                c++;
              }
            }
          }
        }
      } catch (Exception e) {
      }
    }
    
    //trim array
    double[] repsArrayTrim = new double[c];
    try {
      for(int i = 0; i < c; i++) {
        repsArrayTrim[i] = repsArray[i];
      }
    }
    catch(Exception e) {}
    
    return repsArrayTrim;
  }
  
  /**
   * Parses reps value and return reps
   * @param reps
   * @param sets
   * @return reps
   */
  private static double[] parseWeights(int sets, String weights) {

    //if empty -> no weights
    if(weights.length() == 0) {
      weights = "0";
    }
    
    //split reps
    String[] weights_arr = weights.split(",");
    
    double[] weightsArray = new double[20];
    
    //go through each value
    int c = 0;
    for(int i=0; i < weights_arr.length; i++) {
      try {
        String rep = weights_arr[i];
        
        //remove spaces
        rep = rep.replace(" ", "");
        
        if(rep.length() > 0) {
          //if numeric
          boolean isNumeric = false;
          double nro = 0;
          try {
            nro = Double.parseDouble(rep);
            isNumeric = true;
          } catch (NumberFormatException e) {
          }
          
          //single number
          if(isNumeric) {
            //if single rep found -> use this value for rest of sets
            if(weights_arr.length == 1) {
              for(int k = 0; k < sets ; k++) {
                weightsArray[c] = nro;
                c++; 
              }
            }
            else {
              weightsArray[c] = nro;
              c++; 
            }
          }
          //if 2+1 (=3)
          else if(rep.matches("([0-9.]*)([+]{1}([0-9.]*))*")) {
            String[] arr = rep.split("\\+");
            //use only first weight
            weightsArray[c] = Double.parseDouble(arr[0]);
            c++;
          }
          //if 3x10
          else if(rep.matches("([0-9.]*)x([0-9.]*)")) {
            String[] arr = rep.split("x");
            double s = Double.parseDouble(arr[0]);
            double w = Double.parseDouble(arr[1]);
            //if weights are smaller than times (probably other way)
            if(w < s) {
              double wTemp = w;
              w = s;
              s = wTemp;
            }
            //add each sub set
            for(int j = 0; j < s; j++) {
              weightsArray[c] = w;
              c++;
            }
          }
          //if 6-10 (means for example 6,7,8,9,10)
          else if(rep.matches("([0-9.]*)-([0-9.]*)")) {
            String[] arr = rep.split("-");
            //get total reps
            double first_rep = Double.parseDouble(arr[0]);
            double last_rep = Double.parseDouble(arr[1]);
            int sets_count = (sets - (weights_arr.length) + 1);
            //check each set
            //if increasing
            if(last_rep > first_rep) {
              for(double j=first_rep; j <= last_rep; j += ((last_rep - first_rep) / (sets_count - 1))) {
                weightsArray[c] = j;
                c++;
              }
            }
            //decreasing
            else {
              for(double j=first_rep; j >= last_rep; j -= ((first_rep - last_rep) / (sets_count - 1))) {
                weightsArray[c] = j;
                c++;
              }
            }
          }
        }
      } catch (Exception e) {
      }
    }
    
    //trim array
    double[] repsArrayTrim = new double[c];
    try {
      for(int i = 0; i < c; i++) {
        repsArrayTrim[i] = weightsArray[i];
      }
    }
    catch(Exception e) {}
    
    return repsArrayTrim;
  }

  public static class ExerciseInfo {

    //original values
    public int origSets = 0;
    public String origReps = "";
    public String origWeights = "";
    
    //calculated values
    public double max = -1;
    public double work = 0;   //how good/bad the sets were
    public int sets = 2;   //how many sets
    public double[] reps = null;
    public double[] weights = null;
    private boolean ok = false;
    
    public ExerciseInfo(int sets, String reps, String weights) {
      origSets = sets;
      origReps = reps;
      origWeights = weights;
    }
    
    public boolean isOk() {
      return ok;
    }
    
    public void setOk(boolean ok) {
      this.ok = ok;
    }
  }
}
