package com.revature.comicappv2.repositories;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.revature.comicappv2.exceptions.InvalidComicException;
import com.revature.comicappv2.exceptions.NotFoundException;
import com.revature.comicappv2.models.Comic;

/**
 * Implementation of ComicDao for Postgres
 * 
 * TODO: throw Exceptions so we can provide feedback to clients
 * @author Revature
 *
 */
public class ComicDaoPostgres implements ComicDao {

  private static Connection conn;

  // This guy will run when the class loads, after static fields are initialized.
  static {
    // This explicitly loads the Driver class:
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e1) {
      e1.printStackTrace();
    }
    try {
      conn = DriverManager.getConnection(System.getenv("connstring"), System.getenv("username"),
          System.getenv("password"));
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Comic get(int id) {
    Comic out = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try {
      stmt = conn.prepareStatement("SELECT * FROM comics WHERE id = ?");
      // 1 is the index of the ?
      stmt.setInt(1, id);
      // Now our statement is ready to go. Lets run it.
      if (stmt.execute()) {
        // Now we have some results. Lets get them.
        rs = stmt.getResultSet();
      }
      // This line is typical, but not particularly useful for our 1-line rs
      while (rs.next()) {
        out = new Comic(rs.getInt("id"), rs.getString("title"), rs.getInt("page_count"),
            rs.getDouble("price"), rs.getInt("rating"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new NotFoundException(e);
    }

    return out;
  }

  @Override
  public void save(Comic comic) {
    PreparedStatement stmt = null;
    try {
      stmt = conn.prepareStatement(
          "INSERT INTO comics(title, page_count, price, rating) VALUES (?,?,?,?)");
      stmt.setString(1, comic.getTitle());
      stmt.setInt(2, comic.getPageCount());
      stmt.setDouble(3, comic.getPrice());
      stmt.setInt(4, comic.getRating());

      stmt.execute();
    } catch (SQLException e) {
      e.printStackTrace();
      throw new InvalidComicException(e);
    }
  }

  @Override
  public void update(Comic comic) {
    PreparedStatement stmt = null;
    try {
      stmt = conn.prepareStatement(
          "UPDATE comics SET title = ?, page_count = ?, price = ?, rating = ? WHERE id = ?");
      stmt.setString(1, comic.getTitle());
      stmt.setInt(2, comic.getPageCount());
      stmt.setDouble(3, comic.getPrice());
      stmt.setInt(4, comic.getRating());
      stmt.setInt(5, comic.getId());

      stmt.execute();

    } catch (SQLException e) {
      e.printStackTrace();
      throw new InvalidComicException(e);
    }

  }

  @Override
  public List<Comic> getAll() {
    List<Comic> allComics = new ArrayList<Comic>();

    PreparedStatement stmt = null;
    ResultSet rs = null;

    try {
      stmt = conn.prepareStatement("SELECT * FROM comics");

      if (stmt.execute()) {
        rs = stmt.getResultSet();
      }
      while (rs.next()) {
        allComics.add(new Comic(rs.getInt("id"), rs.getString("title"), rs.getInt("page_count"),
            rs.getDouble("price"), rs.getInt("rating")));
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new NotFoundException(e);
    }
    return allComics;
  }

  @Override
  public List<Comic> getByPriceRange(double lower, double upper) {
    List<Comic> comicsInRange = new ArrayList<Comic>();

    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      stmt = conn.prepareStatement("SELECT * FROM comics WHERE price < ? AND price > ?");
      stmt.setDouble(1, upper);
      stmt.setDouble(2, lower);

      if (stmt.execute()) {
        rs = stmt.getResultSet();
      }
      while (rs.next()) {
        comicsInRange.add(new Comic(rs.getInt("id"), rs.getString("title"), rs.getInt("page_count"),
            rs.getDouble("price"), rs.getInt("rating")));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return comicsInRange;
  }

}
