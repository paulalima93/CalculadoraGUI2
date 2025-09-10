import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CalculadoraDAO {
    public void salvarOperacao(String valorExpressao, double valorResultado){
        String sql = "INSERT INTO historico (expressao, resultado) VALUES (?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, valorExpressao);
            stmt.setDouble(2, valorResultado);
            stmt.executeUpdate();

        } catch (SQLException e){
            e.printStackTrace();
        }
    }


}
