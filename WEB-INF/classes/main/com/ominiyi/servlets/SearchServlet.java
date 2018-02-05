package com.ominiyi.servlets;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ominiyi.model.Movie;
import com.ominiyi.model.SearchResult;
import com.ominiyi.utilities.FullTextSearch;

@SuppressWarnings("serial")
@WebServlet(name = "SearchServlet", urlPatterns = { "/search" })
public class SearchServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Set<Movie> foundMovies = Movie.fetchUniqueMovies();
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print(makeJsonResponse(foundMovies));
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String searchQuery = request.getParameter("searchQuery");
		
		Set<Movie> foundMovies = FullTextSearch.findDocumentsWithContent(searchQuery);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print(makeJsonResponse(foundMovies));
	}

	private String makeJsonResponse(Set<Movie> foundMovies) throws JsonProcessingException {
		Collection<SearchResult> obj = SearchResult.make(foundMovies);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(obj);
	}
}