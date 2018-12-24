package preparateur;

/**
 * Adapter les fichiers JSON du jeu au moteur Java sur des sp�cificit�s.
 */
public abstract class Nettoyeur {
	
	/**
	 * Lancer le nettoyeur.
	 * @param args rien du tout
	 */
	public static void main(final String[] args) {
		// V�rifier si le nettoyage a d�j� �t� fait
		if (leNettoyageADejaEteFait()) {
			return;
		}

		// Noms des touches du clavier dans les messages
		//reecrireLesTouchesDuClavier();
		
		// Egaliser les musiques
		//egaliserLesMusiques();

	}

	private static boolean leNettoyageADejaEteFait() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Les dialogues mentionnent les touches du clavier, mais en Java elles sont diff�rentes !
	 */
	private static void reecrireLesTouchesDuClavier() {
		// TODO Auto-generated method stub
	}

	private static void egaliserLesMusiques() {
		//TODO calculer le volume moyen de chaque musique
		//TODO d�finir 1.0 comme volume par d�faut pour la plus basse
		//TODO d�duire les autres volumes par d�faut par produit en croix
		//TODO recenser les occurences de chaque musique dans le jeu avec leur volume assign�
		//TODO pour le plus grand volumed'usage, utiliser le volume par d�faut
		//TODO d�duire les remplacements des autres volumes d'usage par produit en croix 
	}
	
}
