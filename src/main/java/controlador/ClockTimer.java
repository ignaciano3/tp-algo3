package controlador;

import java.util.Timer;
import java.util.TimerTask;

public class ClockTimer {
	
	ClockTimer(int minutes){
		Timer tm = new Timer();
		
		TimerTask timerTask = new TimerTask() {
			int seconds = 60*minutes;
			int i = 0;
			
			 public void run() {
				 i++;
				 if (seconds - i == 0) {
					 System.out.print("Paso el tiempo");
				 }
				 System.out.println(seconds-i);
			 }
		};
		tm.schedule(timerTask, 0, 1000);
	}
}
