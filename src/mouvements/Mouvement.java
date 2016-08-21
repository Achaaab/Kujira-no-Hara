package mouvements;

import commandes.Deplacement;
import map.Event;

/**
 * Toute CommandeEvent qui provoque le Mouvement d'un Event doit impl�menter cette interface.
 */
public abstract class Mouvement {
	/** Nombre d'�tapes du Mouvement qui ont �t� faites */
	protected int ceQuiAEteFait;
	/** Nombre d'�tapes � faire */
	protected int etapes;
	/** D�placement dont fait partie ce Mouvement */
	public Deplacement deplacement;

	/**
	 * Si la Page de comportement doit �tre rejou�e, il faut r�initialiser cette Commande.
	 * R�initialiser un mouvement le d�clare non fait, et change la direction en cas de mouvement al�atoire.
	 */
	public final void reinitialiser() {
		//r�initialisation sp�cifique � ce type de Mouvement en particulier
		this.reinitialiserSpecifique();
		
		//r�nitialisation commune � tous les Mouvements
		this.ceQuiAEteFait = 0;
	}
	
	/**
	 * Actions � effectuer pour pouvoir �ventuellement rejouer le Mouvement encore.
	 * Sp�cifique � un type de Mouvement.
	 */
	protected abstract void reinitialiserSpecifique();
	
	/**
	 * Proc�der aux modifications de donn�es permettant au LecteurMap d'afficher l'Event au bon endroit.
	 * M�thode appel�e lors de l'ex�cution des D�placements.
	 * @param deplacement (naturel ou forc� d'un Event) dont fait partie ce Mouvement
	 */
	public final void executerLeMouvement(final Deplacement deplacement) {
		try {
			final Event event = this.deplacement.getEventADeplacer();

			if ( this.mouvementPossible() ) {
				//appliquer l'effet du Mouvement sur la Map et les Events
				calculDuMouvement(event);

				//quelle sera la commande suivante ?
				if ( this.ceQuiAEteFait >= this.etapes ) {
					//d�clarer le Mouvement comme termin� (car il est r�ellement termin�)
					terminerLeMouvement(event);
				}
			} else {
				//d�clarer le Mouvement comme termin� (car ignor�)
				ignorerLeMouvement(event);
				event.avance = false;
			}
			
			//m�me si le Mouvement est avort�, le changement de direction a lieu
			mettreEventDansLaDirectionDuMouvement();
			
		} catch (Exception e) {
			System.err.println("Erreur lors du mouvement de l'�v�nement :");
			e.printStackTrace();
		}
	}
	
	/**
	 * Le Mouvement est-il possible sur cette Map ?
	 * @return true si le Mouvement est possible
	 */
	public abstract boolean mouvementPossible();
	
	
	
	/** 
	 * Applique l'effet du Mouvement sur la Map et les Events.
	 * Puis incr�mente le compteur "ceQuiAEteFait".
	 * @param event subissant le Mouvement
	 */
	protected abstract void calculDuMouvement(final Event event);
	
	/** 
	 * D�clarer le Mouvement comme termin�.
	 * @param event subissant le Mouvement
	 */
	private void terminerLeMouvement(final Event event) {
		//finalisation sp�cifique � ce type de Mouvement en particulier
		terminerLeMouvementSpecifique(event); //d�pend du type de Mouvement
		
		//finalisation commune � tous les Mouvements
		this.reinitialiser();
		
		//est-on dans un D�placement naturel ou forc� ?
		Deplacement deplacementNaturelOuForce;
		if (this.deplacement.naturel) {
			deplacementNaturelOuForce = event.deplacementNaturelActuel;
		} else {
			deplacementNaturelOuForce = event.deplacementForce;
		}
		
		//si le D�placement est perp�tuel, on remet ce Mouvement en fin de liste
		if (this.deplacement.repeterLeDeplacement) {
			deplacementNaturelOuForce.mouvements.add(this);
		}
		
		//on retire ce Mouvement de la liste
		if (deplacementNaturelOuForce.mouvements.size() >= 1) {
			deplacementNaturelOuForce.mouvements.remove(0);
		} else {
			//cas th�oriquement impossible
			System.err.println("Impossible de retirer le premier Mouvement " + this.toString() 
			+ " du D�placement " + (this.deplacement.naturel ? "naturel" : "forc�")
			+ " de l'Event " + event.numero + " (" + event.nom + ")");
		}
	}
	
	/**
	 * Actions � effectuer lors de la fin du Mouvement.
	 * Sp�cifique � un type de Mouvement.
	 * @param event subissant le Mouvement
	 */
	protected abstract void terminerLeMouvementSpecifique(final Event event);
	
	/** 
	 * D�clarer le Mouvement comme termin� car ignor�.
	 * @param event subissant le Mouvement
	 */
	private void ignorerLeMouvement(final Event event) {
		//interruption sp�cifique � ce type de Mouvement en particulier
		ignorerLeMouvementSpecifique(event);
		
		//interruption commune � tous les Mouvements
		if (this.deplacement.ignorerLesMouvementsImpossibles) {
			//on ignore ce Mouvement impossible et on passe au suivant
			terminerLeMouvement(event);
		}
	}
	
	/**
	 * Actions � effectuer lors de l'ignorage du Mouvement.
	 * Sp�cifique � un type de Mouvement.
	 * @param event subissant le Mouvement
	 */
	protected abstract void ignorerLeMouvementSpecifique(final Event event);
	
	/**
	 * D�crire le Mouvement textuellement
	 * @return description du Mouvement
	 */
	public abstract String toString();
	
	/**
	 * Pendant le Mouvement, l'Event est suceptible de changer de direction.
	 * Il faut tourner l'Event dans la direction dict�e par le Mouvement.
	 */
	public final void mettreEventDansLaDirectionDuMouvement() {
		final Event event = this.deplacement.getEventADeplacer();
		if (!event.directionFixeActuelle) {
			final int directionImposee = this.getDirectionImposee();
			if (directionImposee != -1) {
				event.direction = directionImposee;
			}
		}
	}
	
	/**
	 * Quelle est la direction impos�e par le Mouvement � l'Event ?
	 * @return direction impos�e par le Mouvement � l'Event ou -1 si aucune.
	 */
	public abstract int getDirectionImposee();
}
