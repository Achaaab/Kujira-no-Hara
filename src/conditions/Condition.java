package conditions;

import java.util.ArrayList;

import comportementEvent.CommandeEvent;

public abstract class Condition extends CommandeEvent {
	public int numero; //le num�ro de condition est le m�me que le num�ro de fin de condition qui correspond
	
	public abstract Boolean estVerifiee();
	
	public int executer(int curseurActuel, ArrayList<CommandeEvent> commandes){
		if( estVerifiee() ){
			return curseurActuel+1;
		}else{
			int nouveauCurseur = curseurActuel;
			Boolean onATrouveLaFinDeSi = false;
			while(!onATrouveLaFinDeSi){
				nouveauCurseur++;
				try{
					//la fin de si a le m�me numero que la condition
					if( ((Condition)commandes.get(nouveauCurseur)).numero == numero ){
						onATrouveLaFinDeSi = true;
					}
				}catch(IndexOutOfBoundsException e){
					System.out.println("L'�v�nement n�"+this.page.event.numero+" n'a pas trouv� sa fin de condition "+this.numero+" :");
					e.printStackTrace();
				}catch(Exception e){
					//pas une condition
				}
			}
			return nouveauCurseur+1;
		}
	}
}
