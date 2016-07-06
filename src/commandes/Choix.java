package commandes;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;

import main.Commande;
import menu.Texte;
import utilitaire.Graphismes;
import utilitaire.InterpreteurDeJson;

/**
 * Un Choix donne la possibilit� au joueur de choisir jusqu'� quatre alternatives.
 * Le Choix s'affiche comme un Message, mais avec un curseur � d�placer.
 * Selon la s�lection du joueur, un embranchement diff�rent du code Event est utilis�.
 */
public class Choix extends Message {
	/** Num�ro du Choix */
	public int numero;
	
	/** Diff�rentes alternatives propos�es par le Choix */
	public final ArrayList<String> alternatives;

	private int positionCurseurAffichee = -1;
	public int positionCurseurChoisie = 0;
	public ArrayList<BufferedImage> imagesAlternatives = new ArrayList<BufferedImage>();
	 
	/**
	 * Constructeur explicite
	 * @param numero du Choix
	 * @param texte affich� dans la bo�te de dialogue
	 * @param alternatives offertes par le choix au joueur
	 */
	public Choix(final int numero, final String texte, final ArrayList<String> alternatives) {
		super(texte);
		this.numero = numero;
		this.alternatives = alternatives;
	}
	
	/**
	 * Constructeur g�n�rique
	 * @param parametres liste de param�tres issus de JSON
	 */
	public Choix(final HashMap<String, Object> parametres) {
		this( (int) parametres.get("numero"),
				(String) parametres.get("texte"),
				InterpreteurDeJson.recupererLesAlternativesDUnChoix((JSONArray) parametres.get("alternatives"))
		);
	}
	
	/**
	 * Fabrique l'image du Message � partir de l'image de la bo�te de dialogue et du texte.
	 * Une image est fabriqu�e pour chaque alternative � s�lectionner.
	 * @return image du Message
	 */
	@Override
	protected final BufferedImage produireImageDuMessage() {
		//TODO factoriser partiellement avec la m�thode m�re
		BufferedImage imageMessage = new BufferedImage(
				IMAGE_BOITE_MESSAGE.getWidth(), 
				IMAGE_BOITE_MESSAGE.getWidth(), 
				IMAGE_BOITE_MESSAGE.getType()
		);
		
		// Ajout de l'image de bo�te de dialogue
		imageMessage = Graphismes.superposerImages(imageMessage, IMAGE_BOITE_MESSAGE, 0, 0);
		
		// Ajout du texte
		final Texte t = new Texte(texte);
		imageMessage = Graphismes.superposerImages(IMAGE_BOITE_MESSAGE, t.texteToImage(), MARGE_DU_TEXTE, MARGE_DU_TEXTE);
		
		//TODO fabriquer l'image de base avec le texte et les diff�rentes alternatives, sans s�lection
		//...
		//TODO fabriquer les images avec les diff�rentes s�lections possibles
		//...
		
		return imageMessage;
	}
	/**
	 * Le curseur du Choix a-t-il boug� ?
	 * Si oui il faut remplacer l'image de Message affich�e.
	 * @return 
	 */
	@Override
	protected final boolean siChoixLeCurseurATIlBouge() {
		final boolean reponse = positionCurseurAffichee != positionCurseurChoisie;
		positionCurseurAffichee = positionCurseurChoisie;
		return reponse;
	}
	
	/**
	 * La Commande suivante d�pend de l'alternative choisie par le joueur.
	 */
	@Override
	protected final int redirectionSelonLeChoix(final int curseurActuel, final ArrayList<Commande> commandes) {
		for (int i = 0; i < commandes.size(); i++) {
			final Commande commande = commandes.get(i);
			if (commande instanceof ChoixAlternative) {
				final ChoixAlternative alternative = (ChoixAlternative) commande;
				if (alternative.numeroChoix == this.numero 
						&& alternative.numeroAlternative == positionCurseurAffichee
				) {
					//c'est l'alternative choisie par le joueur !
					return i+1;
				}
			}
		}
		//le d�but de Boucle n'a pas �t� trouv�
		System.err.println("L'alternative " + positionCurseurAffichee
				+ " du choix num�ro " + numero + " n'a pas �t� trouv�e !");
		return curseurActuel+1;
	}
}
