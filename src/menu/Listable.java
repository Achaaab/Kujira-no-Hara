package menu;

import java.util.Map;

/**
 * Un Listable peut avoir ses informations affich�es dans une Liste.
 */
public interface Listable {
	
	/**
	 * Les informations qu'on peut afficher dans la Liste pour ce Listable.
	 * Chaque type d'information a un nom et une valeur.
	 * @return association cl�-valeur
	 */
	public Map<String, Object> obtenirLesInformations();
}
