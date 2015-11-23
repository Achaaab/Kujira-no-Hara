package map;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

import org.json.JSONObject;

import comportementEvent.CommandeEvent;
import conditions.Condition;
import conditions.ConditionParler;

public class PageDeComportement {
	public Event event;
	public int numero;
	private Boolean sOuvreParParole = false; //�quivalent � poss�der la condition de d�clenchement "parler"
	
	//conditions de d�clenchement
	public ArrayList<Condition> conditions;
	
	//liste de commandes
	public ArrayList<CommandeEvent> commandes;
	 /**
	  * Le curseur indique quelle commande executer.
	  * Il se d�place incr�mentalement, mais on peut lui faire faire des sauts.
	  */
	public int curseurCommandes = 0;
	
	//apparence
	public String nomImage;
	public BufferedImage image = null;
	
	//param�tres
	public Boolean animeALArret = false;
	public Boolean animeEnMouvement = true;
	public Boolean traversable = false;
	public Boolean auDessusDeTout = false;
	public int vitesse = 4;
	public int frequence = 4;
	
	//mouvement
	public ArrayList<CommandeEvent> deplacement;
	public Boolean repeterLeDeplacement = true;
	public Boolean ignorerLesMouvementsImpossibles = false;
	
	public PageDeComportement(ArrayList<Condition> conditions, ArrayList<CommandeEvent> commandes, String nomImage,
			ArrayList<CommandeEvent> deplacement){
		this.conditions = conditions;
		this.commandes = commandes;
		this.nomImage = nomImage;
		this.deplacement = deplacement;
		//ouverture de l'image d'apparence
		try {
			this.image = ImageIO.read(new File(".\\ressources\\Graphics\\Characters\\"+nomImage));
		} catch (IOException e) {
			System.out.println("Erreur lors de l'ouverture de l'apparence de l'event :");
			e.printStackTrace();
		}
		//on pr�cise si c'est une page qui s'ouvre en parlant � l'�vent
		if(conditions!=null){
			for(Condition cond : conditions){
				//TODO dans le futur il y aura aussi la condition "arriv�e sur la case" en plus de "parler" :
				if(cond.getClass().getName().equals("conditions.ConditionParler")){
					this.sOuvreParParole = true;
				}
			}
		}
	}

	/**
	 * La page de comportement est cr��e � partir du fichier JSON.
	 * @param pageJSON objet JSON d�crivant la page de comportements
	 */
	public PageDeComportement(JSONObject pageJSON) {
		// TODO Auto-generated constructor stub
	}

	public void executer() {
		//si la page est une page "Parler", elle active le stopEvent qui fige tous les events
		if(sOuvreParParole){
			this.event.map.lecteur.stopEvent = true;
		}
		//lecture des commandes event
		if(commandes!=null){
			try{
				if(curseurCommandes >= commandes.size()){
					curseurCommandes = 0;
					if(sOuvreParParole){
						ConditionParler.frameDeLaDerniereFermetureDUnePageQuiACetteCondition = this.event.map.lecteur.frameActuelle;
						this.event.map.lecteur.stopEvent = false; //on d�sactive le stopEvent si fin de la page
					}
				}
				Boolean onAvanceDansLesCommandes = true;
				while(onAvanceDansLesCommandes){
					int ancienCurseur = curseurCommandes;
					curseurCommandes = this.commandes.get(curseurCommandes).executer(curseurCommandes,commandes);
					if(curseurCommandes==ancienCurseur){ 
						//le curseur n'a pas chang�, c'est donc une commande qui prend du temps
						onAvanceDansLesCommandes = false;
					}
				}
			}catch(IndexOutOfBoundsException e){
				//on a fini la page
				curseurCommandes = 0;
				if(sOuvreParParole){
					ConditionParler.frameDeLaDerniereFermetureDUnePageQuiACetteCondition = this.event.map.lecteur.frameActuelle;
					this.event.map.lecteur.stopEvent = false; //on d�sactive le stopEvent si fin de la page
				}
				this.event.activerUnePage();
			}
		}
	}
	
}
