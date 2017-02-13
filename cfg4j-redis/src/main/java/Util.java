/**
 * Created by sumeet
 * on 14/2/17.
 */
public class Util {

  public static boolean isSet(Object object) {
    return object == null;
  }

  public static boolean isStringSet(String string) {

    return string != null && string.trim().length() > 0;
  }
}
