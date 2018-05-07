package com.ominiyi.utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.GetRequest;
import com.google.appengine.api.search.GetResponse;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.PutException;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchException;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.search.StatusCode;

import com.ominiyi.model.Movie;

/**
 * The FullTextSearch class is responsible for interacting with the AppEngine full-text 
 * search index. 
 *
 * @author  Adedayo Ominiyi
 */
public class FullTextSearch {

	private final static Logger LOGGER = Logger.getLogger(FullTextSearch.class.getName());
	
	// Hack: Only about 200 documents can be processed by App Engine at a time.
	public static final int DOCUMENTS_PROCESSING_LIMIT = 200;
	private static final String SEARCH_INDEX = "movieFullTextSearchIndex";

	private FullTextSearch() {
	}

	private static Index getIndex() {
		IndexSpec indexSpec = IndexSpec.newBuilder().setName(SEARCH_INDEX).build();
		Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);
		return index;
	}

	public static void deleteAllDocuments() throws InterruptedException {
		// Hack: The maximum size of 200 causes it to fail from time to time.
		final int DELETE_PROCESSING_LIMIT = DOCUMENTS_PROCESSING_LIMIT / 4;
		Index index = getIndex();

		int delay = 2;
		while (true) {
			List<String> docIds = new ArrayList<>();

			// Return a set of doc_ids.
			GetRequest request = GetRequest.newBuilder().setLimit(DELETE_PROCESSING_LIMIT).setReturningIdsOnly(true)
					.build();
			GetResponse<Document> response = index.getRange(request);
			if (response.getResults().isEmpty()) {
				break;
			}
			for (Document doc : response) {
				docIds.add(doc.getId());
			}
			try {
				index.delete(docIds);
				docIds.clear();
			} catch (Exception ex) {
				LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
				Thread.sleep(delay * 1000);
				delay *= 2; // easy exponential backoff
				continue;
			}
		}
	}

	public static void indexAllMovies(Set<Movie> moviesWithGeoPointsSet)
			throws InterruptedException, ExecutionException {
		List<Document> moviesDocuments = new ArrayList<>();
		for (Movie movie : moviesWithGeoPointsSet) {
			moviesDocuments.add(movie.makeDocument());

			if (moviesDocuments.size() == FullTextSearch.DOCUMENTS_PROCESSING_LIMIT) {
				indexDocuments(moviesDocuments);
				moviesDocuments.clear();
			}
		}

		// Index remaining documents.
		indexDocuments(moviesDocuments);
	}

	private static void indexDocuments(List<Document> documents)
			throws InterruptedException, ExecutionException {
		if (documents == null || documents.isEmpty()) {
			return;
		}
		int delay = 2;
		Index index = getIndex();
		while (true) {
			try {
				index.put(documents);
			} catch (Exception ex) {
				LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
				Thread.sleep(delay * 1000);
				delay *= 2; // easy exponential backoff
				continue;
			}
			break;
		}
	}

	public static Set<Movie> findDocumentsWithContent(String searchInput) {
		if (searchInput == null || searchInput.trim().isEmpty()) {
			return Collections.emptySet();
		}
		Set<Movie> searchResults = new HashSet<>();

		searchInput = searchInput.toLowerCase();

		final int maxRetry = 3;
		int attempts = 0;
		int delay = 2;

		String searchFormat = "title = %s OR location = %s OR releaseYear = %s";
		Index index = getIndex();
		while (true) {
			try {
				String queryString = String.format(searchFormat, searchInput, searchInput, searchInput);
				Results<ScoredDocument> results = index.search(queryString);

				// Iterate over the documents in the results
				for (ScoredDocument document : results) {
					// handle results
					String title = document.getOnlyField("originalTitle").getText();
					String location = document.getOnlyField("originalLocation").getText();
					int releaseYear = document.getOnlyField("releaseYear").getNumber().intValue();
					double latitude = document.getOnlyField("latitude").getNumber().doubleValue();
					double longitude = document.getOnlyField("longitude").getNumber().doubleValue();
					
					Movie movie = new Movie(title, location, releaseYear);
					movie.setLatitude(latitude);
					movie.setLongitude(longitude);
					searchResults.add(movie);
				}
			} catch (SearchException ex) {
				LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
				if (StatusCode.TRANSIENT_ERROR.equals(ex.getOperationResult().getCode()) && ++attempts < maxRetry) {
					// retry
					try {
						Thread.sleep(delay * 1000);
					} catch (InterruptedException e1) {
						// ignore
					}
					delay *= 2; // easy exponential backoff
					continue;
				} else {
					throw ex;
				}
			}
			break;
		}
		
		return searchResults;
	}

	// [START putting_document_with_retry]
	public static void indexADocument(Document document) throws InterruptedException {
		Index index = getIndex();

		final int maxRetry = 3;
		int attempts = 0;
		int delay = 2;
		while (true) {
			try {
				index.put(document);
			} catch (PutException ex) {
				LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
				if (StatusCode.TRANSIENT_ERROR.equals(ex.getOperationResult().getCode()) && ++attempts < maxRetry) { // retrying
					Thread.sleep(delay * 1000);
					delay *= 2; // easy exponential backoff
					continue;
				} else {
					throw ex; // otherwise throw
				}
			}
			break;
		}
	}
}
