package commandes;

import java.util.ArrayList;

import main.Commande;
import main.Fenetre;
import map.Deplacement;
import map.Event;
import map.LecteurMap;

/**
 * Toute CommandeEvent qui provoque le Mouvement d'un Event doit impl�menter cette interface.
 */
public abstract class Mouvement extends Commande implements CommandeEvent {
	/** id de l'Event qui va �tre d�plac� durant ce Mouvement */
	protected Integer idEventADeplacer; //Integer car cl� d'une HashMap, et null lorsque "cet Event"
	/** Le Mouvement est-il commenc� ? */
	protected boolean commence;
	/** Le Mouvement est-il termin� ? */
	protected boolean termine;
	/** Nombre d'�tapes du Mouvement qui ont �t� faites */
	protected int ceQuiAEteFait;
	/** Nombre d'�tapes � faire */
	protected int etapes;
	
	/**
	 * Si la Page de comportement doit �tre rejou�e, il faut r�initialiser cette Commande.
	 * R�initialiser un mouvement le d�clare non fait, et change la direction en cas de mouvement al�atoire.
	 */
	public final void reinitialiser() {
		this.commence = false;
		this.termine = false;
		this.ceQuiAEteFait = 0;
		reinitialiserSpecifique();
	}
	
	/**
	 * Actions � effectuer pour pouvoir �ventuellement rejouer le Mouvement encore.
	 * Sp�cifique � un type de Mouvement.
	 */
	protected abstract void reinitialiserSpecifique();
	
	/**
	 * Ajouter ce Mouvement � la liste des Mouvements forc�s pour cet Event.
	 */
	@Override
	public final int executer(final int curseurActuel, final ArrayList<Commande> commandes) {
		if (!this.commence) {
			//le Mouvement n'a pas encore �t� ajout� � la liste des Mouvements forc�s
			final Event event = this.getEventADeplacer();
			event.deplacementForce.mouvements.add(this);
		}
		
		if (this.termine) {
			//le Mouvement est termin�, on passe � la CommandeEvent suivante
			this.reinitialiser();
			return curseurActuel+1;
		} else {
			//le Mouvement n'est pas termin�, on attend avant de passer � la CommandeEvent suivante
			return curseurActuel;
		}
	}
	
	/**
	 * Proc�der aux modifications de donn�es permettant au LecteurMap d'afficher l'Event au bon endroit.
	 * @param deplacement dont fait partie ce mouvement
	 */
	public final void executerLeMouvement(final Deplacement deplacement) {
		try {
			final Event event = this.getEventADeplacer();
			if ( this.mouvementPossible() ) {
				//appliquer l'effet du Mouvement sur la Map et les Events
				calculDuMouvement(event);
				
				//quelle sera la commande suivante ?
				if ( this.ceQuiAEteFait >= this.etapes ) {
					//d�clarer le Mouvement comme termin� (car il est r�ellement termin�)
					terminerLeMouvementSpecifique(event, deplacement); //d�pend du type de Mouvement
					terminerLeMouvement(deplacement); //factoris� dans la classe m�re
				}
			} else {
				//d�clarer le Mouvement comme termin� (car ignor�)
				ignorerLeMouvementSpecifique(event, deplacement); //d�pend du type de Mouvement
				ignorerLeMouvement(event, deplacement); //factoris� dans la classe m�re
			}
		} catch (Exception e) {
			System.out.println("Erreur lors du mouvement de l'�v�nement :");
			e.printStackTrace();
		}
	}
	
	/**
	 * Le Mouvement est-il possible sur cette Map ?
	 * @return true si le Mouvement est possible
	 */
	public abstract boolean mouvementPossible();
	
	/**
	 * Tout Mouvement d�place un Event de la Map en particulier.
	 * @return Event qui va �tre d�plac�
	 */
	public final Event getEventADeplacer() {
		if (this.idEventADeplacer !=null) {
			//un num�ro d'Event � d�placer a �t� sp�cifi� dans le JSON
			return ((LecteurMap) Fenetre.getFenetre().lecteur).map.eventsHash.get((Integer) this.idEventADeplacer);
		} else {
			//aucun num�ro n'a �t� sp�ifi�, on d�place l'Event qui a lanc� la Commande
			return this.page.event;
		}
		
	}
	
	/** 
	 * Appliquer l'effet du Mouvement sur la Map et les Events.
	 * @param event subissant le Mouvement
	 */
	protected abstract void calculDuMouvement(final Event event);
	
	/** 
	 * D�clarer le Mouvement comme termin�.
	 * @param deplacement dont fait partie ce mouvement
	 */
	private void terminerLeMouvement(final Deplacement deplacement) {
		System.out.println("terminerLeMouvement "+this.getClass().getName());
		//le Mouvement est termin�
		this.termine = true;
		
		//on retire ce Mouvement de la liste
		deplacement.mouvements.remove(0);
		
		//si le D�placement est perp�tuel, on remet ce Mouvement en fin de liste
		if (deplacement.repeterLeDeplacement) {
			//on le r�initialise et on le met en bout de file
			this.reinitialiser();
			deplacement.mouvements.add(this);
		}
	}
	
	/**
	 * Actions � effectuer lors de la fin du Mouvement.
	 * Sp�cifique � un type de Mouvement.
	 * @param event subissant le Mouvement
	 * @param deplacement dont fait partie le Mouvement
	 */
	protected abstract void terminerLeMouvementSpecifique(final Event event, final Deplacement deplacement);
	
	/** 
	 * D�clarer le Mouvement comme termin� car ignor�.
	 * @param event subissant le Mouvement
	 * @param deplacement dont fait partie ce mouvement
	 */
	private void ignorerLeMouvement(final Event event, final Deplacement deplacement) {
		event.avance = false;
		event.saute = false;
		if (deplacement.ignorerLesMouvementsImpossibles) {
			//on ignore ce Mouvement impossible et on passe au suivant
			this.termine = true;
			if (deplacement.repeterLeDeplacement) {
				//on le r�initialise et on le met en bout de file
				this.reinitialiser();
				deplacement.mouvements.add(this);
			}
			//on passe au mouvement suivant
			deplacement.mouvements.remove(0);
		}
	}
	
	/**
	 * Actions � effectuer lors de l'ignorage du Mouvement.
	 * Sp�cifique � un type de Mouvement.
	 * @param event subissant le Mouvement
	 * @param deplacement dont fait partie le Mouvement
	 */
	protected abstract void ignorerLeMouvementSpecifique(final Event event, final Deplacement deplacement);
	
}
