package com.revature.comicappv2.servlets;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.comicappv2.exceptions.ComicException;
import com.revature.comicappv2.models.Comic;
import com.revature.comicappv2.repositories.ComicDaoPostgres;
import com.revature.comicappv2.services.ComicService;

@WebServlet(name = "FrontController", urlPatterns = {"/api/*"})
public class FrontController extends HttpServlet {
  
  private ComicService comicService;
  private ObjectMapper om;
  
  @Override
  public void init() throws ServletException {
    this.comicService =  new ComicService(new ComicDaoPostgres());
    this.om = new ObjectMapper();
    super.init();
  }
  
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    System.out.println("URI:" + req.getRequestURI());
    String[] tokens = req.getRequestURI().split("/");
    
    if(tokens[3].equals("comics")) {
      boolean idWasSpecified = tokens.length > 4;
      if(idWasSpecified) {
        Integer id = Integer.valueOf(tokens[4]);
        Comic comic = comicService.get(id);
        //TODO: separate this logic out
        if(comic == null) {
          resp.setStatus(404);
        }
        resp.getWriter().write(om.writeValueAsString(comic));
      } else {
        List<Comic> comics = null;
        if(req.getParameter("lower")==null && req.getParameter("upper")==null) {
          comics = comicService.getAll();
        } else {
          try {
          comics = comicService.getByPriceRange(
              Double.valueOf(req.getParameter("lower")),
              Double.valueOf(req.getParameter("upper")));
          } catch (NullPointerException e) {
            resp.sendError(400, "must specify both lower and upper if one is specified");
          }
        }
        
        resp.getWriter().write(om.writeValueAsString(comics));
      }
    }
  }
  
  /**
   * TODO: In our Front Controller, we can use req.getMethod() to check the method ourselves
   * This would let us remove duplicate logic, so we should do it.
   */
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    System.out.println("URI: " + req.getRequestURI());
    String[] tokens = req.getRequestURI().split("/");

    switch(tokens[3]) {
      case "comics":
        Comic receivedComic = om.readValue(req.getReader(), Comic.class);
        System.out.println(receivedComic);
        try {
          comicService.save(receivedComic);
        } catch (ComicException e) {
          //another option:
          resp.setStatus(400);
          resp.getWriter().write(om.writeValueAsString(e));
          //this is a fine option:
          //resp.sendError(400, "Failed to save comic");
        }
        break;
      default:
        resp.setStatus(404);
          
    }
  }
  
}
