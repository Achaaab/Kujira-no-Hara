package mouvements;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import commandes.Deplacement;
import map.Event;

/**
 * Toute CommandeEvent qui provoque le Mouvement d'un Event doit impl�menter cette interface.
 */
public abstract class Mouvement {
	private static final Logger LOG = LogManager.getLogger(Mouvement.class);
	
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
				
				mettreEventDansLaDirectionDuMouvement();
			} else {
				//d�clarer le Mouvement comme termin� (car ignor�)
				ignorerLeMouvement(event);
				event.avance = false;
			}
			
		} catch (Exception e) {
			LOG.error("Erreur lors du mouvement de l'�v�nement :", e);
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
	protected abstract void calculDuMouvement(Event event);
	
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
		final Deplacement deplacementNaturelOuForce;
		if (this.deplacement.naturel) {
			deplacementNaturelOuForce = event.deplacementNaturelActuel;
		} else {
			deplacementNaturelOuForce = event.deplacementForce;
		}
		
		//on regarde si la Page n'a pas chang�,
		//car on ne remet pas en bout de file un Mouvement naturel issu d'une autre Page 
		final Integer pageAvant = this.deplacement.page.numero;
		final Integer pageMaintenant;
		if (event.pageActive != null) {
			pageMaintenant = event.pageActive.numero;
		} else if (event.pageDApparence != null) {
			pageMaintenant = event.pageDApparence.numero;
		} else {
			pageMaintenant = null;
		}
		final boolean laPageEstToujoursLaMeme = pageAvant.equals(pageMaintenant);
		
		
		//si le D�placement est perp�tuel, on remet ce Mouvement en fin de liste
		if (this.deplacement.repeterLeDeplacement) {
			if (!this.deplacement.naturel //un Mouvement forc� perp�tuel ne s'arr�te pas m�me si la Page de l'Event change
			|| laPageEstToujoursLaMeme) { //un Mouvement naturel perp�tuel s'arr�te si la Page change
				deplacementNaturelOuForce.mouvements.add(this);
			} else {
				LOG.warn("On ne remet pas en bout de file le Mouvement "+this.getClass().getName()
						+" car la Page de l'Event "+this.deplacement.page.event.numero+" ("
						+this.deplacement.page.event.nom+") a chang� : de "+pageAvant+" vers "+pageMaintenant);
			}

		}
		
		//on retire ce Mouvement de la liste
		if (laPageEstToujoursLaMeme 
				&& deplacementNaturelOuForce.mouvements.size() >= 1
				&& deplacementNaturelOuForce.mouvements.get(0).equals(this)) {
			deplacementNaturelOuForce.mouvements.remove(0);
		} else {
			//cas th�oriquement impossible
			LOG.error("Impossible de retirer le premier Mouvement " + this.toString() 
			+ " du D�placement " + (this.deplacement.naturel ? "naturel" : "forc�")
			+ " de l'Event " + event.numero + " (" + event.nom + ")");
		}
	}
	
	/**
	 * Actions � effectuer lors de la fin du Mouvement.
	 * Sp�cifique � un type de Mouvement.
	 * @param event subissant le Mouvement
	 */
	protected abstract void terminerLeMouvementSpecifique(Event event);
	
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
	protected abstract void ignorerLeMouvementSpecifique(Event event);
	
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
