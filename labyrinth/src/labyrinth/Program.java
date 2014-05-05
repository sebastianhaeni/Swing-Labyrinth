package labyrinth;

/**
 * 
 * @author Sebastian Häni <haeni.sebastian@gmail.com>
 *
 */
public class Program {
	public static void main(String[] args) {
		if (args.length >= 1) {
			new Main(args[0]);
		} else {
			new Main(null);
		}
	}
}
