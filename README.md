<b>SF Movies</b>

A service that shows on a map where movies have been filmed in San Francisco. The user can filter the view by providing a search criteria.
<br />
<br />
<b>Demo URL</b>: http://dayohack1.appspot.com/
<br />
<br />
<b>Data Source</b>: The data was gotten from DataSF (https://data.sfgov.org/Culture-and-Recreation/Film-Locations-in-San-Francisco/yitu-d5am). Issues when dealing with the data were: Missing locations, Missing movie titles, Duplicate entries. In addition the data had to be geocoded using Google Geocoding Service in order to convert the location names to proper latitude/longitude combinations to display on a map. Hence, from an original data size of 1622 records, only 1408 were actually usable.
<br />
<br />
<b>Backend API:</b>
<br />
<b>GET /search</b>: Get all locations where movies were filmed in San Francisco.
<br />
<b>POST /search?searchQuery=<Search_Criteria></b>: Get all locations based on the given search criteria. The search can be based on the Movie title, the name of the San Francisco location or the Release Year of the movie. Partial matches are also supported.
<br />
<br />
The API response for both GET and POST is:
<b>
[{<br />
  location:&lt;String&gt;,<br />
  latitude:&lt;Double&gt;,<br />
  longitude:&lt;Double&gt;,<br />
  moviesForLocation:[{<br />
    title:&lt;String&gt;,<br />
    releaseYear:&lt;Integer&gt;<br />
  }]&nbsp;
}]
</b>
<br />
<br />
<b>Technical Specs</b>: 
Google App Engine SDK 1.9.60, 
Java, 
Google Maps JavaScript API, 
Google Maps Geocoding API, 
Twitter Bootstrap 4.0.0, 
Twitter Typeahead, 
Objectify 5.1.22, 
Apache Maven 3.5.2, 
Apache Commons CSV 1.5, 
Jackson JSON project 2.8.7, 
Eclipse IDE 4.7.2 (Oxygen), 
JQuery 3.3.1,
Junit 4,
PowerMock/Mockito 2,
Maven 3
