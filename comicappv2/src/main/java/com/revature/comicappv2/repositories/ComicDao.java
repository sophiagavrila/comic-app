package com.revature.comicappv2.repositories;

import java.util.List;
import com.revature.comicappv2.models.Comic;

public interface ComicDao {
  
  /**
   * Get a Comic by its id
   * @param id
   * @return
   */
  Comic get(int id);
  
  /**
   * Gets all Comics
   * @return
   */
  List<Comic> getAll();
  
  /**
   * Gets all comics between two prices.
   * @param lower
   * @param upper
   * @return
   */
  List<Comic> getByPriceRange(double lower, double upper);
  
  /**
   * Save a new comic.
   * @param comic A comic not yet persisted.
   */
  void save(Comic comic);
  
  /**
   * Update an existing comic.  Works by id.
   * @param comic
   */
  void update(Comic comic);

}
