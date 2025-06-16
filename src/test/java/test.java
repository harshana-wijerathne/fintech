import org.mindrot.jbcrypt.BCrypt;

public class test {
    public static void main(String[] args) {
        String hashpw = BCrypt.hashpw("admin123", BCrypt.gensalt());
        System.out.println(hashpw);
    }
}
