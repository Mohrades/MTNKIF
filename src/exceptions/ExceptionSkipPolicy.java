package exceptions;

import java.util.List;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;


/**
*
Implementing a skip policy with no skip limit
*/
public class ExceptionSkipPolicy implements SkipPolicy {

	/*private Class<? extends Exception> exceptionClassToSkip;*/
	private List<Class<? extends Exception>> exceptionsClassesToSkip;

	/*public ExceptionSkipPolicy(Class<? extends Exception> exceptionClassToSkip) {*/
	public ExceptionSkipPolicy(List<Class<? extends Exception>> exceptionsClassesToSkip) {
		super();

		/*this.exceptionClassToSkip = exceptionClassToSkip;*/
		this.exceptionsClassesToSkip = exceptionsClassesToSkip;
	}

	@Override
	/**
	 *
	Skips on Exception class and subclasses
	 */
	public boolean shouldSkip(Throwable t, int skipCount) throws SkipLimitExceededException {
		// TODO Auto-generated method stub

		for(Class<? extends Exception> exceptionClassToSkip : exceptionsClassesToSkip) {
			if(exceptionClassToSkip != null) {
				try {
					/**
					 *
					On peut s'assurer qu'un objet est bien une instance d'une classe donn�e en recourant � l'op�rateur instanceOf.
					Par exemple, l'expression p instanceOf Point vaudra true si p est (exactement) de type Point.
					Mais ce test ne reponds pas � nos probl�matiques ici. Et donc, nous n'utilisons pas cette m�thode.
					
					Les conversions explicites de r�f�rences:
					Nous avons largement insist� sur la compatibilit� qui existe entre r�f�rence � un objet d�un type donn� et r�f�rence � un objet d'un type ascendant.
					Comme on peut s'y attendre, la compatibilit� n'a pas lieu dans le sens inverse. Consid�rons cet exemple, fond� sur nos classes Point et Pointcol habituelles :
					class Point { ..... }
					class Pointcol extends Point { ..... }
					   .....
					Pointcol pc ;
					pc = new Point (...) ;    // erreur de compilation
					Si l'affectation �tait l�gale, un simple appel tel que pc.colore(...) conduirait � attribuer une
					couleur � un objet de type Point, ce qui poserait quelques probl�mes � l'ex�cution...
					Mais consid�rons cette situation :
					Point p ;
					Pointcol pc1 = new Pointcol(...), pc2 ;
					   .....
					p = pc1 ;   // p contient la r�f�rence � un objet de type Pointcol
					   .....
					pc2 = p ;   // refus� en compilation
					L'affectation pc2 = p est tout naturellement refus�e. Cependant, nous sommes certains que p contient bien ici la r�f�rence � un objet de type Pointcol.
					En fait, nous pouvons forcer le compilateur � r�aliser la conversion correspondante en utilisant l'op�rateur de cast d�j� rencontr� pour les types primitifs. Ici, nous �crirons simplement :
					pc2 = (Pointcol) p ;   // accept� en compilation 
					Toutefois, lors de l'ex�cution, Java s'assurera que p contient bien une r�f�rence � un objet de type Pointcol (ou d�riv�) afin de ne pas compromettre la bonne ex�cution du programme.
					Dans le cas contraire, on obtiendra une exception ClassCastException qui, si elle n'est pas trait�e, conduira � un arr�t de l'ex�cution.

					!! CECI EST JUSTE UNE INFORMATION A SE RAPPELER

					 */
					if(exceptionClassToSkip.isAssignableFrom(t.getClass())) {
						return true;
					}

				} catch(NullPointerException ex) {

				} catch(Throwable th) {

				}
			}
		}

		return false;

		/*return exceptionClassToSkip.isAssignableFrom(t.getClass());*/
		// return false;
	}

}
