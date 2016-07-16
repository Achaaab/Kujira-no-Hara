package map;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import commandes.Deplacement;
import main.Fenetre;
import mouvements.Avancer;
import mouvements.Mouvement;
import utilitaire.GestionClavier;
import utilitaire.InterpreteurDeJson;

/**
 * Event particulier qui est d�plac� par le joueur � l'aide du clavier
 */
public class Heros extends Event {
	public static final Event MODELE = creerModele();
	
	/**
	 * L'animation d'attaque vaut 0 si le h�ros n'attaque pas.
	 * Au d�but d'une attaque, elle est mise au maximum (longueur de l'animation de l'attaque).
	 * A chaque frame, elle est affich�e puis d�cr�ment�e.
	 */
	public int animationAttaque = 0;

	/**
	 * Constructeur explicite
	 * @param x position x du H�ros sur la Map
	 * @param y position y du H�ros sur la Map
	 * @param directionEnDebutDeMap directiondu H�ros au d�but de la Map
	 * @throws FileNotFoundException 
	 */
	public Heros(final int x, final int y, final int directionEnDebutDeMap) throws FileNotFoundException {
		super(x, y, MODELE.nom, MODELE.id, MODELE.pages, MODELE.largeurHitbox, MODELE.hauteurHitbox);
		this.direction = directionEnDebutDeMap;
	}
	
	/**
	 * Le H�ros est cr�� � partir d'un mod�le.
	 * Ce mod�le est un Event g�n�rique.
	 * @return Event mod�le qui sert � la cr�ation du H�ros
	 */
	private static final Event creerModele() {
		JSONObject jsonEventGenerique = null;
		try {
			jsonEventGenerique = InterpreteurDeJson.ouvrirJsonEventGenerique("Heros");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		int largeur = jsonEventGenerique.has("largeur") ? (int) jsonEventGenerique.get("largeur") : Event.LARGEUR_HITBOX_PAR_DEFAUT;
		int hauteur = jsonEventGenerique.has("hauteur") ? (int) jsonEventGenerique.get("hauteur") : Event.LARGEUR_HITBOX_PAR_DEFAUT;
		final JSONArray jsonPages = jsonEventGenerique.getJSONArray("pages");
		final ArrayList<PageEvent> pages = creerListeDesPagesViaJson(jsonPages, 0);
		
		final Event modele = new Event (0, 0, "heros", 0, pages, largeur, hauteur);
		return modele;
	}
	
	@Override
	public final void deplacer() {
		if (animationAttaque > 0) {
			//pas de d�placement si animation d'attaque
			this.animation = Fenetre.getPartieActuelle().getArmeEquipee().framesDAnimation[animationAttaque-1];
			
			animationAttaque--;
		} else if (this.deplacementForce!=null && this.deplacementForce.mouvements.size()>0) {
			//il y a un d�placement forc�
			this.deplacementForce.executerLePremierMouvement();
		} else {
			//d�placement selon les touches et les obstacles rencontr�s
			boolean ilYADeplacement = false;
			if ( GestionClavier.ToucheRole.HAUT.pressee && !GestionClavier.ToucheRole.BAS.pressee ) {
				if ( unPasVers(Event.Direction.HAUT).mouvementPossible() ) {
					ilYADeplacement = true;
					this.y -= pageActive.vitesse;
				}
			}
			if ( GestionClavier.ToucheRole.BAS.pressee && !GestionClavier.ToucheRole.HAUT.pressee ) {
				if ( unPasVers(Event.Direction.BAS).mouvementPossible() ) {
					ilYADeplacement = true;
					this.y += pageActive.vitesse;
				}
			}
			if ( GestionClavier.ToucheRole.GAUCHE.pressee && !GestionClavier.ToucheRole.DROITE.pressee ) {
				if ( unPasVers(Event.Direction.GAUCHE).mouvementPossible() ) {
					ilYADeplacement = true;
					this.x -= pageActive.vitesse;
				}
			}
			if ( GestionClavier.ToucheRole.DROITE.pressee && !GestionClavier.ToucheRole.GAUCHE.pressee ) {
				if ( unPasVers(Event.Direction.DROITE).mouvementPossible() ) {
					ilYADeplacement = true;
					this.x += pageActive.vitesse;
				}
			}
			if (ilYADeplacement) {
				this.avance = true;
				//on profite du d�placement pour remettre le H�ros dans la bonne direction
				this.mettreDansLaBonneDirection();
			} else {
				this.avance = false;
				//le H�ros n'attaque pas et ne bouge pas donc on remet sa premi�re frame d'animation
				this.animation = 0;
			}
		}
	}
	
	/**
	 * Cr�er un pas dans la direction voulue.
	 * @param dir direction du pas
	 * @return un pas dans la direction demand�e
	 */
	private Mouvement unPasVers(final int dir) {
		if(this.pageActive == null){
			this.activerUnePage();
		}
		final Mouvement pas = new Avancer(dir, pageActive.vitesse);
		pas.deplacement = new Deplacement(0, new ArrayList<Mouvement>(), true, false, false);
		pas.deplacement.page = this.pageActive; //on apprend au D�placement quelle est sa Page
		return pas;
	}
	
	/**
	 * Tourner le H�ros dans la bonne Direction selon l'entr�e clavier.
	 * Il faut qu'� ce moment le H�ros soit libre de ses Mouvements.
	 */
	public final void mettreDansLaBonneDirection() {
		final Heros heros = map.heros;
		if (!this.map.lecteur.stopEvent //pas de gel des Events
				&& heros.animationAttaque <= 0 //pas en attaque
				&& (this.deplacementForce == null || this.deplacementForce.mouvements.size() <= 0) //pas de D�placement forc�
		) {
			if ( GestionClavier.ToucheRole.GAUCHE.pressee ) {
				heros.direction = Event.Direction.GAUCHE;
			} else if ( GestionClavier.ToucheRole.DROITE.pressee ) {
				heros.direction = Event.Direction.DROITE;
			} else if ( GestionClavier.ToucheRole.BAS.pressee ) {
				heros.direction = Event.Direction.BAS;
			} else if ( GestionClavier.ToucheRole.HAUT.pressee ) {
				heros.direction = Event.Direction.HAUT;
			}
		}
	}
	
}
