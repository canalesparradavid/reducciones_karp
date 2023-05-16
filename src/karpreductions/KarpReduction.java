package karpreductions;
/**
 *
 * @author MAZ
 * @param <A>: clase de las instancias del problema A
 * @param <B>: clase de las instancias del problema B
 * 
 * El sentido de la reducciÃ³n es B <= A.
 */
public interface KarpReduction<B, A> {
  
  public A transform (final B instance);
  
}