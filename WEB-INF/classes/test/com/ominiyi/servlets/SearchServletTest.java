package com.ominiyi.servlets;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import com.ominiyi.model.Movie;

/**
 * @author Adedayo Ominiyi
 */
@PrepareForTest({ Movie.class, SearchServlet.class })
@RunWith(PowerMockRunner.class)
public class SearchServletTest {

	@Test
	public void testDoGet() throws Exception {
		PowerMockito.mockStatic(Movie.class);

		final Movie expectedMovie = new Movie("180", "Epic Roasthouse (399 Embarcadero)", 2011);
		expectedMovie.setLatitude(37.7908379);
		expectedMovie.setLongitude(-122.3893566);

		final Set<Movie> foundMovies = Collections.unmodifiableSet(new HashSet<Movie>() {
			private static final long serialVersionUID = 1L;

			{
				add(expectedMovie);
			}
		});
		
		PowerMockito.when(Movie.fetchUniqueMovies()).thenReturn(foundMovies);
		
		SearchServlet searchServlet = PowerMockito.spy(new SearchServlet());
		final String expectedJsonResponse = Whitebox.invokeMethod(searchServlet, "makeJsonResponse", foundMovies);
			
		MockHttpServletResponse response = new MockHttpServletResponse();

		// Hack: Objectify needs a filter context to run normally. However, this test
		// runs outside the scope of a filter so this is the workaround.
		ObjectifyService.run(new VoidWork() {

			@Override
			public void vrun() {

				try {
					searchServlet.doGet(null, response);
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			}
		});

		PowerMockito.verifyPrivate(searchServlet).invoke("printJsonResponse", foundMovies, response);
		
		Assert.assertEquals("application/json", response.getContentType());
		Assert.assertEquals("UTF-8", response.getCharacterEncoding());
		Assert.assertEquals(expectedJsonResponse, response.getWriterContent().toString());
	}
}
