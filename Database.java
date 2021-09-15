package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.*;

public class Database {
    static String url = "jdbc:sqlite:";

    public static boolean depositMoney(String toCard, int toAdd) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        boolean toReturn = false;

        String update = "UPDATE card SET balance = balance + ? WHERE number = ?";

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(update)) {
                preparedStatement.setInt(1, toAdd);
                preparedStatement.setString(2, toCard);

                int i = preparedStatement.executeUpdate();
                if (i == 1) {
                    toReturn = true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    public static boolean withdrawMoney(String fromCard, int toWithdraw) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        boolean toReturn = false;

        String update = "UPDATE card SET balance = balance - ? WHERE number = ?";

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(update)) {
                preparedStatement.setInt(1, toWithdraw);
                preparedStatement.setString(2, fromCard);

                int i = preparedStatement.executeUpdate();
                if (i == 1) {
                    toReturn = true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    public static boolean transferMoney(String fromCard, String toCard, int transferAmount) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        boolean toReturn = false;

        if (withdrawMoney(fromCard, transferAmount)) {
            toReturn = depositMoney(toCard, transferAmount);
        }

        return toReturn;
    }

    public static void closeAcc(String number) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        String delete = "DELETE FROM card WHERE number = ?";

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(delete)) {
                preparedStatement.setString(1, number);

                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addCreditCard(String number, String pin) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        String insert = "INSERT INTO card (number, pin) VALUES (?, ?)";

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(insert)) {
                preparedStatement.setString(1, number);
                preparedStatement.setString(2, pin);

                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean doesCardExist(String t1) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        boolean toReturn = false;

        String select = "SELECT id, number, pin FROM card WHERE number = ?";

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(select)) {
                preparedStatement.setString(1, t1);

                try (ResultSet set = preparedStatement.executeQuery()) {
                    if (set.next()) {
                        toReturn = true;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    public static boolean isLoggedIn(String t1, String t2) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        boolean toReturn = false;

        String select = "SELECT id, number, pin FROM card WHERE " +
                "number = ? AND pin = ?";

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(select)) {
                preparedStatement.setString(1, t1);
                preparedStatement.setString(2, t2);

                try (ResultSet set = preparedStatement.executeQuery()) {
                    if (set.next()) {
                        String n = set.getString("number");
                        String p = set.getString("pin");
                        if (n.equals(t1) && p.equals(t2)) {
                            toReturn = true;
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    public static int getBalance(String number, String pin) {
        int balance = 0;
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        String select = "SELECT balance FROM card WHERE number = ? AND pin = ?";

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(select)) {
                preparedStatement.setString(1, number);
                preparedStatement.setString(2, pin);

                try (ResultSet set = preparedStatement.executeQuery()) {
                    if (set.next()) {
                        balance = set.getInt("balance");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return balance;
    }

    public static void createTable(String filename) {
        url = url.concat(filename);

        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        String sql = "CREATE TABLE IF NOT EXISTS card (" +
                "id INTEGER PRIMARY KEY, " +
                "number TEXT, " +
                "pin TEXT," +
                "balance INTEGER DEFAULT 0);";

        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
